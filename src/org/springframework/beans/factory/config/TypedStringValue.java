/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class TypedStringValue implements BeanMetadataElement {
	private String value;
	private volatile Object targetType;
	private Object source;
	private String specifiedTypeName;
	private volatile boolean dynamic;

	public TypedStringValue(String value) {
		this.setValue(value);
	}

	public TypedStringValue(String value, Class targetType) {
		this.setValue(value);
		this.setTargetType(targetType);
	}

	public TypedStringValue(String value, String targetTypeName) {
		this.setValue(value);
		this.setTargetTypeName(targetTypeName);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setTargetType(Class targetType) {
		Assert.notNull(targetType, "\'targetType\' must not be null");
		this.targetType = targetType;
	}

	public Class getTargetType() {
		Object targetTypeValue = this.targetType;
		if (!(targetTypeValue instanceof Class)) {
			throw new IllegalStateException(
					"Typed String value does not carry a resolved target type");
		} else {
			return (Class) targetTypeValue;
		}
	}

	public void setTargetTypeName(String targetTypeName) {
		Assert.notNull(targetTypeName, "\'targetTypeName\' must not be null");
		this.targetType = targetTypeName;
	}

	public String getTargetTypeName() {
		Object targetTypeValue = this.targetType;
		return targetTypeValue instanceof Class ? ((Class) targetTypeValue)
				.getName() : (String) targetTypeValue;
	}

	public boolean hasTargetType() {
		return this.targetType instanceof Class;
	}

	public Class resolveTargetType(ClassLoader classLoader)
			throws ClassNotFoundException {
		if (this.targetType == null) {
			return null;
		} else {
			Class resolvedClass = ClassUtils.forName(this.getTargetTypeName(),
					classLoader);
			this.targetType = resolvedClass;
			return resolvedClass;
		}
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return this.source;
	}

	public void setSpecifiedTypeName(String specifiedTypeName) {
		this.specifiedTypeName = specifiedTypeName;
	}

	public String getSpecifiedTypeName() {
		return this.specifiedTypeName;
	}

	public void setDynamic() {
		this.dynamic = true;
	}

	public boolean isDynamic() {
		return this.dynamic;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof TypedStringValue)) {
			return false;
		} else {
			TypedStringValue otherValue = (TypedStringValue) other;
			return ObjectUtils.nullSafeEquals(this.value, otherValue.value)
					&& ObjectUtils.nullSafeEquals(this.targetType,
							otherValue.targetType);
		}
	}

	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.value) * 29
				+ ObjectUtils.nullSafeHashCode(this.targetType);
	}

	public String toString() {
		return "TypedStringValue: value [" + this.value + "], target type ["
				+ this.targetType + "]";
	}
}
