package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;

public abstract class AbstractDelegatingCallable implements Callable<Object> {

	private Callable<Object> next;

	public void setNextCallable(Callable<Object> nextCallable) {
		this.next = nextCallable;
	}

	protected Callable<Object> getNextCallable() {
		return this.next;
	}

}
