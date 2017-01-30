/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.service.CellUtility;
import org.tiefaces.components.websheet.service.ShiftFormulaUtility;
import org.tiefaces.exception.EvaluationException;

/**
 * The Class ConfigurationHelper.
 */
public final class ConfigurationHelper {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(ConfigurationHelper.class.getName());

	/**
	 * hide constructor.
	 */
	private ConfigurationHelper() {
		// not called
	}


	/**
	 * Evaluate.
	 *
	 * @param context
	 *            the context
	 * @param cell
	 *            the cell
	 * @param engine
	 *            the engine
	 */
	public static void evaluate(final Map<String, Object> context,
			final Cell cell, final ExpressionEngine engine) {
		Object evaluationResult;
		if ((cell != null) && (cell.getCellTypeEnum() == CellType.STRING)) {
			String strValue = cell.getStringCellValue();
			if (isUserFormula(strValue)) {
				String formulaStr = strValue.substring(2,
						strValue.length() - 1);
				if ((formulaStr != null) && (!formulaStr.isEmpty())) {
					cell.setCellFormula(formulaStr);
				}
			} else {
				if (strValue.contains(TieConstants.METHOD_PREFIX)) {

					evaluationResult = evaluate(strValue, context, engine);
					if (evaluationResult == null) {
						evaluationResult = "";
					}
					CellUtility.setCellValue(cell,
							evaluationResult.toString());
				}
			}
		}
	}

	/**
	 * Save data to object in context.
	 *
	 * @param context
	 *            the context
	 * @param saveAttr
	 *            the save attr
	 * @param strValue
	 *            the str value
	 * @param engine
	 *            the engine
	 */
	public static void saveDataToObjectInContext(
			final Map<String, Object> context, final String saveAttr,
			final String strValue, final ExpressionEngine engine) {

		int index = saveAttr.lastIndexOf('.');
		if (index > 0) {
			String strObject = saveAttr.substring(0, index);
			String strMethod = saveAttr.substring(index + 1);
			Object object = evaluate(strObject, context, engine);
			CellControlsHelper.setObjectProperty(object, strMethod,
					strValue, true);
		}
	}

	/**
	 * Parses the save attr.
	 *
	 * @param cell
	 *            the cell
	 * @return the string
	 */
	public static String parseSaveAttr(final Cell cell) {
		if ((cell != null) && (cell.getCellTypeEnum() == CellType.STRING)
				&& !cell.getCellStyle().getLocked()) {
			String saveAttr = parseSaveAttrString(
					cell.getStringCellValue());
			if (!saveAttr.isEmpty()) {
				return "$" + cell.getColumnIndex() + "=" + saveAttr + ",";
			}
		}
		return "";
	}

	/**
	 * Parses the save attr string.
	 *
	 * @param strValue
	 *            the str value
	 * @return the string
	 */
	public static String parseSaveAttrString(final String strValue) {
		if (strValue != null) {
			int first = strValue.indexOf(TieConstants.METHOD_PREFIX);
			int last = strValue.lastIndexOf(TieConstants.METHOD_PREFIX);
			int end = strValue.lastIndexOf(TieConstants.METHOD_END);
			if ((first >= 0) && (first == last) && (end > 1)) {
				return strValue.substring(first + 2, end);
			}
		}
		return "";
	}

