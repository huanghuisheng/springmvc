package org.springframework.beans;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;

public class SimpleTypeConverter extends PropertyEditorRegistrySupport
		implements TypeConverter {
	private final TypeConverterDelegate typeConverterDelegate = new TypeConverterDelegate(
			this);

	public SimpleTypeConverter() {
		this.registerDefaultEditors();
	}

	public <T> T convertIfNecessary(Object value, Class<T> requiredType)
			throws TypeMismatchException {
		return this.convertIfNecessary(value, requiredType,
				(MethodParameter) null);
	}

	public <T> T convertIfNecessary(Object value, Class<T> requiredType,
			MethodParameter methodParam) throws TypeMismatchException {
		try {
			return this.typeConverterDelegate.convertIfNecessary(value,
					requiredType, methodParam);
		} catch (ConverterNotFoundException arg4) {
			throw new ConversionNotSupportedException(value, requiredType, arg4);
		} catch (ConversionException arg5) {
			throw new TypeMismatchException(value, requiredType, arg5);
		} catch (IllegalStateException arg6) {
			throw new ConversionNotSupportedException(value, requiredType, arg6);
		} catch (IllegalArgumentException arg7) {
			throw new TypeMismatchException(value, requiredType, arg7);
		}
	}
}
