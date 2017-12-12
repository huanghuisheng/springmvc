package org.springframework.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AntPathStringMatcher {
	private static final Pattern GLOB_PATTERN = Pattern
			.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");
	private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";
	private final Pattern pattern;
	private String str;
	private final List<String> variableNames = new LinkedList();
	private final Map<String, String> uriTemplateVariables;

	AntPathStringMatcher(String pattern, String str,
			Map<String, String> uriTemplateVariables) {
		this.str = str;
		this.uriTemplateVariables = uriTemplateVariables;
		this.pattern = this.createPattern(pattern);
	}

	private Pattern createPattern(String pattern) {
		StringBuilder patternBuilder = new StringBuilder();
		Matcher m = GLOB_PATTERN.matcher(pattern);

		int end;
		for (end = 0; m.find(); end = m.end()) {
			patternBuilder.append(this.quote(pattern, end, m.start()));
			String match = m.group();
			if ("?".equals(match)) {
				patternBuilder.append('.');
			} else if ("*".equals(match)) {
				patternBuilder.append(".*");
			} else if (match.startsWith("{") && match.endsWith("}")) {
				int colonIdx = match.indexOf(58);
				if (colonIdx == -1) {
					patternBuilder.append("(.*)");
					this.variableNames.add(m.group(1));
				} else {
					String variablePattern = match.substring(colonIdx + 1,
							match.length() - 1);
					patternBuilder.append('(');
					patternBuilder.append(variablePattern);
					patternBuilder.append(')');
					String variableName = match.substring(1, colonIdx);
					this.variableNames.add(variableName);
				}
			}
		}

		patternBuilder.append(this.quote(pattern, end, pattern.length()));
		return Pattern.compile(patternBuilder.toString());
	}

	private String quote(String s, int start, int end) {
		return start == end ? "" : Pattern.quote(s.substring(start, end));
	}

	public boolean matchStrings() {
		Matcher matcher = this.pattern.matcher(this.str);
		if (!matcher.matches()) {
			return false;
		} else {
			if (this.uriTemplateVariables != null) {
				Assert.isTrue(
						this.variableNames.size() == matcher.groupCount(),
						"The number of capturing groups in the pattern segment "
								+ this.pattern
								+ " does not match the number of URI template variables it defines, which can occur if "
								+ " capturing groups are used in a URI template regex. Use non-capturing groups instead.");

				for (int i = 1; i <= matcher.groupCount(); ++i) {
					String name = (String) this.variableNames.get(i - 1);
					String value = matcher.group(i);
					this.uriTemplateVariables.put(name, value);
				}
			}

			return true;
		}
	}
}
