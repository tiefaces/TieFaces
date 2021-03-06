/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.common;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.faces.context.FacesContext;

/**
 * FacesContext related common utility classes.
 * 
 * @author Jason Jiang
 *
 */
public final class FacesUtility {

	/**
	 * hide constructor.
	 */
	private FacesUtility() {
		// not called
	}

	/**
	 * return faces context resource paths.
	 * 
	 * @param path
	 *            path.
	 * @return a set of path.
	 */
	public static Set<String> getResourcePaths(final String path) {
		return getResourcePaths(getContext(), path);
	}

	/**
	 * return faces context resource path.
	 * 
	 * @param context
	 *            face context.
	 * @param path
	 *            path.
	 * @return a set of path.
	 */
	public static Set<String> getResourcePaths(final FacesContext context,
			final String path) {
		return context.getExternalContext().getResourcePaths(path);
	}

	/**
	 * get resource file as stream.
	 * 
	 * @param path
	 *            resource file path.
	 * @return stream.
	 */
	public static InputStream getResourceAsStream(final String path) {
		return getResourceAsStream(getContext(), path);
	}

	/**
	 * get resource file as stream.
	 * 
	 * @param context
	 *            faces context.
	 * @param path
	 *            resource file path.
	 * @return stream.
	 */
	public static InputStream getResourceAsStream(
			final FacesContext context, final String path) {
		return context.getExternalContext().getResourceAsStream(path);
	}

	/**
	 * get metadata attributes.
	 * 
	 * @return map of metadata attributes.
	 */
	public static Map<String, Object> getMetadataAttributes() {
		return getMetadataAttributes(getContext());
	}

	/**
	 * get metadata attributes.
	 * 
	 * @param context
	 *            faces context.
	 * @return map of metadata attributes.
	 */
	public static Map<String, Object> getMetadataAttributes(
			final FacesContext context) {
		return context.getViewRoot().getAttributes();
	}

	/**
	 * evaluate expression.
	 * 
	 * @param <T>
	 *            This is the type parameter.
	 * @param expression
	 *            expression.
	 * @return evaluated object.
	 */
	public static <T> T evaluateExpressionGet(final String expression) {
		return evaluateExpressionGet(getContext(), expression);
	}

	/**
	 * evaluate expression.
	 * 
	 * @param <T>
	 *            This is the type parameter
	 * @param context
	 *            faces context.
	 * @param expression
	 *            expression.
	 * @return evaluated object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T evaluateExpressionGet(final FacesContext context,
			final String expression) {
		if (expression == null) {
			return null;
		}

		return (T) context.getApplication().evaluateExpressionGet(context,
				expression, Object.class);
	}

	/**
	 * return faces context.
	 * 
	 * @return faces context.
	 */
	public static FacesContext getContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * remove prefix path of the full path.
	 * 
	 * @param prefix
	 *            prefix string.
	 * @param resource
	 *            normal resource.
	 * @return removed path.
	 */
	public static String removePrefixPath(final String prefix,
			final String resource) {
		String normalizedResource = resource;
		if (normalizedResource.startsWith(prefix)) {
			normalizedResource = normalizedResource
					.substring(prefix.length() - 1);
		}

		return normalizedResource;
	}

	/**
	 * evaluate input type.
	 * 
	 * @param input
	 *            input string.
	 * @param type
	 *            input type.
	 * @return true if input meet type, otherwise false.
	 */
	public static boolean evalInputType(final String input,
			final String type) {

		Scanner scanner = new Scanner(input);
		boolean ireturn = false;
		if ("Integer".equalsIgnoreCase(type)) {
			ireturn = scanner.hasNextInt();
		} else if ("Double".equalsIgnoreCase(type)) {
			ireturn = scanner.hasNextDouble();
		} else if ("Boolean".equalsIgnoreCase(type)) {
			ireturn = scanner.hasNextBoolean();
		} else if ("Byte".equalsIgnoreCase(type)) {
			ireturn = scanner.hasNextByte();
		} else if (type.toLowerCase().startsWith("text")) {
			ireturn = true;
		}
		scanner.close();
		return ireturn;
	}

	/**
	 * Find bean in context.
	 * 
	 * @param <T>
	 *            This is the type parameter.
	 * @param beanName
	 *            bean name.
	 * @return bean object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findBean(final String beanName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return (T) context.getApplication().evaluateExpressionGet(context,
				TieConstants.EL_START + beanName + TieConstants.EL_END,
				Object.class);
	}

	/**
	 * evaluate expression.
	 * 
	 * @param <T>
	 *            This is the type parameter.
	 * @param expression
	 *            expression.
	 * @param expected
	 *            exprected type.
	 * @return evaluated object.
	 */
	public static <T> T evaluateExpression(final String expression,
			final Class<? extends T> expected) {
		return evaluateExpression(FacesContext.getCurrentInstance(),
				expression, expected);
	}

	/**
	 * evaluate expression.
	 * 
	 * @param <T>
	 *            This is the type parameter.
	 * @param context
	 *            faces context.
	 * @param expression
	 *            expression.
	 * @param expected
	 *            expected type.
	 * @return evaluated object.
	 */

	public static <T> T evaluateExpression(final FacesContext context,
			final String expression, final Class<? extends T> expected) {
		return context.getApplication().evaluateExpressionGet(context,
				expression, expected);
	}

	/**
	 * join string.
	 * 
	 * @param aArr
	 *            attribute list.
	 * @param sSep
	 *            seperator string.
	 * @return joined string.
	 */
	public static String strJoin(final short[] aArr, final String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0) {
				sbStr.append(sSep);
			}
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}
	/**
	 * join string
	 * @param aArr attr list.
	 * @param sSep seperator string.
	 * @return joined string.
	 */
	public static String strJoin(final String[] aArr, final String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0) {
				sbStr.append(sSep);
			}
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}

	/**
	 * round number according to decimal places.
	 * 
	 * @param value
	 *            double value.
	 * @param places
	 *            decimal places.
	 * @return rounded number.
	 */
	public static double round(final double value, final int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
