package org.springframework.context;

public interface Lifecycle {

	/**
	 * Start this component.
	 * Should not throw an exception if the component is already running.
	 * <p>In the case of a container, this will propagate the start signal
	 * to all components that apply.
	 */
	void start();

	/**
	 * Stop this component, typically in a synchronous fashion, such that
	 * the component is fully stopped upon return of this method. Consider
	 * implementing {@link SmartLifecycle} and its {@code stop(Runnable)}
	 * variant in cases where asynchronous stop behavior is necessary.
	 * <p>Should not throw an exception if the component isn't started yet.
	 * <p>In the case of a container, this will propagate the stop signal
	 * to all components that apply.
	 * @see SmartLifecycle#stop(Runnable)
	 */
	void stop();

	/**
	 * Check whether this component is currently running.
	 * <p>In the case of a container, this will return <code>true</code>
	 * only if <i>all</i> components that apply are currently running.
	 * @return whether the component is currently running
	 */
	boolean isRunning();

}
