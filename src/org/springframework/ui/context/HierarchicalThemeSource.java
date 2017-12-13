package org.springframework.ui.context;

public interface HierarchicalThemeSource extends ThemeSource {
	/**
	 * Set the parent that will be used to try to resolve theme messages
	 * that this object can't resolve.
	 * @param parent the parent ThemeSource that will be used to
	 * resolve messages that this object can't resolve.
	 * May be <code>null</code>, in which case no further resolution is possible.
	 */
	void setParentThemeSource(ThemeSource parent);

	/**
	 * Return the parent of this ThemeSource, or <code>null</code> if none.
	 */
	ThemeSource getParentThemeSource();

}
