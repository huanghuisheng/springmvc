package org.springframework.beans.factory;

public interface HierarchicalBeanFactory extends BeanFactory {
	
	/**
	 * Return the parent bean factory, or <code>null</code> if there is none.
	 */
	BeanFactory getParentBeanFactory();

	/**
	 * Return whether the local bean factory contains a bean of the given name,
	 * ignoring beans defined in ancestor contexts.
	 * <p>This is an alternative to <code>containsBean</code>, ignoring a bean
	 * of the given name from an ancestor bean factory.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is defined in the local factory
	 * @see org.springframework.beans.factory.BeanFactory#containsBean
	 */
	boolean containsLocalBean(String name);

}