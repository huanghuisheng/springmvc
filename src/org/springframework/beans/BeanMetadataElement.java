package org.springframework.beans;

public interface BeanMetadataElement {

	/**
	 * Return the configuration source <code>Object</code> for this metadata element
	 * (may be <code>null</code>).
	 */
	Object getSource();

}