package org.springframework.ui.context;

import org.springframework.context.MessageSource;

public interface Theme {

	/**
	 * Return the name of the theme.
	 * @return the name of the theme (never <code>null</code>)
	 */
	String getName();

	/**
	 * Return the specific MessageSource that resolves messages
	 * with respect to this theme.
	 * @return the theme-specific MessageSource (never <code>null</code>)
	 */
	MessageSource getMessageSource();

}