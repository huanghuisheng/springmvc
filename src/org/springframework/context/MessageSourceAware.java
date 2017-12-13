package org.springframework.context;

import org.springframework.beans.factory.Aware;

public interface MessageSourceAware extends Aware {

	/**
	 * Set the MessageSource that this object runs in.
	 * <p>Invoked after population of normal bean properties but before an init
	 * callback like InitializingBean's afterPropertiesSet or a custom init-method.
	 * Invoked before ApplicationContextAware's setApplicationContext.
	 * @param messageSource message sourceto be used by this object
	 */
	void setMessageSource(MessageSource messageSource);

}
