package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.util.ClassUtils;

public class UnsatisfiedDependencyException extends BeanCreationException {

	/**
	 * Create a new UnsatisfiedDependencyException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param beanName the name of the bean requested
	 * @param propertyName the name of the bean property that couldn't be satisfied
	 * @param msg the detail message
	 */
	public UnsatisfiedDependencyException(
			String resourceDescription, String beanName, String propertyName, String msg) {

		super(resourceDescription, beanName,
				"Unsatisfied dependency expressed through bean property '" + propertyName + "'" +
				(msg != null ? ": " + msg : ""));
	}

	/**
	 * Create a new UnsatisfiedDependencyException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param beanName the name of the bean requested
	 * @param propertyName the name of the bean property that couldn't be satisfied
	 * @param ex the bean creation exception that indicated the unsatisfied dependency
	 */
	public UnsatisfiedDependencyException(
			String resourceDescription, String beanName, String propertyName, BeansException ex) {

		this(resourceDescription, beanName, propertyName, (ex != null ? ": " + ex.getMessage() : ""));
		initCause(ex);
	}

	/**
	 * Create a new UnsatisfiedDependencyException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param beanName the name of the bean requested
	 * @param ctorArgIndex the index of the constructor argument that couldn't be satisfied
	 * @param ctorArgType the type of the constructor argument that couldn't be satisfied
	 * @param msg the detail message
	 */
	public UnsatisfiedDependencyException(
			String resourceDescription, String beanName, int ctorArgIndex, Class ctorArgType, String msg) {

		super(resourceDescription, beanName,
				"Unsatisfied dependency expressed through constructor argument with index " +
				ctorArgIndex + " of type [" + ClassUtils.getQualifiedName(ctorArgType) + "]" +
				(msg != null ? ": " + msg : ""));
	}

	/**
	 * Create a new UnsatisfiedDependencyException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param beanName the name of the bean requested
	 * @param ctorArgIndex the index of the constructor argument that couldn't be satisfied
	 * @param ctorArgType the type of the constructor argument that couldn't be satisfied
	 * @param ex the bean creation exception that indicated the unsatisfied dependency
	 */
	public UnsatisfiedDependencyException(
			String resourceDescription, String beanName, int ctorArgIndex, Class ctorArgType, BeansException ex) {

		this(resourceDescription, beanName, ctorArgIndex, ctorArgType, (ex != null ? ": " + ex.getMessage() : ""));
		initCause(ex);
	}

}
