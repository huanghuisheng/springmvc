package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class BeanInitializationException extends FatalBeanException {

	/**
	 * Create a new BeanInitializationException with the specified message.
	 * @param msg the detail message
	 */
	public BeanInitializationException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeanInitializationException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeanInitializationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}