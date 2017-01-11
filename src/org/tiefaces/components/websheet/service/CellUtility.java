/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
public final class CellUtility {

	/** logger. */
	private static final Logger LOG = Logger.getLogger(CellUtility.class
			.getName());

	/**
	 * Instantiates a new cell helper.
	 */
	private CellUtility() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static String getCellValueWithFormat(Cell poiCell,
			FormulaEvaluator formulaEvaluator, DataFormatter dataFormatter) {

		if (poiCell == null) {
			return null;
		}

		String result;
		try {
			int cellType = poiCell.getCellType();
			if (cellType == Cell.CELL_TYPE_FORMULA) {
				cellType = formulaEvaluator.evaluate(poiCell).getCellType();
			}
			if (cellType == Cell.CELL_TYPE_ERROR) {
				result = "";
			} else {
				result = dataFormatter.formatCellValue(poiCell,
						formulaEvaluator);
			}
		} catch (Exception e) {
			LOG.severe("Web Form WebFormHelper getCellValue Error row = "
					+ poiCell.getRowIndex() + " col = "
					+ poiCell.getColumnIndex() + " error = "
					+ e.getLocalizedMessage()
					+ "; Change return result to blank");
			result = "";
		}
		LOG.fine("getCellValueWithFormat result = " + result + " row = "
				+ poiCell.getRowIndex() + " col = "
				+ poiCell.getColumnIndex());
		return result;
	}

	/**
	 * get input cell value. none input return blank
	 * 
	 * @param poiCell
	 *            cell.
	 * @return String cell value.
	 */
	public static String getCellValueWithoutFormat(final Cell poiCell) {

		if (poiCell == null) {
			return null;
		}

		if (poiCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return getCellStringValueWithType(poiCell,
					poiCell.getCachedFormulaResultType());
		} else {
			return getCellStringValueWithType(poiCell,
					poiCell.getCellType());
		}
	}

	/**
	 * Get cell value as string but with giving type.
	 * 
	 * @param poiCell
	 *            cell.
	 * @param cellType
	 *            cell type.
	 * @return Sting cell value.
	 */
	private static String getCellStringValueWithType(final Cell poiCell,
			final int cellType) {

		switch (cellType) {
		case Cell.CELL_TYPE_BOOLEAN:
			if (poiCell.getBooleanCellValue()) {
				return "Y";
			} else {
				return "N";
			}
		case Cell.CELL_TYPE_NUMERIC:
			String result;
			if (DateUtil.isCellDateFormatted(poiCell)) {
				result = poiCell.getDateCellValue().toString();
			} else {
				result = BigDecimal.valueOf(poiCell.getNumericCellValue())
						.toPlainString();
				// remove .0 from end for int
				if (result.endsWith(".0")) {
					result = result.substring(0, result.length() - 2);
				}
			}
			return result;
		case Cell.CELL_TYPE_STRING:
			return poiCell.getStringCellValue();
		} // switch

		// others all return blank
		return "";
	}

