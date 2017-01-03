/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.configuration;

import static org.tiefaces.common.TieConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.CellRange;
import org.tiefaces.components.websheet.service.CellUtility;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

/**
 * Handler class for configuration setting.
 * 
 * (1). Below are old way for configuration Basically configuration are dived
 * into two parts: 1. form level 2. attributes level. attrCol is the first
 * column which indicate the attributes level starting setting attrCol into
 * variable just for easy extend the form level range attribute column starting
 * index private int attrCol = 9;
 * 
 * (2). Later on introduced new way which are defined in comments. ---- still
 * working on
 * 
 * @author Jason Jiang
 *
 */
public class ConfigurationHandler {

	/** refer to parent bean class. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger log = Logger.getLogger(
			Thread.currentThread().getStackTrace()[0].getClassName());

	/**
	 * constructor.
	 *
	 * @param parent
	 *            the parent
	 */
	public ConfigurationHandler(final TieWebSheetBean parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Builds the configuration.
	 *
	 * @return the map
	 */
	public Map<String, SheetConfiguration> buildConfiguration() {

		Map<String, SheetConfiguration> sheetConfigMap = new LinkedHashMap<String, SheetConfiguration>();
		// in buildsheet, it's possible to add sheets in workbook.
		// so cache the sheetname first here.
		List<String> sheetNames = new ArrayList<String>();
		String sname = null;
		for (int i = 0; i < parent.getWb().getNumberOfSheets(); i++) {
			sname = parent.getWb().getSheetName(i);
			if (!sname.startsWith(COPY_SHEET_PREFIX)) {
				sheetNames.add(sname);
			}
		}

		for (String sheetName : sheetNames) {
			Sheet sheet = parent.getWb().getSheet(sheetName);
			buildSheet(sheet, sheetConfigMap,
					parent.getCellAttributesMap());
		}
		log.fine("buildConfiguration map = " + sheetConfigMap);
		return sheetConfigMap;
		/*
		 * log.fine("parent configuration tab = " +
		 * parent.getConfigurationTab()); Sheet sheet1 =
		 * parent.getWb().getSheet( parent.getConfigurationTab());
		 * 
		 * if (sheet1 == null) // no configuration tab return
		 * buildConfigurationWithoutTab(sheetConfigMap); else return
		 * buildConfigurationWithTab(sheet1, sheetConfigMap);
		 */
	}

	/**
	 * Gets the sheet configuration.
	 *
	 * @param sheet
	 *            the sheet
	 * @param formName
	 *            the form name
	 * @return the sheet configuration
	 */
	private SheetConfiguration getSheetConfiguration(Sheet sheet,
			String formName) {

		SheetConfiguration sheetConfig = new SheetConfiguration();
		sheetConfig.setFormName(formName);
		sheetConfig.setSheetName(sheet.getSheetName());
		int leftCol = sheet.getLeftCol();
		int lastRow = sheet.getLastRowNum();
		int firstRow = 0;
		int rightCol = 0;
		int maxRow = 0;
		for (Row row : sheet) {
			if (row.getRowNum() > TIE_WEB_SHEET_MAX_ROWS) {
				break;
			}
			maxRow = row.getRowNum();
			int firstCellNum = row.getFirstCellNum();
			if (firstCellNum >= 0 && firstCellNum < leftCol) {
				leftCol = firstCellNum;
			}
			if ((row.getLastCellNum() - 1) > rightCol) {
				int verifiedcol = verifyLastCell(row, rightCol);
				if (verifiedcol > rightCol) {
					rightCol = verifiedcol;
				}
			}
		}
		if (maxRow < lastRow) {
			lastRow = maxRow;
		}
		log.fine("form tabName = " + formName + " maxRow = " + maxRow);

		Cell firstCell = sheet.getRow(firstRow).getCell(leftCol,
				MissingCellPolicy.CREATE_NULL_AS_BLANK);
		// header range row set to 0 while column set to first column to
		// max
		// column (FF) e.g. $A$0 : $FF$0
		String tempStr = "$"
				+ TieWebSheetUtility.GetExcelColumnName(leftCol) + "$0 : $"
				+ TieWebSheetUtility.GetExcelColumnName(rightCol) + "$0";
		sheetConfig.setFormHeaderRange(tempStr);
		sheetConfig.setHeaderCellRange(new CellRange(tempStr));
		// body range row set to first row to last row while column set
		// to
		// first column to max column (FF) e.g. $A$1 : $FF$1000
		tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol) + "$"
				+ (firstRow + 1) + " : $"
				+ TieWebSheetUtility.GetExcelColumnName(rightCol) + "$"
				+ (lastRow + 1);
		sheetConfig.setFormBodyRange(tempStr);
		sheetConfig.setBodyCellRange(new CellRange(tempStr));
		sheetConfig.setFormBodyType(TIE_WEBSHEET_FORM_TYPE_FREE);
		sheetConfig.setCellFormAttributes(
				new HashMap<String, List<CellFormAttributes>>());

		// check it's a hidden sheet
		int sheetIndex = parent.getWb().getSheetIndex(sheet);
		if (parent.getWb().isSheetHidden(sheetIndex)
				|| parent.getWb().isSheetVeryHidden(sheetIndex)) {
			sheetConfig.setHidden(true);
		}

		FormCommand fcommand = buildFormCommandFromSheetConfig(sheetConfig,
				sheet, firstCell, rightCol, lastRow);
		sheetConfig.setFormCommand(fcommand);
		return sheetConfig;

	}

