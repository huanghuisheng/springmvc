package org.springframework.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class PropertyPlaceholderHelper {
	private static final Log logger = LogFactory
			.getLog(PropertyPlaceholderHelper.class);
	private static final Map<String, String> wellKnownSimplePrefixes = new HashMap(
			4);
	private final String placeholderPrefix;
	private final String placeholderSuffix;
	private final String simplePrefix;
	private final String valueSeparator;
	private final boolean ignoreUnresolvablePlaceholders;

	public PropertyPlaceholderHelper(String placeholderPrefix,
			String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, (String) null, true);
	}

	public PropertyPlaceholderHelper(String placeholderPrefix,
			String placeholderSuffix, String valueSeparator,
			boolean ignoreUnresolvablePlaceholders) {
		Assert.notNull(placeholderPrefix, "placeholderPrefix must not be null");
		Assert.notNull(placeholderSuffix, "placeholderSuffix must not be null");
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		String simplePrefixForSuffix = (String) wellKnownSimplePrefixes
				.get(this.placeholderSuffix);
		if (simplePrefixForSuffix != null
				&& this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
			this.simplePrefix = simplePrefixForSuffix;
		} else {
			this.simplePrefix = this.placeholderPrefix;
		}

		this.valueSeparator = valueSeparator;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	public String replacePlaceholders(String value, final Properties properties) {
		Assert.notNull(properties, "Argument \'properties\' must not be null.");
		return this.replacePlaceholders(value,
				new PropertyPlaceholderHelper.PlaceholderResolver() {
					public String resolvePlaceholder(String placeholderName) {
						return properties.getProperty(placeholderName);
					}
				});
	}

	public String replacePlaceholders(String value,
			PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver) {
		Assert.notNull(value, "Argument \'value\' must not be null.");
		return this.parseStringValue(value, placeholderResolver, new HashSet());
	}

	protected String parseStringValue(String strVal,
			PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver,
			Set<String> visitedPlaceholders) {
		StringBuilder buf = new StringBuilder(strVal);
		int startIndex = strVal.indexOf(this.placeholderPrefix);

		while (startIndex != -1) {
			int endIndex = this.findPlaceholderEndIndex(buf, startIndex);
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex
						+ this.placeholderPrefix.length(), endIndex);
				String originalPlaceholder = placeholder;
				if (!visitedPlaceholders.add(placeholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference \'" + placeholder
									+ "\' in property definitions");
				}

				placeholder = this.parseStringValue(placeholder,
						placeholderResolver, visitedPlaceholders);
				String propVal = placeholderResolver
						.resolvePlaceholder(placeholder);
				if (propVal == null && this.valueSeparator != null) {
					int separatorIndex = placeholder
							.indexOf(this.valueSeparator);
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0,
								separatorIndex);
						String defaultValue = placeholder
								.substring(separatorIndex
										+ this.valueSeparator.length());
						propVal = placeholderResolver
								.resolvePlaceholder(actualPlaceholder);
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}

				if (propVal != null) {
					propVal = this.parseStringValue(propVal,
							placeholderResolver, visitedPlaceholders);
					buf.replace(startIndex,
							endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder \'" + placeholder
								+ "\'");
					}

					startIndex = buf.indexOf(this.placeholderPrefix, startIndex
							+ propVal.length());
				} else {
					if (!this.ignoreUnresolvablePlaceholders) {
						throw new IllegalArgumentException(
								"Could not resolve placeholder \'"
										+ placeholder + "\'");
					}

					startIndex = buf.indexOf(this.placeholderPrefix, endIndex
							+ this.placeholderSuffix.length());
				}

				visitedPlaceholders.remove(originalPlaceholder);
			} else {
				startIndex = -1;
			}
		}

		return buf.toString();
	}

	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();
		int withinNestedPlaceholder = 0;

		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
				if (withinNestedPlaceholder <= 0) {
					return index;
				}

				--withinNestedPlaceholder;
				index += this.placeholderSuffix.length();
			} else if (StringUtils
					.substringMatch(buf, index, this.simplePrefix)) {
				++withinNestedPlaceholder;
				index += this.simplePrefix.length();
			} else {
				++index;
			}
		}

		return -1;
	}

	static {
		wellKnownSimplePrefixes.put("}", "{");
		wellKnownSimplePrefixes.put("]", "[");
		wellKnownSimplePrefixes.put(")", "(");
	}

	public interface PlaceholderResolver {
		String resolvePlaceholder(String arg0);
	}
}