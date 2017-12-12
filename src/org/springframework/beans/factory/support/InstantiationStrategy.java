package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

public interface InstantiationStrategy {
	Object instantiate(RootBeanDefinition arg0, String arg1, BeanFactory arg2)
			throws BeansException;

	Object instantiate(RootBeanDefinition arg0, String arg1, BeanFactory arg2,
			Constructor<?> arg3, Object[] arg4) throws BeansException;

	Object instantiate(RootBeanDefinition arg0, String arg1, BeanFactory arg2,
			Object arg3, Method arg4, Object[] arg5) throws BeansException;
}
