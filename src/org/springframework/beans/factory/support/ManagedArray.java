package org.springframework.beans.factory.support;

import org.springframework.util.Assert;

public class ManagedArray extends ManagedList<Object> {

	/** Resolved element type for runtime creation of the target array */
	volatile Class resolvedElementType;


	/**
	 * Create a new managed array placeholder.
	 * @param elementTypeName the target element type as a class name
	 * @param size the size of the array
	 */
	public ManagedArray(String elementTypeName, int size) {
		super(size);
		Assert.notNull(elementTypeName, "elementTypeName must not be null");
		setElementTypeName(elementTypeName);
	}

}

