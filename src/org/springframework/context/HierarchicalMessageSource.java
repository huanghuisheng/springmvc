package org.springframework.context;

public interface HierarchicalMessageSource extends MessageSource {
	
	/** 
	 * Set the parent that will be used to try to resolve messages
	 * that this object can't resolve.
	 * @param parent the parent MessageSource that will be used to
	 * resolve messages that this object can't resolve.
	 * May be <code>null</code>, in which case no further resolution is possible.
	 */
	void setParentMessageSource(MessageSource parent);

	/**
	 * Return the parent of this MessageSource, or <code>null</code> if none.
	 */
	MessageSource getParentMessageSource();

}