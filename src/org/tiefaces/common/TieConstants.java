/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.common;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.util.regex.Pattern;

/**
 * The Class TieConstants.
 */
public final class TieConstants {

	/** The Constant TIE_WEBSHEET_COMPONENT_ID. */
	public static final String TIE_WEBSHEET_COMPONENT_ID = "websheettable";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SHEET. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SHEET = "Configuration";

	/** The Constant TIE_WEBSHEET_ATTRS_WEBSHEETBEAN. */
	public static final String TIE_WEBSHEET_ATTRS_WEBSHEETBEAN = "webSheetBean";

	/** The Constant EXCEL_2003_TYPE. */
	public static final String EXCEL_2003_TYPE = "xls";

	/** The Constant EXCEL_2007_TYPE. */
	public static final String EXCEL_2007_TYPE = "xlsx";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_VERSION. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_VERSION = "Version";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_TAB_NAME. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_TAB_NAME = "Tab Name";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_SHEET_NAME. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_SHEET_NAME = "Sheet Name";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_HEADER_RANGE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_HEADER_RANGE = "Form Header Range";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_RANGE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_RANGE = "Form Body Range";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_FOOTER_RANGE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_FOOTER_RANGE = "Form Footer Range";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_TYPE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_TYPE = "Form Body Type";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_ALLOW_ADD_ROW. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_ALLOW_ADD_ROW = "Allow ADD/DELETE ROW";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_INIT_ROWS. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_INIT_ROWS = "Initial Rows";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_PAGE_TYPE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_PAGE_TYPE = "Form Page Type";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_WIDTH. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_WIDTH = "Form Width";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_MAX_ROWS_PER_PAGE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_MAX_ROWS_PER_PAGE = "Max Rows Per Page";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_BEFORE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_BEFORE = "Saved Rows Before Repeat";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_AFTER. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_AFTER = "Saved Rows After Rpeat";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL = "Target Column/Cell";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE = "Attributes Type";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE = "Attributes Value";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG. */
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG = "Validation Error Messages";

	/** The Constant TIE_WEBSHEET_CONFIGURATION_ATTR_CONTROL. */
	public static final String TIE_WEBSHEET_CONFIGURATION_ATTR_CONTROL = "Control";

	/** The Constant TIE_WEBSHEET_FORM_TYPE_FREE. */
	public static final String TIE_WEBSHEET_FORM_TYPE_FREE = "Free";

	/** The Constant TIE_WEBSHEET_FORM_TYPE_REPEAT. */
	public static final String TIE_WEBSHEET_FORM_TYPE_REPEAT = "Repeat";

	/** The Constant TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE. */
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE = "double";

	/** The Constant TIE_WEBSHEET_CELL_INPUT_TYPE_INTEGER. */
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_INTEGER = "integer";

	/** The Constant TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE. */
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE = "percentage";

	/** The Constant TIE_WEBSHEET_CELL_INPUT_TYPE_DATE. */
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_DATE = "date";

	/** The Constant TIE_WEBSHEET_CELL_INPUT_TYPE_TEXT. */
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_TEXT = "text";

	/** The Constant TIE_WEBSHEET_CELL_INPUT_TYPE_TEXTAREA. */
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_TEXTAREA = "textarea";

	/** The Constant COPY_SHEET_PREFIX. */
	public static final String COPY_SHEET_PREFIX = "cptieTemp_";

	/** The Constant EXCEL_SHEET_NAME_LIMIT. */
	public static final short EXCEL_SHEET_NAME_LIMIT = 31;
	/** method prefix. */
	/** command's prefix. */
	public static final String COMMAND_PREFIX = "tie:";
	/** form command. */
	public static final String COMMAND_FORM = "form";
	/** each command. */
	public static final String COMMAND_EACH = "each";
	/** true string. */
	public static final String TRUE_STRING = "true";
	/** false string. */
	public static final String FALSE_STRING = "false";
	/** attribute prefix. */
	public static final String ATTR_PREFIX = "(";
	/** attribute suffix. */
	public static final String ATTR_SUFFIX = ")";
	/** attribute's regex. */
	public static final String ATTR_REGEX = "\\s*\\w+\\s*=\\s*([\"|'])(?:(?!\\1).)*\\1";
	/** attribute's regex pattern. */
	public static final Pattern ATTR_REGEX_PATTERN = Pattern
			.compile(ATTR_REGEX);

	/** The Constant TIE_WEBSHEET_CELL_DATA_ROW. */
	public static final String TIE_WEBSHEET_CELL_DATA_ROW = "data-row";

	/** The Constant TIE_WEBSHEET_CELL_DATA_COLUMN. */
	public static final String TIE_WEBSHEET_CELL_DATA_COLUMN = "data-column";

	/** The Constant TIE_WEBSHEET_CELL_DATA_SYMBOL. */
	public static final String TIE_WEBSHEET_CELL_DATA_SYMBOL = "data-symbol";