	/**
	 * Gets the save attr list from row.
	 *
	 * @param row
	 *            the row
	 * @return the save attr list from row
	 */
	public static String getSaveAttrListFromRow(final Row row) {
		if (row != null) {
			Cell cell = row
					.getCell(TieConstants.HIDDEN_SAVE_OBJECTS_COLUMN);
			if (cell != null) {
				String str = cell.getStringCellValue();
				if ((str != null) && (!str.isEmpty())) {
					return str;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the save attr from list.
	 *
	 * @param columnIndex
	 *            the column index
	 * @param saveAttrs
	 *            the save attrs
	 * @return the save attr from list
	 */
	public static String getSaveAttrFromList(final int columnIndex,
			final String saveAttrs) {
		if ((saveAttrs != null) && (!saveAttrs.isEmpty())) {
			String str = "$" + columnIndex + "=";
			int istart = saveAttrs.indexOf(str);
			if (istart >= 0) {
				int iend = saveAttrs.indexOf(',', istart);
				if (iend > istart) {
					return saveAttrs.substring(istart + str.length(), iend);

				}
			}
		}
		return null;
	}

	/**
	 * Checks if is checks for save attr.
	 *
	 * @param cell
	 *            the cell
	 * @return true, if is checks for save attr
	 */
	public static boolean isHasSaveAttr(final Cell cell) {
		Cell scell = cell.getRow()
				.getCell(TieConstants.HIDDEN_SAVE_OBJECTS_COLUMN);
		if (scell != null) {
			return isHasSaveAttr(cell.getColumnIndex(),
					scell.getStringCellValue());
		}
		return false;
	}

	/**
	 * Checks if is checks for save attr.
	 *
	 * @param columnIndex
	 *            the column index
	 * @param saveAttrs
	 *            the save attrs
	 * @return true, if is checks for save attr
	 */
	public static boolean isHasSaveAttr(final int columnIndex,
			final String saveAttrs) {
		String str = "$" + columnIndex + "=";
		if ((saveAttrs != null) && (saveAttrs.indexOf(str) >= 0)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is user formula.
	 *
	 * @param str
	 *            the str
	 * @return true, if is user formula
	 */
	private static boolean isUserFormula(final String str) {
		return str.startsWith(TieConstants.USER_FORMULA_PREFIX)
				&& str.endsWith(TieConstants.USER_FORMULA_SUFFIX);
	}

	/**
	 * Evaluate.
	 *
	 * @param strValue
	 *            the str value
	 * @param context
	 *            the context
	 * @param engine
	 *            the engine
	 * @return the object
	 */
	public static Object evaluate(final String strValue,
			final Map<String, Object> context,
			final ExpressionEngine engine) {
		StringBuffer sb = new StringBuffer();
		int beginExpressionLength = TieConstants.METHOD_PREFIX.length();
		int endExpressionLength = TieConstants.METHOD_END.length();
		Matcher exprMatcher = TieConstants.EXPRESSION_NOTATION_PATTERN
				.matcher(strValue);
		String matchedString;
		String expression;
		Object lastMatchEvalResult = null;
		int matchCount = 0;
		int endOffset = 0;
		while (exprMatcher.find()) {
			endOffset = exprMatcher.end();
			matchCount++;
			matchedString = exprMatcher.group();
			expression = matchedString.substring(beginExpressionLength,
					matchedString.length() - endExpressionLength);
			lastMatchEvalResult = engine.evaluate(expression, context);
			exprMatcher.appendReplacement(sb,
					Matcher.quoteReplacement(lastMatchEvalResult != null
							? lastMatchEvalResult.toString() : ""));
		}
		String lastStringResult = lastMatchEvalResult != null
				? lastMatchEvalResult.toString() : "";
		boolean isAppendTail = matchCount == 1
				&& endOffset < strValue.length();
		Object evaluationResult = null;
		if (matchCount > 1 || isAppendTail) {
			exprMatcher.appendTail(sb);
			evaluationResult = sb.toString();
		} else if (matchCount == 1) {
			if (sb.length() > lastStringResult.length()) {
				evaluationResult = sb.toString();
			} else {
				evaluationResult = lastMatchEvalResult;
			}
		} else if (matchCount == 0) {
			evaluationResult = strValue;
		}
		return evaluationResult;
	}

	/**
	 * Transform to collection object.
	 *
	 * @param engine
	 *            the engine
	 * @param collectionName
	 *            the collection name
	 * @param context
	 *            the context
	 * @return the collection
	 */
	@SuppressWarnings("rawtypes")
	public static Collection transformToCollectionObject(
			final ExpressionEngine engine, final String collectionName,
			final Map<String, Object> context) {
		Object collectionObject = engine.evaluate(collectionName, context);
		if (!(collectionObject instanceof Collection)) {
			throw new EvaluationException(
					collectionName + " expression is not a collection");
		}
		return (Collection) collectionObject;
	}

	/**
	 * Checks if is condition true.
	 *
	 * @param engine
	 *            the engine
	 * @param context
	 *            the context
	 * @return the boolean
	 */
	public static Boolean isConditionTrue(final ExpressionEngine engine,
			final Map<String, Object> context) {
		Object conditionResult = engine.evaluate(context);
		if (!(conditionResult instanceof Boolean)) {
			throw new EvaluationException(
					"Condition result is not a boolean value - "
							+ engine.getJexlExpression().getExpression());
		}
		return (Boolean) conditionResult;
	}

	/**
	 * Checks if is row allow add.
	 *
	 * @param row
	 *            the row
	 * @param sheetConfig
	 *            the sheet config
	 * @return true, if is row allow add
	 */
	public static boolean isRowAllowAdd(final Row row,
			final SheetConfiguration sheetConfig) {
		String fullName = getFullNameFromRow(row);
		if (fullName != null) {
			ConfigRangeAttrs attrs = sheetConfig.getShiftMap()
					.get(fullName);
			if ((attrs != null) && (attrs.isAllowAdd()) && (row
					.getRowNum() == attrs.getFirstRowRef().getRowIndex())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the full name from row.
	 *
	 * @param row
	 *            the row
	 * @return the full name from row
	 */
	public static String getFullNameFromRow(final Row row) {
		if (row != null) {
			Cell cell = row.getCell(TieConstants.HIDDEN_FULL_NAME_COLUMN);
			if (cell != null) {
				return cell.getStringCellValue();
			}
		}
		return null;
	}

	/**
	 * Adds the row.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param rowIndex
	 *            the row index
	 * @param dataContext
	 *            the data context
	 * @return the int
	 */
	@SuppressWarnings({ "rawtypes" })
	public static int addRow(final ConfigBuildRef configBuildRef,
			final int rowIndex, final Map<String, Object> dataContext) {
		String fullName = getFullNameFromRow(
				configBuildRef.getSheet().getRow(rowIndex));
		if (fullName == null) {
			return -1;
		}
		String[] parts = fullName.split(":");
		if (parts == null) {
			return -1;
		}

		Collection lastCollection = null;
		int lastCollectionIndex = -1;
		EachCommand eachCommand = null;
		// replace the lastCollection.
		// since here's add one row.
		// Then we should insert one empty object in the list.
		// The collection must be a list to support add/delete function.
		// and the object must support empty constructor.
		try {
			// prepare collection data in context.
			// must loop through the full name which may have multiple
			// layer.
			// i.e. E.department.1:E.employee.0
			// need prepare department.1 and employee.0
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				if (part.startsWith(
						TieConstants.EACH_COMMAND_FULL_NAME_PREFIX)) {
					String[] varparts = part.split("\\.");
					eachCommand = getEachCommandFromPartsName(
							configBuildRef, varparts);
					lastCollection = transformToCollectionObject(
							configBuildRef.getEngine(),
							eachCommand.getItems(), dataContext);
					lastCollectionIndex = prepareCollectionDataInContext(
							varparts, eachCommand, lastCollection,
							dataContext);
				}
			}
			if (lastCollectionIndex < 0) {
				// no each command in the loop.
				return 0;
			}

			String unitFullName = insertEmptyObjectInContext(fullName,
					lastCollection, eachCommand, lastCollectionIndex,
					dataContext);
			RowsMapping unitRowsMapping = new RowsMapping();
			ConfigRangeAttrs savedRangeAttrs = configBuildRef.getShiftMap()
					.get(fullName);
			int insertPosition = savedRangeAttrs.getFirstRowRef()
					.getRowIndex() + savedRangeAttrs.getFinalLength();
			configBuildRef.setInsertPosition(insertPosition);
			insertEachTemplate(eachCommand.getConfigRange(), configBuildRef,
					lastCollectionIndex + 1, insertPosition,
					unitRowsMapping);
			ConfigRange currentRange = buildCurrentRange(
					eachCommand.getConfigRange(), configBuildRef.getSheet(),
					insertPosition);
			List<RowsMapping> currentRowsMappingList = findParentRowsMappingFromShiftMap(
					parts, configBuildRef.getShiftMap());
			currentRowsMappingList.add(unitRowsMapping);
			currentRange.getAttrs().setAllowAdd(true);
			configBuildRef.setBodyAllowAdd(true);
			// reverse order of changeMap.
			Map<String, String> changeMap = new TreeMap<>(
					Collections.reverseOrder());
			increaseIndexNumberInHiddenColumn(
					configBuildRef, currentRange.getAttrs()
							.getLastRowPlusRef().getRowIndex(),
					fullName, changeMap);
			increaseIndexNumberInShiftMap(configBuildRef.getShiftMap(),
					changeMap);
			configBuildRef.putShiftAttrs(unitFullName,
					currentRange.getAttrs(), unitRowsMapping);
			int length = currentRange.buildAt(unitFullName, configBuildRef,
					insertPosition, dataContext, currentRowsMappingList);
			currentRange.getAttrs().setFinalLength(length);

			reBuildUpperLevelFormula(configBuildRef, fullName);
			increaseUpperLevelFinalLength(configBuildRef.getShiftMap(),
					fullName, length);
			insertPosition += length;
			currentRowsMappingList.remove(unitRowsMapping);
			dataContext.remove(eachCommand.getVar());

			return length;

		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Add row error=" + ex.getMessage(), ex);
			return -1;
		}

	}

	/**
	 * Insert empty object in context.
	 *
	 * @param fullName
	 *            the full name
	 * @param lastCollection
	 *            the last collection
	 * @param eachCommand
	 *            the each command
	 * @param lastCollectionIndex
	 *            the last collection index
	 * @param dataContext
	 *            the data context
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String insertEmptyObjectInContext(final String fullName,
			final Collection lastCollection, final EachCommand eachCommand,
			final int lastCollectionIndex,
			final Map<String, Object> dataContext) throws Exception {
		if (!(lastCollection instanceof List)) {
			throw new EvaluationException(eachCommand.getVar()
					+ TieConstants.EACH_COMMAND_INVALID_MSG);
		}
		List collectionList = (List) lastCollection;
		// the object must support empty constructor.
		Object currentObj = collectionList.get(lastCollectionIndex);
		Object insertObj = currentObj.getClass().newInstance();
		collectionList.add(lastCollectionIndex + 1, insertObj);
		dataContext.put(eachCommand.getVar(), insertObj);
		return fullName.substring(0, fullName.lastIndexOf('.') + 1)
				+ (lastCollectionIndex + 1);

	}

	/**
	 * Gets the each command from parts name.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param varparts
	 *            the varparts
	 * @return the each command from parts name
	 */
	private static EachCommand getEachCommandFromPartsName(
			final ConfigBuildRef configBuildRef, final String[] varparts) {
		if (varparts.length == TieConstants.DEFAULT_COMMAND_PART_LENGTH) {
			return (EachCommand) configBuildRef.getCommandIndexMap()
					.get(TieConstants.EACH_COMMAND_FULL_NAME_PREFIX
							+ varparts[1]);
		}
		return null;

	}

	/**
	 * Prepare collection data in context.
	 *
	 * @param varparts
	 *            the varparts
	 * @param eachCommand
	 *            the each command
	 * @param collection
	 *            the collection
	 * @param dataContext
	 *            the data context
	 * @return the int
	 */
	@SuppressWarnings("rawtypes")
	private static int prepareCollectionDataInContext(
			final String[] varparts, final EachCommand eachCommand,
			final Collection collection, final Map<String, Object> dataContext) {
		if (varparts.length == TieConstants.DEFAULT_COMMAND_PART_LENGTH) {
			int collectionIndex = Integer.parseInt(varparts[2]);
			Object obj = findItemInCollection(collection, collectionIndex);
			if (obj != null) {
				dataContext.put(varparts[1], obj);
				return collectionIndex;
			}
		}
		return -1;
	}

	/**
	 * Re build upper level formula.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param addFullName
	 *            the add full name
	 */
	public static void reBuildUpperLevelFormula(
			final ConfigBuildRef configBuildRef, final String addFullName) {
		Map<Cell, String> cachedMap = configBuildRef.getCachedCells();
		Map<String, List<RowsMapping>> rowsMap = new HashMap<>();
		for (Map.Entry<Cell, String> entry : cachedMap.entrySet()) {
			Cell cell = entry.getKey();
			String originFormula = entry.getValue();
			if (originFormula != null) {
				String fullName = getFullNameFromRow(cell.getRow());
				// it's upper level
				if (addFullName.startsWith(fullName + ":")) {
					List<RowsMapping> currentRowsMappingList = rowsMap
							.get(fullName);
					if (currentRowsMappingList == null) {
						currentRowsMappingList = gatherRowsMappingByFullName(
								configBuildRef, fullName);
						rowsMap.put(fullName, currentRowsMappingList);
					}
					ShiftFormulaRef shiftFormulaRef = new ShiftFormulaRef(
							configBuildRef.getWatchList(),
							currentRowsMappingList);
					shiftFormulaRef.setFormulaChanged(0);
					buildCellFormulaForShiftedRows(
							configBuildRef.getSheet(),
							configBuildRef.getWbWrapper(), shiftFormulaRef,
							cell, originFormula);
					if (shiftFormulaRef.getFormulaChanged() > 0) {
						configBuildRef.getCachedCells().put(cell,
								originFormula);
					}

				}
			}
		}

	}

	/**
	 * Builds the cell formula for shifted rows.
	 *
	 * @param sheet
	 *            the sheet
	 * @param wbWrapper
	 *            the wb wrapper
	 * @param shiftFormulaRef
	 *            the shift formula ref
	 * @param cell
	 *            the cell
	 * @param originFormula
	 *            the origin formula
	 */
	public static void buildCellFormulaForShiftedRows(final Sheet sheet,
			final XSSFEvaluationWorkbook wbWrapper,
			final ShiftFormulaRef shiftFormulaRef, final Cell cell,
			final String originFormula) {
		// only shift when there's watchlist exist.
		if ((shiftFormulaRef.getWatchList() != null)
				&& (shiftFormulaRef.getWatchList().size() > 0)) {
			Ptg[] ptgs = FormulaParser.parse(originFormula, wbWrapper,
					FormulaType.CELL,
					sheet.getWorkbook().getSheetIndex(sheet));
			Ptg[] convertedFormulaPtg = ShiftFormulaUtility
					.convertSharedFormulas(ptgs, shiftFormulaRef);
			if (shiftFormulaRef.getFormulaChanged() > 0) {
				// only change formula when indicator is true
				cell.setCellFormula(FormulaRenderer
						.toFormulaString(wbWrapper, convertedFormulaPtg));

			}
		}
	}

	/**
	 * Gather rows mapping by full name.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param fullName
	 *            the full name
	 * @return the list
	 */
	public static List<RowsMapping> gatherRowsMappingByFullName(
			final ConfigBuildRef configBuildRef, final String fullName) {
		List<RowsMapping> list = new ArrayList<>();
		Map<String, ConfigRangeAttrs> shiftMap = configBuildRef
				.getShiftMap();
		for (Map.Entry<String, ConfigRangeAttrs> entry : shiftMap
				.entrySet()) {
			String fname = entry.getKey();
			if (fname.startsWith(fullName + ":")
					|| fname.equals(fullName)) {
				ConfigRangeAttrs attrs = entry.getValue();
				list.add(attrs.getUnitRowsMapping());
			}
		}
		return list;
	}

	/**
	 * Increase index number in shift map.
	 *
	 * @param shiftMap
	 *            the shift map
	 * @param changeMap
	 *            the change map
	 */
	public static void increaseIndexNumberInShiftMap(
			final Map<String, ConfigRangeAttrs> shiftMap,
			final Map<String, String> changeMap) {
		for (Map.Entry<String, String> entry : changeMap.entrySet()) {
			String key = entry.getKey();
			String newKey = entry.getValue();
			ConfigRangeAttrs attrs = shiftMap.get(key);
			if (attrs != null) {
				shiftMap.remove(key);
				shiftMap.put(newKey, attrs);
			}
		}
	}

	/**
	 * Increase upper level final length.
	 *
	 * @param shiftMap
	 *            the shift map
	 * @param addedFullName
	 *            the added full name
	 * @param increasedLength
	 *            the increased length
	 */
	public static void increaseUpperLevelFinalLength(
			final Map<String, ConfigRangeAttrs> shiftMap,
			final String addedFullName, final int increasedLength) {
		String[] parts = addedFullName.split(":");
		String fname = null;
		for (int i = 0; i < (parts.length - 1); i++) {
			if (i == 0) {
				fname = parts[i];
			} else {
				fname = fname + ":" + parts[i];
			}
			shiftMap.get(fname).setFinalLength(
					shiftMap.get(fname).getFinalLength() + increasedLength);
		}
	}

	/**
	 * Increase index number in hidden column.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param startRowIndex
	 *            the start row index
	 * @param fullName
	 *            the full name
	 * @param changeMap
	 *            the change map
	 */
	public static void increaseIndexNumberInHiddenColumn(
			final ConfigBuildRef configBuildRef, final int startRowIndex,
			final String fullName, final Map<String, String> changeMap) {
		String searchName = fullName.substring(0,
				fullName.lastIndexOf('.') + 1);
		Sheet sheet = configBuildRef.getSheet();
		for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String fname = getFullNameFromRow(row);
			int sindex = fname.indexOf(searchName);
			// no search found, then no need to change.
			if (sindex < 0) {
				break;
			}
			String snum = fname.substring(sindex + searchName.length());
			int sufindex = snum.indexOf(':');
			String suffix = "";
			if (sufindex > 0) {
				snum = snum.substring(0, sufindex);
				suffix = ":";
			}
			int increaseNum = Integer.parseInt(snum) + 1;
			String realFullName = fname.substring(sindex);
			String changeName = fname.replace(searchName + snum + suffix,
					searchName + increaseNum + suffix);
			if (changeMap.get(realFullName) == null) {
				changeMap.put(realFullName, changeName.substring(sindex));
			}
			setFullNameInHiddenColumn(row, changeName);
		}
	}

	/**
	 * Sets the full name in hidden column.
	 *
	 * @param row
	 *            the row
	 * @param fullName
	 *            the full name
	 */
	public static void setFullNameInHiddenColumn(final Row row,
			final String fullName) {
		Cell cell = row.getCell(TieConstants.HIDDEN_FULL_NAME_COLUMN,
				MissingCellPolicy.CREATE_NULL_AS_BLANK);

		cell.setCellValue(fullName);
	}

	/**
	 * Sets the save objects in hidden column.
	 *
	 * @param row
	 *            the row
	 * @param saveAttr
	 *            the save attr
	 */
	public static void setSaveObjectsInHiddenColumn(final Row row,
			final String saveAttr) {
		Cell cell = row.getCell(TieConstants.HIDDEN_SAVE_OBJECTS_COLUMN,
				MissingCellPolicy.CREATE_NULL_AS_BLANK);

		cell.setCellValue(saveAttr);
	}

	/**
	 * Sets the save attrs for sheet.
	 *
	 * @param sheet
	 *            the sheet
	 * @param minRowNum
	 *            the min row num
	 * @param maxRowNum
	 *            the max row num
	 */
	public static void setSaveAttrsForSheet(final Sheet sheet,
			final int minRowNum, final int maxRowNum) {

		for (Row row : sheet) {
			int rowIndex = row.getRowNum();
			if ((rowIndex >= minRowNum) && (rowIndex <= maxRowNum)) {
				StringBuilder saveAttr = new StringBuilder();
				for (Cell cell : row) {
					String sAttr = ConfigurationHelper.parseSaveAttr(cell);
					if (!sAttr.isEmpty()) {
						saveAttr.append(sAttr);
					}
				}
				if (saveAttr.length() > 0) {
					ConfigurationHelper.setSaveObjectsInHiddenColumn(row,
							saveAttr.toString());
				}
			}
		}
	}

	/**
	 * Gets the original row num in hidden column.
	 *
	 * @param row
	 *            the row
	 * @return the original row num in hidden column
	 */
	public static int getOriginalRowNumInHiddenColumn(final Row row) {
		if (row != null) {
			Cell cell = row.getCell(
					TieConstants.HIDDEN_ORIGIN_ROW_NUMBER_COLUMN,
					MissingCellPolicy.CREATE_NULL_AS_BLANK);
			String rowNum = cell.getStringCellValue();
			try {
				if ((rowNum != null) && (!rowNum.isEmpty())) {
					return Integer.parseInt(rowNum);
				}

			} catch (Exception ex) {
				LOG.log(Level.SEVERE,
						"getOriginalRowNumInHiddenColumn rowNum = " + rowNum
								+ " error = " + ex.getLocalizedMessage(),
						ex);

			}
		}
		return -1;
	}

	/**
	 * Sets the original row num in hidden column.
	 *
	 * @param row
	 *            the row.
	 * @param rowNum
	 *            row number.
	 */
	public static void setOriginalRowNumInHiddenColumn(final Row row,
			final int rowNum) {
		Cell cell = row.getCell(
				TieConstants.HIDDEN_ORIGIN_ROW_NUMBER_COLUMN,
				MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cell.setCellValue(rowNum + "");
		cell.setCellType(CellType.STRING);
	}

	/**
	 * Find parent rows mapping from shift map.
	 *
	 * @param parts
	 *            the parts
	 * @param shiftMap
	 *            the shift map
	 * @return the list
	 */
	public static List<RowsMapping> findParentRowsMappingFromShiftMap(
			final String[] parts,
			final Map<String, ConfigRangeAttrs> shiftMap) {

		StringBuilder fullName = new StringBuilder();
		List<RowsMapping> rowsMappingList = new ArrayList<>();
		/**
		 * skip first one and last one. first one is line no. last one is it's
		 * self.
		 */
		for (int i = 1; i < parts.length - 1; i++) {
			String part = parts[i];
			if (fullName.length() == 0) {
				fullName.append(part);
			} else {
				fullName.append(":" + part);
			}
			if (fullName.length() > 0) {
				ConfigRangeAttrs rangeAttrs = shiftMap
						.get(fullName.toString());
				if (rangeAttrs != null) {
					rowsMappingList.add(rangeAttrs.getUnitRowsMapping());
				}
			}
		}
		return rowsMappingList;
	}

	/**
	 * Find child rows mapping from shift map.
	 *
	 * @param fullName
	 *            the full name
	 * @param shiftMap
	 *            the shift map
	 * @return the list
	 */
	public static List<RowsMapping> findChildRowsMappingFromShiftMap(
			final String fullName,
			final TreeMap<String, ConfigRangeAttrs> shiftMap) {

		List<RowsMapping> rowsMappingList = new ArrayList<>();
		NavigableMap<String, ConfigRangeAttrs> tailmap = shiftMap
				.tailMap(fullName, false);
		for (Map.Entry<String, ConfigRangeAttrs> entry : tailmap
				.entrySet()) {
			String key = entry.getKey();
			// check it's children
			if (key.startsWith(fullName)) {
				rowsMappingList.add(entry.getValue().getUnitRowsMapping());
			} else {
				break;
			}
		}
		return rowsMappingList;
	}

	/**
	 * Find item in collection.
	 *
	 * @param collection
	 *            the collection
	 * @param index
	 *            the index
	 * @return the object
	 */
	@SuppressWarnings("rawtypes")
	public static Object findItemInCollection(final Collection collection,
			final int index) {
		if (index >= 0) {
			if (collection instanceof List) {
				List list = (List) collection;
				return list.get(index);
			}
			int i = 0;
			for (Object object : collection) {
				if (i == index) {
					return object;
				}
				i++;
			}

		}
		return null;
	}

	/**
	 * Insert each template.
	 *
	 * @param sourceConfigRange
	 *            the source config range
	 * @param configBuildRef
	 *            the config build ref
	 * @param index
	 *            the index
	 * @param insertPosition
	 *            the insert position
	 * @param unitRowsMapping
	 *            the unit rows mapping
	 */
	public static void insertEachTemplate(
			final ConfigRange sourceConfigRange,
			final ConfigBuildRef configBuildRef, final int index,
			final int insertPosition, final RowsMapping unitRowsMapping) {
		int srcStartRow = sourceConfigRange.getFirstRowAddr().getRow();
		int srcEndRow = sourceConfigRange.getLastRowPlusAddr().getRow() - 1;

		Sheet sheet = configBuildRef.getSheet();
		Workbook wb = sheet.getWorkbook();
		// excel sheet name has limit 31 chars
		String copyName = TieConstants.COPY_SHEET_PREFIX
				+ sheet.getSheetName();
		if (copyName.length() > TieConstants.EXCEL_SHEET_NAME_LIMIT) {
			copyName = copyName.substring(0,
					TieConstants.EXCEL_SHEET_NAME_LIMIT);
		}
		Sheet srcSheet = wb.getSheet(copyName);
		if (index > 0) {
			CellUtility.copyRows(srcSheet, sheet, srcStartRow,
					srcEndRow, insertPosition, false, true);
		}

		for (int rowIndex = srcStartRow; rowIndex <= srcEndRow; rowIndex++) {
			if (configBuildRef.getWatchList().contains(rowIndex)
					&& (isStaticRow(sourceConfigRange, rowIndex))) {
				unitRowsMapping.addRow(rowIndex, sheet
						.getRow(insertPosition + rowIndex - srcStartRow));
			}
		}
	}

	/**
	 * Builds the current range.
	 *
	 * @param sourceConfigRange
	 *            the source config range
	 * @param sheet
	 *            the sheet
	 * @param insertPosition
	 *            the insert position
	 * @return the config range
	 */
	public static ConfigRange buildCurrentRange(
			final ConfigRange sourceConfigRange, final Sheet sheet,
			final int insertPosition) {
		ConfigRange current = new ConfigRange(sourceConfigRange);
		int shiftNum = insertPosition
				- sourceConfigRange.getFirstRowAddr().getRow();
		current.shiftRowRef(sheet, shiftNum);
		return current;
	}

	/**
	 * Whether the row is static. This only check rowIndex against original
	 * template.
	 *
	 * @param sourceConfigRange
	 *            the source config range
	 * @param rowIndex
	 *            the row index
	 * @return true is static false is not.
	 */
	public static boolean isStaticRow(final ConfigRange sourceConfigRange,
			final int rowIndex) {
		if (sourceConfigRange.getCommandList() != null) {
			for (int i = 0; i < sourceConfigRange.getCommandList()
					.size(); i++) {
				Command command = sourceConfigRange.getCommandList().get(i);
				if ((rowIndex >= command.getConfigRange().getFirstRowAddr()
						.getRow())
						&& (rowIndex < (command.getConfigRange()
								.getLastRowPlusAddr().getRow()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Whether the row is static.This check row after shifted.
	 *
	 * @param sourceConfigRange
	 *            the source config range
	 * @param row
	 *            the row for check.
	 * @return true is static false is not.
	 */
	public static boolean isStaticRowRef(
			final ConfigRange sourceConfigRange, final Row row) {
		if (sourceConfigRange.getCommandList() != null) {
			for (int i = 0; i < sourceConfigRange.getCommandList()
					.size(); i++) {
				Command command = sourceConfigRange.getCommandList().get(i);
				int rowIndex = row.getRowNum();
				if ((rowIndex >= command.getTopRow())
						&& (rowIndex < (command.getTopRow()
								+ command.getFinalLength()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Index command range.
	 *
	 * @param sourceConfigRange
	 *            the source config range
	 * @param indexMap
	 *            the index map
	 */
	public static void indexCommandRange(
			final ConfigRange sourceConfigRange,
			final Map<String, Command> indexMap) {
		if (sourceConfigRange.getCommandList() != null) {
			for (int i = 0; i < sourceConfigRange.getCommandList()
					.size(); i++) {
				// cellRange.resetChangeMatrix();
				Command command = sourceConfigRange.getCommandList().get(i);
				indexMap.put(command.getCommandName(), command);
				command.getConfigRange().indexCommandRange(indexMap);
			}
		}

	}

	/**
	 * Creates the cell comment.
	 *
	 * @param cell
	 *            the cell
	 * @param newComment
	 *            the new comment
	 * @param finalCommentMap
	 *            the final comment map
	 */
	public static void createCellComment(final Cell cell,
			final String newComment,
			final Map<Cell, String> finalCommentMap) {
		// due to poi's bug. the comment must be set in sorted order ( row first
		// then column),
		// otherwise poi will mess up.
		// workaround solution is to save all comments into a map,
		// and output them together when download workbook.

		if (newComment != null) {
			finalCommentMap.put(cell, newComment);

		}
	}

}
