/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.core.io.support;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.VfsResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.VfsPatternUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public class PathMatchingResourcePatternResolver
		implements
			ResourcePatternResolver {
	private static final Log logger = LogFactory
			.getLog(PathMatchingResourcePatternResolver.class);
	private static Method equinoxResolveMethod;
	private final ResourceLoader resourceLoader;
	private PathMatcher pathMatcher = new AntPathMatcher();

	public PathMatchingResourcePatternResolver() {
		this.resourceLoader = new DefaultResourceLoader();
	}

	public PathMatchingResourcePatternResolver(ClassLoader classLoader) {
		this.resourceLoader = new DefaultResourceLoader(classLoader);
	}

	public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "ResourceLoader must not be null");
		this.resourceLoader = resourceLoader;
	}

	public ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	public ClassLoader getClassLoader() {
		return this.getResourceLoader().getClassLoader();
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
	}

	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	public Resource getResource(String location) {
		return this.getResourceLoader().getResource(location);
	}

	public Resource[] getResources(String locationPattern) throws IOException {
		Assert.notNull(locationPattern, "Location pattern must not be null");
		if (locationPattern.startsWith("classpath*:")) {
			return this.getPathMatcher().isPattern(
					locationPattern.substring("classpath*:".length())) ? this
					.findPathMatchingResources(locationPattern) : this
					.findAllClassPathResources(locationPattern
							.substring("classpath*:".length()));
		} else {
			int prefixEnd = locationPattern.indexOf(":") + 1;
			return this.getPathMatcher().isPattern(
					locationPattern.substring(prefixEnd))
					? this.findPathMatchingResources(locationPattern)
					: new Resource[]{this.getResourceLoader().getResource(
							locationPattern)};
		}
	}

	protected Resource[] findAllClassPathResources(String location)
			throws IOException {
		String path = location;
		if (location.startsWith("/")) {
			path = location.substring(1);
		}

		Enumeration resourceUrls = this.getClassLoader().getResources(path);
		LinkedHashSet result = new LinkedHashSet(16);

		while (resourceUrls.hasMoreElements()) {
			URL url = (URL) resourceUrls.nextElement();
			result.add(this.convertClassLoaderURL(url));
		}

		return (Resource[]) result.toArray(new Resource[result.size()]);
	}

	protected Resource convertClassLoaderURL(URL url) {
		return new UrlResource(url);
	}

	protected Resource[] findPathMatchingResources(String locationPattern)
			throws IOException {
		String rootDirPath = this.determineRootDir(locationPattern);
		String subPattern = locationPattern.substring(rootDirPath.length());
		Resource[] rootDirResources = this.getResources(rootDirPath);
		LinkedHashSet result = new LinkedHashSet(16);
		Resource[] arr$ = rootDirResources;
		int len$ = rootDirResources.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Resource rootDirResource = arr$[i$];
			rootDirResource = this.resolveRootDirResource(rootDirResource);
			if (this.isJarResource(rootDirResource)) {
				result.addAll(this.doFindPathMatchingJarResources(
						rootDirResource, subPattern));
			} else if (rootDirResource.getURL().getProtocol().startsWith("vfs")) {
				result.addAll(PathMatchingResourcePatternResolver.VfsResourceMatchingDelegate
						.findMatchingResources(rootDirResource, subPattern,
								this.getPathMatcher()));
			} else {
				result.addAll(this.doFindPathMatchingFileResources(
						rootDirResource, subPattern));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Resolved location pattern [" + locationPattern
					+ "] to resources " + result);
		}

		return (Resource[]) result.toArray(new Resource[result.size()]);
	}

	protected String determineRootDir(String location) {
		int prefixEnd = location.indexOf(":") + 1;

		int rootDirEnd;
		for (rootDirEnd = location.length(); rootDirEnd > prefixEnd
				&& this.getPathMatcher().isPattern(
						location.substring(prefixEnd, rootDirEnd)); rootDirEnd = location
				.lastIndexOf(47, rootDirEnd - 2) + 1) {
			;
		}

		if (rootDirEnd == 0) {
			rootDirEnd = prefixEnd;
		}

		return location.substring(0, rootDirEnd);
	}

	protected Resource resolveRootDirResource(Resource original)
			throws IOException {
		if (equinoxResolveMethod != null) {
			URL url = original.getURL();
			if (url.getProtocol().startsWith("bundle")) {
				return new UrlResource((URL) ReflectionUtils.invokeMethod(
						equinoxResolveMethod, (Object) null, new Object[]{url}));
			}
		}

		return original;
	}

	protected boolean isJarResource(Resource resource) throws IOException {
		return ResourceUtils.isJarURL(resource.getURL());
	}

	protected Set<Resource> doFindPathMatchingJarResources(
			Resource rootDirResource, String subPattern) throws IOException {
		URLConnection con = rootDirResource.getURL().openConnection();
		boolean newJarFile = false;
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		if (con instanceof JarURLConnection) {
			JarURLConnection result = (JarURLConnection) con;
			ResourceUtils.useCachesIfNecessary(result);
			jarFile = result.getJarFile();
			jarFileUrl = result.getJarFileURL().toExternalForm();
			JarEntry entries = result.getJarEntry();
			rootEntryPath = entries != null ? entries.getName() : "";
		} else {
			String result2 = rootDirResource.getURL().getFile();
			int entries1 = result2.indexOf("!/");
			if (entries1 != -1) {
				jarFileUrl = result2.substring(0, entries1);
				rootEntryPath = result2.substring(entries1 + "!/".length());
				jarFile = this.getJarFile(jarFileUrl);
			} else {
				jarFile = new JarFile(result2);
				jarFileUrl = result2;
				rootEntryPath = "";
			}

			newJarFile = true;
		}

		LinkedHashSet entries2;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Looking for matching resources in jar file ["
						+ jarFileUrl + "]");
			}

			if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
				rootEntryPath = rootEntryPath + "/";
			}

			LinkedHashSet result1 = new LinkedHashSet(8);
			Enumeration entries3 = jarFile.entries();

			while (entries3.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries3.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(rootEntryPath)) {
					String relativePath = entryPath.substring(rootEntryPath
							.length());
					if (this.getPathMatcher().match(subPattern, relativePath)) {
						result1.add(rootDirResource
								.createRelative(relativePath));
					}
				}
			}

			entries2 = result1;
		} finally {
			if (newJarFile) {
				jarFile.close();
			}

		}

		return entries2;
	}

	protected JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith("file:")) {
			try {
				return new JarFile(ResourceUtils.toURI(jarFileUrl)
						.getSchemeSpecificPart());
			} catch (URISyntaxException arg2) {
				return new JarFile(jarFileUrl.substring("file:".length()));
			}
		} else {
			return new JarFile(jarFileUrl);
		}
	}

	protected Set<Resource> doFindPathMatchingFileResources(
			Resource rootDirResource, String subPattern) throws IOException {
		File rootDir;
		try {
			rootDir = rootDirResource.getFile().getAbsoluteFile();
		} catch (IOException arg4) {
			if (logger.isWarnEnabled()) {
				logger.warn(
						"Cannot search for matching files underneath "
								+ rootDirResource
								+ " because it does not correspond to a directory in the file system",
						arg4);
			}

			return Collections.emptySet();
		}

		return this.doFindMatchingFileSystemResources(rootDir, subPattern);
	}

	protected Set<Resource> doFindMatchingFileSystemResources(File rootDir,
			String subPattern) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for matching resources in directory tree ["
					+ rootDir.getPath() + "]");
		}

		Set matchingFiles = this.retrieveMatchingFiles(rootDir, subPattern);
		LinkedHashSet result = new LinkedHashSet(matchingFiles.size());
		Iterator i$ = matchingFiles.iterator();

		while (i$.hasNext()) {
			File file = (File) i$.next();
			result.add(new FileSystemResource(file));
		}

		return result;
	}

	protected Set<File> retrieveMatchingFiles(File rootDir, String pattern)
			throws IOException {
		if (!rootDir.exists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping [" + rootDir.getAbsolutePath()
						+ "] because it does not exist");
			}

			return Collections.emptySet();
		} else if (!rootDir.isDirectory()) {
			if (logger.isWarnEnabled()) {
				logger.warn("Skipping [" + rootDir.getAbsolutePath()
						+ "] because it does not denote a directory");
			}

			return Collections.emptySet();
		} else if (!rootDir.canRead()) {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot search for matching files underneath directory ["
						+ rootDir.getAbsolutePath()
						+ "] because the application is not allowed to read the directory");
			}

			return Collections.emptySet();
		} else {
			String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(),
					File.separator, "/");
			if (!pattern.startsWith("/")) {
				fullPattern = fullPattern + "/";
			}

			fullPattern = fullPattern
					+ StringUtils.replace(pattern, File.separator, "/");
			LinkedHashSet result = new LinkedHashSet(8);
			this.doRetrieveMatchingFiles(fullPattern, rootDir, result);
			return result;
		}
	}

	protected void doRetrieveMatchingFiles(String fullPattern, File dir,
			Set<File> result) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Searching directory [" + dir.getAbsolutePath()
					+ "] for files matching pattern [" + fullPattern + "]");
		}

		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not retrieve contents of directory ["
						+ dir.getAbsolutePath() + "]");
			}

		} else {
			File[] arr$ = dirContents;
			int len$ = dirContents.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				File content = arr$[i$];
				String currPath = StringUtils.replace(
						content.getAbsolutePath(), File.separator, "/");
				if (content.isDirectory()
						&& this.getPathMatcher().matchStart(fullPattern,
								currPath + "/")) {
					if (!content.canRead()) {
						if (logger.isDebugEnabled()) {
							logger.debug("Skipping subdirectory ["
									+ dir.getAbsolutePath()
									+ "] because the application is not allowed to read the directory");
						}
					} else {
						this.doRetrieveMatchingFiles(fullPattern, content,
								result);
					}
				}

				if (this.getPathMatcher().match(fullPattern, currPath)) {
					result.add(content);
				}
			}

		}
	}

	static {
		try {
			Class ex = PathMatchingResourcePatternResolver.class
					.getClassLoader().loadClass(
							"org.eclipse.core.runtime.FileLocator");
			equinoxResolveMethod = ex.getMethod("resolve",
					new Class[]{URL.class});
			logger.debug("Found Equinox FileLocator for OSGi bundle URL resolution");
		} catch (Throwable arg0) {
			equinoxResolveMethod = null;
		}

	}

	private static class PatternVirtualFileVisitor implements InvocationHandler {
		private final String subPattern;
		private final PathMatcher pathMatcher;
		private final String rootPath;
		private final Set<Resource> resources = new LinkedHashSet();

		public PatternVirtualFileVisitor(String rootPath, String subPattern,
				PathMatcher pathMatcher) {
			this.subPattern = subPattern;
			this.pathMatcher = pathMatcher;
			this.rootPath = rootPath.length() != 0 && !rootPath.endsWith("/")
					? rootPath + "/"
					: rootPath;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String methodName = method.getName();
			if (Object.class.equals(method.getDeclaringClass())) {
				if (methodName.equals("equals")) {
					return Boolean.valueOf(proxy == args[0]);
				}

				if (methodName.equals("hashCode")) {
					return Integer.valueOf(System.identityHashCode(proxy));
				}
			} else {
				if ("getAttributes".equals(methodName)) {
					return this.getAttributes();
				}

				if ("visit".equals(methodName)) {
					this.visit(args[0]);
					return null;
				}

				if ("toString".equals(methodName)) {
					return this.toString();
				}
			}

			throw new IllegalStateException("Unexpected method invocation: "
					+ method);
		}

		public void visit(Object vfsResource) {
			if (this.pathMatcher.match(this.subPattern, VfsPatternUtils
					.getPath(vfsResource).substring(this.rootPath.length()))) {
				this.resources.add(new VfsResource(vfsResource));
			}

		}

		public Object getAttributes() {
			return VfsPatternUtils.getVisitorAttribute();
		}

		public Set<Resource> getResources() {
			return this.resources;
		}

		public int size() {
			return this.resources.size();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("sub-pattern: ").append(this.subPattern);
			sb.append(", resources: ").append(this.resources);
			return sb.toString();
		}
	}

	private static class VfsResourceMatchingDelegate {
		public static Set<Resource> findMatchingResources(
				Resource rootResource, String locationPattern,
				PathMatcher pathMatcher) throws IOException {
			Object root = VfsPatternUtils.findRoot(rootResource.getURL());
			PathMatchingResourcePatternResolver.PatternVirtualFileVisitor visitor = new PathMatchingResourcePatternResolver.PatternVirtualFileVisitor(
					VfsPatternUtils.getPath(root), locationPattern, pathMatcher);
			VfsPatternUtils.visit(root, visitor);
			return visitor.getResources();
		}
	}
}