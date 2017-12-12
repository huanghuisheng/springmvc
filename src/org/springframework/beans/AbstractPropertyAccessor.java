/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;

public abstract class AbstractPropertyAccessor
		extends
			PropertyEditorRegistrySupport
		implements
			ConfigurablePropertyAccessor {
	private boolean extractOldValueForEditor = false;

	public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
		this.extractOldValueForEditor = extractOldValueForEditor;
	}

	public boolean isExtractOldValueForEditor() {
		return this.extractOldValueForEditor;
	}

	public void setPropertyValue(PropertyValue pv) throws BeansException {
		this.setPropertyValue(pv.getName(), pv.getValue());
	}

	public void setPropertyValues(Map<?, ?> map) throws BeansException {
		this.setPropertyValues((PropertyValues) (new MutablePropertyValues(map)));
	}

	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		this.setPropertyValues(pvs, false, false);
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)
			throws BeansException {
		this.setPropertyValues(pvs, ignoreUnknown, false);
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown,
			boolean ignoreInvalid) throws BeansException {
		LinkedList propertyAccessExceptions = null;
		List propertyValues = pvs instanceof MutablePropertyValues
				? ((MutablePropertyValues) pvs).getPropertyValueList()
				: Arrays.asList(pvs.getPropertyValues());
		Iterator paeArray = propertyValues.iterator();

		while (paeArray.hasNext()) {
			PropertyValue pv = (PropertyValue) paeArray.next();

			try {
				this.setPropertyValue(pv);
			} catch (NotWritablePropertyException arg8) {
				if (!ignoreUnknown) {
					throw arg8;
				}
			} catch (NullValueInNestedPathException arg9) {
				if (!ignoreInvalid) {
					throw arg9;
				}
			} catch (PropertyAccessException arg10) {
				if (propertyAccessExceptions == null) {
					propertyAccessExceptions = new LinkedList();
				}

				propertyAccessExceptions.add(arg10);
			}
		}

		if (propertyAccessExceptions != null) {
			PropertyAccessException[] paeArray1 = (PropertyAccessException[]) propertyAccessExceptions
					.toArray(new PropertyAccessException[propertyAccessExceptions
							.size()]);
			throw new PropertyBatchUpdateException(paeArray1);
		}
	}

	public <T> T convertIfNecessary(Object value, Class<T> requiredType)
			throws TypeMismatchException {
		return this.convertIfNecessary(value, requiredType,
				(MethodParameter) null);
	}

	public Class getPropertyType(String propertyPath) {
		return null;
	}

	public abstract Object getPropertyValue(String arg0) throws BeansException;

	public abstract void setPropertyValue(String arg0, Object arg1)
			throws BeansException;
}