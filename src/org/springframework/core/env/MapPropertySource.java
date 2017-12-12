package org.springframework.core.env;

import java.util.Map;
import org.springframework.core.env.EnumerablePropertySource;

public class MapPropertySource
		extends
			EnumerablePropertySource<Map<String, Object>> {
	public MapPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}

	public Object getProperty(String name) {
		return ((Map) this.source).get(name);
	}

	public String[] getPropertyNames() {
		return (String[]) ((Map) this.source).keySet().toArray(
				EMPTY_NAMES_ARRAY);
	}
}