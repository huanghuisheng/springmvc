package org.springframework.core.env;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.util.Assert;

abstract class ReadOnlySystemAttributesMap implements Map<String, String> {
	public boolean containsKey(Object key) {
		return this.get(key) != null;
	}

	public String get(Object key) {
		Assert.isInstanceOf(String.class, key, String.format(
				"expected key [%s] to be of type String, got %s", new Object[]{
						key, key.getClass().getName()}));
		return this.getSystemAttribute((String) key);
	}

	public boolean isEmpty() {
		return false;
	}

	protected abstract String getSystemAttribute(String arg0);

	public int size() {
		throw new UnsupportedOperationException();
	}

	public String put(String key, String value) {
		throw new UnsupportedOperationException();
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		throw new UnsupportedOperationException();
	}

	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

	public Set<Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}
}
