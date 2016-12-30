/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.common;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.util.regex.Pattern;

public final class TieConstants {

	public static final String TIE_WEBSHEET_COMPONENT_ID = "websheettable";
	public static final String TIE_WEBSHEET_CONFIGURATION_SHEET = "Configuration";
	public static final String TIE_WEBSHEET_ATTRS_WEBSHEETBEAN = "webSheetBean";

	public static final String EXCEL_2003_TYPE = "xls";

	public static final String EXCEL_2007_TYPE = "xlsx";

	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_VERSION = "Version";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_TAB_NAME = "Tab Name";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_SHEET_NAME = "Sheet Name";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_HEADER_RANGE = "Form Header Range";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_RANGE = "Form Body Range";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_FOOTER_RANGE = "Form Footer Range";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_TYPE = "Form Body Type";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_ALLOW_ADD_ROW = "Allow ADD/DELETE ROW";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_INIT_ROWS = "Initial Rows";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_PAGE_TYPE = "Form Page Type";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_WIDTH = "Form Width";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_MAX_ROWS_PER_PAGE = "Max Rows Per Page";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_BEFORE = "Saved Rows Before Repeat";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_AFTER = "Saved Rows After Rpeat";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL = "Target Column/Cell";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE = "Attributes Type";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE = "Attributes Value";
	public static final String TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG = "Validation Error Messages";
	public static final String TIE_WEBSHEET_CONFIGURATION_ATTR_CONTROL = "Control";

	public static final String TIE_WEBSHEET_FORM_TYPE_FREE = "Free";
	public static final String TIE_WEBSHEET_FORM_TYPE_REPEAT = "Repeat";

	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE = "double";
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_INTEGER = "integer";
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE = "percentage";
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_DATE = "date";
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_TEXT = "text";
	public static final String TIE_WEBSHEET_CELL_INPUT_TYPE_TEXTAREA = "textarea";

	public static final String COPY_SHEET_PREFIX = "cptieTemp_";
	public static final short EXCEL_SHEET_NAME_LIMIT = 31;
	/** method prefix. */
	public static final String METHOD_PREFIX = "${";
	/** method prefix. */
	public static final String METHOD_WIDGET_PREFIX = "$widget.";
	/** method regex. */
	public static final String METHOD_REGEX = "\\$+[^{$]+\\{+[^{}$]+\\}";
	/** command's prefix. */
	public static final String COMMAND_PREFIX = "tie:";
	/** form command. */
	public static final String COMMAND_FORM = "form";
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

	public static final String TIE_WEBSHEET_CELL_DATA_ROW = "data-row";
	public static final String TIE_WEBSHEET_CELL_DATA_COLUMN = "data-column";
	public static final String TIE_WEBSHEET_CELL_DATA_SYMBOL = "data-symbol";

	public static final int TIE_WEB_SHEET_MAX_ROWS = 99999;

	public static final String EL_START = "#{";
	public static final String EL_END = "}";
	
	public static final String TAB_TYPE_NONE = "none";
	
	public static final String TAB_STYLE_VISIBLE = "height: 530px;";
	public static final String TAB_STYLE_INVISIBLE = "height: 30px;";
	
	public static final int defaultMaxRowsPerPage = 80;
	
	public static final String defaultTableWidthStyle = "100%;";
	
	public static final int defaultLineNumberColumnWidth = 26;
	public static final int defaultAddRowColumnWidth = 38;
	
	/** default basic stroke width. */
	public static final float defaultBasicStroke = 0.1f;
	
	public static final int defaultLegentItemShapeWidth = 8;
	public static final int defaultLegentItemShapeHeight = 8;
	
	public static final float defaultBarStyleItemMargin= 0.02f;

	public static final int defaultBarStyleForegroundAlpha = 1;

	
}
