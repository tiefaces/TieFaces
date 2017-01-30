/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.utility;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;

/**
 * Cell controls helper.
 * 
 * @author JASON JIANG.
 *
 */
public final class CellControlsUtility {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(CellControlsUtility.class.getName());

	/** list of supported components. */
	private static List<String> supportComponents = Arrays.asList(
			"Calendar", "SelectOneMenu", "InputNumber", "InputTextarea");

	/**
	 * array list for possible parameter's type.
	 */

	@SuppressWarnings("rawtypes")
	private static Class[] paraMatchArray = { String.class, boolean.class,
			Boolean.class, int.class, Integer.class, long.class, Long.class,
			float.class, Float.class, double.class, Double.class,
			byte.class, Byte.class, short.class, Short.class };

	/**
	 * hide constructor.
	 */
	private CellControlsUtility() {
		// not called
	}

	/**
	 * find component according it's class.
	 * 
	 * @param component
	 *            component.
	 * @return component name.
	 */
	private static String findComponentNameFromClass(
			final UIComponent component) {

		String cname = component.getClass().getSimpleName();
		if (supportComponents.contains(cname)) {
			return cname;
		}
		return null;

	}

	/**
	 * populate attributes.
	 * 
	 * @param component
	 *            component obj.
	 * @param fcell
	 *            facescell.
	 * @param defaultControlMap
	 *            default control map.
	 */
	public static void populateAttributes(final UIComponent component,
			final FacesCell fcell,
			final Map<String, Map<String, String>> defaultControlMap) {

		List<CellFormAttributes> inputAttrs = fcell.getInputAttrs();
		String cname = findComponentNameFromClass(component);
		if (cname == null) {
			return;
		}

		Map<String, String> defaultMap = defaultControlMap.get(cname);
		if (defaultMap == null) {
			defaultMap = new HashMap<>();
			defaultControlMap.put(cname, defaultMap);
		}
		for (Map.Entry<String, String> entry : defaultMap.entrySet()) {
			setObjectProperty(component, entry.getKey(), entry.getValue(),
					true);
		}
		for (CellFormAttributes attr : inputAttrs) {
			String propertyName = attr.getType();
			String propertyValue = attr.getValue();
			if (!defaultMap.containsKey(propertyName)) {
				String defaultValue = getObjectPropertyValue(component,
						propertyName, true);
				defaultMap.put(propertyName, defaultValue);
			}
			setObjectProperty(component, propertyName, propertyValue, true);
		}

	}

	/**
	 * match parameter of method.
	 * 
	 * @param obj
	 *            object.
	 * @param methodName
	 *            method name.
	 * @return index of paraMatchArray if mached. otherwise return -1.
	 */
	private static int matchParaMeterOfMethod(final Object obj,
			final String methodName) {

		for (int i = 0; i < paraMatchArray.length; i++) {
			try {
				obj.getClass().getMethod(methodName,
						new Class[] { paraMatchArray[i] });
				return i;

			} catch (Exception ex) {
				LOG.log(Level.FINE, " error in matchParaMeterOfMethod = "
						+ ex.getLocalizedMessage(), ex);
			}
		}
		return -1;
	}

	/**
	 * convert object.
	 * 
	 * @param clazz
	 *            object class.
	 * @param value
	 *            value.
	 * @return object according to class.
	 */
	@SuppressWarnings("rawtypes")
	static Object convertToObject(final Class clazz,
			final String value) {
		if (String.class == clazz) {
			return value;
		}
		if (Boolean.class == clazz || Boolean.TYPE == clazz) {
			return Boolean.parseBoolean(value);
		}
		if (Byte.class == clazz || Byte.TYPE == clazz) {
			return Byte.parseByte(value);
		}
		if (Short.class == clazz || Short.TYPE == clazz) {
			return Short.parseShort(value);
		}
		if (Integer.class == clazz || Integer.TYPE == clazz) {
			return Integer.parseInt(value);
		}
		if (Long.class == clazz || Long.TYPE == clazz) {
			return Long.parseLong(value);
		}
		if (Float.class == clazz || Float.TYPE == clazz) {
			return Float.parseFloat(value);
		}
		if (Double.class == clazz || Double.TYPE == clazz) {
			return Double.parseDouble(value);
		}
		return value;
	}

	/**
	 * set object property.
	 * 
	 * @param obj
	 *            object.
	 * @param propertyName
	 *            property name.
	 * @param propertyValue
	 *            value.
	 * @param ignoreNonExisting
	 *            true if ignore non exist property.
	 */

	public static void setObjectProperty(final Object obj,
			final String propertyName, final String propertyValue,
			final boolean ignoreNonExisting) {
		try {

			String methodName = "set"
					+ Character.toUpperCase(propertyName.charAt(0))
					+ propertyName.substring(1);
			int parameterType = matchParaMeterOfMethod(obj, methodName);
			if (parameterType > -1) {
				Method method = obj.getClass().getMethod(methodName,
						new Class[] { paraMatchArray[parameterType] });
				method.invoke(obj, convertToObject(
						paraMatchArray[parameterType], propertyValue));
			}
		} catch (Exception e) {
			String msg = "failed to set property '" + propertyName
					+ "' to value '" + propertyValue + "' for object "
					+ obj;
			if (ignoreNonExisting) {
				LOG.log(Level.FINE, msg, e);
			} else {
				LOG.warning(msg);
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * get object property value.
	 * 
	 * @param obj
	 *            object.
	 * @param propertyName
	 *            property name.
	 * @param ignoreNonExisting
	 *            true ignore no existing.
	 * @return perperty value.
	 */
	private static String getObjectPropertyValue(final Object obj,
			final String propertyName, final boolean ignoreNonExisting) {
		try {
			Method method = obj.getClass()
					.getMethod("get"
							+ Character.toUpperCase(propertyName.charAt(0))
							+ propertyName.substring(1));
			return (String) method.invoke(obj);
		} catch (Exception e) {
			String msg = "failed to get property '" + propertyName
					+ "' for object " + obj;
			if (ignoreNonExisting) {
				LOG.log(Level.FINE, msg, e);
			} else {
				LOG.warning(msg);
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

}
