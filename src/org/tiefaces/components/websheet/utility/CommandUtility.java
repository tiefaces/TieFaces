/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.utility;

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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.configuration.Command;
import org.tiefaces.components.websheet.configuration.ConfigBuildRef;
import org.tiefaces.components.websheet.configuration.ConfigRange;
import org.tiefaces.components.websheet.configuration.ConfigRangeAttrs;
import org.tiefaces.components.websheet.configuration.EachCommand;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;
import org.tiefaces.components.websheet.configuration.RowsMapping;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CollectionObject;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.TieCell;
import org.tiefaces.exception.AddRowException;
import org.tiefaces.exception.DeleteRowException;
import org.tiefaces.exception.EvaluationException;

/**
 * Helper class for command.
 */
public final class CommandUtility {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(CommandUtility.class.getName());

	/**
	 * hide constructor.
	 */
	private CommandUtility() {
		// not called
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

		// replace the lastCollection.
		// since here's add one row.
		// Then we should insert one empty object in the list.
		// The collection must be a list to support add/delete function.
		// and the object must support empty constructor.

		String fullName = ConfigurationUtility.getFullNameFromRow(
				configBuildRef.getSheet().getRow(rowIndex));
		String[] parts = fullName.split(":");
		configBuildRef.getCellHelper().restoreDataContext(fullName);
		CollectionObject collect = configBuildRef.getCellHelper()
				.getLastCollect(fullName);

		Collection lastCollection = collect.getLastCollection();
		int lastCollectionIndex = collect.getLastCollectionIndex();
		EachCommand eachCommand = collect.getEachCommand();
		if (lastCollectionIndex < 0) {
			// no each command in the loop.
			throw new AddRowException("No each command found.");
		}
		String unitFullName = CommandUtility.insertEmptyObjectInContext(
				fullName, lastCollection, eachCommand, lastCollectionIndex,
				dataContext);
		RowsMapping unitRowsMapping = new RowsMapping();
		ConfigRangeAttrs savedRangeAttrs = configBuildRef.getShiftMap()
				.get(fullName);
		int insertPosition = savedRangeAttrs.getFirstRowRef().getRowIndex()
				+ savedRangeAttrs.getFinalLength();
		configBuildRef.setInsertPosition(insertPosition);
		CommandUtility.insertEachTemplate(eachCommand.getConfigRange(),
				configBuildRef, lastCollectionIndex + 1, insertPosition,
				unitRowsMapping);
		ConfigRange currentRange = ConfigurationUtility.buildCurrentRange(
				eachCommand.getConfigRange(), configBuildRef.getSheet(),
				insertPosition);
		List<RowsMapping> currentRowsMappingList = ConfigurationUtility
				.findParentRowsMappingFromShiftMap(parts,
						configBuildRef.getShiftMap());
		currentRowsMappingList.add(unitRowsMapping);
		currentRange.getAttrs().setAllowAdd(true);
		configBuildRef.setBodyAllowAdd(true);
		// reverse order of changeMap.
		Map<String, String> changeMap = new TreeMap<>(
				Collections.reverseOrder());
		ConfigurationUtility.changeIndexNumberInHiddenColumn(configBuildRef,
				currentRange.getAttrs().getLastRowPlusRef().getRowIndex(),
				fullName, changeMap, 1);
		ConfigurationUtility.changeIndexNumberInShiftMap(
				configBuildRef.getShiftMap(), changeMap);
		configBuildRef.putShiftAttrs(unitFullName, currentRange.getAttrs(),
				unitRowsMapping);
		int length = currentRange.buildAt(unitFullName, configBuildRef,
				insertPosition, dataContext, currentRowsMappingList);
		currentRange.getAttrs().setFinalLength(length);

		ConfigurationUtility.reBuildUpperLevelFormula(configBuildRef,
				fullName);
		ConfigurationUtility.changeUpperLevelFinalLength(
				configBuildRef.getShiftMap(), fullName, length);
		currentRowsMappingList.remove(unitRowsMapping);
		dataContext.remove(eachCommand.getVar());

		return length;

	}

