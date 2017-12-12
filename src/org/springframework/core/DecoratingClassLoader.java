/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.util.Assert;

public abstract class DecoratingClassLoader extends ClassLoader {
	private final Set<String> excludedPackages = new HashSet();
	private final Set<String> excludedClasses = new HashSet();
	private final Object exclusionMonitor = new Object();

	public DecoratingClassLoader() {
	}

	public DecoratingClassLoader(ClassLoader parent) {
		super(parent);
	}

	public void excludePackage(String packageName) {
		Assert.notNull(packageName, "Package name must not be null");
		Object arg1 = this.exclusionMonitor;
		synchronized (this.exclusionMonitor) {
			this.excludedPackages.add(packageName);
		}
	}

	public void excludeClass(String className) {
		Assert.notNull(className, "Class name must not be null");
		Object arg1 = this.exclusionMonitor;
		synchronized (this.exclusionMonitor) {
			this.excludedClasses.add(className);
		}
	}

	protected boolean isExcluded(String className) {
		Object arg1 = this.exclusionMonitor;
		synchronized (this.exclusionMonitor) {
			if (this.excludedClasses.contains(className)) {
				return true;
			} else {
				Iterator i$ = this.excludedPackages.iterator();

				String packageName;
				do {
					if (!i$.hasNext()) {
						return false;
					}

					packageName = (String) i$.next();
				} while (!className.startsWith(packageName));

				return true;
			}
		}
	}
}