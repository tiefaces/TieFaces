/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;

/**
 * Cell controls helper.
 * 
 * @author JASON JIANG.
 *
 */
public final class CellControlsHelper {

	/**
	 * hide constructor.
	 */
	private CellControlsHelper() {
		// not called
	}

	/** logger. */
	private final static Logger log = Logger.getLogger(
			Thread.currentThread().getStackTrace()[0].getClassName());

	/** list of supported components. */
	private static List<String> supportComponents = Arrays
			.asList("Calendar", "SelectOneMenu", "InputNumber");

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
			defaultMap = new HashMap<String, String>();
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

		// }
	}

	/**
	 * array list for possible parameter's type.
	 */

	@SuppressWarnings("rawtypes")
	private static Class[] paraMatchArray = { String.class, boolean.class,
			Boolean.class, int.class, Integer.class, long.class, Long.class,
			float.class, Float.class, double.class, Double.class,
			byte.class, Byte.class, short.class, Short.class };

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
				log.fine(ex.getLocalizedMessage());
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
	private static Object convertToObject(final Class clazz,
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
				log.fine(msg);
			} else {
				log.warning(msg);
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
			String value = (String) method.invoke(obj);
			return value;
		} catch (Exception e) {
			String msg = "failed to get property '" + propertyName
					+ "' for object " + obj;
			if (ignoreNonExisting) {
				log.fine(msg);
			} else {
				log.warning(msg);
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	/**
	 * parse input attributes.
	 * 
	 * @param clist
	 *            list of cellformattributes.
	 * @param controlAttrs
	 *            control attrs.
	 */
	public static void parseInputAttributes(
			final List<CellFormAttributes> clist,
			final String controlAttrs) {
		// only one type control allowed for one cell.
		clist.clear();
		if (controlAttrs != null) {
			String[] cattrs = controlAttrs
					.split("\" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			for (String cattr : cattrs) {
				String[] details = cattr.split("=");
				if (details.length > 1) {
					CellFormAttributes attr = new CellFormAttributes();
					attr.setType(details[0].trim());
					attr.setValue(details[1].replaceAll("\"", ""));
					clist.add(attr);
				}
			}
		}
	}

	/** select item labels. */
	private static final String SELECT_ITEM_LABELS = "itemlabels";
	/** select item values. */
	private static final String SELECT_ITEM_VALUES = "itemvalues";
	/** default select item label. */
	private static final String DEFAULT_SELECT_ITEM_LABEL = "defaultlabel";
	/** default select item value. */
	private static final String DEFAULT_SELECT_ITEM_VALUE = "defaultvalue";

	/**
	 * parse select item attributes.
	 * 
	 * @param key
	 *            key.
	 * @param type
	 *            type.
	 * @param inputs
	 *            inputs.
	 * @param cellAttributesMap
	 *            cellattributesmap.
	 */
	public static void parseSelectItemsAttributes(final String key,
			final String type, final List<CellFormAttributes> inputs,
			final CellAttributesMap cellAttributesMap) {
		String[] selectLabels = null;
		String[] selectValues = null;
		String defaultSelectLabel = null;
		String defaultSelectValue = null;
		String defaultDatePattern = "";
		for (CellFormAttributes attr : inputs) {
			String attrKey = attr.getType();
			if (attrKey.equalsIgnoreCase(SELECT_ITEM_LABELS)) {
				selectLabels = attr.getValue().split(";");
			}
			if (attrKey.equalsIgnoreCase(SELECT_ITEM_VALUES)) {
				selectValues = attr.getValue().split(";");
			}
			if (attrKey.equalsIgnoreCase(DEFAULT_SELECT_ITEM_LABEL)) {
				defaultSelectLabel = attr.getValue();
			}
			if (attrKey.equalsIgnoreCase(DEFAULT_SELECT_ITEM_VALUE)) {
				defaultSelectValue = attr.getValue();
			}
			if (type.equalsIgnoreCase("calendar")
					&& attrKey.equalsIgnoreCase("pattern")) {
				defaultDatePattern = attr.getValue();
			}
		}

		if (selectLabels != null) {
			if ((selectValues == null)
					|| (selectValues.length != selectLabels.length)) {
				selectValues = selectLabels;
			}
			Map<String, String> smap = cellAttributesMap
					.getCellSelectItemsAttributes().get(key);
			if (smap == null) {
				smap = new LinkedHashMap<String, String>();
			}
			smap.clear();
			if (defaultSelectLabel != null) {
				smap.put(defaultSelectLabel, defaultSelectValue);
			}
			for (int i = 0; i < selectLabels.length; i++) {
				smap.put(selectLabels[i], selectValues[i]);
			}
			cellAttributesMap.getCellSelectItemsAttributes().put(key, smap);
		}
		if (type.equalsIgnoreCase("calendar")) {
			if (defaultDatePattern.isEmpty()) {
				defaultDatePattern = getDefaultDatePattern();
			}
			cellAttributesMap.getCellDatePattern().put(key,
					defaultDatePattern);
		}
	}

	/**
	 * get default date pattern.
	 * 
	 * @return default date pattern.
	 */
	private static String getDefaultDatePattern() {
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT,
				Locale.getDefault());
		return ((SimpleDateFormat) formatter).toLocalizedPattern();
	}

}
