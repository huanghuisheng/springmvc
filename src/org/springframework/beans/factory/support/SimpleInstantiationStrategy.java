/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.InstantiationStrategy;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class SimpleInstantiationStrategy implements InstantiationStrategy {
	private static final ThreadLocal<Method> currentlyInvokedFactoryMethod = new ThreadLocal();

	public Object instantiate(RootBeanDefinition beanDefinition,
			String beanName, BeanFactory owner) {
		if (beanDefinition.getMethodOverrides().isEmpty()) {
			Object arg4 = beanDefinition.constructorArgumentLock;
			Constructor constructorToUse;
			synchronized (beanDefinition.constructorArgumentLock) {
				constructorToUse = (Constructor) beanDefinition.resolvedConstructorOrFactoryMethod;
				if (constructorToUse == null) {
					final Class clazz = beanDefinition.getBeanClass();
					if (clazz.isInterface()) {
						throw new BeanInstantiationException(clazz,
								"Specified class is an interface");
					}

					try {
						if (System.getSecurityManager() != null) {
							constructorToUse = (Constructor) AccessController
									.doPrivileged(new PrivilegedExceptionAction() {
										public Constructor run()
												throws Exception {
											return clazz
													.getDeclaredConstructor((Class[]) null);
										}
									});
						} else {
							constructorToUse = clazz
									.getDeclaredConstructor((Class[]) null);
						}

						beanDefinition.resolvedConstructorOrFactoryMethod = constructorToUse;
					} catch (Exception arg8) {
						throw new BeanInstantiationException(clazz,
								"No default constructor found", arg8);
					}
				}
			}

			return BeanUtils.instantiateClass(constructorToUse, new Object[0]);
		} else {
			return this.instantiateWithMethodInjection(beanDefinition,
					beanName, owner);
		}
	}

	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName,
			BeanFactory owner) {
		throw new UnsupportedOperationException(
				"Method Injection not supported in SimpleInstantiationStrategy");
	}

	public Object instantiate(RootBeanDefinition beanDefinition,
			String beanName, BeanFactory owner, final Constructor<?> ctor,
			Object[] args) {
		if (beanDefinition.getMethodOverrides().isEmpty()) {
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						ReflectionUtils.makeAccessible(ctor);
						return null;
					}
				});
			}

			return BeanUtils.instantiateClass(ctor, args);
		} else {
			return this.instantiateWithMethodInjection(beanDefinition,
					beanName, owner, ctor, args);
		}
	}

	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName,
			BeanFactory owner, Constructor ctor, Object[] args) {
		throw new UnsupportedOperationException(
				"Method Injection not supported in SimpleInstantiationStrategy");
	}

	public Object instantiate(RootBeanDefinition beanDefinition,
			String beanName, BeanFactory owner, Object factoryBean,
			final Method factoryMethod, Object[] args) {
		try {
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						ReflectionUtils.makeAccessible(factoryMethod);
						return null;
					}
				});
			} else {
				ReflectionUtils.makeAccessible(factoryMethod);
			}

			Method ex = (Method) currentlyInvokedFactoryMethod.get();

			Object arg7;
			try {
				currentlyInvokedFactoryMethod.set(factoryMethod);
				arg7 = factoryMethod.invoke(factoryBean, args);
			} finally {
				if (ex != null) {
					currentlyInvokedFactoryMethod.set(ex);
				} else {
					currentlyInvokedFactoryMethod.remove();
				}

			}

			return arg7;
		} catch (IllegalArgumentException arg14) {
			throw new BeanDefinitionStoreException(
					"Illegal arguments to factory method [" + factoryMethod
							+ "]; " + "args: "
							+ StringUtils.arrayToCommaDelimitedString(args));
		} catch (IllegalAccessException arg15) {
			throw new BeanDefinitionStoreException(
					"Cannot access factory method [" + factoryMethod
							+ "]; is it public?");
		} catch (InvocationTargetException arg16) {
			throw new BeanDefinitionStoreException("Factory method ["
					+ factoryMethod + "] threw exception",
					arg16.getTargetException());
		}
	}

	public static Method getCurrentlyInvokedFactoryMethod() {
		return (Method) currentlyInvokedFactoryMethod.get();
	}
}