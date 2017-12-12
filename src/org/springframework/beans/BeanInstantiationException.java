package org.springframework.beans;

public class BeanInstantiationException extends FatalBeanException {

	private Class beanClass;


	/**
	 * Create a new BeanInstantiationException.
	 * @param beanClass the offending bean class
	 * @param msg the detail message
	 */
	public BeanInstantiationException(Class beanClass, String msg) {
		this(beanClass, msg, null);
	}

	/**
	 * Create a new BeanInstantiationException.
	 * @param beanClass the offending bean class
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeanInstantiationException(Class beanClass, String msg, Throwable cause) {
		super("Could not instantiate bean class [" + beanClass.getName() + "]: " + msg, cause);
		this.beanClass = beanClass;
	}

	/**
	 * Return the offending bean class.
	 */
	public Class getBeanClass() {
		return beanClass;
	}

}
