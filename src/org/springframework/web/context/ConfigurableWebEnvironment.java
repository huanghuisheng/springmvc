package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.core.env.ConfigurableEnvironment;

public interface ConfigurableWebEnvironment extends ConfigurableEnvironment {

	/**
	 * Replace any {@linkplain
	 * org.springframework.core.env.PropertySource.StubPropertySource stub property source}
	 * instances acting as placeholders with real servlet context/config property sources
	 * using the given parameters.
	 * @param servletContext the {@link ServletContext} (may not be {@code null})
	 * @param servletConfig the {@link ServletContext} ({@code null} if not available)
	 */
	void initPropertySources(ServletContext servletContext, ServletConfig servletConfig);

}