	/**
	 * Delete row.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param rowIndex
	 *            the row index
	 * @param dataContext
	 *            the data context
	 * @param sheetConfig
	 *            the sheet config
	 * @param bodyRows
	 *            the body rows
	 * @return the int
	 * @throws DeleteRowException
	 *             the delete row exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public static int deleteRow(final ConfigBuildRef configBuildRef,
			final int rowIndex, final Map<String, Object> dataContext,
			final SheetConfiguration sheetConfig,
			final List<FacesRow> bodyRows) {

		String fullName = ConfigurationUtility.getFullNameFromRow(
				configBuildRef.getSheet().getRow(rowIndex));

		configBuildRef.getCellHelper().restoreDataContext(fullName);
		CollectionObject collect = configBuildRef.getCellHelper()
				.getLastCollect(fullName);

		Collection lastCollection = collect.getLastCollection();
		int lastCollectionIndex = collect.getLastCollectionIndex();
		EachCommand eachCommand = collect.getEachCommand();
		if (lastCollectionIndex < 0) {
			// no each command in the loop.
			throw new DeleteRowException("No each command found.");
		}
		if (lastCollection.size() <= 1) {
			// this is the last record and no parent left.
			throw new DeleteRowException(
					"Cannot delete the last record in the group.");
		}

		CommandUtility.deleteObjectInContext(lastCollection, eachCommand,
				lastCollectionIndex, dataContext);

		// find range from shiftmap.
		ConfigRangeAttrs currentRangeAttrs = configBuildRef.getShiftMap()
				.get(fullName);
		if (currentRangeAttrs == null) {
			throw new DeleteRowException("Cannot find delete range.");

		}

		// The lastRowRef is wrong in rangeAttrs. So use length to recalc it.
		int startRow = currentRangeAttrs.getFirstRowIndex();
		int length = currentRangeAttrs.getFinalLength();
		int endRow = startRow + length - 1;

		List<String> removeFullNameList = findRemoveFullNameList(
				configBuildRef.getSheet(), startRow, endRow);
		// remove range from shiftmap.
		removeRangesFromShiftMap(configBuildRef.getShiftMap(),
				removeFullNameList);
		// 1. remove ranged rows from sheet
		String var = eachCommand.getVar();
		CommandUtility.removeRowsInSheet(configBuildRef.getSheet(),
				startRow, endRow, configBuildRef.getCachedCells());
		// 2. reset FacesRow row index.
		CommandUtility.removeRowsInBody(sheetConfig, bodyRows, startRow,
				endRow);
		// 3. decrease index number in hidden column
		Map<String, String> changeMap = new TreeMap<>();
		ConfigurationUtility.changeIndexNumberInHiddenColumn(configBuildRef,
				startRow, fullName, changeMap, -1);
		// 4. decrease index number in shift map
		ConfigurationUtility.changeIndexNumberInShiftMap(
				configBuildRef.getShiftMap(), changeMap);
		// 5. rebuild upper level formula
		ConfigurationUtility.reBuildUpperLevelFormula(configBuildRef,
				fullName);
		// 6. decrease upper level final length
		ConfigurationUtility.changeUpperLevelFinalLength(
				configBuildRef.getShiftMap(), fullName, -length);

		dataContext.remove(var);

		return length;

	}

	/**
	 * Removes the ranges from shift map.
	 *
	 * @param shiftMap
	 *            the shift map
	 * @param removeFullNameList
	 *            the remove full name list
	 */
	private static void removeRangesFromShiftMap(
			final NavigableMap<String, ConfigRangeAttrs> shiftMap,
			final List<String> removeFullNameList) {
		for (String fname : removeFullNameList) {
			shiftMap.remove(fname);
		}

	}

