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
import org.apache.poi.ss.util.CellRangeAddress;
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
	 * @param actionFullName
	 *            the action full name
	 */
	public static void reBuildUpperLevelFormula(
			final ConfigBuildRef configBuildRef,
			final String actionFullName) {
		Map<Cell, String> cachedMap = configBuildRef.getCachedCells();
		Map<String, List<RowsMapping>> rowsMap = new HashMap<>();
		for (Map.Entry<Cell, String> entry : cachedMap.entrySet()) {
			Cell cell = entry.getKey();
			String originFormula = entry.getValue();
			if (originFormula != null) {
				setupUpperLevelFormula(cell, originFormula, actionFullName,
						rowsMap, configBuildRef);
			}
		}

	}

	/**
	 * Setup upper level formula.
	 *
	 * @param cell
	 *            cell.
	 * @param originFormula
	 *            originFormula.
	 * @param actionFullName
	 *            add full name.
	 * @param rowsMap
	 *            rowsmap.
	 * @param configBuildRef
	 *            config build ref.
	 */
	private static void setupUpperLevelFormula(final Cell cell,
			final String originFormula, final String actionFullName,
			final Map<String, List<RowsMapping>> rowsMap,
			final ConfigBuildRef configBuildRef) {
		String fullName = getFullNameFromRow(cell.getRow());
		// check wither it's upper level
		if (actionFullName.startsWith(fullName + ":")) {
			// get rows mapping for upper level row
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
	public static void changeIndexNumberInShiftMap(
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
	public static void changeUpperLevelFinalLength(
			final Map<String, ConfigRangeAttrs> shiftMap,
			final String addedFullName, final int increasedLength) {
		String[] parts = addedFullName.split(":");
		StringBuilder fname = new StringBuilder();
		for (int i = 0; i < (parts.length - 1); i++) {
			if (i == 0) {
				fname.append(parts[i]);
			} else {
				fname.append(":").append(parts[i]);
			}
			String sname = fname.toString();
			shiftMap.get(sname).setFinalLength(
					shiftMap.get(sname).getFinalLength()
							+ increasedLength);
		}
	}

	/**
	 * Change index number in hidden column.
	 *
	 * @param configBuildRef
	 *            the config build ref
	 * @param startRowIndex
	 *            the start row index
	 * @param fullName
	 *            the full name
	 * @param changeMap
	 *            the change map
	 * @param steps
	 *            the steps ( 1 add -1 delete ).
	 */
	public static void changeIndexNumberInHiddenColumn(
			final ConfigBuildRef configBuildRef, final int startRowIndex,
			final String fullName, final Map<String, String> changeMap,
			final int steps) {
		String searchName = fullName.substring(0,
				fullName.lastIndexOf('.') + 1);
		Sheet sheet = configBuildRef.getSheet();
		for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String fname = getFullNameFromRow(row);
			if ((fname != null) && (fname.indexOf(searchName) >= 0)) {
				int sindex = fname.indexOf(searchName);
				String snum = fname.substring(sindex + searchName.length());
				int sufindex = snum.indexOf(':');
				String suffix = "";
				if (sufindex > 0) {
					snum = snum.substring(0, sufindex);
					suffix = ":";
				}
				int increaseNum = Integer.parseInt(snum) + steps;
				String realFullName = fname.substring(sindex);
				String changeName = fname.replace(
						searchName + snum + suffix,
						searchName + increaseNum + suffix);
				if (changeMap.get(realFullName) == null) {
					changeMap.put(realFullName,
							changeName.substring(sindex));
				}
				setFullNameInHiddenColumn(row, changeName);
			} else {
				return;
			}
		}
	}

	/**
	 * Decrease index number in hidden column.
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
	public static void decreaseIndexNumberInHiddenColumn(
			final ConfigBuildRef configBuildRef, final int startRowIndex,
			final String fullName, final Map<String, String> changeMap) {
		String searchName = fullName.substring(0,
				fullName.lastIndexOf('.') + 1);
		Sheet sheet = configBuildRef.getSheet();
		for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String fname = getFullNameFromRow(row);
			if ((fname != null) && (fname.indexOf(searchName) >= 0)) {
				int sindex = fname.indexOf(searchName);
				String snum = fname.substring(sindex + searchName.length());
				int sufindex = snum.indexOf(':');
				String suffix = "";
				if (sufindex > 0) {
					snum = snum.substring(0, sufindex);
					suffix = ":";
				}
				int increaseNum = Integer.parseInt(snum) - 1;
				String realFullName = fname.substring(sindex);
				String changeName = fname.replace(
						searchName + snum + suffix,
						searchName + increaseNum + suffix);
				if (changeMap.get(realFullName) == null) {
					changeMap.put(realFullName,
							changeName.substring(sindex));
				}
				setFullNameInHiddenColumn(row, changeName);
			}
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

	/**
	 * Remove last each command index from full name. e.g. for
	 * F.departments:E.department.1:E.employee.2 will return
	 * F.departments:E.department.1:E.employee
	 * 
	 * Reason for this is for last layer collection. e.g. E.employee.1 and
	 * E.employee.2 The E.emloyee is the same. *
	 *
	 * @param fullName
	 *            the full name
	 * @return the full data collect name from full name
	 */
	public static String getFullDataCollectNameFromFullName(
			final String fullName) {
		if (fullName == null) {
			return "";
		}

		int lastEachCommandPos = fullName
				.lastIndexOf(TieConstants.EACH_COMMAND_FULL_NAME_PREFIX);
		if (lastEachCommandPos < 0) {
			return "";
		}
		int lastEachCommandIndexPos = fullName.indexOf('.',
				lastEachCommandPos
						+ TieConstants.EACH_COMMAND_FULL_NAME_PREFIX
								.length());
		if (lastEachCommandIndexPos < 0) {
			return fullName;
		}
		return fullName.substring(0, lastEachCommandIndexPos);
	}

	// This method mainly doing 2 things
	// 1. covert $A to $A$rowIndex
	// 2. Get value of $A$rowIndex and replace it in the string
	// i.e. $value >= $E
	// first $value has been taken cared before to actual value like 100
	// Here change $E to $E$8, Then get $E$8 value. Replace it in string like
	/**
	 * Replace expression with cell value.
	 *
	 * @param attrValue
	 *            the attr value
	 * @param rowIndex
	 *            the row index
	 * @param sheet
	 *            the sheet
	 * @return the string
	 */
	// 100 >= 80
	public static String replaceExpressionWithCellValue(
			final String attrValue, final int rowIndex, final Sheet sheet) {

		int ibegin = 0;
		int ifind;
		int iblank;
		String tempStr;
		String findStr;
		String replaceStr;
		String returnStr = attrValue;
		while ((ifind = attrValue.indexOf(TieConstants.CELL_ADDR_PRE_FIX,
				ibegin)) > 0) {
			iblank = attrValue.indexOf(' ', ifind);
			if (iblank > 0) {
				findStr = attrValue.substring(ifind, iblank);
			} else {
				findStr = attrValue.substring(ifind);
			}
			if (findStr.indexOf(TieConstants.CELL_ADDR_PRE_FIX, 1) < 0) {
				// only $A
				tempStr = findStr + TieConstants.CELL_ADDR_PRE_FIX
						+ (rowIndex + 1);
			} else {
				tempStr = findStr;
			}
			replaceStr = CellUtility.getCellValueWithoutFormat(
					WebSheetUtility.getCellByReference(tempStr, sheet));
			if (replaceStr == null) {
				replaceStr = "";
			}
			returnStr = attrValue.replace(findStr, replaceStr);

			ibegin = ifind + 1;

		}
		return returnStr;
	}

	/**
	 * Index merged region.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @return the map
	 */
	public static Map<String, CellRangeAddress> indexMergedRegion(
			final Sheet sheet1) {

		int numRegions = sheet1.getNumMergedRegions();
		Map<String, CellRangeAddress> cellRangeMap = new HashMap<>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				cellRangeMap.put(CellUtility.getCellIndexNumberKey(
						caddress.getFirstColumn(), caddress.getFirstRow()),
						caddress);
			}
		}
		return cellRangeMap;
	}

	/**
	 * Skipped region cells.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @return the list
	 */
	public static List<String> skippedRegionCells(final Sheet sheet1) {
		int numRegions = sheet1.getNumMergedRegions();
		List<String> skipCellList = new ArrayList<>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				addSkipCellToListInTheRegion(skipCellList, caddress);
			}
		}
		return skipCellList;
	}

	/**
	 * Add skipped cell into the list of a region.
	 * 
	 * @param skipCellList
	 *            list.
	 * @param caddress
	 *            region.
	 */
	private static void addSkipCellToListInTheRegion(
			final List<String> skipCellList,
			final CellRangeAddress caddress) {
		for (int col = caddress.getFirstColumn(); col <= caddress
				.getLastColumn(); col++) {
			for (int row = caddress.getFirstRow(); row <= caddress
					.getLastRow(); row++) {
				if ((col == caddress.getFirstColumn())
						&& (row == caddress.getFirstRow())) {
					continue;
				}
				skipCellList
						.add(CellUtility.getCellIndexNumberKey(col, row));
			}
		}
	}

}
