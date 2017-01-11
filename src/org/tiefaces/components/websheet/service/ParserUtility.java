/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.faces.component.UIComponent;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.configuration.CellControlsHelper;
import org.tiefaces.components.websheet.configuration.ConfigurationHelper;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.utility.ColorUtility;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

/**
 * Helper class for web sheet cells.
 * 
 * @author Jason Jiang
 *
 */
public final class ParserUtility {

	/** logger. */
	private static final Logger LOG = Logger.getLogger(ParserUtility.class
			.getName());

	/**
	 * Instantiates a new cell helper.
	 */
	private ParserUtility() {
	}

	/**
	 * check it's a command comment.
	 * 
	 * @param str
	 *            comment string.
	 * @return ture if it's command.
	 */
	public static boolean isCommandString(final String str) {
		return str.startsWith(TieConstants.COMMAND_PREFIX);
	}

	/**
	 * method string is start as $ follow by method name then with { and }. i.e.
	 * $init{department.name}
	 *
	 * @param str
	 *            the str
	 * @return true, if is method string
	 */
	public static boolean isMethodString(final String str) {
		return str.matches(TieConstants.METHOD_REGEX);
	}

	/**
	 * empty method string is start as $ follow with { and }. i.e.
	 * ${department.name}
	 *
	 * @param str
	 *            the str
	 * @return true, if is empty method string
	 */
	public static boolean isEmptyMethodString(final String str) {
		return str.startsWith(TieConstants.METHOD_PREFIX);
	}

	/**
	 * widget method start with $widget. e.g. $widget.calendar{....
	 *
	 * @param str
	 *            the str
	 * @return true, if is widget method string
	 */
	public static boolean isWidgetMethodString(final String str) {
		return str.startsWith(TieConstants.METHOD_WIDGET_PREFIX);
	}

	/**
	 * Parses the widget attributes.
	 *
	 * @param cell
	 *            the cell
	 * @param newComment
	 *            the new comment
	 * @param cellAttributesMap
	 *            the cell attributes map
	 */
	public static void parseWidgetAttributes(final Cell cell,
			final String newComment,
			final CellAttributesMap cellAttributesMap) {

		String type = newComment.substring(
				newComment.indexOf(TieConstants.METHOD_WIDGET_PREFIX)
						+ TieConstants.METHOD_WIDGET_PREFIX.length(),
				newComment.indexOf(TieConstants.EL_START_BRACKET));

		String values = newComment.substring(
				newComment.indexOf(TieConstants.EL_START_BRACKET) + 1,
				newComment.indexOf(TieConstants.EL_END));
		// map's key is sheetName!$columnIndex$rowIndex
		String key = cell.getSheet().getSheetName() + "!"
				+ TieConstants.cellAddrPrefix + cell.getColumnIndex()
				+ TieConstants.cellAddrPrefix + cell.getRowIndex();
		// one cell only has one control widget
		cellAttributesMap.getCellInputType().put(key, type);
		List<CellFormAttributes> inputs = cellAttributesMap
				.getCellInputAttributes().get(key);
		if (inputs == null) {
			inputs = new ArrayList<CellFormAttributes>();
			cellAttributesMap.getCellInputAttributes().put(key, inputs);
		}
		parseInputAttributes(inputs, values);

		parseSelectItemsAttributes(key, type, inputs, cellAttributesMap);

	}

	/**
	 * Parse the attributes from string.
	 * 
	 * @param attrString
	 *            command string.
	 * @return attributes map.
	 */
	public static Map<String, String> parseCommandAttributes(
			final String attrString) {
		Map<String, String> attrMap = new LinkedHashMap<String, String>();
		Matcher attrMatcher = TieConstants.ATTR_REGEX_PATTERN
				.matcher(attrString);
		while (attrMatcher.find()) {
			String attrData = attrMatcher.group();
			int attrNameEndIndex = attrData.indexOf("=");
			String attrName = attrData.substring(0, attrNameEndIndex)
					.trim();
			String attrValuePart = attrData.substring(attrNameEndIndex + 1)
					.trim();
			String attrValue = attrValuePart.substring(1,
					attrValuePart.length() - 1);
			attrMap.put(attrName, attrValue);
		}
		return attrMap;
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
			final List<CellFormAttributes> clist, final String controlAttrs) {
		// only one type control allowed for one cell.
		clist.clear();
		if (controlAttrs != null) {
			String[] cattrs = controlAttrs.split(
					"\" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
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
			if (attrKey.equalsIgnoreCase(TieConstants.SELECT_ITEM_LABELS)) {
				selectLabels = attr.getValue().split(";");
			}
			if (attrKey.equalsIgnoreCase(TieConstants.SELECT_ITEM_VALUES)) {
				selectValues = attr.getValue().split(";");
			}
			if (attrKey
					.equalsIgnoreCase(TieConstants.DEFAULT_SELECT_ITEM_LABEL)) {
				defaultSelectLabel = attr.getValue();
			}
			if (attrKey
					.equalsIgnoreCase(TieConstants.DEFAULT_SELECT_ITEM_VALUE)) {
				defaultSelectValue = attr.getValue();
			}
			if (type.equalsIgnoreCase(TieConstants.WIDGET_CALENDAR)
					&& attrKey
							.equalsIgnoreCase(TieConstants.WIDGET_ATTR_PATTERN)) {
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
