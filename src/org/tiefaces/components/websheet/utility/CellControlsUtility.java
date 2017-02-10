/*
 * Copyright 2017 TieFaces.
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

	public enum AttributesType {
		STRING(String.class) {
			@Override
			public Object parseValue(String value) {
				return value;
			}
		},
		BOOLEAN(Boolean.class) {
			@Override
			public Object parseValue(String value) {
				return Boolean.parseBoolean(value);
			}
		},
		BOOLEANTYPE(boolean.class) {
			@Override
			public Object parseValue(String value) {
				return Boolean.parseBoolean(value);
			}
		},
		INTEGER(Integer.class) {
			@Override
			public Object parseValue(String value) {
				return Integer.parseInt(value);
			}
		},
		INTEGERTYPE(int.class) {
			@Override
			public Object parseValue(String value) {
				return Integer.parseInt(value);
			}
		},
		LONG(Long.class) {
			@Override
			public Object parseValue(String value) {
				return Long.parseLong(value);
			}
		},
		LONGTYPE(long.class) {
			@Override
			public Object parseValue(String value) {
				return Long.parseLong(value);
			}
		},
		FLOAT(Float.class) {
			@Override
			public Object parseValue(String value) {
				return Float.parseFloat(value);
			}
		},
		FLOATTYPE(float.class) {
			@Override
			public Object parseValue(String value) {
				return Float.parseFloat(value);
			}
		},
		DOUBLE(Double.class) {
			@Override
			public Object parseValue(String value) {
				return Double.parseDouble(value);
			}
		},
		DOUBLETYPE(double.class) {
			@Override
			public Object parseValue(String value) {
				return Double.parseDouble(value);
			}
		},
		BYTE(Byte.class) {
			@Override
			public Object parseValue(String value) {
				return Byte.parseByte(value);
			}
		},
		BYTETYPE(byte.class) {
			@Override
			public Object parseValue(String value) {
				return Byte.parseByte(value);
			}
		},
		SHORT(Short.class) {
			@Override
			public Object parseValue(String value) {
				return Short.parseShort(value);
			}
		},
		SHORTTYPE(short.class) {
			@Override
			public Object parseValue(String value) {
				return Short.parseShort(value);
			}
		};

		private final Class clazz; // class name

		AttributesType(Class pclazz) {
			this.clazz = pclazz;
		}

		public abstract Object parseValue(String value);
	}

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
	private static AttributesType matchParaMeterOfMethod(final Object obj,
			final String methodName) {

		for (AttributesType attr : AttributesType.values()) {
			try {
				obj.getClass().getMethod(methodName,
						new Class[] { attr.clazz });
				return attr;

			} catch (Exception ex) {
				LOG.log(Level.FINE, " error in matchParaMeterOfMethod = "
						+ ex.getLocalizedMessage(), ex);
			}
		}
		return null;
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
	static Object convertToObject(AttributesType attr, final String value) {

		return attr.parseValue(value);

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
			AttributesType parameterType = matchParaMeterOfMethod(obj,
					methodName);
			if (parameterType != null) {
				Method method = obj.getClass().getMethod(methodName,
						new Class[] { parameterType.clazz });
				method.invoke(obj,
						convertToObject(parameterType, propertyValue));
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
