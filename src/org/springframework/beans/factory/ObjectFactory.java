package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

public interface ObjectFactory<T> {

	/**
	 * Return an instance (possibly shared or independent)
	 * of the object managed by this factory.
	 * @return an instance of the bean (should never be <code>null</code>)
	 * @throws BeansException in case of creation errors
	 */
	T getObject() throws BeansException;

}
