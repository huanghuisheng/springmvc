/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans.factory.support;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.util.Assert;

public class RootBeanDefinition extends AbstractBeanDefinition {
	private final Set<Member> externallyManagedConfigMembers;
	private final Set<String> externallyManagedInitMethods;
	private final Set<String> externallyManagedDestroyMethods;
	private BeanDefinitionHolder decoratedDefinition;
	boolean isFactoryMethodUnique;
	Object resolvedConstructorOrFactoryMethod;
	boolean constructorArgumentsResolved;
	Object[] resolvedConstructorArguments;
	Object[] preparedConstructorArguments;
	final Object constructorArgumentLock;
	volatile Boolean beforeInstantiationResolved;
	boolean postProcessed;
	final Object postProcessingLock;

	public RootBeanDefinition() {
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
	}

	public RootBeanDefinition(Class beanClass) {
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
	}

	@Deprecated
	public RootBeanDefinition(Class beanClass, boolean singleton) {
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
		this.setSingleton(singleton);
	}

	@Deprecated
	public RootBeanDefinition(Class beanClass, int autowireMode) {
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
		this.setAutowireMode(autowireMode);
	}

	public RootBeanDefinition(Class beanClass, int autowireMode,
			boolean dependencyCheck) {
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
		this.setAutowireMode(autowireMode);
		if (dependencyCheck && this.getResolvedAutowireMode() != 3) {
			this.setDependencyCheck(1);
		}

	}

	@Deprecated
	public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs) {
		super((ConstructorArgumentValues) null, pvs);
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
	}

	@Deprecated
	public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs,
			boolean singleton) {
		super((ConstructorArgumentValues) null, pvs);
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
		this.setSingleton(singleton);
	}

	public RootBeanDefinition(Class beanClass, ConstructorArgumentValues cargs,
			MutablePropertyValues pvs) {
		super(cargs, pvs);
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClass(beanClass);
	}

	public RootBeanDefinition(String beanClassName) {
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClassName(beanClassName);
	}

	public RootBeanDefinition(String beanClassName,
			ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		this.setBeanClassName(beanClassName);
	}

	public RootBeanDefinition(RootBeanDefinition original) {
		this((BeanDefinition) original);
	}

	RootBeanDefinition(BeanDefinition original) {
		super(original);
		this.externallyManagedConfigMembers = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedInitMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.externallyManagedDestroyMethods = Collections
				.synchronizedSet(new HashSet(0));
		this.constructorArgumentsResolved = false;
		this.constructorArgumentLock = new Object();
		this.postProcessed = false;
		this.postProcessingLock = new Object();
		if (original instanceof RootBeanDefinition) {
			RootBeanDefinition originalRbd = (RootBeanDefinition) original;
			this.decoratedDefinition = originalRbd.decoratedDefinition;
			this.isFactoryMethodUnique = originalRbd.isFactoryMethodUnique;
		}

	}

	public String getParentName() {
		return null;
	}

	public void setParentName(String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException(
					"Root bean cannot be changed into a child bean with parent reference");
		}
	}

	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		this.setFactoryMethodName(name);
		this.isFactoryMethodUnique = true;
	}

	public boolean isFactoryMethod(Method candidate) {
		return candidate != null
				&& candidate.getName().equals(this.getFactoryMethodName());
	}

	public Method getResolvedFactoryMethod() {
		Object arg0 = this.constructorArgumentLock;
		synchronized (this.constructorArgumentLock) {
			Object candidate = this.resolvedConstructorOrFactoryMethod;
			return candidate instanceof Method ? (Method) candidate : null;
		}
	}

	public void registerExternallyManagedConfigMember(Member configMember) {
		this.externallyManagedConfigMembers.add(configMember);
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		return this.externallyManagedConfigMembers.contains(configMember);
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		this.externallyManagedInitMethods.add(initMethod);
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		return this.externallyManagedInitMethods.contains(initMethod);
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		this.externallyManagedDestroyMethods.add(destroyMethod);
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		return this.externallyManagedDestroyMethods.contains(destroyMethod);
	}

	public void setDecoratedDefinition(BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	public BeanDefinitionHolder getDecoratedDefinition() {
		return this.decoratedDefinition;
	}

	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	public boolean equals(Object other) {
		return this == other || other instanceof RootBeanDefinition
				&& super.equals(other);
	}

	public String toString() {
		return "Root bean: " + super.toString();
	}
}