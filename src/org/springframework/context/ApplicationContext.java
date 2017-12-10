package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

/**
* Return the unique id of this application context.
* @return the unique id of the context, or <code>null</code> if none
*/
String getId();

/**
* Return a friendly name for this context.
* @return a display name for this context (never <code>null</code>)
*/
String getDisplayName();

/**
* Return the timestamp when this context was first loaded.
* @return the timestamp (ms) when this context was first loaded
*/
long getStartupDate();

/**
* Return the parent context, or <code>null</code> if there is no parent
* and this is the root of the context hierarchy.
* @return the parent context, or <code>null</code> if there is no parent
*/
ApplicationContext getParent();

/**
* Expose AutowireCapableBeanFactory functionality for this context.
* <p>This is not typically used by application code, except for the purpose
* of initializing bean instances that live outside the application context,
* applying the Spring bean lifecycle (fully or partly) to them.
* <p>Alternatively, the internal BeanFactory exposed by the
* {@link ConfigurableApplicationContext} interface offers access to the
* AutowireCapableBeanFactory interface too. The present method mainly
* serves as convenient, specific facility on the ApplicationContext
* interface itself.
* @return the AutowireCapableBeanFactory for this context
* @throws IllegalStateException if the context does not support
* the AutowireCapableBeanFactory interface or does not hold an autowire-capable
* bean factory yet (usually if <code>refresh()</code> has never been called)
* @see ConfigurableApplicationContext#refresh()
* @see ConfigurableApplicationContext#getBeanFactory()
*/
AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
