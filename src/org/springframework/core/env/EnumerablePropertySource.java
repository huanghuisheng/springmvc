package org.springframework.core.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

public abstract class EnumerablePropertySource<T> extends PropertySource<T> {
	protected static final String[] EMPTY_NAMES_ARRAY = new String[0];
	protected final Log logger = LogFactory.getLog(this.getClass());

	public EnumerablePropertySource(String name, T source) {
		super(name, source);
	}

	public abstract String[] getPropertyNames();

	public boolean containsProperty(String name) {
		Assert.notNull(name, "property name must not be null");
		String[] arr$ = this.getPropertyNames();
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			String candidate = arr$[i$];
			if (candidate.equals(name)) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug(String.format(
							"PropertySource [%s] contains \'%s\'",
							new Object[]{this.getName(), name}));
				}

				return true;
			}
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace(String.format(
					"PropertySource [%s] does not contain \'%s\'",
					new Object[]{this.getName(), name}));
		}

		return false;
	}
}