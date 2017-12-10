package org.springframework.context;

public interface ApplicationEventPublisher {

	/**
	 * Notify all listeners registered with this application of an application
	 * event. Events may be framework events (such as RequestHandledEvent)
	 * or application-specific events.
	 * @param event the event to publish
	 * @see org.springframework.web.context.RequestHandledEvent
	 */
	void publishEvent(ApplicationEvent event);

}