	/**
	 * Builds the form command from sheet config.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param sheet
	 *            the sheet
	 * @param firstCell
	 *            the first cell
	 * @param rightCol
	 *            the right col
	 * @param lastRow
	 *            the last row
	 * @return the form command
	 */
	private FormCommand buildFormCommandFromSheetConfig(
			SheetConfiguration sheetConfig, Sheet sheet, Cell firstCell,
			int rightCol, int lastRow) {
		FormCommand fcommand = new FormCommand();
		fcommand.setCommandTypeName(COMMAND_FORM);
		if (sheetConfig.isHidden()) {
			fcommand.setHidden(TRUE_STRING);
		} else {
			fcommand.setHidden(FALSE_STRING);
		}
		fcommand.setName(sheetConfig.getFormName());
		fcommand.getConfigRange().setFirstRowRef(firstCell, true);
		fcommand.getConfigRange().setLastRowPlusRef(sheet, rightCol,
				lastRow, true);
		return fcommand;
	}

	/**
	 * Row cell.
	 *
	 * @param row
	 *            the row
	 * @param cn
	 *            the cn
	 * @return the string
	 */
	private String rowCell(Row row, Integer cn) {
		String value = null;
		if (cn != null)
			value = CellUtility.getCellValueWithFormat(row.getCell(cn),
					parent.getFormulaEvaluator(),
					parent.getDataFormatter());
		if (value == null)
			value = "";
		return value.trim();
	}

