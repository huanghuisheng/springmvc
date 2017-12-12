/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public final class PropertyMatches {
	public static final int DEFAULT_MAX_DISTANCE = 2;
	private final String propertyName;
	private String[] possibleMatches;

	public static PropertyMatches forProperty(String propertyName,
			Class beanClass) {
		return forProperty(propertyName, beanClass, 2);
	}

	public static PropertyMatches forProperty(String propertyName,
			Class beanClass, int maxDistance) {
		return new PropertyMatches(propertyName, beanClass, maxDistance);
	}

	private PropertyMatches(String propertyName, Class beanClass,
			int maxDistance) {
		this.propertyName = propertyName;
		this.possibleMatches = this.calculateMatches(
				BeanUtils.getPropertyDescriptors(beanClass), maxDistance);
	}

	public String[] getPossibleMatches() {
		return this.possibleMatches;
	}

	public String buildErrorMessage() {
		StringBuilder msg = new StringBuilder();
		msg.append("Bean property \'");
		msg.append(this.propertyName);
		msg.append("\' is not writable or has an invalid setter method. ");
		if (ObjectUtils.isEmpty(this.possibleMatches)) {
			msg.append("Does the parameter type of the setter match the return type of the getter?");
		} else {
			msg.append("Did you mean ");

			for (int i = 0; i < this.possibleMatches.length; ++i) {
				msg.append('\'');
				msg.append(this.possibleMatches[i]);
				if (i < this.possibleMatches.length - 2) {
					msg.append("\', ");
				} else if (i == this.possibleMatches.length - 2) {
					msg.append("\', or ");
				}
			}

			msg.append("\'?");
		}

		return msg.toString();
	}

	private String[] calculateMatches(PropertyDescriptor[] propertyDescriptors,
			int maxDistance) {
		ArrayList candidates = new ArrayList();
		PropertyDescriptor[] arr$ = propertyDescriptors;
		int len$ = propertyDescriptors.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			PropertyDescriptor pd = arr$[i$];
			if (pd.getWriteMethod() != null) {
				String possibleAlternative = pd.getName();
				if (this.calculateStringDistance(this.propertyName,
						possibleAlternative) <= maxDistance) {
					candidates.add(possibleAlternative);
				}
			}
		}

		Collections.sort(candidates);
		return StringUtils.toStringArray(candidates);
	}

	private int calculateStringDistance(String s1, String s2) {
		if (s1.length() == 0) {
			return s2.length();
		} else if (s2.length() == 0) {
			return s1.length();
		} else {
			int[][] d = new int[s1.length() + 1][s2.length() + 1];

			int i;
			for (i = 0; i <= s1.length(); d[i][0] = i++) {
				;
			}

			for (i = 0; i <= s2.length(); d[0][i] = i++) {
				;
			}

			for (i = 1; i <= s1.length(); ++i) {
				char s_i = s1.charAt(i - 1);

				for (int j = 1; j <= s2.length(); ++j) {
					char t_j = s2.charAt(j - 1);
					byte cost;
					if (s_i == t_j) {
						cost = 0;
					} else {
						cost = 1;
					}

					d[i][j] = Math.min(
							Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
							d[i - 1][j - 1] + cost);
				}
			}

			return d[s1.length()][s2.length()];
		}
	}
}