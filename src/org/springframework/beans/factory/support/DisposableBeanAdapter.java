/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans.factory.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class DisposableBeanAdapter implements DisposableBean, Runnable, Serializable {
	private static final Log logger = LogFactory
			.getLog(DisposableBeanAdapter.class);
	private final Object bean;
	private final String beanName;
	private final boolean invokeDisposableBean;
	private final boolean nonPublicAccessAllowed;
	private String destroyMethodName;
	private transient Method destroyMethod;
	private List<DestructionAwareBeanPostProcessor> beanPostProcessors;
	private final AccessControlContext acc;

	public DisposableBeanAdapter(Object bean, String beanName,
			RootBeanDefinition beanDefinition,
			List<BeanPostProcessor> postProcessors, AccessControlContext acc) {
		Assert.notNull(bean, "Disposable bean must not be null");
		this.bean = bean;
		this.beanName = beanName;
		this.invokeDisposableBean = this.bean instanceof DisposableBean
				&& !beanDefinition.isExternallyManagedDestroyMethod("destroy");
		this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
		this.acc = acc;
		this.inferDestroyMethodIfNecessary(beanDefinition);
		String destroyMethodName = beanDefinition.getDestroyMethodName();
		if (destroyMethodName != null
				&& (!this.invokeDisposableBean || !"destroy"
						.equals(destroyMethodName))
				&& !beanDefinition
						.isExternallyManagedDestroyMethod(destroyMethodName)) {
			this.destroyMethodName = destroyMethodName;
			this.destroyMethod = this.determineDestroyMethod();
			if (this.destroyMethod == null) {
				if (beanDefinition.isEnforceDestroyMethod()) {
					throw new BeanDefinitionValidationException(
							"Couldn\'t find a destroy method named \'"
									+ destroyMethodName
									+ "\' on bean with name \'" + beanName
									+ "\'");
				}
			} else {
				Class[] paramTypes = this.destroyMethod.getParameterTypes();
				if (paramTypes.length > 1) {
					throw new BeanDefinitionValidationException(
							"Method \'"
									+ destroyMethodName
									+ "\' of bean \'"
									+ beanName
									+ "\' has more than one parameter - not supported as destroy method");
				}

				if (paramTypes.length == 1
						&& !paramTypes[0].equals(Boolean.TYPE)) {
					throw new BeanDefinitionValidationException(
							"Method \'"
									+ destroyMethodName
									+ "\' of bean \'"
									+ beanName
									+ "\' has a non-boolean parameter - not supported as destroy method");
				}
			}
		}

		this.beanPostProcessors = this.filterPostProcessors(postProcessors);
	}

	private void inferDestroyMethodIfNecessary(RootBeanDefinition beanDefinition) {
		if ("(inferred)".equals(beanDefinition.getDestroyMethodName())) {
			try {
				Method ex = this.bean.getClass().getMethod("close",
						new Class[0]);
				if (Modifier.isPublic(ex.getModifiers())) {
					beanDefinition.setDestroyMethodName(ex.getName());
				}
			} catch (NoSuchMethodException arg2) {
				beanDefinition.setDestroyMethodName((String) null);
			}
		}

	}

	private DisposableBeanAdapter(Object bean, String beanName,
			boolean invokeDisposableBean, boolean nonPublicAccessAllowed,
			String destroyMethodName,
			List<DestructionAwareBeanPostProcessor> postProcessors) {
		this.bean = bean;
		this.beanName = beanName;
		this.invokeDisposableBean = invokeDisposableBean;
		this.nonPublicAccessAllowed = nonPublicAccessAllowed;
		this.destroyMethodName = destroyMethodName;
		this.beanPostProcessors = postProcessors;
		this.acc = null;
	}

	private List<DestructionAwareBeanPostProcessor> filterPostProcessors(
			List<BeanPostProcessor> postProcessors) {
		ArrayList filteredPostProcessors = null;
		if (postProcessors != null && !postProcessors.isEmpty()) {
			filteredPostProcessors = new ArrayList(postProcessors.size());
			Iterator i$ = postProcessors.iterator();

			while (i$.hasNext()) {
				BeanPostProcessor postProcessor = (BeanPostProcessor) i$.next();
				if (postProcessor instanceof DestructionAwareBeanPostProcessor) {
					filteredPostProcessors
							.add((DestructionAwareBeanPostProcessor) postProcessor);
				}
			}
		}

		return filteredPostProcessors;
	}

	public void run() {
		this.destroy();
	}

	public void destroy() {
		if (this.beanPostProcessors != null
				&& !this.beanPostProcessors.isEmpty()) {
			Iterator methodToCall = this.beanPostProcessors.iterator();

			while (methodToCall.hasNext()) {
				DestructionAwareBeanPostProcessor msg = (DestructionAwareBeanPostProcessor) methodToCall
						.next();
				msg.postProcessBeforeDestruction(this.bean, this.beanName);
			}
		}

		if (this.invokeDisposableBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking destroy() on bean with name \'"
						+ this.beanName + "\'");
			}

			try {
				if (System.getSecurityManager() != null) {
					AccessController.doPrivileged(
							new PrivilegedExceptionAction() {
								public Object run() throws Exception {
									((DisposableBean) DisposableBeanAdapter.this.bean)
											.destroy();
									return null;
								}
							}, this.acc);
				} else {
					((DisposableBean) this.bean).destroy();
				}
			} catch (Throwable arg2) {
				String msg1 = "Invocation of destroy method failed on bean with name \'"
						+ this.beanName + "\'";
				if (logger.isDebugEnabled()) {
					logger.warn(msg1, arg2);
				} else {
					logger.warn(msg1 + ": " + arg2);
				}
			}
		}

		if (this.destroyMethod != null) {
			this.invokeCustomDestroyMethod(this.destroyMethod);
		} else if (this.destroyMethodName != null) {
			Method methodToCall1 = this.determineDestroyMethod();
			if (methodToCall1 != null) {
				this.invokeCustomDestroyMethod(methodToCall1);
			}
		}

	}

	private Method determineDestroyMethod() {
		try {
			return System.getSecurityManager() != null
					? (Method) AccessController
							.doPrivileged(new PrivilegedAction() {
								public Method run() {
									return DisposableBeanAdapter.this
											.findDestroyMethod();
								}
							}) : this.findDestroyMethod();
		} catch (IllegalArgumentException arg1) {
			throw new BeanDefinitionValidationException(
					"Couldn\'t find a unique destroy method on bean with name \'"
							+ this.beanName + ": " + arg1.getMessage());
		}
	}

	private Method findDestroyMethod() {
		return this.nonPublicAccessAllowed ? BeanUtils
				.findMethodWithMinimalParameters(this.bean.getClass(),
						this.destroyMethodName) : BeanUtils
				.findMethodWithMinimalParameters(this.bean.getClass()
						.getMethods(), this.destroyMethodName);
	}

	private void invokeCustomDestroyMethod(final Method destroyMethod) {
		Class[] paramTypes = destroyMethod.getParameterTypes();
		final Object[] args = new Object[paramTypes.length];
		if (paramTypes.length == 1) {
			args[0] = Boolean.TRUE;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Invoking destroy method \'" + this.destroyMethodName
					+ "\' on bean with name \'" + this.beanName + "\'");
		}

		try {
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						ReflectionUtils.makeAccessible(destroyMethod);
						return null;
					}
				});

				try {
					AccessController.doPrivileged(
							new PrivilegedExceptionAction() {
								public Object run() throws Exception {
									destroyMethod.invoke(
											DisposableBeanAdapter.this.bean,
											args);
									return null;
								}
							}, this.acc);
				} catch (PrivilegedActionException arg5) {
					throw (InvocationTargetException) arg5.getException();
				}
			} else {
				ReflectionUtils.makeAccessible(destroyMethod);
				destroyMethod.invoke(this.bean, args);
			}
		} catch (InvocationTargetException arg6) {
			String msg = "Invocation of destroy method \'"
					+ this.destroyMethodName + "\' failed on bean with name \'"
					+ this.beanName + "\'";
			if (logger.isDebugEnabled()) {
				logger.warn(msg, arg6.getTargetException());
			} else {
				logger.warn(msg + ": " + arg6.getTargetException());
			}
		} catch (Throwable arg7) {
			logger.error("Couldn\'t invoke destroy method \'"
					+ this.destroyMethodName + "\' on bean with name \'"
					+ this.beanName + "\'", arg7);
		}

	}

	protected Object writeReplace() {
		ArrayList serializablePostProcessors = null;
		if (this.beanPostProcessors != null) {
			serializablePostProcessors = new ArrayList();
			Iterator i$ = this.beanPostProcessors.iterator();

			while (i$.hasNext()) {
				DestructionAwareBeanPostProcessor postProcessor = (DestructionAwareBeanPostProcessor) i$
						.next();
				if (postProcessor instanceof Serializable) {
					serializablePostProcessors.add(postProcessor);
				}
			}
		}

		return new DisposableBeanAdapter(this.bean, this.beanName,
				this.invokeDisposableBean, this.nonPublicAccessAllowed,
				this.destroyMethodName, serializablePostProcessors);
	}
}