package org.springframework.core;

import java.util.Map;

@Deprecated
public interface ConcurrentMap extends Map {
	Object putIfAbsent(Object arg0, Object arg1);

	boolean remove(Object arg0, Object arg1);

	boolean replace(Object arg0, Object arg1, Object arg2);

	Object replace(Object arg0, Object arg1);
}