	/**
	 * Find remove full name list.
	 *
	 * @param sheet
	 *            the sheet
	 * @param startRow
	 *            the start row
	 * @param endRow
	 *            the end row
	 * @return the list
	 */
	private static List<String> findRemoveFullNameList(final Sheet sheet,
			final int startRow, final int endRow) {

		List<String> list = new ArrayList<>();

		for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
			String fullName = ConfigurationUtility
					.getFullNameFromRow(sheet.getRow(rowIndex));
			if (!list.contains(fullName)) {
				list.add(fullName);
			}
		}

		return list;
	}

	/**
	 * Gets the each command from parts name.
	 *
	 * @param commandIndexMap
	 *            the command index map
	 * @param varparts
	 *            the varparts
	 * @return the each command from parts name
	 */
	public static EachCommand getEachCommandFromPartsName(
			final Map<String, Command> commandIndexMap,
			final String[] varparts) {
		if (varparts.length == TieConstants.DEFAULT_COMMAND_PART_LENGTH) {
			return (EachCommand) commandIndexMap
					.get(TieConstants.EACH_COMMAND_FULL_NAME_PREFIX
							+ varparts[1]);
		}
		return null;

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
	 * @throws EvaluationException
	 *             the evaluation exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String insertEmptyObjectInContext(final String fullName,
			final Collection lastCollection, final EachCommand eachCommand,
			final int lastCollectionIndex,
			final Map<String, Object> dataContext) {
		if (!(lastCollection instanceof List)) {
			throw new EvaluationException(
					"Collection must be list in order to insert/delete.");
		}
		List collectionList = (List) lastCollection;
		// the object must support empty constructor.
		Object currentObj = collectionList.get(lastCollectionIndex);
		Object insertObj;
		try {
			insertObj = currentObj.getClass().newInstance();
			collectionList.add(lastCollectionIndex + 1, insertObj);
			dataContext.put(eachCommand.getVar(), insertObj);
			return fullName.substring(0, fullName.lastIndexOf('.') + 1)
					+ (lastCollectionIndex + 1);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EvaluationException(e);

		}

	}

	/**
	 * Delete object in context.
	 *
	 * @param lastCollection
	 *            the last collection
	 * @param eachCommand
	 *            the each command
	 * @param lastCollectionIndex
	 *            the last collection index
	 * @param dataContext
	 *            the data context
	 */
	@SuppressWarnings({ "rawtypes" })
	private static void deleteObjectInContext(
			final Collection lastCollection, final EachCommand eachCommand,
			final int lastCollectionIndex,
			final Map<String, Object> dataContext) {
		if (!(lastCollection instanceof List)) {
			throw new EvaluationException(eachCommand.getVar()
					+ TieConstants.EACH_COMMAND_INVALID_MSG);
		}
		List collectionList = (List) lastCollection;
		// the object must support empty constructor.

		collectionList.remove(lastCollectionIndex);
		dataContext.remove(eachCommand.getVar());

	}

	/**
	 * Prepare collection data in context.
	 *
	 * @param varparts
	 *            the varparts
	 * @param collection
	 *            the collection
	 * @param dataContext
	 *            the data context
	 * @return the int
	 */
	@SuppressWarnings("rawtypes")
	public static int prepareCollectionDataInContext(
			final String[] varparts, final Collection collection,
			final Map<String, Object> dataContext) {
		if (varparts.length == TieConstants.DEFAULT_COMMAND_PART_LENGTH) {
			int collectionIndex = Integer.parseInt(varparts[2]);
			Object obj = ConfigurationUtility
					.findItemInCollection(collection, collectionIndex);
			if (obj != null) {
				dataContext.put(varparts[1], obj);
				return collectionIndex;
			}
		}
		return -1;
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
				Command command = sourceConfigRange.getCommandList().get(i);
				indexMap.put(command.getCommandName(), command);
				command.getConfigRange().indexCommandRange(indexMap);
			}
		}

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
		String fullName = ConfigurationUtility.getFullNameFromRow(row);
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
			CellUtility.copyRows(srcSheet, sheet, srcStartRow, srcEndRow,
					insertPosition, false, true);
		}

		for (int rowIndex = srcStartRow; rowIndex <= srcEndRow; rowIndex++) {
			if (configBuildRef.getWatchList().contains(rowIndex)
					&& (ConfigurationUtility.isStaticRow(sourceConfigRange,
							rowIndex))) {
				unitRowsMapping.addRow(rowIndex, sheet
						.getRow(insertPosition + rowIndex - srcStartRow));
			}
		}
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
	@SuppressWarnings("deprecation")
	public static void evaluate(final Map<String, Object> context,
			final Cell cell, final ExpressionEngine engine) {
		if ((cell != null) && (cell.getCellTypeEnum() == CellType.STRING)) {
			String strValue = cell.getStringCellValue();
			if (isUserFormula(strValue)) {
				evaluateUserFormula(cell, strValue);
			} else {
				evaluateNormalCells(cell, strValue, context, engine);
			}
		}
	}

	/**
	 * Evaluate normal cells.
	 *
	 * @param cell
	 *            cell.
	 * @param strValue
	 *            string value.
	 * @param context
	 *            context.
	 * @param engine
	 *            engine.
	 */
	private static void evaluateNormalCells(final Cell cell,
			final String strValue, final Map<String, Object> context,
			final ExpressionEngine engine) {
		if (strValue.contains(TieConstants.METHOD_PREFIX)) {

			Object evaluationResult = evaluate(strValue, context, engine);
			if (evaluationResult == null) {
				evaluationResult = "";
			}
			CellUtility.setCellValue(cell, evaluationResult.toString());

			createTieCell(cell, context, engine);

		}
	}

	private static void createTieCell(final Cell cell, final Map<String, Object> context,
	    final ExpressionEngine engine) {

        	@SuppressWarnings("unchecked")
        	HashMap<String, TieCell> tieCells = (HashMap<String, TieCell>) context.get("tiecells");
        
        	// if tiecells exists is because tieWebSheetBean.isAdvancedContext() is
        	// true
        	if (tieCells != null) {
        
        	    if (SaveAttrsUtility.isHasSaveAttr(cell)) {
        
        		String saveAttrList = SaveAttrsUtility.getSaveAttrListFromRow(cell.getRow());
        
        		if (saveAttrList != null) {
        		    String saveAttr = SaveAttrsUtility.getSaveAttrFromList(cell.getColumnIndex(), saveAttrList);
        		    if (saveAttr != null) {
        
        			int index = saveAttr.lastIndexOf('.');
        			if (index > 0) {
        			    String strObject = saveAttr.substring(0, index);
        			    String strMethod = saveAttr.substring(index + 1);
        			    strObject = "${" + strObject + "}";
        
        			    Object object = CommandUtility.evaluate(strObject, context, engine);
        
        			    if (object != null) {
        				TieCell tieCell = CellUtility.getOrAddTieCellInMap(cell, tieCells);
        				tieCell.setContextObject(object);
        				tieCell.setObjectStr(strObject);
        				tieCell.setMethodStr(strMethod);
        			    }
        
        			}
        
        		    }
        		}
        
        	    }
        
        	}
	}


	/**
	 * Evaluate user formula.
	 *
	 * @param cell
	 *            the cell
	 * @param strValue
	 *            the str value
	 */
	private static void evaluateUserFormula(final Cell cell,
			final String strValue) {
		String formulaStr = strValue.substring(2, strValue.length() - 1);
		if ((formulaStr != null) && (!formulaStr.isEmpty())) {
			cell.setCellFormula(formulaStr);
		}
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

	/**
	 * evaluate boolean express.
	 *
	 * @param expEngine
	 *            expression engine.
	 * @param pscript
	 *            script.
	 * @return true (express is true) false ( express is false or invalid).
	 */
	public static boolean evalBoolExpression(
			final ExpressionEngine expEngine, final String pscript) {
		Object result = null;
		String script = "( " + pscript + " )";
		script = script.toUpperCase().replace("AND", "&&");
		script = script.toUpperCase().replace("OR", "||");
		try {
			result = expEngine.evaluate(script);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"WebForm WebFormHelper evalBoolExpression script = "
							+ script + "; error = "
							+ e.getLocalizedMessage(),
					e);
		}
		if (result != null) {
			return ((Boolean) result).booleanValue();
		} else {
			return false;
		}
	}

	/**
	 * Remove the rows.
	 *
	 * @param sheet
	 *            the sheet
	 * @param rowIndexStart
	 *            start row index.
	 * @param rowIndexEnd
	 *            end row index.
	 * @param cachedMap
	 *            the cached map
	 */
	public static void removeRowsInSheet(final Sheet sheet,
			final int rowIndexStart, final int rowIndexEnd,
			final Map<Cell, String> cachedMap) {

		for (int irow = rowIndexStart; irow <= rowIndexEnd; irow++) {
			removeCachedCellForRow(sheet, irow, cachedMap);
		}
		int irows = rowIndexEnd - rowIndexStart + 1;
		if ((irows < 1) || (rowIndexStart < 0)) {
			return;
		}
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndexEnd < lastRowNum) {
			sheet.shiftRows(rowIndexEnd + 1, lastRowNum, -irows);
		}
		if (rowIndexEnd == lastRowNum) {
			// reverse order to delete rows.
			for (int i = rowIndexEnd; i >= rowIndexStart; i--) {
				removeSingleRowInSheet(sheet, rowIndexStart);
			}
		}
	}

	/**
	 * Removes the single row in sheet.
	 *
	 * @param sheet
	 *            the sheet
	 * @param rowIndexStart
	 *            the row index start
	 */
	private static void removeSingleRowInSheet(final Sheet sheet,
			final int rowIndexStart) {
		Row removingRow = sheet.getRow(rowIndexStart);
		if (removingRow != null) {
			sheet.removeRow(removingRow);
		}
	}

	/**
	 * Removes the cached cell for row.
	 *
	 * @param sheet
	 *            the sheet
	 * @param rowIndexStart
	 *            the row index start
	 * @param cachedMap
	 *            the cached map
	 */
	private static void removeCachedCellForRow(final Sheet sheet,
			final int rowIndexStart, final Map<Cell, String> cachedMap) {
		Row removingRow = sheet.getRow(rowIndexStart);
		if (removingRow != null) {
			// remove cached cell.
			for (Cell cell : removingRow) {
				cachedMap.remove(cell);
			}
		}
	}

	/**
	 * Removes the rows in body.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param bodyRows
	 *            the body rows
	 * @param rowIndexStart
	 *            the row index start
	 * @param rowIndexEnd
	 *            the row index end
	 */
	public static void removeRowsInBody(
			final SheetConfiguration sheetConfig,
			final List<FacesRow> bodyRows, final int rowIndexStart,
			final int rowIndexEnd) {
		int top = sheetConfig.getBodyCellRange().getTopRow();
		if ((rowIndexEnd < rowIndexStart) || (rowIndexStart < top)) {
			return;
		}

		int irows = rowIndexEnd - rowIndexStart + 1;
		for (int rowIndex = rowIndexEnd; rowIndex >= rowIndexStart; rowIndex--) {
			bodyRows.remove(rowIndex - top);
		}
		for (int irow = rowIndexStart - top; irow < bodyRows
				.size(); irow++) {
			FacesRow facesrow = bodyRows.get(irow);
			facesrow.setRowIndex(facesrow.getRowIndex() - irows);
		}
	}
}
