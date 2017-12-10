package org.springframework.beans;

public interface PropertyEditorRegistrar {
	
	/**
	 * Register custom {@link java.beans.PropertyEditor PropertyEditors} with
	 * the given <code>PropertyEditorRegistry</code>.
	 * <p>The passed-in registry will usually be a {@link BeanWrapper} or a
	 * {@link org.springframework.validation.DataBinder DataBinder}.
	 * <p>It is expected that implementations will create brand new
	 * <code>PropertyEditors</code> instances for each invocation of this
	 * method (since <code>PropertyEditors</code> are not threadsafe).
	 * @param registry the <code>PropertyEditorRegistry</code> to register the
	 * custom <code>PropertyEditors</code> with
	 */
	void registerCustomEditors(PropertyEditorRegistry registry);

}