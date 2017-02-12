/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.tiefaces.exception.EvaluationException;

/**
 * expEngine use jexl engine evaluate an expression. see apache jexl project for
 * detail. use local thread for thread safe.
 * 
 * @author Jason Jiang
 *
 */
public class ExpressionEngine {
	/** expression. */
	private Expression jExpression;

	/**
	 * context. private JexlContext jContext;
	 */
	/** jexlEngine local map. */
	private static final ThreadLocal<JexlEngine> 
	JEXL_LOCAL = new ThreadLocal<JexlEngine>() {
		@Override
		protected JexlEngine initialValue() {
			return new JexlEngine();
		}
	};
	/** jexpmap local map. */
	private static final ThreadLocal<Map<String, Expression>> 
	JEXL_MAP_LOCAL = new ThreadLocal<Map<String, Expression>>() {
		@Override
		protected Map<String, Expression> initialValue() {
			return new HashMap<>();
		}
	};

	/**
	 * empty context used for evaluate single script.
	 */
	private static final Map<String, Object> 
	EMPTY_CONTEXT = new HashMap<String, Object>();

	/**
	 * constructor.
	 * 
	 * @param pExpression
	 *            expression.
	 */
	public ExpressionEngine(final String pExpression) {
		JexlEngine jexl = JEXL_LOCAL.get();
		jExpression = jexl.createExpression(pExpression);
	}



	/**
	 * constructor.
	 */
	public ExpressionEngine() {
		super();
	}

	/**
	 * Evaluate the expression.
	 *
	 * @param expression
	 *            string input expression.
	 * @return evaluated object.
	 */
	public final Object evaluate(final String expression) {
		return evaluate(expression, EMPTY_CONTEXT);
	}

	/**
	 * Evaluate the expression.
	 * 
	 * @param expression
	 *            string input expression.
	 * @param context
	 *            context map.
	 * @return evaluated object.
	 */
	public final Object evaluate(final String expression,
			final Map<String, Object> context) {
		JexlContext jexlContext = new MapContext(context);
		try {
			JexlEngine jexl = JEXL_LOCAL.get();
			Map<String, Expression> expMap = JEXL_MAP_LOCAL.get();
			Expression jexlExpression = expMap.get(expression);
			if (jexlExpression == null) {
				jexlExpression = jexl.createExpression(expression);
				expMap.put(expression, jexlExpression);
			}
			return jexlExpression.evaluate(jexlContext);
		} catch (Exception e) {
			throw new EvaluationException(
					"An error occurred when evaluating expression "
							+ expression);
		}
	}

	/**
	 * evaluate from giving context.
	 * 
	 * @param context
	 *            context.
	 * @return object evaluated.
	 */
	public final Object evaluate(final Map<String, Object> context) {
		JexlContext jexlContext = new MapContext(context);
		try {
			return jExpression.evaluate(jexlContext);
		} catch (Exception e) {
			throw new EvaluationException(
					"An error occurred when evaluating expression "
							+ jExpression.getExpression(), e);
		}
	}

	/**
	 * get jexpression.
	 * 
	 * @return jexlexpression.
	 */

	public final Expression getJexlExpression() {
		return jExpression;
	}

	/**
	 * get local exl engine.
	 * 
	 * @return jexlengine.
	 */
	public final JexlEngine getJexlEngine() {
		return JEXL_LOCAL.get();
	}

}
