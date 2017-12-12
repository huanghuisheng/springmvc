package org.springframework.core.env;

import java.util.Map;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

public class SystemEnvironmentPropertySource extends MapPropertySource {
	public SystemEnvironmentPropertySource(String name,
			Map<String, Object> source) {
		super(name, source);
	}

	public boolean containsProperty(String name) {
		return this.resolvePropertyName(name) != null;
	}

	public Object getProperty(String name) {
		Assert.notNull(name, "property name must not be null");
		String actualName = this.resolvePropertyName(name);
		if (actualName == null) {
			return null;
		} else {
			if (this.logger.isDebugEnabled() && !name.equals(actualName)) {
				this.logger
						.debug(String
								.format("PropertySource [%s] does not contain \'%s\', but found equivalent \'%s\'",
										new Object[]{this.getName(), name,
												actualName}));
			}

			return super.getProperty(actualName);
		}
	}

	private String resolvePropertyName(String name) {
		if (super.containsProperty(name)) {
			return name;
		} else {
			String usName = name.replace('.', '_');
			if (!name.equals(usName) && super.containsProperty(usName)) {
				return usName;
			} else {
				String ucName = name.toUpperCase();
				if (!name.equals(ucName)) {
					if (super.containsProperty(ucName)) {
						return ucName;
					}

					String usUcName = ucName.replace('.', '_');
					if (!ucName.equals(usUcName)
							&& super.containsProperty(usUcName)) {
						return usUcName;
					}
				}

				return null;
			}
		}
	}
}