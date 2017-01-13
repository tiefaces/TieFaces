/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

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

import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;

/**
 * Helper class for web sheet cells.
 * 
 * @author Jason Jiang
 *
 */
public final class ParserUtility {

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
		if (str == null) {
			return false;
		}
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
		if (str == null) {
			return false;
		}
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
		if (str == null) {
			return false;
		}
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
		if (str == null) {
			return false;
		}
		return str.startsWith(TieConstants.METHOD_WIDGET_PREFIX);
	}

	/**
	 * validate method start with $validate{ rule="..." error="..."
	 *
	 * @param str
	 *            the str
	 * @return true, if is validate method string
	 */
	public static boolean isValidateMethodString(final String str) {
		if (str == null) {
			return false;
		}
		return str.startsWith(TieConstants.METHOD_VALIDATE_PREFIX);
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

		if ((newComment == null) || (newComment.isEmpty())) {
			return;
		}
		int widgetStart = newComment
				.indexOf(TieConstants.METHOD_WIDGET_PREFIX);
		int elStart = newComment.indexOf(TieConstants.EL_START_BRACKET);
		if ((widgetStart < 0) || (widgetStart >= elStart)) {
			return;
		}

		String type = newComment.substring(widgetStart
				+ TieConstants.METHOD_WIDGET_PREFIX.length(), elStart);

		String values = getStringBetweenBracket(newComment);
		if (values == null) {
			return;
		}
		// map's key is sheetName!$columnIndex$rowIndex
		String key = getAttributeKeyInMapByCell(cell);
		// one cell only has one control widget
		cellAttributesMap.getCellInputType().put(key, type);
		List<CellFormAttributes> inputs = cellAttributesMap
				.getCellInputAttributes().get(key);
		if (inputs == null) {
			inputs = new ArrayList<CellFormAttributes>();
			cellAttributesMap.getCellInputAttributes().put(key, inputs);
		}
		parseInputAttributes(inputs, values);

		parseSpecialAttributes(key, type, inputs, cellAttributesMap);

	}

	
	public static String getAttributeKeyInMapByCell(Cell cell) {
		if (cell==null) {
			return null;
		}
		// map's key is sheetName!$columnIndex$rowIndex
		String key = cell.getSheet().getSheetName() + "!"
				+ TieConstants.cellAddrPrefix + cell.getColumnIndex()
				+ TieConstants.cellAddrPrefix + cell.getRowIndex();
		return key;
		
	}
	
	/**
	 * Parses the validate attributes.
	 *
	 * @param cell
	 *            the cell
	 * @param newComment
	 *            the new comment
	 * @param cellAttributesMap
	 *            the cell attributes map
	 */
	public static void parseValidateAttributes(final Cell cell,
			final String newComment,
			final CellAttributesMap cellAttributesMap) {

		if ((newComment == null) || (newComment.isEmpty())) {
			return;
		}
		if (!newComment.startsWith(TieConstants.METHOD_VALIDATE_PREFIX)) {
			return;
		}
		
		String values = getStringBetweenBracket(newComment);
		if (values == null) {
			return;
		}
		// map's key is sheetName!$columnIndex$rowIndex
		String key =getAttributeKeyInMapByCell(cell);
		List<CellFormAttributes> attrs = cellAttributesMap
				.getCellValidateAttributes().get(key);
		if (attrs == null) {
			attrs = new ArrayList<CellFormAttributes>();
			cellAttributesMap.getCellValidateAttributes().put(key, attrs);
		}
		parseValidateAttributes(attrs, values);
		
	}
	
	/**
	 * get the string between two bracket. 
	 * .e.g. $save{employee.name} return employee.name.
	 * @param newComment input string.
	 * @return return string.
	 */
	
	private static String getStringBetweenBracket(String newComment) {
		
		int elStart = newComment.indexOf(TieConstants.EL_START_BRACKET);
		int elEnd = newComment.indexOf(TieConstants.EL_END);
		if (elStart >= elEnd) {
			return null;
		}
		
		return newComment.substring(elStart + 1, elEnd);
		
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
			String[] cattrs = controlAttrs.split(TieConstants.SPLIT_SPACE_SEPERATE_ATTRS_REGX, -1);
			for (String cattr : cattrs) {
				String[] details = splitByEualSign(cattr);
				if ((details != null) && (details.length > 1)) {
					CellFormAttributes attr = new CellFormAttributes();
					attr.setType(details[0].trim());
					attr.setValue(details[1].replaceAll("\"", ""));
					clist.add(attr);
				}
			}
		}
	}

	/**
	 * parse validate attributes.
	 * 
	 * @param clist
	 *            list of cellformattributes.
	 * @param controlAttrs
	 *            control attrs.
	 */
	public static void parseValidateAttributes(
			final List<CellFormAttributes> clist, final String controlAttrs) {
		// one cell could have multiple validation rules.
		if (controlAttrs != null) {
			String[] cattrs = controlAttrs.split( TieConstants.SPLIT_SPACE_SEPERATE_ATTRS_REGX , -1);
			
			String rule=null;
			String errorMsg=null;
			for (String cattr : cattrs) {
				
				
				
				String[] details = splitByEualSign(cattr);
				if ((details != null) && (details.length > 1)) {
					if (details[0].equalsIgnoreCase(TieConstants.VALIDATION_RULE_TAG)) {
						rule = details[1].replaceAll("\"", "");
					} else if (details[0].equalsIgnoreCase(TieConstants.VALIDATION_ERROR_MSG_TAG)) {
							errorMsg = details[1].replaceAll("\"", "");
					}
				}
			}
			if ((rule!=null)&&(!rule.isEmpty())) {
				CellFormAttributes attr = new CellFormAttributes();
				attr.setValue(rule);
				attr.setMessage(errorMsg);
				clist.add(attr);
			}
		}	
	}
	
	
	private static String[] splitByEualSign(String attrData) { 
		int attrNameEndIndex = attrData.indexOf("=");
		if (attrNameEndIndex < 0) {
			return null;
		}
		String attrName = attrData.substring(0, attrNameEndIndex)
				.trim();
		String attrValue = attrData.substring(attrNameEndIndex + 1)
				.trim();
		String[] rlist = new String[2];
		rlist[0] = attrName;
		rlist[1] = attrValue;
		return rlist;
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
	public static void parseSpecialAttributes(final String key,
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
		if (type.equalsIgnoreCase(TieConstants.WIDGET_CALENDAR)) {
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

	/**
	 * Parse Comment To Map
	 * 
	 * Normal comment : key $$ Not Normal comment: key e.g. $save
	 * @param cellKey
	 *            cellKey.
	 * @param newComment
	 *            updated comment.
	 * @param sheetCommentMap
	 *            the sheet comment map.
	 * @param normalComment
	 *            the normal comment.
	 */
	public static void parseCommentToMap(final String cellKey,
			final String newComment,
			final Map<String, Map<String, String>> sheetCommentMap,
			final boolean normalComment) {
		if ((newComment != null) && (!newComment.trim().isEmpty())) {
			// normal comment key is $$
			String commentKey = TieConstants.NORMAL_COMMENT_KEY_IN_MAP;
			if (!normalComment) {
				// not normal comment. e.g. ${... or $init{... or
				// key = $ or key = $init 
				commentKey = newComment.substring(0,
						newComment.indexOf(TieConstants.EL_START_BRACKET));
			}
			Map<String, String> map = sheetCommentMap.get(commentKey);
			if (map == null) {
				map = new HashMap<String, String>();
			}
			// inner map's key is sheetName!$columnIndex$rowIndex
			map.put(cellKey, newComment);
			sheetCommentMap.put(commentKey, map);
		}
	}

}
