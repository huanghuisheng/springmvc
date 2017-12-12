package org.springframework.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntPathMatcher implements PathMatcher {
	private static final Pattern VARIABLE_PATTERN = Pattern
			.compile("\\{[^/]+?\\}");
	public static final String DEFAULT_PATH_SEPARATOR = "/";
	private String pathSeparator = "/";

	public void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator != null ? pathSeparator : "/";
	}

	public boolean isPattern(String path) {
		return path.indexOf(42) != -1 || path.indexOf(63) != -1;
	}

	public boolean match(String pattern, String path) {
		return this.doMatch(pattern, path, true, (Map) null);
	}

	public boolean matchStart(String pattern, String path) {
		return this.doMatch(pattern, path, false, (Map) null);
	}

	protected boolean doMatch(String pattern, String path, boolean fullMatch,
			Map<String, String> uriTemplateVariables) {
		if (path.startsWith(this.pathSeparator) != pattern
				.startsWith(this.pathSeparator)) {
			return false;
		} else {
			String[] pattDirs = StringUtils.tokenizeToStringArray(pattern,
					this.pathSeparator);
			String[] pathDirs = StringUtils.tokenizeToStringArray(path,
					this.pathSeparator);
			int pattIdxStart = 0;
			int pattIdxEnd = pattDirs.length - 1;
			int pathIdxStart = 0;

			int pathIdxEnd;
			String i;
			for (pathIdxEnd = pathDirs.length - 1; pattIdxStart <= pattIdxEnd
					&& pathIdxStart <= pathIdxEnd; ++pathIdxStart) {
				i = pattDirs[pattIdxStart];
				if ("**".equals(i)) {
					break;
				}

				if (!this.matchStrings(i, pathDirs[pathIdxStart],
						uriTemplateVariables)) {
					return false;
				}

				++pattIdxStart;
			}

			int arg18;
			if (pathIdxStart > pathIdxEnd) {
				if (pattIdxStart > pattIdxEnd) {
					return pattern.endsWith(this.pathSeparator) ? path
							.endsWith(this.pathSeparator) : !path
							.endsWith(this.pathSeparator);
				} else if (!fullMatch) {
					return true;
				} else if (pattIdxStart == pattIdxEnd
						&& pattDirs[pattIdxStart].equals("*")
						&& path.endsWith(this.pathSeparator)) {
					return true;
				} else {
					for (arg18 = pattIdxStart; arg18 <= pattIdxEnd; ++arg18) {
						if (!pattDirs[arg18].equals("**")) {
							return false;
						}
					}

					return true;
				}
			} else if (pattIdxStart > pattIdxEnd) {
				return false;
			} else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
				return true;
			} else {
				while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
					i = pattDirs[pattIdxEnd];
					if (i.equals("**")) {
						break;
					}

					if (!this.matchStrings(i, pathDirs[pathIdxEnd],
							uriTemplateVariables)) {
						return false;
					}

					--pattIdxEnd;
					--pathIdxEnd;
				}

				if (pathIdxStart > pathIdxEnd) {
					for (arg18 = pattIdxStart; arg18 <= pattIdxEnd; ++arg18) {
						if (!pattDirs[arg18].equals("**")) {
							return false;
						}
					}

					return true;
				} else {
					while (pattIdxStart != pattIdxEnd
							&& pathIdxStart <= pathIdxEnd) {
						arg18 = -1;

						int patLength;
						for (patLength = pattIdxStart + 1; patLength <= pattIdxEnd; ++patLength) {
							if (pattDirs[patLength].equals("**")) {
								arg18 = patLength;
								break;
							}
						}

						if (arg18 == pattIdxStart + 1) {
							++pattIdxStart;
						} else {
							patLength = arg18 - pattIdxStart - 1;
							int strLength = pathIdxEnd - pathIdxStart + 1;
							int foundIdx = -1;
							int i1 = 0;

							label140 : while (i1 <= strLength - patLength) {
								for (int j = 0; j < patLength; ++j) {
									String subPat = pattDirs[pattIdxStart + j
											+ 1];
									String subStr = pathDirs[pathIdxStart + i1
											+ j];
									if (!this.matchStrings(subPat, subStr,
											uriTemplateVariables)) {
										++i1;
										continue label140;
									}
								}

								foundIdx = pathIdxStart + i1;
								break;
							}

							if (foundIdx == -1) {
								return false;
							}

							pattIdxStart = arg18;
							pathIdxStart = foundIdx + patLength;
						}
					}

					for (arg18 = pattIdxStart; arg18 <= pattIdxEnd; ++arg18) {
						if (!pattDirs[arg18].equals("**")) {
							return false;
						}
					}

					return true;
				}
			}
		}
	}

	private boolean matchStrings(String pattern, String str,
			Map<String, String> uriTemplateVariables) {
		AntPathStringMatcher matcher = new AntPathStringMatcher(pattern, str,
				uriTemplateVariables);
		return matcher.matchStrings();
	}

	public String extractPathWithinPattern(String pattern, String path) {
		String[] patternParts = StringUtils.tokenizeToStringArray(pattern,
				this.pathSeparator);
		String[] pathParts = StringUtils.tokenizeToStringArray(path,
				this.pathSeparator);
		StringBuilder builder = new StringBuilder();
		int puts = 0;

		int i;
		for (i = 0; i < patternParts.length; ++i) {
			String patternPart = patternParts[i];
			if ((patternPart.indexOf(42) > -1 || patternPart.indexOf(63) > -1)
					&& pathParts.length >= i + 1) {
				if (puts > 0 || i == 0
						&& !pattern.startsWith(this.pathSeparator)) {
					builder.append(this.pathSeparator);
				}

				builder.append(pathParts[i]);
				++puts;
			}
		}

		for (i = patternParts.length; i < pathParts.length; ++i) {
			if (puts > 0 || i > 0) {
				builder.append(this.pathSeparator);
			}

			builder.append(pathParts[i]);
		}

		return builder.toString();
	}

	public Map<String, String> extractUriTemplateVariables(String pattern,
			String path) {
		LinkedHashMap variables = new LinkedHashMap();
		boolean result = this.doMatch(pattern, path, true, variables);
		Assert.state(result, "Pattern \"" + pattern
				+ "\" is not a match for \"" + path + "\"");
		return variables;
	}

	public String combine(String pattern1, String pattern2) {
		if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {
			return "";
		} else if (!StringUtils.hasText(pattern1)) {
			return pattern2;
		} else if (!StringUtils.hasText(pattern2)) {
			return pattern1;
		} else if (!pattern1.equals(pattern2) && !pattern1.contains("{")
				&& this.match(pattern1, pattern2)) {
			return pattern2;
		} else if (pattern1.endsWith("/*")) {
			return pattern2.startsWith("/") ? pattern1.substring(0,
					pattern1.length() - 1)
					+ pattern2.substring(1) : pattern1.substring(0,
					pattern1.length() - 1)
					+ pattern2;
		} else if (pattern1.endsWith("/**")) {
			return pattern2.startsWith("/") ? pattern1 + pattern2 : pattern1
					+ "/" + pattern2;
		} else {
			int dotPos1 = pattern1.indexOf(46);
			if (dotPos1 == -1) {
				return !pattern1.endsWith("/") && !pattern2.startsWith("/")
						? pattern1 + "/" + pattern2
						: pattern1 + pattern2;
			} else {
				String fileName1 = pattern1.substring(0, dotPos1);
				String extension1 = pattern1.substring(dotPos1);
				int dotPos2 = pattern2.indexOf(46);
				String fileName2;
				String extension2;
				if (dotPos2 != -1) {
					fileName2 = pattern2.substring(0, dotPos2);
					extension2 = pattern2.substring(dotPos2);
				} else {
					fileName2 = pattern2;
					extension2 = "";
				}

				String fileName = fileName1.endsWith("*")
						? fileName2
						: fileName1;
				String extension = extension1.startsWith("*")
						? extension2
						: extension1;
				return fileName + extension;
			}
		}
	}

	public Comparator<String> getPatternComparator(String path) {
		return new AntPathMatcher.AntPatternComparator(path);
	}

	private static class AntPatternComparator implements Comparator<String> {
		private final String path;

		private AntPatternComparator(String path) {
			this.path = path;
		}

		public int compare(String pattern1, String pattern2) {
			if (pattern1 == null && pattern2 == null) {
				return 0;
			} else if (pattern1 == null) {
				return 1;
			} else if (pattern2 == null) {
				return -1;
			} else {
				boolean pattern1EqualsPath = pattern1.equals(this.path);
				boolean pattern2EqualsPath = pattern2.equals(this.path);
				if (pattern1EqualsPath && pattern2EqualsPath) {
					return 0;
				} else if (pattern1EqualsPath) {
					return -1;
				} else if (pattern2EqualsPath) {
					return 1;
				} else {
					int wildCardCount1 = this.getWildCardCount(pattern1);
					int wildCardCount2 = this.getWildCardCount(pattern2);
					int bracketCount1 = StringUtils.countOccurrencesOf(
							pattern1, "{");
					int bracketCount2 = StringUtils.countOccurrencesOf(
							pattern2, "{");
					int totalCount1 = wildCardCount1 + bracketCount1;
					int totalCount2 = wildCardCount2 + bracketCount2;
					if (totalCount1 != totalCount2) {
						return totalCount1 - totalCount2;
					} else {
						int pattern1Length = this.getPatternLength(pattern1);
						int pattern2Length = this.getPatternLength(pattern2);
						return pattern1Length != pattern2Length
								? pattern2Length - pattern1Length
								: (wildCardCount1 < wildCardCount2
										? -1
										: (wildCardCount2 < wildCardCount1
												? 1
												: (bracketCount1 < bracketCount2
														? -1
														: (bracketCount2 < bracketCount1
																? 1
																: 0))));
					}
				}
			}
		}

		private int getWildCardCount(String pattern) {
			if (pattern.endsWith(".*")) {
				pattern = pattern.substring(0, pattern.length() - 2);
			}

			return StringUtils.countOccurrencesOf(pattern, "*");
		}

		private int getPatternLength(String pattern) {
			Matcher m = AntPathMatcher.VARIABLE_PATTERN.matcher(pattern);
			return m.replaceAll("#").length();
		}
	}
}