	/** The Constant TIE_WEB_SHEET_MAX_ROWS. */
	public static final int TIE_WEB_SHEET_MAX_ROWS = 99999;

	/** The Constant EL_START. */
	public static final String EL_START = "#{";

	/** The Constant EL_START_BRACKET. */
	public static final String EL_START_BRACKET = "{";

	/** The Constant EL_END. */
	public static final String EL_END = "}";

	/** The Constant TAB_TYPE_NONE. */
	public static final String TAB_TYPE_NONE = "none";

	/** The Constant TAB_STYLE_VISIBLE. */
	public static final String TAB_STYLE_VISIBLE = "height: 530px;";

	/** The Constant TAB_STYLE_INVISIBLE. */
	public static final String TAB_STYLE_INVISIBLE = "height: 30px;";

	/** The Constant defaultMaxRowsPerPage. */
	public static final int defaultMaxRowsPerPage = 80;

	/** The Constant defaultTableWidthStyle. */
	public static final String defaultTableWidthStyle = "100%;";

	/** The Constant defaultLineNumberColumnWidth. */
	public static final int defaultLineNumberColumnWidth = 26;

	/** The Constant excel letter total numbers. */
	public static final int EXCEL_LETTER_NUMBERS = 26;
	
	/** The Constant defaultAddRowColumnWidth. */
	public static final int defaultAddRowColumnWidth = 38;

	/** default basic stroke width. */
	public static final float defaultBasicStroke = 0.1f;

	/** The Constant defaultLegentItemShapeWidth. */
	public static final int defaultLegentItemShapeWidth = 8;

	/** The Constant defaultLegentItemShapeHeight. */
	public static final int defaultLegentItemShapeHeight = 8;

	/** The Constant defaultBarStyleItemMargin. */
	public static final float defaultBarStyleItemMargin = 0.02f;

	/** The Constant defaultBarStyleForegroundAlpha. */
	public static final int defaultBarStyleForegroundAlpha = 1;

	/** The Constant eachCommandInvalidMsg. */
	public static final String eachCommandInvalidMsg = " is not a list collection, cannot support add/delete function.";

	/** The Constant defaultCommandPartLength. */
	public static final int defaultCommandPartLength = 3;

	/** The Constant USER_FORMULA_PREFIX. */
	public static final String USER_FORMULA_PREFIX = "$[";

	/** The Constant USER_FORMULA_SUFFIX. */
	public static final String USER_FORMULA_SUFFIX = "]";

	/** The Constant hiddenFullNameColumn. */
	public static final int hiddenFullNameColumn = 255;

	/** The Constant hiddenSaveObjectsColumn. */
	public static final int hiddenSaveObjectsColumn = 256;

	/** The Constant cellMapKeyChart. */
	public static final String cellMapKeyChart = "chart";

	/** The Constant cellMapKeyPicture. */
	public static final String cellMapKeyPicture = "picture";

	/** The Constant cellMapKeyFormat. */
	public static final String cellMapKeyFormat = "format";

	/** The Constant cellMapKeyPercent. */
	public static final String cellMapKeyPercent = "percent";

	/** The Constant cellFormatPercentageSymbol. */
	public static final String cellFormatPercentageSymbol = "%";

	/** The Constant cellFormatPercentageValue. */
	public static final int cellFormatPercentageValue = 100;
	/** The Constant cellAddrPrefix. */
	public static final String cellAddrPrefix = "$";
	/** The Constant rgb max. */
	public static final short RGB_MAX = 256;
	/** select item labels. */
	public static final String SELECT_ITEM_LABELS = "itemlabels";
	/** select item values. */
	public static final String SELECT_ITEM_VALUES = "itemvalues";
	/** default select item label. */
	public static final String DEFAULT_SELECT_ITEM_LABEL = "defaultlabel";
	/** default select item value. */
	public static final String DEFAULT_SELECT_ITEM_VALUE = "defaultvalue";
	/** widget calendar. */
	public static final String WIDGET_CALENDAR = "calendar";
	/** widget attribute pattern. */
	public static final String WIDGET_ATTR_PATTERN = "pattern";
	/** component attributes locale. */
	public static final String COMPONENT_ATTR_LOCALE = "locale";
	/** method regex. */
	public static final String METHOD_REGEX = "\\$+[^{$]+\\{+[^{}$]+\\}";
	/** method prefix. */
	public static final String METHOD_PREFIX = "${";
	/** The expression notation end. */
	public static final String METHOD_END = "}";
	/** method widget prefix. */
	public static final String METHOD_WIDGET_PREFIX = "$widget.";
	
	/** The expression notation pattern. */
	public static final Pattern expressionNotationPattern = Pattern
			.compile("\\$\\{[^}]*}");
	
	public static final float DEFAULT_HEADER_ROW_HEIGHT = 12;
	
}
