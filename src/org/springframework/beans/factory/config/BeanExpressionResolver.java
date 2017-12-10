package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

public interface BeanExpressionResolver {

	/**
	 * Evaluate the given value as an expression, if applicable;
	 * return the value as-is otherwise.
	 * @param value the value to check
	 * @param evalContext the evaluation context
	 * @return the resolved value (potentially the given value as-is)
	 * @throws BeansException if evaluation failed
	 */
	Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException;

}