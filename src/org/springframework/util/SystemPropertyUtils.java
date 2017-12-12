package org.springframework.util;

import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

public abstract class SystemPropertyUtils {
	public static final String PLACEHOLDER_PREFIX = "${";
	public static final String PLACEHOLDER_SUFFIX = "}";
	public static final String VALUE_SEPARATOR = ":";
	private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(
			"${", "}", ":", false);
	private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(
			"${", "}", ":", true);

	public static String resolvePlaceholders(String text) {
		return resolvePlaceholders(text, false);
	}

	public static String resolvePlaceholders(String text,
			boolean ignoreUnresolvablePlaceholders) {
		PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders
				? nonStrictHelper
				: strictHelper;
		return helper
				.replacePlaceholders(
						text,
						new SystemPropertyUtils.SystemPropertyPlaceholderResolver(
								text));
	}

	private static class SystemPropertyPlaceholderResolver
			implements
				PlaceholderResolver {
		private final String text;

		public SystemPropertyPlaceholderResolver(String text) {
			this.text = text;
		}

		public String resolvePlaceholder(String placeholderName) {
			try {
				String ex = System.getProperty(placeholderName);
				if (ex == null) {
					ex = System.getenv(placeholderName);
				}

				return ex;
			} catch (Throwable arg2) {
				System.err.println("Could not resolve placeholder \'"
						+ placeholderName + "\' in [" + this.text
						+ "] as system property: " + arg2);
				return null;
			}
		}
	}
}
