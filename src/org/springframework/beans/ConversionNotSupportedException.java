package org.springframework.beans;

import java.beans.PropertyChangeEvent;

public class ConversionNotSupportedException extends TypeMismatchException {

	/**
	 * Create a new ConversionNotSupportedException.
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param requiredType the required target type (or <code>null</code> if not known)
	 * @param cause the root cause (may be <code>null</code>)
	 */
	public ConversionNotSupportedException(PropertyChangeEvent propertyChangeEvent, Class requiredType, Throwable cause) {
		super(propertyChangeEvent, requiredType, cause);
	}

	/**
	 * Create a new ConversionNotSupportedException.
	 * @param value the offending value that couldn't be converted (may be <code>null</code>)
	 * @param requiredType the required target type (or <code>null</code> if not known)
	 * @param cause the root cause (may be <code>null</code>)
	 */
	public ConversionNotSupportedException(Object value, Class requiredType, Throwable cause) {
		super(value, requiredType, cause);
	}

}