	/**
	 * Adds the attributes to map.
	 *
	 * @param map
	 *            the map
	 * @param row
	 *            the row
	 * @param schemaMap
	 *            the schema map
	 */
	private void addAttributesToMap(
			Map<String, List<CellFormAttributes>> map, Row row,
			Map<String, Integer> schemaMap) {
		List<CellFormAttributes> attributes = map.get(rowCell(row, schemaMap
				.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)));
		if (attributes == null) {
			map.put(rowCell(row,
					schemaMap
							.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)),
					new ArrayList<CellFormAttributes>());
			attributes = map.get(rowCell(row, schemaMap.get(
					TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)));
		}
		CellFormAttributes cellattribute = new CellFormAttributes();
		cellattribute.setType(rowCell(row, schemaMap
				.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE)));
		cellattribute.setValue(rowCell(row, schemaMap
				.get(TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE)));
		cellattribute.setMessage(rowCell(row, schemaMap.get(
				TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG)));
		attributes.add(cellattribute);
	}

	/**
	 * check last column. if it's blank then treat it as null cell.
	 * 
	 * @param row
	 *            row object.
	 * @param stoppoint
	 *            the left cell we want to stop check.
	 * @return integer. the last column without blank cell.
	 */
	private int verifyLastCell(final Row row, final int stoppoint) {
		int lastCol = row.getLastCellNum() - 1;
		int col;
		for (col = lastCol; col >= stoppoint; col--) {

			Cell cell = row.getCell(col);
			if ((cell != null)
					&& (cell.getCellType() != Cell.CELL_TYPE_BLANK)) {
				break;
			}
		}
		return col;
	}

	/** new implement of configuration with setting in comments. */

	/** command map. */
	@SuppressWarnings("rawtypes")
	private static Map<String, Class> commandMap = new HashMap<String, Class>();

	static {
		commandMap.put(COMMAND_FORM, FormCommand.class);
		commandMap.put("each", EachCommand.class);
	}

	/**
	 * build a sheet for configuration map.
	 *
	 * @param sheet
	 *            sheet.
	 * @param sheetConfigMap
	 *            sheetConfiguration map.
	 * @param cellAttributesMap
	 *            the cell attributes map
	 */
	public final void buildSheet(final Sheet sheet,
			final Map<String, SheetConfiguration> sheetConfigMap,
			final CellAttributesMap cellAttributesMap) {

		int sheetRightCol = TieWebSheetUtility.getSheetRightCol(sheet);
		List<ConfigCommand> commandList = buildCommandListFromSheetComment(
				(XSSFSheet) sheet, sheetRightCol, cellAttributesMap);

		List<String> formList = new ArrayList<String>();

		buildSheetConfigMapFromFormCommand(sheet, sheetConfigMap,
				sheetRightCol, commandList, formList);
		// match parent command
		matchParentCommand(commandList);
		// setup save attrs in hidden column in the sheet.
		// loop command list again to assemble other command list into sheet
		// configuration
		matchSheetConfigForm(sheet, sheetConfigMap, commandList, formList);
	}

	/**
	 * build command list from comments. after transfer the comment to command,
	 * remove it from comments.
	 *
	 * @param sheet
	 *            sheet.
	 * @param sheetRightCol
	 *            the sheet right col
	 * @param cellAttributesMap
	 *            the cell attributes map
	 * @return command list.
	 */
	private List<ConfigCommand> buildCommandListFromSheetComment(
			final XSSFSheet sheet, final int sheetRightCol,
			final CellAttributesMap cellAttributesMap) {
		List<ConfigCommand> commandList = new ArrayList<ConfigCommand>();
		Map<CellAddress, ? extends Comment> comments = null;

		try {
			// due to a poi bug. null exception throwed if no comments in the
			// sheet.
			comments = sheet.getCellComments();
		} catch (Exception ex) {
			log.fine(
					"due to a poi bug, null exception throwed where there's no comment. exeption = "
							+ ex.getLocalizedMessage());
		}
		if (comments == null) {
			return commandList;
		}

		// not sure the map is sorted. So use tree map to sort it.
		SortedSet<CellAddress> keys = new TreeSet<CellAddress>(
				comments.keySet());
		// go through each comments
		// if found tie command then transfer it to list also remove from
		// comments.
		for (CellAddress key : keys) {
			Cell cell = sheet.getRow(key.getRow()).getCell(key.getColumn(),
					MissingCellPolicy.CREATE_NULL_AS_BLANK);
			commandList = buildCommandList(sheet, sheetRightCol, cell,
					commandList, cellAttributesMap);
		}
		return commandList;

	}

	/**
	 * build top level configuration map from command list. User can either put
	 * tie:form command in the comments (which will transfer to sheetConfig), Or
	 * just ignore it, then use whole sheet as one form.
	 *
	 * @param sheet
	 *            sheet.
	 * @param sheetConfigMap
	 *            sheetConfigMap.
	 * @param SheetRightCol
	 *            the sheet right col
	 * @param commandList
	 *            command list.
	 * @param formList
	 *            form list.
	 */
	private void buildSheetConfigMapFromFormCommand(final Sheet sheet,
			final Map<String, SheetConfiguration> sheetConfigMap,
			final int SheetRightCol, final List<ConfigCommand> commandList,
			final List<String> formList) {
		boolean foundForm = false;
		int minRowNum = sheet.getLastRowNum();
		int maxRowNum = sheet.getFirstRowNum();
		for (Command command : commandList) {
			// check whether is form command
			if (command.getCommandTypeName()
					.equalsIgnoreCase(COMMAND_FORM)) {
				foundForm = true;
				FormCommand fcommand = (FormCommand) command;
				sheetConfigMap.put(fcommand.getName(),
						getSheetConfigurationFromConfigCommand(sheet,
								fcommand));
				formList.add(fcommand.getName());
				if (fcommand.getTopRow() < minRowNum) {
					minRowNum = fcommand.getTopRow();
				}
				if (fcommand.getLastRow() > maxRowNum) {
					maxRowNum = fcommand.getLastRow();
				}
			}
		}
		// if no form found, then use the whole sheet as form
		if (!foundForm) {
			String formName = sheet.getSheetName();
			sheetConfigMap.put(formName,
					getSheetConfiguration(sheet, formName));
			formList.add(formName);
			minRowNum = sheet.getFirstRowNum();
			maxRowNum = sheet.getLastRowNum();
		}

		ConfigurationHelper.setSaveAttrsForSheet(sheet, minRowNum,
				maxRowNum);

	}

	/**
	 * Set up parent attribute for each command (exclude form command). The top
	 * level commands have no parent.
	 *
	 * @param commandList
	 *            the command list
	 */
	private void matchParentCommand(final List<ConfigCommand> commandList) {

		if (commandList != null) {
			for (int i = 0; i < commandList.size(); i++) {
				ConfigCommand child = commandList.get(i);
				if (!child.getCommandTypeName()
						.equalsIgnoreCase(COMMAND_FORM)) {
					int matchIndex = -1;
					ConfigRange matchRange = null;
					for (int j = 0; j < commandList.size(); j++) {
						if (j != i) {
							Command commandParent = commandList.get(j);
							if (!commandParent.getCommandTypeName()
									.equalsIgnoreCase(COMMAND_FORM)) {
								if (TieWebSheetUtility.insideRange(
										child.getConfigRange(),
										commandParent.getConfigRange())) {
									if ((matchRange == null)
											|| (TieWebSheetUtility
													.insideRange(
															commandParent
																	.getConfigRange(),
															matchRange))) {
										matchRange = commandParent
												.getConfigRange();
										matchIndex = j;
									}
								}
							}
						}
					}
					if (matchIndex >= 0) {
						commandList.get(matchIndex).getConfigRange()
								.addCommand(child);
						child.setParentFound(true);
					}
				}
			}
		}
	}

	/**
	 * Assemble top level command to sheetConfiguration (form). top level
	 * commands are those haven't matched from matchParentCommand function.
	 * 
	 * @param sheet
	 *            sheet.
	 * @param sheetConfigMap
	 *            sheet configuration map.
	 * @param commandList
	 *            command list.
	 * @param formList
	 *            form list.
	 */
	private void matchSheetConfigForm(final Sheet sheet,
			final Map<String, SheetConfiguration> sheetConfigMap,
			final List<ConfigCommand> commandList,
			final List<String> formList) {
		for (ConfigCommand command : commandList) {
			// check weather it's form command
			if (!command.getCommandTypeName().equalsIgnoreCase(COMMAND_FORM)
					&& (!command.isParentFound())) {
				for (String formname : formList) {
					SheetConfiguration sheetConfig = sheetConfigMap
							.get(formname);
					if (TieWebSheetUtility.insideRange(
							command.getConfigRange(), sheetConfig
									.getFormCommand().getConfigRange())) {
						sheetConfig.getFormCommand().getConfigRange()
								.addCommand(command);
						copyTemplateForTieCommands(sheet);
						break;
					}
				}
			}
		}
	}

	/**
	 * Copy the each command area to seperated sheet. As it will be used for
	 * iteration.
	 * 
	 * @param sheet
	 *            sheet.
	 */
	private void copyTemplateForTieCommands(final Sheet sheet) {
		Workbook wb = sheet.getWorkbook();
		String copyName = COPY_SHEET_PREFIX + sheet.getSheetName();
		if (wb.getSheet(copyName) == null) {
			Sheet newSheet = wb.cloneSheet(wb.getSheetIndex(sheet));
			wb.setSheetName(wb.getSheetIndex(newSheet), copyName);
		}
	}

	/**
	 * check it's a command comment.
	 * 
	 * @param str
	 *            comment string.
	 * @return ture if it's command.
	 */
	private boolean isCommandString(final String str) {
		return str.startsWith(COMMAND_PREFIX);
	}

	/**
	 * method string is start as $ follow by method name then with { and }. i.e.
	 * $init{department.name}
	 *
	 * @param str
	 *            the str
	 * @return true, if is method string
	 */
	private boolean isMethodString(final String str) {
		return str.matches(METHOD_REGEX);
	}

	/**
	 * empty method string is start as $ follow with { and }. i.e.
	 * ${department.name}
	 *
	 * @param str
	 *            the str
	 * @return true, if is empty method string
	 */
	private boolean isEmptyMethodString(final String str) {
		return str.startsWith(METHOD_PREFIX);
	}

	/**
	 * widget method start with $widget. e.g. $widget.calendar{....
	 *
	 * @param str
	 *            the str
	 * @return true, if is widget method string
	 */
	private boolean isWidgetMethodString(final String str) {
		return str.startsWith(METHOD_WIDGET_PREFIX);
	}

	/**
	 * build command list from comment.
	 *
	 * @param sheet
	 *            sheet.
	 * @param sheetRightCol
	 *            sheet right column.
	 * @param cell
	 *            the cell
	 * @param cList
	 *            command list.
	 * @param cellAttributesMap
	 *            the cell attributes map
	 * @return command list.
	 */
	private List<ConfigCommand> buildCommandList(final Sheet sheet,
			final int sheetRightCol, final Cell cell,
			final List<ConfigCommand> cList,
			final CellAttributesMap cellAttributesMap) {

		Comment comment = cell.getCellComment();
		String text = comment.getString().getString();
		String[] commentLines = text.split("\\n");
		String newComment = null;
		boolean changed = false;
		for (String commentLine : commentLines) {
			String line = commentLine.trim();
			if (isCommandString(line)) {
				int nameEndIndex = line.indexOf(ATTR_PREFIX,
						COMMAND_PREFIX.length());
				if (nameEndIndex < 0) {
					String errMsg = "Failed to parse command line [" + line
							+ "]. Expected '" + ATTR_PREFIX + "' symbol.";
					log.severe(errMsg);
					throw new IllegalStateException(errMsg);
				}
				String commandName = line
						.substring(COMMAND_PREFIX.length(), nameEndIndex)
						.trim();
				Map<String, String> attrMap = buildAttrMap(line,
						nameEndIndex);
				ConfigCommand configCommand = createConfigCommand(sheet,
						cell, sheetRightCol, commandName, attrMap);
				if (configCommand != null) {
					cList.add(configCommand);
				}
				changed = true;
			} else if (isEmptyMethodString(line) || isMethodString(line)) {
				if (isWidgetMethodString(line)) {
					parseWidgetAttributes(cell, line, cellAttributesMap);
				} else {
					saveCellComment(cell, line,
							cellAttributesMap.getTemplateCommentMap(),
							false);
				}
				changed = true;
			} else {
				if (newComment == null) {
					newComment = commentLine;
				}
				newComment += "\\n" + commentLine;
			}
		}
		if (!changed) {
			newComment = text;
		}
		saveCellComment(cell, newComment,
				cellAttributesMap.getTemplateCommentMap(), true);
		return cList;
	}

	/**
	 * change the comment.
	 *
	 * @param cell
	 *            the cell
	 * @param newComment
	 *            updated comment.
	 * @param sheetCommentMap
	 *            the sheet comment map
	 * @param normalComment
	 *            the normal comment
	 */
	private void saveCellComment(final Cell cell, final String newComment,
			final Map<String, Map<String, String>> sheetCommentMap,
			final boolean normalComment) {

		if ((newComment != null) && (!newComment.trim().isEmpty())) {
			// normal comment key is $$
			String key = "$$";
			if (!normalComment) {
				// not normal comment. e.g. ${... or $init{... or
				// $widget.dropdown{...
				// key = $ or key = $init or key = $widget.dropdown
				key = newComment.substring(0, newComment.indexOf("{"));
			}
			Map<String, String> map = sheetCommentMap.get(key);
			if (map == null) {
				map = new HashMap<String, String>();
			}
			// 2nd map's key is sheetName!$columnIndex$rowIndex
			map.put(cell.getSheet().getSheetName() + "!$"
					+ cell.getColumnIndex() + "$" + cell.getRowIndex(),
					newComment);
			sheetCommentMap.put(key, map);
		}
		// after saved to map. remove the cell comments.
		cell.removeCellComment();
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
	private void parseWidgetAttributes(final Cell cell,
			final String newComment,
			final CellAttributesMap cellAttributesMap) {

		String type = newComment.substring(
				newComment.indexOf(METHOD_WIDGET_PREFIX)
						+ METHOD_WIDGET_PREFIX.length(),
				newComment.indexOf("{"));

		String values = newComment.substring(newComment.indexOf("{") + 1,
				newComment.indexOf("}"));
		// map's key is sheetName!$columnIndex$rowIndex
		String key = cell.getSheet().getSheetName() + "!$"
				+ cell.getColumnIndex() + "$" + cell.getRowIndex();
		// one cell only has one control widget
		cellAttributesMap.getCellInputType().put(key, type);
		List<CellFormAttributes> inputs = cellAttributesMap
				.getCellInputAttributes().get(key);
		if (inputs == null) {
			inputs = new ArrayList<CellFormAttributes>();
			cellAttributesMap.getCellInputAttributes().put(key, inputs);
		}
		CellControlsHelper.parseInputAttributes(inputs, values);

		CellControlsHelper.parseSelectItemsAttributes(key, type, inputs,
				cellAttributesMap);

	}

	/**
	 * create configuration command.
	 * 
	 * @param sheet
	 *            sheet.
	 * @param firstCell
	 *            first cell.
	 * @param sheetRightCol
	 *            sheet right col.
	 * @param commandName
	 *            command name.
	 * @param attrMap
	 *            attibutes map.
	 * @return configuration command.
	 */

	private ConfigCommand createConfigCommand(final Sheet sheet,
			final Cell firstCell, final int sheetRightCol,
			final String commandName, final Map<String, String> attrMap) {
		@SuppressWarnings("rawtypes")
		Class clas = commandMap.get(commandName);
		if (clas == null) {
			log.warning(
					"Failed to find Command class mapped to command name '"
							+ commandName + "'");
			return null;
		}
		try {
			ConfigCommand command = (ConfigCommand) clas.newInstance();
			command.setCommandTypeName(commandName);
			for (Map.Entry<String, String> attr : attrMap.entrySet()) {
				TieWebSheetUtility.setObjectProperty(command, attr.getKey(),
						attr.getValue(), true);
			}
			command.getConfigRange().setFirstRowRef(firstCell, true);
			command.getConfigRange().setLastRowPlusRef(sheet, sheetRightCol,
					command.getLastRow(), true);
			return command;
		} catch (Exception e) {
			log.warning("Failed to instantiate command class '"
					+ clas.getName() + "' mapped to command name '"
					+ commandName + "'");
			return null;
		}
	}

	/**
	 * Build the attributes map.
	 * 
	 * @param commandLine
	 *            command line from comment.
	 * @param nameEndIndex
	 *            index of command name's end.
	 * @return attributes map.
	 */
	private Map<String, String> buildAttrMap(final String commandLine,
			final int nameEndIndex) {
		int paramsEndIndex = commandLine.lastIndexOf(ATTR_SUFFIX);
		if (paramsEndIndex < 0) {
			String errMsg = "Failed to parse command line [" + commandLine
					+ "]. Expected '" + ATTR_SUFFIX + "' symbol.";
			throw new IllegalArgumentException(errMsg);
		}
		String attrString = commandLine
				.substring(nameEndIndex + 1, paramsEndIndex).trim();
		return parseCommandAttributes(attrString);
	}

	/**
	 * Parse the attributes from string.
	 * 
	 * @param attrString
	 *            command string.
	 * @return attributes map.
	 */
	private Map<String, String> parseCommandAttributes(
			final String attrString) {
		Map<String, String> attrMap = new LinkedHashMap<String, String>();
		Matcher attrMatcher = ATTR_REGEX_PATTERN.matcher(attrString);
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
	 * Create sheet configuration from form command.
	 * 
	 * @param sheet
	 *            sheet.
	 * @param fcommand
	 *            form command.
	 * @return sheet configuration.
	 */
	private SheetConfiguration getSheetConfigurationFromConfigCommand(
			final Sheet sheet, final FormCommand fcommand) {

		SheetConfiguration sheetConfig = new SheetConfiguration();
		sheetConfig.setFormName(fcommand.getName());
		sheetConfig.setSheetName(sheet.getSheetName());
		int leftCol = fcommand.getLeftCol();
		int lastRow = fcommand.getLastRow();
		int rightCol = 0;
		int maxRow = 0;
		for (Row row : sheet) {
			if (row.getRowNum() > TIE_WEB_SHEET_MAX_ROWS) {
				break;
			}
			maxRow = row.getRowNum();
			if ((row.getLastCellNum() - 1) > rightCol) {
				int verifiedcol = verifyLastCell(row, rightCol);
				if (verifiedcol > rightCol) {
					rightCol = verifiedcol;
				}
			}
		}
		if (maxRow < lastRow) {
			lastRow = maxRow;
		}
		log.fine("tabName = " + fcommand.getName() + " maxRow = " + maxRow);

		// header range row set to 0 while column set to first column to
		// max
		// column (FF) e.g. $A$0 : $FF$0
		String tempStr;
		if (fcommand.calcHeaderLength() == 0) {
			tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol)
					+ "$0 : $"
					+ TieWebSheetUtility.GetExcelColumnName(rightCol)
					+ "$0";
		} else {
			tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol)
					+ "$" + (fcommand.getTopRow() + 1) + " : $"
					+ TieWebSheetUtility.GetExcelColumnName(rightCol) + "$"
					+ (fcommand.getTopRow() + fcommand.calcHeaderLength());
		}
		sheetConfig.setFormHeaderRange(tempStr);
		sheetConfig.setHeaderCellRange(new CellRange(tempStr));
		// body range row set to first row to last row while column set
		// to
		// first column to max column (FF) e.g. $A$1 : $FF$1000
		tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol) + "$"
				+ (fcommand.getTopRow() + fcommand.calcHeaderLength() + 1)
				+ " : $" + TieWebSheetUtility.GetExcelColumnName(rightCol)
				+ "$" + (lastRow + 1);
		sheetConfig.setFormBodyRange(tempStr);

		sheetConfig.setBodyCellRange(new CellRange(tempStr));
		sheetConfig.setFormBodyType(TIE_WEBSHEET_FORM_TYPE_FREE);
		sheetConfig.setCellFormAttributes(
				new HashMap<String, List<CellFormAttributes>>());

		// footer range row set to 0 while column set to first column to
		// max
		// column (FF) e.g. $A$0 : $FF$0
		if (fcommand.calcFooterLength() == 0) {
			tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol)
					+ "$0 : $"
					+ TieWebSheetUtility.GetExcelColumnName(rightCol)
					+ "$0";
		} else {
			tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol)
					+ "$"
					+ (fcommand.getTopRow() + fcommand.calcHeaderLength()
							+ fcommand.calcBodyLength())
					+ " : $"
					+ TieWebSheetUtility.GetExcelColumnName(rightCol) + "$"
					+ (fcommand.getTopRow() + fcommand.calcHeaderLength()
							+ fcommand.calcBodyLength()
							+ fcommand.calcFooterLength() - 1);
		}
		sheetConfig.setFormFooterRange(tempStr);
		sheetConfig.setFooterCellRange(new CellRange(tempStr));

		String hidden = fcommand.getHidden();
		if ((hidden != null) && (Boolean.parseBoolean(hidden))) {
			sheetConfig.setHidden(true);
		}
		sheetConfig.setFormCommand(fcommand);
		return sheetConfig;

	}

}
