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

import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellAttributesMap;
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
		
		/** The string. */
		STRING(String.class) {
			@Override
			public Object parseValue(final String value) {
				return value;
			}
		},
		
		/** The boolean. */
		BOOLEAN(Boolean.class) {
			@Override
			public Object parseValue(final String value) {
				return Boolean.parseBoolean(value);
			}
		},
		
		/** The booleantype. */
		BOOLEANTYPE(boolean.class) {
			@Override
			public Object parseValue(final String value) {
				return Boolean.parseBoolean(value);
			}
		},
		
		/** The integer. */
		INTEGER(Integer.class) {
			@Override
			public Object parseValue(final String value) {
				return Integer.parseInt(value);
			}
		},
		
		/** The integertype. */
		INTEGERTYPE(int.class) {
			@Override
			public Object parseValue(final String value) {
				return Integer.parseInt(value);
			}
		},
		
		/** The long. */
		LONG(Long.class) {
			@Override
			public Object parseValue(final String value) {
				return Long.parseLong(value);
			}
		},
		
		/** The longtype. */
		LONGTYPE(long.class) {
			@Override
			public Object parseValue(final String value) {
				return Long.parseLong(value);
			}
		},
		
		/** The float. */
		FLOAT(Float.class) {
			@Override
			public Object parseValue(final String value) {
				return Float.parseFloat(value);
			}
		},
		
		/** The floattype. */
		FLOATTYPE(float.class) {
			@Override
			public Object parseValue(final String value) {
				return Float.parseFloat(value);
			}
		},
		
		/** The double. */
		DOUBLE(Double.class) {
			@Override
			public Object parseValue(final String value) {
				return Double.parseDouble(value);
			}
		},
		
		/** The doubletype. */
		DOUBLETYPE(double.class) {
			@Override
			public Object parseValue(final String value) {
				return Double.parseDouble(value);
			}
		},
		
		/** The byte. */
		BYTE(Byte.class) {
			@Override
			public Object parseValue(final String value) {
				return Byte.parseByte(value);
			}
		},
		
		/** The bytetype. */
		BYTETYPE(byte.class) {
			@Override
			public Object parseValue(final String value) {
				return Byte.parseByte(value);
			}
		},
		
		/** The short. */
		SHORT(Short.class) {
			@Override
			public Object parseValue(final String value) {
				return Short.parseShort(value);
			}
		},
		
		/** The shorttype. */
		SHORTTYPE(short.class) {
			@Override
			public Object parseValue(final String value) {
				return Short.parseShort(value);
			}
		};

		/** The clazz. */
		private final Class clazz; // class name

		/**
		 * Instantiates a new attributes type.
		 *
		 * @param pclazz
		 *            the pclazz
		 */
		AttributesType(final Class pclazz) {
			this.clazz = pclazz;
		}

		/**
		 * Parses the value.
		 *
		 * @param value
		 *            the value
		 * @return the object
		 */
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
	 * @param attr
	 *            the attr
	 * @param value
	 *            value.
	 * @return object according to class.
	 */
	static Object convertToObject(final AttributesType attr, final String value) {

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
	public static String getObjectPropertyValue(final Object obj,
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

	/**
	 * Setup control attributes.
	 *
	 * @param originRowIndex
	 *            the origin row index
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param sheetConfig
	 *            the sheet config
	 * @param cellAttributesMap
	 *            the cell attributes map
	 */
	public static void setupControlAttributes(final int originRowIndex,
			final FacesCell fcell, final Cell poiCell,
			final SheetConfiguration sheetConfig,
			final CellAttributesMap cellAttributesMap) {
		int rowIndex = originRowIndex;
		if (rowIndex < 0) {
			rowIndex = poiCell.getRowIndex();
		}

		String skey = poiCell.getSheet().getSheetName() + "!" + CellUtility
				.getCellIndexNumberKey(poiCell.getColumnIndex(), rowIndex);

		Map<String, String> commentMap = cellAttributesMap
				.getTemplateCommentMap().get("$$");
		if (commentMap != null) {
			String comment = commentMap.get(skey);
			if (comment != null) {
				CommandUtility.createCellComment(poiCell, comment,
						sheetConfig.getFinalCommentMap());
			}
		}

		String widgetType = cellAttributesMap.getCellInputType().get(skey);
		if (widgetType != null) {
			fcell.setControl(widgetType.toLowerCase());

			fcell.setInputAttrs(
					cellAttributesMap.getCellInputAttributes().get(skey));
			fcell.setSelectItemAttrs(cellAttributesMap
					.getCellSelectItemsAttributes().get(skey));
			fcell.setDatePattern(
					cellAttributesMap.getCellDatePattern().get(skey));
		}

	}

	/**
	 * Find cell validate attributes.
	 *
	 * @param validateMaps
	 *            validateMaps.
	 * @param originRowIndex
	 * 			   original Row Index from facesRow.           
	 * @param cell
	 *            cell.
	 * @return list.
	 */
	public static List<CellFormAttributes> findCellValidateAttributes(
			final Map<String, List<CellFormAttributes>> validateMaps,
			final int originRowIndex,
			final Cell cell) {
		String key = cell.getSheet().getSheetName() + "!" + CellUtility
				.getCellIndexNumberKey(cell.getColumnIndex(), originRowIndex);
		
		return validateMaps.get(key);
	}
	
}
