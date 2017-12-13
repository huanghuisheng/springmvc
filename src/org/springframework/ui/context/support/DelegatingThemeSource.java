package org.springframework.ui.context.support;

import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

public class DelegatingThemeSource implements HierarchicalThemeSource {

	private ThemeSource parentThemeSource;


	public void setParentThemeSource(ThemeSource parentThemeSource) {
		this.parentThemeSource = parentThemeSource;
	}

	public ThemeSource getParentThemeSource() {
		return parentThemeSource;
	}


	public Theme getTheme(String themeName) {
		if (this.parentThemeSource != null) {
			return this.parentThemeSource.getTheme(themeName);
		}
		else {
			return null;
		}
	}

}