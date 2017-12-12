package org.springframework.util;

import java.util.Comparator;
import java.util.Map;

public interface PathMatcher {
	boolean isPattern(String arg0);

	boolean match(String arg0, String arg1);

	boolean matchStart(String arg0, String arg1);

	String extractPathWithinPattern(String arg0, String arg1);

	Map<String, String> extractUriTemplateVariables(String arg0, String arg1);

	Comparator<String> getPatternComparator(String arg0);

	String combine(String arg0, String arg1);
}