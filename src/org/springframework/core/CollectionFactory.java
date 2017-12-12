package org.springframework.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.core.ConcurrentMap;
import org.springframework.util.LinkedCaseInsensitiveMap;

public abstract class CollectionFactory {
	private static Class navigableSetClass = null;
	private static Class navigableMapClass = null;
	private static final Set<Class> approximableCollectionTypes = new HashSet(
			10);
	private static final Set<Class> approximableMapTypes = new HashSet(6);

	@Deprecated
	public static <T> Set<T> createLinkedSetIfPossible(int initialCapacity) {
		return new LinkedHashSet(initialCapacity);
	}

	@Deprecated
	public static <T> Set<T> createCopyOnWriteSet() {
		return new CopyOnWriteArraySet();
	}

	@Deprecated
	public static <K, V> Map<K, V> createLinkedMapIfPossible(int initialCapacity) {
		return new LinkedHashMap(initialCapacity);
	}

	@Deprecated
	public static Map createLinkedCaseInsensitiveMapIfPossible(
			int initialCapacity) {
		return new LinkedCaseInsensitiveMap(initialCapacity);
	}

	@Deprecated
	public static Map createIdentityMapIfPossible(int initialCapacity) {
		return new IdentityHashMap(initialCapacity);
	}

	@Deprecated
	public static Map createConcurrentMapIfPossible(int initialCapacity) {
		return new ConcurrentHashMap(initialCapacity);
	}

	@Deprecated
	public static ConcurrentMap createConcurrentMap(int initialCapacity) {
		return new CollectionFactory.JdkConcurrentHashMap(initialCapacity);
	}

	public static boolean isApproximableCollectionType(Class<?> collectionType) {
		return collectionType != null
				&& approximableCollectionTypes.contains(collectionType);
	}

	public static Collection createApproximateCollection(Object collection,
			int initialCapacity) {
		return (Collection) (collection instanceof LinkedList ? new LinkedList()
				: (collection instanceof List ? new ArrayList(initialCapacity)
						: (collection instanceof SortedSet ? new TreeSet(
								((SortedSet) collection).comparator())
								: new LinkedHashSet(initialCapacity))));
	}

	public static Collection createCollection(Class<?> collectionType,
			int initialCapacity) {
		if (collectionType.isInterface()) {
			if (List.class.equals(collectionType)) {
				return new ArrayList(initialCapacity);
			} else if (!SortedSet.class.equals(collectionType)
					&& !collectionType.equals(navigableSetClass)) {
				if (!Set.class.equals(collectionType)
						&& !Collection.class.equals(collectionType)) {
					throw new IllegalArgumentException(
							"Unsupported Collection interface: "
									+ collectionType.getName());
				} else {
					return new LinkedHashSet(initialCapacity);
				}
			} else {
				return new TreeSet();
			}
		} else if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("Unsupported Collection type: "
					+ collectionType.getName());
		} else {
			try {
				return (Collection) collectionType.newInstance();
			} catch (Exception arg2) {
				throw new IllegalArgumentException(
						"Could not instantiate Collection type: "
								+ collectionType.getName());
			}
		}
	}

	public static boolean isApproximableMapType(Class<?> mapType) {
		return mapType != null && approximableMapTypes.contains(mapType);
	}

	public static Map createApproximateMap(Object map, int initialCapacity) {
		return (Map) (map instanceof SortedMap ? new TreeMap(
				((SortedMap) map).comparator()) : new LinkedHashMap(
				initialCapacity));
	}

	public static Map createMap(Class<?> mapType, int initialCapacity) {
		if (mapType.isInterface()) {
			if (Map.class.equals(mapType)) {
				return new LinkedHashMap(initialCapacity);
			} else if (!SortedMap.class.equals(mapType)
					&& !mapType.equals(navigableMapClass)) {
				throw new IllegalArgumentException(
						"Unsupported Map interface: " + mapType.getName());
			} else {
				return new TreeMap();
			}
		} else if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException("Unsupported Map type: "
					+ mapType.getName());
		} else {
			try {
				return (Map) mapType.newInstance();
			} catch (Exception arg2) {
				throw new IllegalArgumentException(
						"Could not instantiate Map type: " + mapType.getName());
			}
		}
	}

	static {
		approximableCollectionTypes.add(Collection.class);
		approximableCollectionTypes.add(List.class);
		approximableCollectionTypes.add(Set.class);
		approximableCollectionTypes.add(SortedSet.class);
		approximableMapTypes.add(Map.class);
		approximableMapTypes.add(SortedMap.class);
		ClassLoader cl = CollectionFactory.class.getClassLoader();

		try {
			navigableSetClass = cl.loadClass("java.util.NavigableSet");
			navigableMapClass = cl.loadClass("java.util.NavigableMap");
			approximableCollectionTypes.add(navigableSetClass);
			approximableMapTypes.add(navigableMapClass);
		} catch (ClassNotFoundException arg1) {
			;
		}

		approximableCollectionTypes.add(ArrayList.class);
		approximableCollectionTypes.add(LinkedList.class);
		approximableCollectionTypes.add(HashSet.class);
		approximableCollectionTypes.add(LinkedHashSet.class);
		approximableCollectionTypes.add(TreeSet.class);
		approximableMapTypes.add(HashMap.class);
		approximableMapTypes.add(LinkedHashMap.class);
		approximableMapTypes.add(TreeMap.class);
	}

	@Deprecated
	private static class JdkConcurrentHashMap extends ConcurrentHashMap
			implements ConcurrentMap {
		private JdkConcurrentHashMap(int initialCapacity) {
			super(initialCapacity);
		}
	}
}
