/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PropertyBatchUpdateException extends BeansException {
	private PropertyAccessException[] propertyAccessExceptions;

	public PropertyBatchUpdateException(
			PropertyAccessException[] propertyAccessExceptions) {
		super((String) null);
		Assert.notEmpty(propertyAccessExceptions,
				"At least 1 PropertyAccessException required");
		this.propertyAccessExceptions = propertyAccessExceptions;
	}

	public final int getExceptionCount() {
		return this.propertyAccessExceptions.length;
	}

	public final PropertyAccessException[] getPropertyAccessExceptions() {
		return this.propertyAccessExceptions;
	}

	public PropertyAccessException getPropertyAccessException(
			String propertyName) {
		PropertyAccessException[] arr$ = this.propertyAccessExceptions;
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			PropertyAccessException pae = arr$[i$];
			if (ObjectUtils.nullSafeEquals(propertyName, pae.getPropertyName())) {
				return pae;
			}
		}

		return null;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder("Failed properties: ");

		for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
			sb.append(this.propertyAccessExceptions[i].getMessage());
			if (i < this.propertyAccessExceptions.length - 1) {
				sb.append("; ");
			}
		}

		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()).append(
				"; nested PropertyAccessExceptions (");
		sb.append(this.getExceptionCount()).append(") are:");

		for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
			sb.append('\n').append("PropertyAccessException ").append(i + 1)
					.append(": ");
			sb.append(this.propertyAccessExceptions[i]);
		}

		return sb.toString();
	}

	public void printStackTrace(PrintStream ps) {
		synchronized (ps) {
			ps.println(this.getClass().getName()
					+ "; nested PropertyAccessException details ("
					+ this.getExceptionCount() + ") are:");

			for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
				ps.println("PropertyAccessException " + (i + 1) + ":");
				this.propertyAccessExceptions[i].printStackTrace(ps);
			}

		}
	}

	public void printStackTrace(PrintWriter pw) {
		synchronized (pw) {
			pw.println(this.getClass().getName()
					+ "; nested PropertyAccessException details ("
					+ this.getExceptionCount() + ") are:");

			for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
				pw.println("PropertyAccessException " + (i + 1) + ":");
				this.propertyAccessExceptions[i].printStackTrace(pw);
			}

		}
	}

	public boolean contains(Class exType) {
		if (exType == null) {
			return false;
		} else if (exType.isInstance(this)) {
			return true;
		} else {
			PropertyAccessException[] arr$ = this.propertyAccessExceptions;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				PropertyAccessException pae = arr$[i$];
				if (pae.contains(exType)) {
					return true;
				}
			}

			return false;
		}
	}
}