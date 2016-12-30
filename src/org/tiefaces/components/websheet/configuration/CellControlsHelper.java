package org.tiefaces.components.websheet.configuration;

import static org.tiefaces.components.websheet.TieWebSheetConstants.*;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.service.CellHelper;

public class CellControlsHelper {
	
	/** logger. */
	private final static Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());
	

	private static List<String> supportComponents = Arrays.asList("Calendar",
			"SelectOneMenu", "InputNumber");

	private static String findComponentNameFromClass(UIComponent component) {

		String cname = component.getClass().getSimpleName();
		if (supportComponents.contains(cname)) {
			return cname;
		}
		return null;

	}

	public static void populateAttributes(UIComponent component,
			FacesCell fcell, Map<String, Map<String, String>> defaultControlMap) {
		// if (component instanceof org.primefaces.component.calendar.Calendar)
		// {
		// org.primefaces.component.selectonemenu.SelectOneMenu cobj =
		// (org.primefaces.component.selectonemenu.SelectOneMenu) component;

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
			setObjectProperty(component, entry.getKey(), entry.getValue(), true);
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

	private static Class paraMatchArray[] = { String.class, boolean.class,
			Boolean.class, int.class, Integer.class, long.class, Long.class,
			float.class, Float.class, double.class, Double.class, byte.class,
			Byte.class, short.class, Short.class };

	private static int matchParaMeterOfMethod(Object obj, String methodName) {

		for (int i = 0; i < paraMatchArray.length; i++) {
			try {
				Method method = obj.getClass().getMethod(methodName,
						new Class[] { paraMatchArray[i] });
				return i;

			} catch (Exception ex) {
				log.fine(ex.getLocalizedMessage());
			}
		}
		return -1;
	}

	private static Object convertToObject(Class clazz, String value) {
		if (String.class == clazz)
			return value;
		if (Boolean.class == clazz || Boolean.TYPE == clazz)
			return Boolean.parseBoolean(value);
		if (Byte.class == clazz || Byte.TYPE == clazz)
			return Byte.parseByte(value);
		if (Short.class == clazz || Short.TYPE == clazz)
			return Short.parseShort(value);
		if (Integer.class == clazz || Integer.TYPE == clazz)
			return Integer.parseInt(value);
		if (Long.class == clazz || Long.TYPE == clazz)
			return Long.parseLong(value);
		if (Float.class == clazz || Float.TYPE == clazz)
			return Float.parseFloat(value);
		if (Double.class == clazz || Double.TYPE == clazz)
			return Double.parseDouble(value);
		return value;
	}

	public static void setObjectProperty(Object obj, String propertyName,
			String propertyValue, boolean ignoreNonExisting) {
		try {

			String methodName = "set"
					+ Character.toUpperCase(propertyName.charAt(0))
					+ propertyName.substring(1);
			int parameterType = matchParaMeterOfMethod(obj, methodName);
			if (parameterType > -1) {
				Method method = obj.getClass().getMethod(methodName,
						new Class[] { paraMatchArray[parameterType] });
				method.invoke(
						obj,
						convertToObject(paraMatchArray[parameterType],
								propertyValue));
			}
		} catch (Exception e) {
			String msg = "failed to set property '" + propertyName
					+ "' to value '" + propertyValue + "' for object " + obj;
			if (ignoreNonExisting) {
				log.fine(msg);
			} else {
				log.warning(msg);
				throw new IllegalArgumentException(e);
			}
		}
	}

	private static String getObjectPropertyValue(Object obj,
			String propertyName, boolean ignoreNonExisting) {
		try {
			Method method = obj.getClass().getMethod(
					"get" + Character.toUpperCase(propertyName.charAt(0))
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

	public static void parseInputAttributes(List<CellFormAttributes> clist,
			String controlAttrs) {
		// only one type control allowed for one cell.
		clist.clear();
		if (controlAttrs != null) {
			String[] cattrs = controlAttrs.split("\" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
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

/*	public static void addAttributesToMap(
			Map<String, List<CellFormAttributes>> attrsMap,
			Map<String, List<CellFormAttributes>> inputsMap,
			Map<String, Map<String, String>> selectItemsMap, Row row,
			Map<String, Integer> schemaMap, CellHelper cellHelper) {
		String key = rowCell(
				row,
				schemaMap
						.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL),
				cellHelper);
		List<CellFormAttributes> attributes = attrsMap.get(key);
		if (attributes == null) {
			attributes = new ArrayList<CellFormAttributes>();
			attrsMap.put(key, attributes);
		}
		CellFormAttributes cellattribute = new CellFormAttributes();
		cellattribute
				.setType(rowCell(
						row,
						schemaMap
								.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE),
						cellHelper));
		cellattribute
				.setValue(rowCell(
						row,
						schemaMap
								.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE),
						cellHelper).trim());
		cellattribute
				.setMessage(rowCell(
						row,
						schemaMap
								.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG),
						cellHelper));
		attributes.add(cellattribute);

		if ((cellattribute.getType() != null)
				&& (cellattribute.getType()
						.equalsIgnoreCase(TIE_WEBSHEET_CONFIGURATION_ATTR_CONTROL))) {
			List<CellFormAttributes> inputs = inputsMap.get(key);
			if (inputs == null) {
				inputs = new ArrayList<CellFormAttributes>();
				inputsMap.put(key, inputs);
			}
			parseInputAttributes(inputs, cellattribute.getMessage());
			parseSelectItemsAttributes(key, inputs, selectItemsMap);

		}
	}
*/
	private static final String SELECT_ITEM_LABELS = "itemlabels";
	private static final String SELECT_ITEM_VALUES = "itemvalues";
	private static final String DEFAULT_SELECT_ITEM_LABEL = "defaultlabel";
	private static final String DEFAULT_SELECT_ITEM_VALUE = "defaultvalue";

	public static void parseSelectItemsAttributes(String key,
			String type,
			List<CellFormAttributes> inputs,
			CellAttributesMap cellAttributesMap) {
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
			if (type.equalsIgnoreCase("calendar") && attrKey.equalsIgnoreCase("pattern")) {
				defaultDatePattern = attr.getValue();
			}
		}

		if (selectLabels != null) {
			if ((selectValues == null)
					|| (selectValues.length != selectLabels.length)) {
				selectValues = selectLabels;
			}
			Map<String, String> smap = cellAttributesMap.cellSelectItemsAttributes.get(key);
			if (smap == null) {
				smap = new LinkedHashMap<String, String>();
			}
			smap.clear();
			if (defaultSelectLabel!=null) {
				smap.put(defaultSelectLabel, defaultSelectValue);
			}
			for (int i = 0; i < selectLabels.length; i++) {
				smap.put(selectLabels[i], selectValues[i]);
			}
			cellAttributesMap.cellSelectItemsAttributes.put(key, smap);
		}
		if (type.equalsIgnoreCase("calendar")) {
			if (defaultDatePattern.isEmpty()) {
				defaultDatePattern = getDefaultDatePattern();
			}
			cellAttributesMap.cellDatePattern.put(key, defaultDatePattern);
		}
	}

	private static String getDefaultDatePattern() {
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
		return ((SimpleDateFormat)formatter).toLocalizedPattern();		
	}
	
	private static String rowCell(Row row, int cn,
			CellHelper cellHelper) {
		return cellHelper.getCellValueWithFormat(
				row.getCell(cn, Row.CREATE_NULL_AS_BLANK));
	}

}
