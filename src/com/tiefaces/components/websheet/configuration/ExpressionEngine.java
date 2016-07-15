package com.tiefaces.components.websheet.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import com.tiefaces.exception.EvaluationException;

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
	/** context. */
	private JexlContext jContext;
	/** jexlEngine local map. */
	private static final ThreadLocal<JexlEngine> jexlLocal = new ThreadLocal<JexlEngine>() {
		@Override
		protected JexlEngine initialValue() {
			return new JexlEngine();
		}
	};
	/** jexpmap local map. */
	private static final ThreadLocal<Map<String, Expression>> jexpMapLocal = new ThreadLocal<Map<String, Expression>>() {
		@Override
		protected Map<String, Expression> initialValue() {
			return new HashMap<>();
		}
	};

	/**
	 * constructor.
	 * 
	 * @param pExpression
	 *            expression.
	 */
	public ExpressionEngine(final String pExpression) {
		JexlEngine jexl = jexlLocal.get();
		jExpression = jexl.createExpression(pExpression);
	}

	/**
	 * constructor.
	 * 
	 * @param context
	 *            context map.
	 */
	public ExpressionEngine(final Map<String, Object> context) {
		jContext = new MapContext(context);
	}

	/**
	 * constructor.
	 * 
	 * @param jexlContext
	 *            jcontext.
	 */
	public ExpressionEngine(final JexlContext jexlContext) {
		jContext = jexlContext;
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
			JexlEngine jexl = jexlLocal.get();
			Map<String, Expression> expMap = jexpMapLocal.get();
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
	 * @param context context.
	 * @return object evaluated.
	 */
	public final Object evaluate(final Map<String, Object> context) {
		jContext = new MapContext(context);
		try {
			return jExpression.evaluate(jContext);
		} catch (Exception e) {
			throw new EvaluationException(
					"An error occurred when evaluating expression "
							+ jExpression.getExpression(), e);
		}
	}

	public final Expression getJexlExpression() {
		return jExpression;
	}

	public final JexlEngine getJexlEngine() {
		return jexlLocal.get();
	}

}