	/**
	 * Set cell value with giving String value.
	 * 
	 * @param c
	 *            cell.
	 * @param value
	 *            giving value.
	 * @return cell.
	 */
	public static Cell setCellValue(final Cell c, final String value) {

		try {
			if (value.length() == 0) {
				c.setCellType(Cell.CELL_TYPE_BLANK);
			} else if (TieWebSheetUtility.isNumeric(value)) {
				double val = Double
						.parseDouble(value.replace("" + ',', ""));
				c.setCellType(Cell.CELL_TYPE_NUMERIC);
				c.setCellValue(val);
			} else if (TieWebSheetUtility.isDate(value)) {
				String date = TieWebSheetUtility.parseDate(value);
				c.setCellType(Cell.CELL_TYPE_STRING);
				c.setCellValue(date);
			} else {
				if (c.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					if (value.equalsIgnoreCase("Y")) {
						c.setCellValue(true);
					} else {
						c.setCellValue(false);
					}
				} else {
					c.setCellType(Cell.CELL_TYPE_STRING);
					c.setCellValue(value);
				}
			}
		} catch (Exception e) {
			c.setCellType(Cell.CELL_TYPE_STRING);
			c.setCellValue(value);
		}
		LOG.fine(" set cell value row = " + c.getRowIndex() + " col = "
				+ c.getColumnIndex() + " value = " + value + " cellType = "
				+ c.getCellType());
		return c;
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
			e.printStackTrace();
			LOG.severe("WebForm WebFormHelper evalBoolExpression script = "
					+ script + "; error = " + e.getLocalizedMessage());
		}
		if (result != null) {
			return ((Boolean) result).booleanValue();
		} else {
			return false;
		}
	}

	/**
	 * Inserts a existing row into a new row, will automatically push down any
	 * existing rows. Copy is done cell by cell and supports, and the command
	 * tries to copy all properties available (style, merged cells,
	 * values,etc...)
	 *
	 * @param wb
	 *            workbook.
	 * @param wbWrapper
	 *            the wb wrapper
	 * @param srcSheet
	 *            the src sheet
	 * @param destSheet
	 *            the dest sheet
	 * @param srcRow
	 *            the src row
	 * @param destRow
	 *            the dest row
	 */
	public static final void copyRow(final Workbook wb,
			final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final int srcRow, final int destRow) {

		copyRows(wb, wbWrapper, srcSheet, destSheet, srcRow, srcRow,
				destRow, true, false);
	}

	/**
	 * Copy rows.
	 *
	 * @param wb
	 *            the wb
	 * @param wbWrapper
	 *            the wb wrapper
	 * @param srcSheet
	 *            the src sheet
	 * @param destSheet
	 *            the dest sheet
	 * @param srcRowStart
	 *            the src row start
	 * @param srcRowEnd
	 *            the src row end
	 * @param destRow
	 *            the dest row
	 * @param checkLock
	 *            the check lock
	 * @param setHiddenColumn
	 *            the set hidden column
	 */
	public static final void copyRows(final Workbook wb,
			final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final int srcRowStart,
			final int srcRowEnd, final int destRow,
			final boolean checkLock, final boolean setHiddenColumn) {

		int length = srcRowEnd - srcRowStart + 1;
		if (length <= 0) {
			return;
		}
		destSheet.shiftRows(destRow, destSheet.getLastRowNum(), length,
				true, false);
		for (int i = 0; i < length; i++) {
			copySingleRow(wb, wbWrapper, srcSheet, destSheet, srcRowStart
					+ i, destRow + i, checkLock, setHiddenColumn);
		}
		// If there are are any merged regions in the source row, copy to new
		// row
		for (int i = 0; i < srcSheet.getNumMergedRegions(); i++) {
			CellRangeAddress cellRangeAddress = srcSheet.getMergedRegion(i);
			if ((cellRangeAddress.getFirstRow() >= srcRowStart)
					&& (cellRangeAddress.getLastRow() <= srcRowEnd)) {
				int targetRowFrom = cellRangeAddress.getFirstRow()
						- srcRowStart + destRow;
				int targetRowTo = cellRangeAddress.getLastRow()
						- srcRowStart + destRow;

				CellRangeAddress newCellRangeAddress = new CellRangeAddress(
						targetRowFrom, targetRowTo,
						cellRangeAddress.getFirstColumn(),
						cellRangeAddress.getLastColumn());
				destSheet.addMergedRegion(newCellRangeAddress);
			}
		}
	}

	/**
	 * Copy single row.
	 *
	 * @param wb
	 *            the wb
	 * @param wbWrapper
	 *            the wb wrapper
	 * @param srcSheet
	 *            the src sheet
	 * @param destSheet
	 *            the dest sheet
	 * @param sourceRowNum
	 *            the source row num
	 * @param destinationRowNum
	 *            the destination row num
	 * @param checkLock
	 *            the check lock
	 * @param setHiddenColumn
	 *            the set hidden column
	 */
	private static void copySingleRow(final Workbook wb,
			final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final int sourceRowNum,
			final int destinationRowNum, final boolean checkLock,
			final boolean setHiddenColumn) {
		// Get the source / new row
		Row newRow = destSheet.getRow(destinationRowNum);
		Row sourceRow = srcSheet.getRow(sourceRowNum);

		if (newRow == null) {
			newRow = destSheet.createRow(destinationRowNum);
		}
		newRow.setHeight(sourceRow.getHeight());
		// Loop through source columns to add to new row
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			// Grab a copy of the old/new cell
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = newRow.createCell(i);

			copyCell(wb, wbWrapper, srcSheet, destSheet, sourceRow, newRow,
					oldCell, newCell, checkLock);
		}
		if (setHiddenColumn) {
			Cell cell = newRow.getCell(TieConstants.hiddenFullNameColumn,
					MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellValue(sourceRow.getRowNum() + ":");
			cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		return;

	}

	/**
	 * Copy cell.
	 *
	 * @param wb
	 *            the wb
	 * @param wbWrapper
	 *            the wb wrapper
	 * @param srcSheet
	 *            the src sheet
	 * @param destSheet
	 *            the dest sheet
	 * @param sourceRow
	 *            the source row
	 * @param newRow
	 *            the new row
	 * @param sourceCell
	 *            the source cell
	 * @param newCell
	 *            the new cell
	 * @param checkLock
	 *            the check lock
	 * @return the int
	 */
	public static int copyCell(final Workbook wb,
			final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final Row sourceRow, final Row newRow,
			final Cell sourceCell, Cell newCell, final boolean checkLock) {
		// If the old cell is null jump to next cell
		if (sourceCell == null) {
			newCell = null;
			return -1;
		}

		// Copy style from old cell and apply to new cell
		CellStyle newCellStyle = wb.createCellStyle();
		newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
		newCell.setCellStyle(newCellStyle);

		// If there is a cell comment, copy
		if (sourceCell.getCellComment() != null) {
			newCell.setCellComment(sourceCell.getCellComment());
		}

		// If there is a cell hyperlink, copy
		if (sourceCell.getHyperlink() != null) {
			newCell.setHyperlink(sourceCell.getHyperlink());
		}

		// Set the cell data type
		newCell.setCellType(sourceCell.getCellType());

		// Set the cell data value
		switch (sourceCell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			if ((!checkLock) || newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getBooleanCellValue());
			}
			break;
		case Cell.CELL_TYPE_ERROR:
			if ((!checkLock) || newCellStyle.getLocked()) {
				newCell.setCellErrorValue(sourceCell.getErrorCellValue());
			}
			break;
		case Cell.CELL_TYPE_FORMULA:
			/*
			 * if (shiftFormula) { Ptg[] sharedFormulaPtg = FormulaParser.parse(
			 * sourceCell.getCellFormula(), wbWrapper, FormulaType.CELL,
			 * wb.getSheetIndex(srcSheet)); Ptg[] convertedFormulaPtg =
			 * ShiftFormula .convertSharedFormulas(sharedFormulaPtg, (newRow
			 * .getRowNum() - sourceRow.getRowNum()), shiftRowStart,
			 * shiftRowEnd);
			 * newCell.setCellFormula(FormulaRenderer.toFormulaString(
			 * wbWrapper, convertedFormulaPtg)); } else {
			 */
			newCell.setCellFormula(sourceCell.getCellFormula());
			// }

			// formulaEvaluator.notifySetFormula(newCell);
			// formulaEvaluator.evaluate(newCell);
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if ((!checkLock) || newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			if ((!checkLock) || newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getRichStringCellValue());
			}
			break;
		default:
			if ((!checkLock) || newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getStringCellValue());
			}
			break;
		}

		// formulaEvaluator.notifyUpdateCell(newCell);
		return 1;
	}

	/**
	 * Return cell attributes with offset. This is used for repeat row which use
	 * same attribute for a group rows.
	 * 
	 * @param sheetConfig
	 *            sheet configuration.
	 * @param cell
	 *            cell.
	 * @param initRows
	 *            initial and actual row size of the group.
	 * @param bodyTopRow
	 *            top row.
	 * @param repeatZone
	 *            ture ( in the repeat zone) false ( not in the repeat zone).
	 * @return list of the attributes.
	 */
	private static final List<CellFormAttributes> findCellAttributesWithOffset(
			final SheetConfiguration sheetConfig, final Cell cell,
			final int initRows, final int bodyTopRow,
			final boolean repeatZone) {
		Map<String, List<CellFormAttributes>> map = sheetConfig
				.getCellFormAttributes();

		String key = findCellAddressWithOffset(cell, initRows, bodyTopRow,
				repeatZone);
		List<CellFormAttributes> result = map.get(key);
		if ((result == null) && repeatZone) {
			key = TieConstants.cellAddrPrefix
					+ TieWebSheetUtility.getExcelColumnName(cell
							.getColumnIndex());
			result = map.get(key);
		}
		return result;

	}

	/**
	 * Find cell address with offset.
	 *
	 * @param cell
	 *            the cell
	 * @param initRows
	 *            the init rows
	 * @param bodyTopRow
	 *            the body top row
	 * @param repeatZone
	 *            the repeat zone
	 * @return the string
	 */
	private static String findCellAddressWithOffset(final Cell cell,
			final int initRows, final int bodyTopRow,
			final boolean repeatZone) {

		String key;
		String columnLetter = TieWebSheetUtility.getExcelColumnName(cell
				.getColumnIndex());

		if (repeatZone) {
			key = TieConstants.cellAddrPrefix + columnLetter
					+ TieConstants.cellAddrPrefix + (bodyTopRow + 1);
		} else {
			key = TieConstants.cellAddrPrefix + columnLetter
					+ TieConstants.cellAddrPrefix
					+ (cell.getRowIndex() - initRows + 1 + 1);
		}
		return key;
	}

	/**
	 * Find cell address after body populated.
	 *
	 * @param oldCellAddr
	 *            the old cell addr
	 * @param sheetConfig
	 *            the sheet config
	 * @return the string
	 */
	public static String findCellAddressAfterBodyPopulated(
			final String oldCellAddr, final SheetConfiguration sheetConfig) {

		if (!sheetConfig.isBodyPopulated()) {
			return null; // not valid
		}
		String[] rowcol = getRowColFromExcelReferenceName(oldCellAddr);
		if (rowcol[0].isEmpty()) {
			// not valid
			return null;
		}
		int row = Integer.parseInt(rowcol[0]);
		int initialRows = sheetConfig.getBodyInitialRows();
		if ((sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat"))
				&& (row > (sheetConfig.getBodyCellRange().getTopRow() + 1))) {
			return TieConstants.cellAddrPrefix + rowcol[1]
					+ TieConstants.cellAddrPrefix + (row + initialRows - 1);
		}
		// no change
		return oldCellAddr;
	}

	/**
	 * Find cell attributes.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param cell
	 *            the cell
	 * @param row
	 *            the row
	 * @param bodyTopRow
	 *            the body top row
	 * @return the list
	 */
	public static List<CellFormAttributes> findCellAttributes(
			final SheetConfiguration sheetConfig, final Cell cell,
			final int row, final int bodyTopRow) {

		boolean repeatZone = false;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			int initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1) {
				initRows = 1;
			}
			if ((row >= bodyTopRow) && (row < (bodyTopRow + initRows))) {
				repeatZone = true;
			}
			return findCellAttributesWithOffset(sheetConfig, cell,
					initRows, bodyTopRow, repeatZone);
		}
		return findCellAttributesWithOffset(sheetConfig, cell, 1,
				bodyTopRow, false);

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
	public static String replaceExpressionWithCellValue(String attrValue,
			final int rowIndex, final Sheet sheet) {

		int ibegin = 0;
		int ifind = 0;
		int iblank = 0;
		String tempStr;
		String findStr;
		String replaceStr;
		while ((ifind = attrValue.indexOf(TieConstants.cellAddrPrefix,
				ibegin)) > 0) {
			iblank = attrValue.indexOf(' ', ifind);
			if (iblank > 0) {
				findStr = attrValue.substring(ifind, iblank);
			} else {
				findStr = attrValue.substring(ifind);
			}
			if (findStr.indexOf(TieConstants.cellAddrPrefix, 1) < 0) {
				// only $A
				tempStr = findStr + TieConstants.cellAddrPrefix
						+ (rowIndex + 1);
			} else {
				tempStr = findStr;
			}
			replaceStr = getCellValueWithoutFormat(TieWebSheetUtility
					.getCellByReference(tempStr, sheet));
			if (replaceStr == null) {
				replaceStr = "";
			}
			attrValue = attrValue.replace(findStr, replaceStr);

			ibegin = ifind + 1;

		}
		return attrValue;
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
		Map<String, CellRangeAddress> cellRangeMap = new HashMap<String, CellRangeAddress>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				cellRangeMap.put(
						TieConstants.cellAddrPrefix
								+ caddress.getFirstColumn()
								+ TieConstants.cellAddrPrefix
								+ caddress.getFirstRow(), caddress);
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
	public static List<String> skippedRegionCells(Sheet sheet1) {
		int numRegions = sheet1.getNumMergedRegions();
		List<String> skipCellList = new ArrayList<String>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				for (int col = caddress.getFirstColumn(); col <= caddress
						.getLastColumn(); col++) {
					for (int row = caddress.getFirstRow(); row <= caddress
							.getLastRow(); row++) {
						if ((col == caddress.getFirstColumn())
								&& (row == caddress.getFirstRow())) {
							continue;
						}
						skipCellList.add(TieConstants.cellAddrPrefix + col
								+ TieConstants.cellAddrPrefix + row);
					}
				}
			}
		}
		LOG.fine("skipCellList = " + skipCellList);
		return skipCellList;
	}

	/**
	 * Removes the row.
	 *
	 * @param sheet
	 *            the sheet
	 * @param rowIndex
	 *            the row index
	 */
	public static void removeRow(Sheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			Row removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}

	/**
	 * Convert cell.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param rowindex
	 *            the rowindex
	 * @param initRows
	 *            the init rows
	 * @param bodyTopRow
	 *            the body top row
	 * @param repeatZone
	 *            the repeat zone
	 * @param cellRangeMap
	 *            the cell range map
	 * @param originRowIndex
	 *            the origin row index
	 * @param cellAttributesMap
	 *            the cell attributes map
	 * @param saveAttrs
	 *            the save attrs
	 */
	// set up facesCell's attribute from poiCell and others.
	public static void convertCell(SheetConfiguration sheetConfig,
			FacesCell fcell, Cell poiCell, int rowindex, int initRows,
			int bodyTopRow, boolean repeatZone,
			Map<String, CellRangeAddress> cellRangeMap, int originRowIndex,
			CellAttributesMap cellAttributesMap, String saveAttrs) {
		boolean bodyPopulated = sheetConfig.isBodyPopulated();
		List<CellFormAttributes> cellAttributes = findCellAttributesWithOffset(
				sheetConfig, poiCell, initRows, bodyTopRow, repeatZone);
		if (cellAttributes != null) {
			for (CellFormAttributes attr : cellAttributes) {
				String attrType = attr.getType().trim();
				if (attrType.equalsIgnoreCase("load") && (!bodyPopulated)) {
					String attrValue = attr.getValue();
					attrValue = attrValue.replace("$rowIndex", rowindex
							+ "");
					if (attrValue.contains(TieConstants.EL_START)) {
						attrValue = FacesUtility.evaluateExpression(
								attrValue, String.class);
						setCellValue(poiCell, attrValue);
					}
				} else if (attrType.equalsIgnoreCase("input")) {
					String attrValue = attr.getValue().toLowerCase();
					fcell.setInputType(attrValue);
					if ((attrValue != null) && (!attrValue.isEmpty())
							&& (!attrValue.equalsIgnoreCase("textarea"))) {
						fcell.setStyle("text-align: right;");
					}
					if (attrValue.equalsIgnoreCase("textarea")) {
						fcell.setControl("textarea");
					}
					if ((fcell.getControl() == null)
							|| (fcell.getControl().isEmpty())) {
						fcell.setControl("text");
					}
				}
			}
		}
		CellRangeAddress caddress = null;
		String key = TieConstants.cellAddrPrefix + poiCell.getColumnIndex()
				+ TieConstants.cellAddrPrefix + poiCell.getRowIndex();
		caddress = cellRangeMap.get(key);
		if (caddress != null) {
			// has col or row span
			fcell.setColspan((caddress.getLastColumn()
					- caddress.getFirstColumn() + 1));
			fcell.setRowspan((caddress.getLastRow()
					- caddress.getFirstRow() + 1));
		}

		setupControlAttributes(originRowIndex, fcell, poiCell, sheetConfig,
				cellAttributesMap);
		fcell.setHasSaveAttr(ConfigurationHelper.isHasSaveAttr(
				poiCell.getColumnIndex(), saveAttrs));

	}

	/**
	 * Setup control attributes.
	 *
	 * @param originRowIndex
	 *            the origin row index
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param sheetConfig
	 *            the sheet config
	 * @param cellAttributesMap
	 *            the cell attributes map
	 */
	private static void setupControlAttributes(int originRowIndex,
			FacesCell fcell, Cell poiCell, SheetConfiguration sheetConfig,
			CellAttributesMap cellAttributesMap) {
		if (originRowIndex >= 0) {
			Map<String, String> commentMap = cellAttributesMap
					.getTemplateCommentMap().get("$$");
			String skey = poiCell.getSheet().getSheetName() + "!"
					+ TieConstants.cellAddrPrefix
					+ poiCell.getColumnIndex()
					+ TieConstants.cellAddrPrefix + originRowIndex;
			if (commentMap != null) {
				String comment = commentMap.get(skey);
				if (comment != null) {
					ConfigurationHelper.createCellComment(poiCell, comment,
							sheetConfig.getFinalCommentMap());
				}
			}
			String widgetType = cellAttributesMap.getCellInputType().get(
					skey);
			if (widgetType != null) {
				fcell.setControl(widgetType.toLowerCase());

				fcell.setInputAttrs(cellAttributesMap
						.getCellInputAttributes().get(skey));
				fcell.setSelectItemAttrs(cellAttributesMap
						.getCellSelectItemsAttributes().get(skey));
				fcell.setDatePattern(cellAttributesMap.getCellDatePattern()
						.get(skey));
			}

		}
	}

	/**
	 * Gets the row style.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @param inputType
	 *            the input type
	 * @param rowHeight
	 *            the row height
	 * @return the row style
	 */
	public static String getRowStyle(Workbook wb, Cell poiCell,
			String inputType, float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			float maxHeight = rowHeight;
			if (!inputType.isEmpty()) {
				maxHeight = Math.min(font.getFontHeightInPoints() + 8,
						rowHeight);
			}
			return "height:" + TieWebSheetUtility.pointsToPixels(maxHeight)
					+ "px;";
		}
		return "";
	}

	/**
	 * Gets the cell font style.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @param inputType
	 *            the input type
	 * @param rowHeight
	 *            the row height
	 * @return the cell font style
	 */
	public static String getCellFontStyle(final Workbook wb,
			final Cell poiCell, final String inputType,
			final float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			if (font.getItalic()) {
				webStyle.append("font-style: italic;");
			}
			webStyle.append("font-size: " + font.getFontHeightInPoints()
					+ "pt;");
			webStyle.append("font-weight:" + font.getBoldweight() + ";");

			String decoration = "";
			if (font.getUnderline() != 0) {
				decoration += " underline";
			}
			if (font.getStrikeout()) {
				decoration += " line-through";
			}
			if (decoration.length() > 0) {
				webStyle.append("text-decoration:" + decoration + ";");
			}
			short[] rgbfix = { TieConstants.RGB_MAX, TieConstants.RGB_MAX,
					TieConstants.RGB_MAX };
			if (font instanceof HSSFFont) {
				HSSFColor color = ((HSSFFont) font)
						.getHSSFColor((HSSFWorkbook) wb);
				if (color != null) {
					rgbfix = color.getTriplet();
				}
			} else if (font instanceof XSSFFont) {
				XSSFColor color = ((XSSFFont) font).getXSSFColor();
				if (color != null) {
					rgbfix = ColorUtility.getTripletFromXSSFColor(color);
				}
			}
			if (rgbfix[0] != TieConstants.RGB_MAX) {
				webStyle.append("color:rgb("
						+ FacesUtility.strJoin(rgbfix, ",") + ");");
			}

		}
		return webStyle.toString();

	}

	/**
	 * Gets the cell style.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @param inputType
	 *            the input type
	 * @return the cell style
	 */
	public static String getCellStyle(final Workbook wb,
			final Cell poiCell, final String inputType) {

		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			if (!inputType.isEmpty()) {
				webStyle.append(getAlignmentFromCell(poiCell, cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(poiCell,
						cellStyle));
			}

			webStyle.append(getBgColorFromCell(wb, poiCell, cellStyle));
		}
		return webStyle.toString();

	}

	/**
	 * Gets the column style.
	 *
	 * @param wb
	 *            the wb
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param rowHeight
	 *            the row height
	 * @return the column style
	 */
	public static String getColumnStyle(final Workbook wb,
			final FacesCell fcell, final Cell poiCell, final float rowHeight) {

		String inputType = fcell.getInputType();
		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			if (fcell.isContainPic() || fcell.isContainChart()) {
				webStyle.append("vertical-align: top;");
			} else {
				webStyle.append(getAlignmentFromCell(poiCell, cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(poiCell,
						cellStyle));
			}
			webStyle.append(getBgColorFromCell(wb, poiCell, cellStyle));
			webStyle.append(getRowStyle(wb, poiCell, inputType, rowHeight));
		} else {
			webStyle.append(getAlignmentFromCellType(poiCell));
		}
		return webStyle.toString();

	}

	/**
	 * Gets the alignment from cell.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @param cellStyle
	 *            the cell style
	 * @return the alignment from cell
	 */
	private static String getAlignmentFromCell(final Cell poiCell,
			final CellStyle cellStyle) {

		String style = "";
		switch (cellStyle.getAlignment()) {
		case CellStyle.ALIGN_LEFT:
			style = "text-align: left;";
			break;
		case CellStyle.ALIGN_RIGHT:
			style = "text-align: right;";
			break;
		case CellStyle.ALIGN_CENTER:
			style = "text-align: center;";
			break;
		case CellStyle.ALIGN_GENERAL:
			style = getAlignmentFromCellType(poiCell);
			break;
		}
		return style;
	}

	/**
	 * Gets the vertical alignment from cell.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @param cellStyle
	 *            the cell style
	 * @return the vertical alignment from cell
	 */
	private static String getVerticalAlignmentFromCell(final Cell poiCell,
			final CellStyle cellStyle) {

		String style = "";
		switch (cellStyle.getVerticalAlignment()) {
		case CellStyle.VERTICAL_TOP:
			style = "vertical-align: top;";
			break;
		case CellStyle.VERTICAL_CENTER:
			style = "vertical-align: middle;";
			break;
		case CellStyle.VERTICAL_BOTTOM:
			style = "vertical-align: bottom;";
			break;
		}
		return style;
	}

	/**
	 * Gets the bg color from cell.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @param cellStyle
	 *            the cell style
	 * @return the bg color from cell
	 */
	private static String getBgColorFromCell(final Workbook wb,
			final Cell poiCell, final CellStyle cellStyle) {

		String style = "";
		if (poiCell instanceof HSSFCell) {
			int bkColorIndex = cellStyle.getFillForegroundColor();
			HSSFColor color = HSSFColor.getIndexHash().get(bkColorIndex);
			if (color != null) {
				// correct color for customPalette
				HSSFPalette palette = ((HSSFWorkbook) wb)
						.getCustomPalette();
				HSSFColor color2 = palette.getColor(bkColorIndex);
				if (!color.getHexString().equalsIgnoreCase(
						color2.getHexString())) {
					color = color2;
				}
				// String hexStr = color.getHexString();
				// if (poiCell.getRowIndex() == 3 && poiCell.getColumnIndex() ==
				// 0) {
				// System.out.println(" hex str = "+hexStr);
				// }
				// if (!hexStr.equalsIgnoreCase("0:0:0")) {
				// if (hexStr.equalsIgnoreCase("FFFF:FFFF:FFFF")) {
				// System.out.println(" poiCell row = "+poiCell.getRowIndex() +
				// " col = "+ poiCell.getColumnIndex());
				// style ="background-color:rgb(0,0,0);";
				// }
				// else
				style = "background-color:rgb("
						+ FacesUtility.strJoin(color.getTriplet(), ",")
						+ ");";
				// }
			}
		} else if (poiCell instanceof XSSFCell) {
			XSSFColor color = ((XSSFCell) poiCell).getCellStyle()
					.getFillForegroundColorColor();
			if (color != null) {
				style = "background-color:rgb("
						+ FacesUtility
								.strJoin(ColorUtility
										.getTripletFromXSSFColor(color),
										",") + ");";
			}
		}
		return style;
	}

	// additionalWidth is to calculate extra width outside spreadsheet for
	// layout purpose
	/**
	 * Calc total width.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param firstCol
	 *            the first col
	 * @param lastCol
	 *            the last col
	 * @param additionalWidth
	 *            the additional width
	 * @return the int
	 */
	// e.g. lineNumberColumnWidth and addRowColumnWidth
	public static int calcTotalWidth(final Sheet sheet1,
			final int firstCol, final int lastCol, final int additionalWidth) {

		int totalWidth = additionalWidth;
		for (int i = firstCol; i <= lastCol; i++) {
			totalWidth += sheet1.getColumnWidth(i);
		}
		return totalWidth;
	}

	/**
	 * Calc total height.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param firstRow
	 *            the first row
	 * @param lastRow
	 *            the last row
	 * @param additionalHeight
	 *            the additional height
	 * @return the int
	 */
	public static int calcTotalHeight(final Sheet sheet1,
			final int firstRow, final int lastRow,
			final int additionalHeight) {

		int totalHeight = additionalHeight;
		for (int i = firstRow; i <= lastRow; i++) {

			totalHeight += sheet1.getRow(i).getHeight();
		}
		return totalHeight;
	}

	/**
	 * Setup cell style.
	 *
	 * @param wb
	 *            the wb
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param rowHeight
	 *            the row height
	 */
	public static void setupCellStyle(Workbook wb, Sheet sheet1,
			FacesCell fcell, Cell poiCell, float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		if ((cellStyle != null) && (!cellStyle.getLocked())) {
			// not locked
			if (fcell.getInputType().isEmpty()) {
				fcell.setInputType(getInputTypeFromCellType(poiCell));
			}
			if (fcell.getControl().isEmpty()
					&& (!fcell.getInputType().isEmpty())) {
				fcell.setControl("text");
			}
			setInputStyleBaseOnInputType(fcell, poiCell);

		}
		String webStyle = getCellStyle(wb, poiCell, fcell.getInputType())
				+ getCellFontStyle(wb, poiCell, fcell.getInputType(),
						rowHeight)
				+ getRowStyle(wb, poiCell, fcell.getInputType(), rowHeight);
		fcell.setStyle(webStyle);
		fcell.setColumnStyle(getColumnStyle(wb, fcell, poiCell, rowHeight));
	}

	/**
	 * set up Input Style parameter for input number component which need those
	 * parameters to make it work. e.g. symbol, symbol position, decimal places.
	 * 
	 * @param fcell
	 *            faces cell
	 * @param poiCell
	 *            poi cell
	 */
	private static void setInputStyleBaseOnInputType(final FacesCell fcell,
			final Cell poiCell) {

		if ((fcell == null) || fcell.getInputType().isEmpty()) {
			return;
		}

		switch (fcell.getInputType()) {
		case TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE:
			fcell.setSymbol("%");
			fcell.setSymbolPosition("p");
			fcell.setDecimalPlaces(getDecimalPlacesFromFormat(poiCell));
			break;

		case TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_INTEGER:
			fcell.setDecimalPlaces((short) 0);
			break;

		case TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE:
			fcell.setDecimalPlaces(getDecimalPlacesFromFormat(poiCell));
			fcell.setSymbol(getSymbolFromFormat(poiCell));
			fcell.setSymbolPosition(getSymbolPositionFromFormat(poiCell));
			break;
		default:
			break;
		}

	}

	/**
	 * Gets the alignment from cell type.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @return the alignment from cell type
	 */
	private static String getAlignmentFromCellType(Cell poiCell) {

		switch (poiCell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			return "text-align: right;";
		case Cell.CELL_TYPE_NUMERIC:
			return "text-align: right;";
		}
		return "";
	}

	/**
	 * Gets the input type from cell type.
	 *
	 * @param cell
	 *            the cell
	 * @return the input type from cell type
	 */
	private static String getInputTypeFromCellType(final Cell cell) {

		String inputType = TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_TEXT;
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			inputType = TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE;
		}
		CellStyle style = cell.getCellStyle();
		if (style != null) {
			int formatIndex = style.getDataFormat();
			String formatString = style.getDataFormatString();
			if (DateUtil.isADateFormat(formatIndex, formatString)) {
				inputType = TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_DATE;
			} else {
				if (isAPercentageCell(formatString)) {
					inputType = TieConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE;
				}
			}
		}
		return inputType;
	}

	/**
	 * Check weather the cell is percentage formatted.
	 *
	 * @param formatString
	 *            the format string
	 * @return true if it's percentage formatted
	 */
	private static boolean isAPercentageCell(final String formatString) {

		if (formatString == null) {
			return false;
		}
		return !(formatString.indexOf("%") < 0);

	}

	/**
	 * get decimal places from format string e.g. 0.00 will return 2
	 *
	 * @param cell
	 *            the cell
	 * @return decimal places of the formatted string
	 */
	private static short getDecimalPlacesFromFormat(final Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return 0;
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return 0;
		}
		int ipos = formatString.indexOf(".");
		if (ipos < 0) {
			return 0;
		}
		short counter = 0;
		for (int i = ipos + 1; i < formatString.length(); i++) {
			if (formatString.charAt(i) == '0') {
				counter++;
			} else {
				break;
			}
		}
		return counter;
	}

	/**
	 * get symbol from format string e.g. [$CAD] #,##0.00 will return CAD. While
	 * $#,##0.00 will return $
	 *
	 * @param cell
	 *            the cell
	 * @return symbol of the formatted string
	 */
	private static String getSymbolFromFormat(Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return null;
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return null;
		}
		if (formatString.indexOf(TieConstants.cellAddrPrefix) < 0) {
			return null;
		}
		int ipos = formatString.indexOf("[$");
		if (ipos < 0) {
			// only $ found, then return default dollar symbol
			return "$";
		}
		// return specified dollar symbol
		return formatString.substring(ipos + 2,
				formatString.indexOf("]", ipos));
	}

	/**
	 * get symbol position from format string e.g. [$CAD] #,##0.00 will return
	 * p. While #,##0.00 $ will return s
	 *
	 * @param cell
	 *            the cell
	 * @return symbol position of the formatted string
	 */
	private static String getSymbolPositionFromFormat(Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return "p";
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return "p";
		}
		int symbolpos = formatString.indexOf("$");
		int numberpos = formatString.indexOf("#");
		if (numberpos < 0) {
			numberpos = formatString.indexOf("0");
		}
		if (symbolpos < numberpos) {
			return "p";
		} else {
			return "s";
		}
	}

	/**
	 * Gets the row col from component attributes.
	 *
	 * @param target
	 *            the target
	 * @return the row col from component attributes
	 */
	public static int[] getRowColFromComponentAttributes(
			final UIComponent target) {

		int rowIndex = (Integer) target.getAttributes().get("data-row");
		int colIndex = (Integer) target.getAttributes().get("data-column");
		LOG.fine("getRowColFromComponentAttributes rowindex = " + rowIndex
				+ " colindex = " + colIndex);
		int[] list = { rowIndex, colIndex };
		return list;
	}

	/**
	 * Gets the row col from excel reference name.
	 *
	 * @param excelRef
	 *            the excel ref
	 * @return the row col from excel reference name
	 */
	public static String[] getRowColFromExcelReferenceName(
			final String excelRef) {
		String[] parts = excelRef.split("\\$");
		String[] list = { "", "" };
		int i = parts.length;
		if (i > 1) {
			list[1] = parts[1]; // column
		}
		if (i > 2) {
			list[0] = parts[2]; // row
		}
		return list;
	}

	/**
	 * Gets the inits the rows from config.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @return the inits the rows from config
	 */
	public static int getInitRowsFromConfig(
			final SheetConfiguration sheetConfig) {
		int initRows = 1;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1) {
				initRows = 1;
			}
		}
		return initRows;
	}

	/**
	 * Gets the body bottom from config.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param initRows
	 *            the init rows
	 * @return the body bottom from config
	 */
	public static int getBodyBottomFromConfig(
			final SheetConfiguration sheetConfig, final int initRows) {

		int bottom = sheetConfig.getBodyCellRange().getBottomRow();
		return bottom;
	}

	/**
	 * Gets the cell value with config.
	 *
	 * @param targetCell
	 *            the target cell
	 * @param datarow
	 *            the datarow
	 * @param initialRows
	 *            the initial rows
	 * @param sheetConfig
	 *            the sheet config
	 * @param sheet
	 *            the sheet
	 * @return the cell value with config
	 */
	public static String getCellValueWithConfig(final String targetCell,
			final int datarow, final int initialRows,
			final SheetConfiguration sheetConfig, final Sheet sheet) {
		Cell cell = getCellReferenceWithConfig(targetCell, datarow,
				initialRows, sheetConfig, sheet);
		if (cell != null) {
			return getCellValueWithoutFormat(cell);
		}
		return "";
	}

	/**
	 * Gets the cell reference with config.
	 *
	 * @param targetCell
	 *            the target cell
	 * @param datarow
	 *            the datarow
	 * @param initialRows
	 *            the initial rows
	 * @param sheetConfig
	 *            the sheet config
	 * @param sheet
	 *            the sheet
	 * @return the cell reference with config
	 */
	public static Cell getCellReferenceWithConfig(String targetCell,
			final int datarow, final int initialRows,
			final SheetConfiguration sheetConfig, final Sheet sheet) {

		String[] rowcol = getRowColFromExcelReferenceName(targetCell);
		if (rowcol[0].isEmpty()) {
			if (rowcol[1].isEmpty()) {
				return null; // both empty meaning not valid targetcell
			}
			targetCell = TieConstants.cellAddrPrefix
					+ rowcol[1]
					+ TieConstants.cellAddrPrefix
					+ (datarow + sheetConfig.getBodyCellRange().getTopRow() + 1);
		} else {
			int row = Integer.parseInt(rowcol[0]);
			if ((sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat"))
					&& (row > (sheetConfig.getBodyCellRange().getTopRow() + 1))) {
				targetCell = TieConstants.cellAddrPrefix + rowcol[1]
						+ TieConstants.cellAddrPrefix
						+ (row + initialRows - 1);
			}
		}
		Cell cell = TieWebSheetUtility
				.getCellByReference(targetCell, sheet);
		return cell;
	}

	/**
	 * Gets the faces cell from body row.
	 *
	 * @param bodyrow
	 *            the bodyrow
	 * @param bodycol
	 *            the bodycol
	 * @param bodyRows
	 *            the body rows
	 * @return the faces cell from body row
	 */
	public static FacesCell getFacesCellFromBodyRow(final int bodyrow,
			final int bodycol, final List<FacesRow> bodyRows) {
		FacesCell cell = null;
		try {
			if (bodyRows.get(bodyrow).getCells().size() > bodycol) {
				cell = bodyRows.get(bodyrow).getCells().get(bodycol);
			}
		} catch (Exception e) {
			LOG.severe("Web Form WebFormHelper getFacesCellFromBodyRow Error bodyrow = "
					+ bodyrow
					+ " bodycol = "
					+ bodycol
					+ "; error = "
					+ e.getLocalizedMessage());
		}
		return cell;
	}

	/**
	 * Gets the poi cell with row col from current page.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @param wb
	 *            workbook.
	 * @return the poi cell with row col from current page
	 */
	public static Cell getPoiCellWithRowColFromCurrentPage(
			final int rowIndex, final int colIndex, final Workbook wb) {
		if (wb != null) {
			return getPoiCellFromSheet(rowIndex, colIndex,
					wb.getSheetAt(wb.getActiveSheetIndex()));
		}
		return null;
	}

	/**
	 * Gets the poi cell from sheet.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @param sheet1
	 *            the sheet 1
	 * @return the poi cell from sheet
	 */
	public static Cell getPoiCellFromSheet(final int rowIndex,
			final int colIndex, final Sheet sheet1) {
		if ((sheet1 != null) && (sheet1.getRow(rowIndex) != null)) {
			return sheet1.getRow(rowIndex).getCell(colIndex);
		}
		return null;
	}

}
