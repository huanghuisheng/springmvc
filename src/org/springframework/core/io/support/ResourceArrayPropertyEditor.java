/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.core.io.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceArrayPropertyEditor extends PropertyEditorSupport {
	private static final Log logger = LogFactory
			.getLog(ResourceArrayPropertyEditor.class);
	private final PropertyResolver propertyResolver;
	private final ResourcePatternResolver resourcePatternResolver;
	private final boolean ignoreUnresolvablePlaceholders;

	public ResourceArrayPropertyEditor() {
		this(new PathMatchingResourcePatternResolver(),
				new StandardEnvironment(), true);
	}

	@Deprecated
	public ResourceArrayPropertyEditor(
			ResourcePatternResolver resourcePatternResolver) {
		this(resourcePatternResolver, new StandardEnvironment(), true);
	}

	public ResourceArrayPropertyEditor(
			ResourcePatternResolver resourcePatternResolver,
			PropertyResolver propertyResolver) {
		this(resourcePatternResolver, propertyResolver, true);
	}

	@Deprecated
	public ResourceArrayPropertyEditor(
			ResourcePatternResolver resourcePatternResolver,
			boolean ignoreUnresolvablePlaceholders) {
		this(resourcePatternResolver, new StandardEnvironment(),
				ignoreUnresolvablePlaceholders);
	}

	public ResourceArrayPropertyEditor(
			ResourcePatternResolver resourcePatternResolver,
			PropertyResolver propertyResolver,
			boolean ignoreUnresolvablePlaceholders) {
		this.resourcePatternResolver = resourcePatternResolver;
		this.propertyResolver = propertyResolver;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	public void setAsText(String text) {
		String pattern = this.resolvePath(text).trim();

		try {
			this.setValue(this.resourcePatternResolver.getResources(pattern));
		} catch (IOException arg3) {
			throw new IllegalArgumentException(
					"Could not resolve resource location pattern [" + pattern
							+ "]: " + arg3.getMessage());
		}
	}

	public void setValue(Object value) throws IllegalArgumentException {
		if (value instanceof Collection || value instanceof Object[]
				&& !(value instanceof Resource[])) {
			Object input = value instanceof Collection
					? (Collection) value
					: Arrays.asList((Object[]) ((Object[]) value));
			ArrayList merged = new ArrayList();
			Iterator i$ = ((Collection) input).iterator();

			while (true) {
				while (i$.hasNext()) {
					Object element = i$.next();
					if (element instanceof String) {
						String arg12 = this.resolvePath((String) element)
								.trim();

						try {
							Resource[] ex = this.resourcePatternResolver
									.getResources(arg12);
							Resource[] arr$ = ex;
							int len$ = ex.length;

							for (int i$1 = 0; i$1 < len$; ++i$1) {
								Resource resource1 = arr$[i$1];
								if (!merged.contains(resource1)) {
									merged.add(resource1);
								}
							}
						} catch (IOException arg11) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Could not retrieve resources for pattern \'"
												+ arg12 + "\'", arg11);
							}
						}
					} else {
						if (!(element instanceof Resource)) {
							throw new IllegalArgumentException(
									"Cannot convert element ["
											+ element
											+ "] to ["
											+ Resource.class.getName()
											+ "]: only location String and Resource object supported");
						}

						Resource resource = (Resource) element;
						if (!merged.contains(resource)) {
							merged.add(resource);
						}
					}
				}

				super.setValue(merged.toArray(new Resource[merged.size()]));
				break;
			}
		} else {
			super.setValue(value);
		}

	}

	protected String resolvePath(String path) {
		return this.ignoreUnresolvablePlaceholders ? this.propertyResolver
				.resolvePlaceholders(path) : this.propertyResolver
				.resolveRequiredPlaceholders(path);
	}
}
