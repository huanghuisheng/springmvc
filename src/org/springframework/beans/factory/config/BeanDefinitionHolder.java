/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class BeanDefinitionHolder implements BeanMetadataElement {
	private final BeanDefinition beanDefinition;
	private final String beanName;
	private final String[] aliases;

	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
		this(beanDefinition, beanName, (String[]) null);
	}

	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName,
			String[] aliases) {
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		Assert.notNull(beanName, "Bean name must not be null");
		this.beanDefinition = beanDefinition;
		this.beanName = beanName;
		this.aliases = aliases;
	}

	public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
		Assert.notNull(beanDefinitionHolder,
				"BeanDefinitionHolder must not be null");
		this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
		this.beanName = beanDefinitionHolder.getBeanName();
		this.aliases = beanDefinitionHolder.getAliases();
	}

	public BeanDefinition getBeanDefinition() {
		return this.beanDefinition;
	}

	public String getBeanName() {
		return this.beanName;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public Object getSource() {
		return this.beanDefinition.getSource();
	}

	public boolean matchesName(String candidateName) {
		return candidateName != null
				&& (candidateName.equals(this.beanName) || ObjectUtils
						.containsElement(this.aliases, candidateName));
	}

	public String getShortDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bean definition with name \'").append(this.beanName)
				.append("\'");
		if (this.aliases != null) {
			sb.append(" and aliases [")
					.append(StringUtils
							.arrayToCommaDelimitedString(this.aliases))
					.append("]");
		}

		return sb.toString();
	}

	public String getLongDescription() {
		StringBuilder sb = new StringBuilder(this.getShortDescription());
		sb.append(": ").append(this.beanDefinition);
		return sb.toString();
	}

	public String toString() {
		return this.getLongDescription();
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof BeanDefinitionHolder)) {
			return false;
		} else {
			BeanDefinitionHolder otherHolder = (BeanDefinitionHolder) other;
			return this.beanDefinition.equals(otherHolder.beanDefinition)
					&& this.beanName.equals(otherHolder.beanName)
					&& ObjectUtils.nullSafeEquals(this.aliases,
							otherHolder.aliases);
		}
	}

	public int hashCode() {
		int hashCode = this.beanDefinition.hashCode();
		hashCode = 29 * hashCode + this.beanName.hashCode();
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.aliases);
		return hashCode;
	}
}