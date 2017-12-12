package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class FactoryBeanNotInitializedException extends FatalBeanException {

	/**
	 * Create a new FactoryBeanNotInitializedException with the default message.
	 */
	public FactoryBeanNotInitializedException() {
		super("FactoryBean is not fully initialized yet");
	}

	/**
	 * Create a new FactoryBeanNotInitializedException with the given message.
	 * @param msg the detail message
	 */
	public FactoryBeanNotInitializedException(String msg) {
		super(msg);
	}

}
