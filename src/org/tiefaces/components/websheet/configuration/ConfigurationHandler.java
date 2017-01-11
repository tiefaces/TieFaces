/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.configuration;

import org.tiefaces.common.TieConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.CellRange;
import org.tiefaces.components.websheet.service.ParserUtility;
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
	private static final Logger LOG = Logger
			.getLogger(ConfigurationHandler.class.getName());

	/**
	 * constructor.
	 *
	 * @param pparent
	 *            the parent
	 */
	public ConfigurationHandler(final TieWebSheetBean pparent) {
		super();
		this.parent = pparent;
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
			if (!sname
					.startsWith(org.tiefaces.common.TieConstants.COPY_SHEET_PREFIX)) {
				sheetNames.add(sname);
			}
		}

		for (String sheetName : sheetNames) {
			Sheet sheet = parent.getWb().getSheet(sheetName);
			buildSheet(sheet, sheetConfigMap, parent.getCellAttributesMap());
		}
		LOG.fine("buildConfiguration map = " + sheetConfigMap);
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
	private SheetConfiguration getSheetConfiguration(final Sheet sheet,
			final String formName) {

		SheetConfiguration sheetConfig = new SheetConfiguration();
		sheetConfig.setFormName(formName);
		sheetConfig.setSheetName(sheet.getSheetName());
		int leftCol = sheet.getLeftCol();
		int lastRow = sheet.getLastRowNum();
		int firstRow = sheet.getFirstRowNum();
		int rightCol = 0;
		int maxRow = 0;
		for (Row row : sheet) {
			if (row.getRowNum() > org.tiefaces.common.TieConstants.TIE_WEB_SHEET_MAX_ROWS) {
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
		LOG.fine("form tabName = " + formName + " maxRow = " + maxRow);

		Cell firstCell = sheet.getRow(firstRow).getCell(leftCol,
				MissingCellPolicy.CREATE_NULL_AS_BLANK);
		// header range row set to 0 while column set to first column to
		// max
		// column (FF) e.g. $A$0 : $FF$0
		String tempStr = TieConstants.cellAddrPrefix
				+ TieWebSheetUtility.getExcelColumnName(leftCol)
				+ TieConstants.cellAddrPrefix + "0 : "
				+ TieConstants.cellAddrPrefix
				+ TieWebSheetUtility.getExcelColumnName(rightCol)
				+ TieConstants.cellAddrPrefix + "0";
		sheetConfig.setFormHeaderRange(tempStr);
		sheetConfig.setHeaderCellRange(new CellRange(tempStr));
		// body range row set to first row to last row while column set
		// to
		// first column to max column (FF) e.g. $A$1 : $FF$1000
		tempStr = TieConstants.cellAddrPrefix
				+ TieWebSheetUtility.getExcelColumnName(leftCol)
				+ TieConstants.cellAddrPrefix + (firstRow + 1) + " : "
				+ TieConstants.cellAddrPrefix
				+ TieWebSheetUtility.getExcelColumnName(rightCol)
				+ TieConstants.cellAddrPrefix + (lastRow + 1);
		sheetConfig.setFormBodyRange(tempStr);
		sheetConfig.setBodyCellRange(new CellRange(tempStr));
		sheetConfig
				.setFormBodyType(org.tiefaces.common.TieConstants.TIE_WEBSHEET_FORM_TYPE_FREE);
		sheetConfig
				.setCellFormAttributes(new HashMap<String, List<CellFormAttributes>>());

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
			final SheetConfiguration sheetConfig, final Sheet sheet,
			final Cell firstCell, final int rightCol, final int lastRow) {
		FormCommand fcommand = new FormCommand();
		fcommand.setCommandTypeName(TieConstants.COMMAND_FORM);
		if (sheetConfig.isHidden()) {
			fcommand.setHidden(TieConstants.TRUE_STRING);
		} else {
			fcommand.setHidden(TieConstants.FALSE_STRING);
		}
		fcommand.setName(sheetConfig.getFormName());
		fcommand.getConfigRange().setFirstRowRef(firstCell, true);
		fcommand.getConfigRange().setLastRowPlusRef(sheet, rightCol,
				lastRow, true);
		return fcommand;
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
		commandMap.put(TieConstants.COMMAND_FORM, FormCommand.class);
		commandMap.put(TieConstants.COMMAND_EACH, EachCommand.class);
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

		if ((sheet.getLastRowNum() <= 0) && (sheet.getRow(0) == null)) {
			// this is a empty sheet. skip it.
			return;
		}
		int sheetRightCol = TieWebSheetUtility.getSheetRightCol(sheet);
		List<ConfigCommand> commandList = buildCommandListFromSheetComment(
				(XSSFSheet) sheet, sheetRightCol, cellAttributesMap);

		List<String> formList = new ArrayList<String>();

		buildSheetConfigMapFromFormCommand(sheet, sheetConfigMap,
				commandList, formList);
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
			LOG.fine("due to a poi bug, null exception throwed where there's no comment. exeption = "
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
	 * @param commandList
	 *            command list.
	 * @param formList
	 *            form list.
	 */
	private void buildSheetConfigMapFromFormCommand(final Sheet sheet,
			final Map<String, SheetConfiguration> sheetConfigMap,
			final List<ConfigCommand> commandList,
			final List<String> formList) {
		boolean foundForm = false;
		int minRowNum = sheet.getLastRowNum();
		int maxRowNum = sheet.getFirstRowNum();
		for (Command command : commandList) {
			// check whether is form command
			if (command.getCommandTypeName().equalsIgnoreCase(
					TieConstants.COMMAND_FORM)) {
				foundForm = true;
				FormCommand fcommand = (FormCommand) command;
				sheetConfigMap.put(
						fcommand.getName(),
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
				if (!child.getCommandTypeName().equalsIgnoreCase(
						TieConstants.COMMAND_FORM)) {
					int matchIndex = -1;
					ConfigRange matchRange = null;
					for (int j = 0; j < commandList.size(); j++) {
						if (j != i) {
							Command commandParent = commandList.get(j);
							if (!commandParent.getCommandTypeName()
									.equalsIgnoreCase(
											TieConstants.COMMAND_FORM)) {
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
			if (!command.getCommandTypeName().equalsIgnoreCase(
					TieConstants.COMMAND_FORM)
					&& (!command.isParentFound())) {
				for (String formname : formList) {
					SheetConfiguration sheetConfig = sheetConfigMap
							.get(formname);
					if (TieWebSheetUtility.insideRange(command
							.getConfigRange(), sheetConfig.getFormCommand()
							.getConfigRange())) {
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
		String copyName = TieConstants.COPY_SHEET_PREFIX
				+ sheet.getSheetName();
		if (wb.getSheet(copyName) == null) {
			Sheet newSheet = wb.cloneSheet(wb.getSheetIndex(sheet));
			int sheetIndex = wb.getSheetIndex(newSheet);
			wb.setSheetName(sheetIndex, copyName);
			wb.setSheetHidden(sheetIndex, Workbook.SHEET_STATE_VERY_HIDDEN);
		}
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
			if (ParserUtility.isCommandString(line)) {
				int nameEndIndex = line.indexOf(TieConstants.ATTR_PREFIX,
						TieConstants.COMMAND_PREFIX.length());
				if (nameEndIndex < 0) {
					String errMsg = "Failed to parse command line [" + line
							+ "]. Expected '" + TieConstants.ATTR_PREFIX
							+ "' symbol.";
					LOG.severe(errMsg);
					throw new IllegalStateException(errMsg);
				}
				String commandName = line.substring(
						TieConstants.COMMAND_PREFIX.length(), nameEndIndex)
						.trim();
				Map<String, String> attrMap = buildAttrMap(line,
						nameEndIndex);
				ConfigCommand configCommand = createConfigCommand(sheet,
						cell, sheetRightCol, commandName, attrMap);
				if (configCommand != null) {
					cList.add(configCommand);
				}
				changed = true;
			} else if (ParserUtility.isEmptyMethodString(line)
					|| ParserUtility.isMethodString(line)) {
				if (ParserUtility.isWidgetMethodString(line)) {
					ParserUtility.parseWidgetAttributes(cell, line,
							cellAttributesMap);
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
			LOG.warning("Failed to find Command class mapped to command name '"
					+ commandName + "'");
			return null;
		}
		try {
			ConfigCommand command = (ConfigCommand) clas.newInstance();
			command.setCommandTypeName(commandName);
			for (Map.Entry<String, String> attr : attrMap.entrySet()) {
				TieWebSheetUtility.setObjectProperty(command,
						attr.getKey(), attr.getValue(), true);
			}
			command.getConfigRange().setFirstRowRef(firstCell, true);
			command.getConfigRange().setLastRowPlusRef(sheet,
					sheetRightCol, command.getLastRow(), true);
			return command;
		} catch (Exception e) {
			LOG.warning("Failed to instantiate command class '"
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
		int paramsEndIndex = commandLine
				.lastIndexOf(TieConstants.ATTR_SUFFIX);
		if (paramsEndIndex < 0) {
			String errMsg = "Failed to parse command line [" + commandLine
					+ "]. Expected '" + TieConstants.ATTR_SUFFIX
					+ "' symbol.";
			throw new IllegalArgumentException(errMsg);
		}
		String attrString = commandLine.substring(nameEndIndex + 1,
				paramsEndIndex).trim();
		return ParserUtility.parseCommandAttributes(attrString);
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
			if (row.getRowNum() > TieConstants.TIE_WEB_SHEET_MAX_ROWS) {
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
		LOG.fine("tabName = " + fcommand.getName() + " maxRow = " + maxRow);

		// header range row set to 0 while column set to first column to
		// max
		// column (FF) e.g. $A$0 : $FF$0
		String tempStr;
		if (fcommand.calcHeaderLength() == 0) {
			tempStr = TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(leftCol)
					+ TieConstants.cellAddrPrefix + "0 : "
					+ TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(rightCol)
					+ TieConstants.cellAddrPrefix + "0";
		} else {
			tempStr = TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(leftCol)
					+ TieConstants.cellAddrPrefix
					+ (fcommand.getTopRow() + 1) + " : "
					+ TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(rightCol)
					+ TieConstants.cellAddrPrefix
					+ (fcommand.getTopRow() + fcommand.calcHeaderLength());
		}
		sheetConfig.setFormHeaderRange(tempStr);
		sheetConfig.setHeaderCellRange(new CellRange(tempStr));
		// body range row set to first row to last row while column set
		// to
		// first column to max column (FF) e.g. $A$1 : $FF$1000
		tempStr = TieConstants.cellAddrPrefix
				+ TieWebSheetUtility.getExcelColumnName(leftCol)
				+ TieConstants.cellAddrPrefix
				+ (fcommand.getTopRow() + fcommand.calcHeaderLength() + 1)
				+ " : " + TieConstants.cellAddrPrefix
				+ TieWebSheetUtility.getExcelColumnName(rightCol)
				+ TieConstants.cellAddrPrefix + (lastRow + 1);
		sheetConfig.setFormBodyRange(tempStr);

		sheetConfig.setBodyCellRange(new CellRange(tempStr));
		sheetConfig
				.setFormBodyType(TieConstants.TIE_WEBSHEET_FORM_TYPE_FREE);
		sheetConfig
				.setCellFormAttributes(new HashMap<String, List<CellFormAttributes>>());

		// footer range row set to 0 while column set to first column to
		// max
		// column (FF) e.g. $A$0 : $FF$0
		if (fcommand.calcFooterLength() == 0) {
			tempStr = TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(leftCol)
					+ TieConstants.cellAddrPrefix + "0 : "
					+ TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(rightCol)
					+ TieConstants.cellAddrPrefix + "0";
		} else {
			tempStr = TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(leftCol)
					+ TieConstants.cellAddrPrefix
					+ (fcommand.getTopRow() + fcommand.calcHeaderLength() + fcommand
							.calcBodyLength())
					+ " : "
					+ TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(rightCol)
					+ TieConstants.cellAddrPrefix
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
