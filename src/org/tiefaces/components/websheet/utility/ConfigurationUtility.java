/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.configuration.Command;
import org.tiefaces.components.websheet.configuration.ConfigBuildRef;
import org.tiefaces.components.websheet.configuration.ConfigRange;
import org.tiefaces.components.websheet.configuration.ConfigRangeAttrs;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;
import org.tiefaces.components.websheet.configuration.RowsMapping;
import org.tiefaces.components.websheet.configuration.ShiftFormulaRef;
import org.tiefaces.exception.EvaluationException;

/**
 * The Class ConfigurationHelper.
 */
public final class ConfigurationUtility {

	/** logger. */
	static final Logger LOG = Logger
			.getLogger(ConfigurationUtility.class.getName());

	/**
	 * hide constructor.
	 */
	private ConfigurationUtility() {
		// not called
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
				setupUpperLevelFormula(cell, originFormula, addFullName,
						rowsMap, configBuildRef);
			}
		}

	}

	/**
	 * @param cell
	 *            cell.
	 * @param originFormula
	 *            originFormula.
	 * @param addFullName
	 *            add full name.
	 * @param rowsMap
	 *            rowsmap.
	 * @param configBuildRef
	 *            config build ref.
	 */
	private static void setupUpperLevelFormula(Cell cell,
			String originFormula, final String addFullName,
			Map<String, List<RowsMapping>> rowsMap,
			final ConfigBuildRef configBuildRef) {
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
					configBuildRef.getWatchList(), currentRowsMappingList);
			shiftFormulaRef.setFormulaChanged(0);
			buildCellFormulaForShiftedRows(configBuildRef.getSheet(),
					configBuildRef.getWbWrapper(), shiftFormulaRef, cell,
					originFormula);
			if (shiftFormulaRef.getFormulaChanged() > 0) {
				configBuildRef.getCachedCells().put(cell, originFormula);
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
				&& (!shiftFormulaRef.getWatchList().isEmpty())) {
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
			if (fname == null) {
				break;
			}
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
		cell.setCellValue(Integer.toString(rowNum));
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
			final NavigableMap<String, ConfigRangeAttrs> shiftMap) {

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

}
