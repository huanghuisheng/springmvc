package org.springframework.ui.context;

public interface ThemeSource {

	/**
	 * Return the Theme instance for the given theme name.
	 * <p>The returned Theme will resolve theme-specific messages, codes,
	 * file paths, etc (e.g. CSS and image files in a web environment).
	 * @param themeName the name of the theme
	 * @return the corresponding Theme, or <code>null</code> if none defined.
	 * Note that, by convention, a ThemeSource should at least be able to
	 * return a default Theme for the default theme name "theme" but may also
	 * return default Themes for other theme names.
	 * @see org.springframework.web.servlet.theme.AbstractThemeResolver#ORIGINAL_DEFAULT_THEME_NAME
	 */
	Theme getTheme(String themeName);

}