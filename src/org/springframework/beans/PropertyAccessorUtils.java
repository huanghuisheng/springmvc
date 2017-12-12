package org.springframework.beans;

public abstract class PropertyAccessorUtils {
	public static String getPropertyName(String propertyPath) {
		int separatorIndex = propertyPath.endsWith("]") ? propertyPath
				.indexOf(91) : -1;
		return separatorIndex != -1
				? propertyPath.substring(0, separatorIndex)
				: propertyPath;
	}

	public static boolean isNestedOrIndexedProperty(String propertyPath) {
		if (propertyPath == null) {
			return false;
		} else {
			for (int i = 0; i < propertyPath.length(); ++i) {
				char ch = propertyPath.charAt(i);
				if (ch == 46 || ch == 91) {
					return true;
				}
			}

			return false;
		}
	}

	public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
		return getNestedPropertySeparatorIndex(propertyPath, false);
	}

	public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
		return getNestedPropertySeparatorIndex(propertyPath, true);
	}

	private static int getNestedPropertySeparatorIndex(String propertyPath,
			boolean last) {
		boolean inKey = false;
		int length = propertyPath.length();
		int i = last ? length - 1 : 0;

		while (true) {
			if (last) {
				if (i < 0) {
					break;
				}
			} else if (i >= length) {
				break;
			}

			switch (propertyPath.charAt(i)) {
				case '.' :
					if (!inKey) {
						return i;
					}
					break;
				case '[' :
				case ']' :
					inKey = !inKey;
			}

			if (last) {
				--i;
			} else {
				++i;
			}
		}

		return -1;
	}

	public static boolean matchesProperty(String registeredPath,
			String propertyPath) {
		return !registeredPath.startsWith(propertyPath)
				? false
				: (registeredPath.length() == propertyPath.length()
						? true
						: (registeredPath.charAt(propertyPath.length()) != 91
								? false
								: registeredPath.indexOf(93,
										propertyPath.length() + 1) == registeredPath
										.length() - 1));
	}

	public static String canonicalPropertyName(String propertyName) {
		if (propertyName == null) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder(propertyName);
			int searchIndex = 0;

			while (true) {
				int keyStart;
				int keyEnd;
				do {
					do {
						if (searchIndex == -1) {
							return sb.toString();
						}

						keyStart = sb.indexOf("[", searchIndex);
						searchIndex = -1;
					} while (keyStart == -1);

					keyEnd = sb.indexOf("]", keyStart + "[".length());
				} while (keyEnd == -1);

				String key = sb.substring(keyStart + "[".length(), keyEnd);
				if (key.startsWith("\'") && key.endsWith("\'")
						|| key.startsWith("\"") && key.endsWith("\"")) {
					sb.delete(keyStart + 1, keyStart + 2);
					sb.delete(keyEnd - 2, keyEnd - 1);
					keyEnd -= 2;
				}

				searchIndex = keyEnd + "]".length();
			}
		}
	}

	public static String[] canonicalPropertyNames(String[] propertyNames) {
		if (propertyNames == null) {
			return null;
		} else {
			String[] result = new String[propertyNames.length];

			for (int i = 0; i < propertyNames.length; ++i) {
				result[i] = canonicalPropertyName(propertyNames[i]);
			}

			return result;
		}
	}
}