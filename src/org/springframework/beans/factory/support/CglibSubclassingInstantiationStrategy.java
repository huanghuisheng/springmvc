/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.beans.factory.support.MethodReplacer;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;

public class CglibSubclassingInstantiationStrategy
		extends
			SimpleInstantiationStrategy {
	private static final int PASSTHROUGH = 0;
	private static final int LOOKUP_OVERRIDE = 1;
	private static final int METHOD_REPLACER = 2;

	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName,
			BeanFactory owner) {
		return (new CglibSubclassingInstantiationStrategy.CglibSubclassCreator(
				beanDefinition, owner)).instantiate((Constructor) null,
				(Object[]) null);
	}

	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName,
			BeanFactory owner, Constructor ctor, Object[] args) {
		return (new CglibSubclassingInstantiationStrategy.CglibSubclassCreator(
				beanDefinition, owner)).instantiate(ctor, args);
	}

	private static class CglibSubclassCreator {
		private static final Log logger = LogFactory
				.getLog(CglibSubclassingInstantiationStrategy.CglibSubclassCreator.class);
		private final RootBeanDefinition beanDefinition;
		private final BeanFactory owner;

		public CglibSubclassCreator(RootBeanDefinition beanDefinition,
				BeanFactory owner) {
			this.beanDefinition = beanDefinition;
			this.owner = owner;
		}

		public Object instantiate(Constructor ctor, Object[] args) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(this.beanDefinition.getBeanClass());
			enhancer.setCallbackFilter(new CglibSubclassingInstantiationStrategy.CglibSubclassCreator.CallbackFilterImpl());
			enhancer.setCallbacks(new Callback[]{
					NoOp.INSTANCE,
					new CglibSubclassingInstantiationStrategy.CglibSubclassCreator.LookupOverrideMethodInterceptor(),
					new CglibSubclassingInstantiationStrategy.CglibSubclassCreator.ReplaceOverrideMethodInterceptor()});
			return ctor == null ? enhancer.create() : enhancer.create(
					ctor.getParameterTypes(), args);
		}

		private class CallbackFilterImpl
				extends
					CglibSubclassingInstantiationStrategy.CglibSubclassCreator.CglibIdentitySupport
				implements
					CallbackFilter {
			private CallbackFilterImpl() {
				super(
						(CglibSubclassingInstantiationStrategy.SyntheticClass_1) null);
			}

			public int accept(Method method) {
				MethodOverride methodOverride = CglibSubclassCreator.this.beanDefinition
						.getMethodOverrides().getOverride(method);
				if (CglibSubclassingInstantiationStrategy.CglibSubclassCreator.logger
						.isTraceEnabled()) {
					CglibSubclassingInstantiationStrategy.CglibSubclassCreator.logger
							.trace("Override for \'" + method.getName()
									+ "\' is [" + methodOverride + "]");
				}

				if (methodOverride == null) {
					return 0;
				} else if (methodOverride instanceof LookupOverride) {
					return 1;
				} else if (methodOverride instanceof ReplaceOverride) {
					return 2;
				} else {
					throw new UnsupportedOperationException(
							"Unexpected MethodOverride subclass: "
									+ methodOverride.getClass().getName());
				}
			}
		}

		private class ReplaceOverrideMethodInterceptor
				extends
					CglibSubclassingInstantiationStrategy.CglibSubclassCreator.CglibIdentitySupport
				implements
					MethodInterceptor {
			private ReplaceOverrideMethodInterceptor() {
				super(
						(CglibSubclassingInstantiationStrategy.SyntheticClass_1) null);
			}

			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy mp) throws Throwable {
				ReplaceOverride ro = (ReplaceOverride) CglibSubclassCreator.this.beanDefinition
						.getMethodOverrides().getOverride(method);
				MethodReplacer mr = (MethodReplacer) CglibSubclassCreator.this.owner
						.getBean(ro.getMethodReplacerBeanName());
				return mr.reimplement(obj, method, args);
			}
		}

		private class LookupOverrideMethodInterceptor
				extends
					CglibSubclassingInstantiationStrategy.CglibSubclassCreator.CglibIdentitySupport
				implements
					MethodInterceptor {
			private LookupOverrideMethodInterceptor() {
				super(
						(CglibSubclassingInstantiationStrategy.SyntheticClass_1) null);
			}

			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy mp) throws Throwable {
				LookupOverride lo = (LookupOverride) CglibSubclassCreator.this.beanDefinition
						.getMethodOverrides().getOverride(method);
				return CglibSubclassCreator.this.owner
						.getBean(lo.getBeanName());
			}
		}

		private class CglibIdentitySupport {
			private CglibIdentitySupport() {
			}

			protected RootBeanDefinition getBeanDefinition() {
				return CglibSubclassCreator.this.beanDefinition;
			}

			public boolean equals(Object other) {
				return other.getClass().equals(this.getClass())
						&& ((CglibSubclassingInstantiationStrategy.CglibSubclassCreator.CglibIdentitySupport) other)
								.getBeanDefinition()
								.equals(CglibSubclassCreator.this.beanDefinition);
			}

			public int hashCode() {
				return CglibSubclassCreator.this.beanDefinition.hashCode();
			}
		}
	}
}