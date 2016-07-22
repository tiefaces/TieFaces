/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.service;

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
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiefaces.common.FacesUtility;
import com.tiefaces.components.websheet.TieWebSheetBean;
import com.tiefaces.components.websheet.TieWebSheetConstants;
import com.tiefaces.components.websheet.configuration.SheetConfiguration;
import com.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import com.tiefaces.components.websheet.dataobjects.FacesCell;
import com.tiefaces.components.websheet.dataobjects.FacesRow;
import com.tiefaces.components.websheet.utility.ColorUtility;
import com.tiefaces.components.websheet.utility.TieWebSheetUtility;

/**
 * Helper class for web sheet cells.
 * 
 * @author Jason Jiang
 *
 */
public class CellHelper {

	/** instance to parent websheet bean. */
	private TieWebSheetBean parent = null;
	/** logger. */
	private static final Logger log = Logger.getLogger(Thread
			.currentThread().getStackTrace()[0].getClassName());

	/**
 * 
 */
	public CellHelper() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param parent
	 *            parent bean.
	 */
	public CellHelper(TieWebSheetBean parent) {
		super();
		this.parent = parent;
	}

	/**
	 * 
	 * @param poiCell
	 *            cell.
	 * @return String cell value with format.
	 */
	public String getCellValueWithFormat(Cell poiCell) {

		if (poiCell == null) {
			return null;
		}

		String result;
		try {
			int cellType = poiCell.getCellType();
			if (cellType == Cell.CELL_TYPE_FORMULA) {
				cellType = parent.getFormulaEvaluator().evaluate(poiCell)
						.getCellType();
			}
			if (cellType == Cell.CELL_TYPE_ERROR) {
				result = "";
			} else {
				result = parent.getDataFormatter().formatCellValue(
						poiCell, parent.getFormulaEvaluator());
			}
		} catch (Exception e) {
			log.severe("Web Form WebFormHelper getCellValue Error row = "
					+ poiCell.getRowIndex() + " col = "
					+ poiCell.getColumnIndex() + " error = "
					+ e.getLocalizedMessage()
					+ "; Change return result to blank");
			result = "";
		}
		log.fine("getCellValueWithFormat result = " + result + " row = "
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
	public String getCellValueWithoutFormat(Cell poiCell) {

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
	private String getCellStringValueWithType(Cell poiCell, int cellType) {

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
				result = BigDecimal
						.valueOf(poiCell.getNumericCellValue())
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
	public Cell setCellValue(Cell c, String value) {

		try {
			if (value.length() == 0) {
				c.setCellType(Cell.CELL_TYPE_BLANK);
			} else if (TieWebSheetUtility.isNumeric(value)) {
				double val = Double.parseDouble(value.replace("" + ',',
						""));
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
		log.fine(" set cell value row = " + c.getRowIndex() + " col = "
				+ c.getColumnIndex() + " value = " + value
				+ " cellType = " + c.getCellType());
		return c;
	}

	/**
	 * recalc whole workbook.
	 */
	public final void reCalc() {

		parent.getFormulaEvaluator().clearAllCachedResultValues();
		try {
			parent.getFormulaEvaluator().evaluateAll();
		} catch (Exception ex) {
			// skip the formula exception when recalc but log it
			log.severe(" recalc formula error : "
					+ ex.getLocalizedMessage());
		}

	}

	/**
	 * evaluate boolean express
	 * 
	 * @param script
	 *            express.
	 * @return true (express is true) false ( express is false or invalid).
	 */
	public boolean evalBoolExpression(String script) {
		Object result = null;
		script = "( " + script + " )";
		script = script.toUpperCase().replace("AND", "&&");
		script = script.toUpperCase().replace("OR", "||");
		try {
			result = parent.getEngine().eval(script);
		} catch (Exception e) {
			e.printStackTrace();
			log.severe("WebForm WebFormHelper evalBoolExpression script = "
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
	 * @param worksheet
	 *            worksheet.
	 * @param sourceRowNum
	 *            Source Row Number.
	 * @param destinationRowNum
	 *            Destination Row Number.
	 */
	public final void copyRow(final Workbook wb, final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final int srcRow, final int destRow) {

		copyRows(wb, wbWrapper, srcSheet, destSheet, srcRow, srcRow, destRow);
	}

	public final void copyRows(final Workbook wb, final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final int srcRowStart,
			final int srcRowEnd, final int destRow) {

		int length = srcRowStart - srcRowEnd + 1;
		if (length <= 0)
			return;
		destSheet.shiftRows(destRow, destSheet.getLastRowNum(), length,
				true, false);
		for (int i = 0; i < length; i++) {
			copySingleRow(wb, wbWrapper, srcSheet, destSheet, srcRowStart
					+ i, destRow + i, true, srcRowStart, srcRowEnd);
		}
		// If there are are any merged regions in the source row, copy to new
		// row
		for (int i = 0; i < srcSheet.getNumMergedRegions(); i++) {
			CellRangeAddress cellRangeAddress = srcSheet
					.getMergedRegion(i);
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

	private final void copySingleRow(final Workbook wb,
			XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final int sourceRowNum,
			final int destinationRowNum, final boolean shiftFormula,
			final int shiftRowStart, final int shiftRowEnd) {
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

			copyCell(wb, wbWrapper, srcSheet, destSheet, sourceRow,
					newRow, oldCell, newCell, shiftFormula,
					shiftRowStart, shiftRowEnd);
		}
		return;

	}

	public int copyCell(final Workbook wb,
			final XSSFEvaluationWorkbook wbWrapper, final Sheet srcSheet,
			final Sheet destSheet, final Row sourceRow, final Row newRow,
			final Cell sourceCell, Cell newCell,
			final boolean shiftFormula, final int shiftRowStart,
			final int shiftRowEnd) {
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
			if (newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getBooleanCellValue());
			}
			break;
		case Cell.CELL_TYPE_ERROR:
			if (newCellStyle.getLocked()) {
				newCell.setCellErrorValue(sourceCell.getErrorCellValue());
			}
			break;
		case Cell.CELL_TYPE_FORMULA:
			if (shiftFormula) {
				Ptg[] sharedFormulaPtg = FormulaParser.parse(
						sourceCell.getCellFormula(), wbWrapper,
						FormulaType.CELL, wb.getSheetIndex(srcSheet));
				Ptg[] convertedFormulaPtg = ShiftFormula
						.convertSharedFormulas(sharedFormulaPtg, (newRow
								.getRowNum() - sourceRow.getRowNum()),
								shiftRowStart, shiftRowEnd);
				newCell.setCellFormula(FormulaRenderer.toFormulaString(
						wbWrapper, convertedFormulaPtg));
			} else {
				newCell.setCellFormula(sourceCell.getCellFormula());
			}

			// formulaEvaluator.notifySetFormula(newCell);
			// formulaEvaluator.evaluate(newCell);
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			if (newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getRichStringCellValue());
			}
			break;
		default:
			if (newCellStyle.getLocked()) {
				newCell.setCellValue(sourceCell.getStringCellValue());
			}
			break;
		}

		// formulaEvaluator.notifyUpdateCell(newCell);
		return 1;
	}

	/**
	 * 
	 public final void copyRows(final Workbook wb, final Sheet srcSheet, final
	 * Sheet descSheet, final int srcStartRow, final int srcEndRow, final int
	 * destStartRow) {
	 * 
	 * int pStartRow = startRow - 1; int pEndRow = endRow - 1; int
	 * targetRowFrom; int targetRowTo; int columnCount; CellRangeAddress region
	 * = null; int i; int j; if (pStartRow == -1 || pEndRow == -1) { return; }
	 * // 拷贝合并的单元格 for (i = 0; i < sheet.getNumMergedRegions(); i++) { region =
	 * sheet.getMergedRegion(i); if ((region.getFirstRow() >= pStartRow) &&
	 * (region.getLastRow() <= pEndRow)) { targetRowFrom = region.getFirstRow()
	 * - pStartRow + pPosition; targetRowTo = region.getLastRow() - pStartRow +
	 * pPosition; CellRangeAddress newRegion = region.copy();
	 * newRegion.setFirstRow(targetRowFrom);
	 * newRegion.setFirstColumn(region.getFirstColumn());
	 * newRegion.setLastRow(targetRowTo);
	 * newRegion.setLastColumn(region.getLastColumn());
	 * sheet.addMergedRegion(newRegion); } } // 设置列宽 for (i = pStartRow; i <=
	 * pEndRow; i++) { HSSFRow sourceRow = sheet.getRow(i); columnCount =
	 * sourceRow.getLastCellNum(); if (sourceRow != null) { HSSFRow newRow =
	 * sheet.createRow(pPosition - pStartRow + i);
	 * newRow.setHeight(sourceRow.getHeight()); for (j = 0; j < columnCount;
	 * j++) { HSSFCell templateCell = sourceRow.getCell(j); if (templateCell !=
	 * null) { HSSFCell newCell = newRow.createCell(j); copyCell(templateCell,
	 * newCell); } } } } }
	 */
	// comment out below. Maybe used in future.
	/* Refactor row formulas */
	// properly refactor an excel formulat on a row change
	/*
	 * public String formulaRowRefactor(String formula, int sourceRow, int
	 * copyRow) { String buf = ""; String new_formula = ""; int i; char c;
	 * boolean skipNext = false, inParen = false; for (i = 0; i <
	 * formula.length(); i++) { c = formula.charAt(i); if (c == '\'') { if
	 * (buf.length() > 0 && buf.length() < 4 && i - buf.length() - 1 >= 0 &&
	 * TieWebSheetUtility.isUpperAlpha(formula.charAt(i - buf.length() - 1))) {
	 * if (!skipNext) { new_formula += carefulRowFormulaRefactorString(buf,
	 * sourceRow, copyRow); buf = ""; } else { new_formula += buf; skipNext =
	 * false; buf = ""; } } else { new_formula += buf; buf = ""; } inParen =
	 * (inParen ? false : true); new_formula += c; } else if (!inParen) { if (c
	 * == '$') { if (buf.length() > 0 && buf.length() < 4 && i - buf.length() -
	 * 1 >= 0 && TieWebSheetUtility.isUpperAlpha(formula.charAt(i - buf.length()
	 * - 1))) { if (!skipNext) { new_formula +=
	 * carefulColFormulaRefactorString(buf, sourceRow, copyRow); buf = ""; }
	 * else { new_formula += buf; skipNext = false; buf = ""; } } else {
	 * new_formula += buf; buf = ""; } skipNext = true; new_formula += c; } else
	 * if (skipNext) { if (!TieWebSheetUtility.isNumeric(c)) { skipNext = false;
	 * } new_formula += c; } else { if (TieWebSheetUtility.isNumeric(c)) { buf
	 * += c; } else { if (buf.length() > 0 && i - buf.length() - 1 >= 0 &&
	 * TieWebSheetUtility.isUpperAlpha(formula .charAt(i - buf.length() - 1))) {
	 * new_formula += carefulRowFormulaRefactorString(buf, sourceRow, copyRow);
	 * buf = ""; } else { new_formula += buf; buf = ""; } new_formula += c; } }
	 * } else { new_formula += c; } } if (!skipNext && !inParen && buf.length()
	 * > 0 && i - buf.length() - 1 >= 0 &&
	 * TieWebSheetUtility.isUpperAlpha(formula.charAt(i - buf.length() - 1))) {
	 * new_formula += carefulRowFormulaRefactorString(buf, sourceRow, copyRow);
	 * buf = ""; } else { new_formula += buf; buf = ""; } return new_formula; }
	 * 
	 * public int carefulRowFormulaRefactorString(String formula, int sourceRow,
	 * int copyRow) { return copyRow + (Integer.parseInt(formula) - sourceRow);
	 * }
	 * 
	 * public String carefulColFormulaRefactorString(String formula, int
	 * sourceCol, int copyCol) { return TieWebSheetUtility
	 * .GetExcelColumnName((copyCol + (TieWebSheetUtility
	 * .convertColToInt(formula) - sourceCol))); }
	 * 
	 * public boolean containsCell(CellRangeAddress cr, int rowIx, int colIx) {
	 * if (cr.getFirstRow() <= rowIx && cr.getLastRow() >= rowIx &&
	 * cr.getFirstColumn() <= colIx && cr.getLastColumn() >= colIx) { return
	 * true; } return false; }
	 */
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
	public final List<CellFormAttributes> findCellAttributesWithOffset(
			final SheetConfiguration sheetConfig, final Cell cell,
			final int initRows, final int bodyTopRow,
			final boolean repeatZone) {
		Map<String, List<CellFormAttributes>> map = sheetConfig
				.getCellFormAttributes();

		String key = findCellAddressWithOffset(cell, initRows,
				bodyTopRow, repeatZone);
		List<CellFormAttributes> result = map.get(key);
		if ((result == null) && repeatZone) {
			key = "$"
					+ TieWebSheetUtility.GetExcelColumnName(cell
							.getColumnIndex());
			result = map.get(key);
		}
		return result;

	}

	public String findCellAddressWithOffset(Cell cell, int initRows,
			int bodyTopRow, boolean repeatZone) {

		String key;
		String columnLetter = TieWebSheetUtility.GetExcelColumnName(cell
				.getColumnIndex());

		if (repeatZone)
			key = "$" + columnLetter + "$" + (bodyTopRow + 1);
		else
			key = "$" + columnLetter + "$"
					+ (cell.getRowIndex() - initRows + 1 + 1);
		return key;
	}

	public String findCellAddressAfterBodyPopulated(String oldCellAddr,
			SheetConfiguration sheetConfig) {

		if (!sheetConfig.isBodyPopulated())
			return null; // not valid
		String[] rowcol = getRowColFromExcelReferenceName(oldCellAddr);
		if (rowcol[0].isEmpty()) {
			// not valid
			return null;
		}
		int row = Integer.parseInt(rowcol[0]);
		int initialRows = sheetConfig.getBodyInitialRows();
		if ((sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat"))
				&& (row > (sheetConfig.getBodyCellRange().getTopRow() + 1))) {
			return "$" + rowcol[1] + "$" + (row + initialRows - 1);
		}
		// no change
		return oldCellAddr;
	}

	// public String findCellRangeAddressWithOffset(int row, int col,
	// int initRows, int bodyTopRow, boolean repeatZone,
	// boolean bodyPopulated) {
	// String key;
	// if (bodyPopulated)
	// key = "$" + col + "$" + row;
	// else {
	//
	// if (repeatZone)
	// key = "$" + col + "$" + bodyTopRow;
	// else
	// key = "$" + col + "$" + (row - initRows + 1);
	// }
	// return key;
	// }

	public List<CellFormAttributes> findCellAttributes(
			SheetConfiguration sheetConfig, Cell cell, int row,
			int bodyTopRow) {

		boolean repeatZone = false;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			int initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1)
				initRows = 1;
			if ((row >= bodyTopRow) && (row < (bodyTopRow + initRows)))
				repeatZone = true;
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
	// 100 >= 80
	public String replaceExpressionWithCellValue(String attrValue,
			int rowIndex, Sheet sheet) {

		int ibegin = 0;
		int ifind = 0;
		int iblank = 0;
		String temp_str;
		String find_str;
		String replace_str;
		while ((ifind = attrValue.indexOf('$', ibegin)) > 0) {
			iblank = attrValue.indexOf(' ', ifind);
			if (iblank > 0) {
				find_str = attrValue.substring(ifind, iblank);
			} else {
				find_str = attrValue.substring(ifind);
			}
			if (find_str.indexOf('$', 1) < 0) {
				// only $A
				temp_str = find_str + "$" + (rowIndex + 1);
			} else
				temp_str = find_str;
			replace_str = getCellValueWithoutFormat(TieWebSheetUtility
					.getCellByReference(temp_str, sheet));
			if (replace_str == null)
				replace_str = "";
			attrValue = attrValue.replace(find_str, replace_str);

			ibegin = ifind + 1;

		}
		return attrValue;
	}

	public Map<String, CellRangeAddress> indexMergedRegion(Sheet sheet1) {

		int numRegions = sheet1.getNumMergedRegions();
		Map<String, CellRangeAddress> cellRangeMap = new HashMap<String, CellRangeAddress>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				cellRangeMap.put("$" + caddress.getFirstColumn() + "$"
						+ caddress.getFirstRow(), caddress);
			}
		}
		return cellRangeMap;
	}

	public List<String> skippedRegionCells(Sheet sheet1) {
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
								&& (row == caddress.getFirstRow()))
							continue;
						skipCellList.add("$" + col + "$" + row);
					}
				}
			}
		}
		log.fine("skipCellList = " + skipCellList);
		return skipCellList;
	}

	public void removeRow(Sheet sheet, int rowIndex) {
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

	// set up facesCell's attribute from poiCell and others.
	public void convertCell(SheetConfiguration sheetConfig,
			FacesCell fcell, Cell poiCell, int rowindex, int initRows,
			int bodyTopRow, boolean repeatZone,
			Map<String, CellRangeAddress> cellRangeMap) {
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
					if (attrValue.contains("#{")) {
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
				}
			}
		}
		CellRangeAddress caddress = null;
		String key = "$" + poiCell.getColumnIndex() + "$"
				+ poiCell.getRowIndex();
		caddress = cellRangeMap.get(key);
		if (caddress != null) {
			// has col or row span
			fcell.setColspan((caddress.getLastColumn()
					- caddress.getFirstColumn() + 1));
			fcell.setRowspan((caddress.getLastRow()
					- caddress.getFirstRow() + 1));
		}
	}

	public String getRowStyle(Workbook wb, Cell poiCell,
			String inputType, float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			float maxHeight = rowHeight;
			if (!inputType.isEmpty()) {
				maxHeight = Math.min(font.getFontHeightInPoints() + 6,
						rowHeight);
			}
			return "height:"
					+ TieWebSheetUtility.pointsToPixels(maxHeight)
					+ "px;";
		}
		return "";
	}

	public String getCellFontStyle(Workbook wb, Cell poiCell,
			String inputType, float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			if (font.getItalic())
				webStyle.append("font-style: italic;");
			webStyle.append("font-size: " + font.getFontHeightInPoints()
					+ "pt;");
			webStyle.append("font-weight:" + font.getBoldweight() + ";");

			String decoration = "";
			if (font.getUnderline() != 0)
				decoration += " underline";
			if (font.getStrikeout())
				decoration += " line-through";
			if (decoration.length() > 0)
				webStyle.append("text-decoration:" + decoration + ";");
			short[] rgbfix = { 256, 256, 256 };
			if (font instanceof HSSFFont) {
				HSSFColor color = ((HSSFFont) font)
						.getHSSFColor((HSSFWorkbook) wb);
				if (color != null)
					rgbfix = color.getTriplet();
			} else if (font instanceof XSSFFont) {
				XSSFColor color = ((XSSFFont) font).getXSSFColor();
				if (color != null) {
					rgbfix = ColorUtility.getTripletFromXSSFColor(color);
				}
			}
			if (rgbfix[0] != 256)
				webStyle.append("color:rgb("
						+ FacesUtility.strJoin(rgbfix, ",") + ");");

		}
		return webStyle.toString();

	}

	public String getCellStyle(Workbook wb, Cell poiCell, String inputType) {

		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			if (!inputType.isEmpty()) {
				webStyle.append(getAlignmentFromCell(poiCell, cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(poiCell,
						cellStyle));
			}
			;
			webStyle.append(getBgColorFromCell(wb, poiCell, cellStyle));
		} else {
			// webStyle.append(getAlignmentFromCellType(poiCell));
		}
		return webStyle.toString();

	}

	public String getColumnStyle(Workbook wb, FacesCell fcell,
			Cell poiCell, float rowHeight) {

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

	private String getAlignmentFromCell(Cell poiCell, CellStyle cellStyle) {

		String style = "";
		switch (cellStyle.getAlignment()) {
		case CellStyle.ALIGN_LEFT: {
			style = "text-align: left;";
			break;
		}
		case CellStyle.ALIGN_RIGHT: {
			style = "text-align: right;";
			break;
		}
		case CellStyle.ALIGN_CENTER: {
			style = "text-align: center;";
			break;
		}
		case CellStyle.ALIGN_GENERAL: {
			style = getAlignmentFromCellType(poiCell);
			break;
		}
		}
		return style;
	}

	private String getVerticalAlignmentFromCell(Cell poiCell,
			CellStyle cellStyle) {

		String style = "";
		switch (cellStyle.getVerticalAlignment()) {
		case CellStyle.VERTICAL_TOP: {
			style = "vertical-align: top;";
			break;
		}
		case CellStyle.VERTICAL_CENTER: {
			style = "vertical-align: middle;";
			break;
		}
		case CellStyle.VERTICAL_BOTTOM: {
			style = "vertical-align: bottom;";
			break;
		}
		}
		return style;
	}

	private String getBgColorFromCell(Workbook wb, Cell poiCell,
			CellStyle cellStyle) {

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
						color2.getHexString()))
					color = color2;
				// String hexStr = color.getHexString();
				// if (poiCell.getRowIndex() == 3 && poiCell.getColumnIndex() ==
				// 0) {
				// System.out.println(" hex str  = "+hexStr);
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
			if (color != null)
				style = "background-color:rgb("
						+ FacesUtility.strJoin(ColorUtility
								.getTripletFromXSSFColor(color), ",")
						+ ");";
		}
		return style;
	}

	// additionalWidth is to calculate extra width outside spreadsheet for
	// layout purpose
	// e.g. lineNumberColumnWidth and addRowColumnWidth
	public int calcTotalWidth(Sheet sheet1, int firstCol, int lastCol,
			int additionalWidth) {

		int totalWidth = additionalWidth;
		for (int i = firstCol; i <= lastCol; i++) {
			System.out.println(" column " + i + " width = "
					+ sheet1.getColumnWidth(i));
			totalWidth += sheet1.getColumnWidth(i);
		}
		return totalWidth;
	}

	public int calcTotalHeight(Sheet sheet1, int firstRow, int lastRow,
			int additionalHeight) {

		int totalHeight = additionalHeight;
		for (int i = firstRow; i <= lastRow; i++) {

			System.out.println(" row " + i + " height = "
					+ sheet1.getRow(i).getHeight());
			totalHeight += sheet1.getRow(i).getHeight();
		}
		return totalHeight;
	}

	public void setupCellStyle(Workbook wb, Sheet sheet1,
			FacesCell fcell, Cell poiCell, float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		if ((cellStyle != null) && (!cellStyle.getLocked())) {
			// not locked
			if (fcell.getInputType().isEmpty()) {
				fcell.setInputType(getInputTypeFromCellType(poiCell));
			}
			setInputStyleBaseOnInputType(fcell, poiCell);
		}
		String webStyle = getCellStyle(wb, poiCell, fcell.getInputType())
				+ getCellFontStyle(wb, poiCell, fcell.getInputType(),
						rowHeight)
				+ getRowStyle(wb, poiCell, fcell.getInputType(),
						rowHeight);
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
	private void setInputStyleBaseOnInputType(final FacesCell fcell,
			final Cell poiCell) {

		if ((fcell == null) || fcell.getInputType().isEmpty()) {
			return;
		}

		switch (fcell.getInputType()) {
		case TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE:
			fcell.setSymbol("%");
			fcell.setSymbolPosition("p");
			fcell.setDecimalPlaces(this
					.getDecimalPlacesFromFormat(poiCell));
			break;

		case TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_INTEGER:
			fcell.setDecimalPlaces((short) 0);
			break;

		case TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE:
			fcell.setDecimalPlaces(getDecimalPlacesFromFormat(poiCell));
			fcell.setSymbol(getSymbolFromFormat(poiCell));
			fcell.setSymbolPosition(getSymbolPositionFromFormat(poiCell));
			break;
		default:
			break;
		}

	}

	private String getAlignmentFromCellType(Cell poiCell) {

		switch (poiCell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			return "text-align: right;";
		case Cell.CELL_TYPE_NUMERIC:
			return "text-align: right;";
		}
		return "";
	}

	private String getInputTypeFromCellType(final Cell cell) {

		String inputType = TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_TEXT;
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			inputType = TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_DOUBLE;
			CellStyle style = cell.getCellStyle();
			if (style != null) {
				int formatIndex = style.getDataFormat();
				String formatString = style.getDataFormatString();
				if (DateUtil.isADateFormat(formatIndex, formatString)) {
					inputType = TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_DATE;
				} else {
					if (isAPercentageCell(formatString)) {
						inputType = TieWebSheetConstants.TIE_WEBSHEET_CELL_INPUT_TYPE_PERCENTAGE;
					}
				}
			}
		}
		return inputType;
	}

	/**
	 * Check weather the cell is percentage formatted
	 * 
	 * @param cell
	 * @return true if it's percentage formatted
	 */
	private boolean isAPercentageCell(String formatString) {

		if (formatString == null) {
			return false;
		}
		if (formatString.indexOf("%") < 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * get decimal places from format string e.g. 0.00 will return 2
	 * 
	 * @param cell
	 * @return decimal places of the formatted string
	 */
	private short getDecimalPlacesFromFormat(Cell cell) {
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
	 * @return symbol of the formatted string
	 */
	private String getSymbolFromFormat(Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return null;
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return null;
		}
		if (formatString.indexOf("$") < 0) {
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
	 * @return symbol position of the formatted string
	 */
	private String getSymbolPositionFromFormat(Cell cell) {
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

	public int[] getRowColFromComponentAttributes(UIComponent target) {

		int rowIndex = (Integer) target.getAttributes().get("data-row");
		int colIndex = (Integer) target.getAttributes()
				.get("data-column");
		log.fine("getRowColFromComponentAttributes rowindex = "
				+ rowIndex + " colindex = " + colIndex);
		int[] list = { rowIndex, colIndex };
		return list;
	}

	// public int[] getRowColFromComponentName(UIComponent component) {
	// String[] parts = component.getClientId().split(":");
	// int row = Integer.parseInt(parts[parts.length - 2]);
	// int col = Integer.parseInt(parts[parts.length - 1].substring(6));
	// int[] list = { row, col };
	// return list;
	// }

	public String[] getRowColFromExcelReferenceName(String excelRef) {
		String[] parts = excelRef.split("\\$");
		String[] list = { "", "" };
		int i = parts.length;
		if (i > 1)
			list[1] = parts[1]; // column
		if (i > 2)
			list[0] = parts[2]; // row
		return list;
	}

	public boolean getRepeatBodyFromConfig(SheetConfiguration sheetConfig) {
		boolean repeatbody = false;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			repeatbody = true;
		}
		return repeatbody;
	}

	public int getInitRowsFromConfig(SheetConfiguration sheetConfig) {
		int initRows = 1;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1)
				initRows = 1;
		}
		return initRows;
	}

	public int getBodyBottomFromConfig(SheetConfiguration sheetConfig,
			int initRows) {

		int bottom = sheetConfig.getBodyCellRange().getBottomRow();
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			if (initRows > 1)
				bottom = bottom + initRows - 1;
		}
		return bottom;
	}

	public String getCellValueWithConfig(String targetCell, int datarow,
			int initialRows, SheetConfiguration sheetConfig, Sheet sheet) {
		Cell cell = getCellReferenceWithConfig(targetCell, datarow,
				initialRows, sheetConfig, sheet);
		if (cell != null) {
			return getCellValueWithoutFormat(cell);
		}
		return "";
	}

	public Cell getCellReferenceWithConfig(String targetCell,
			int datarow, int initialRows, SheetConfiguration sheetConfig,
			Sheet sheet) {

		String[] rowcol = getRowColFromExcelReferenceName(targetCell);
		if (rowcol[0].isEmpty()) {
			if (rowcol[1].isEmpty())
				return null; // both empty meaning not valid targetcell
			targetCell = "$"
					+ rowcol[1]
					+ "$"
					+ (datarow
							+ sheetConfig.getBodyCellRange().getTopRow() + 1);
		} else {
			int row = Integer.parseInt(rowcol[0]);
			if ((sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat"))
					&& (row > (sheetConfig.getBodyCellRange().getTopRow() + 1))) {
				targetCell = "$" + rowcol[1] + "$"
						+ (row + initialRows - 1);
			}
		}
		Cell cell = TieWebSheetUtility.getCellByReference(targetCell,
				sheet);
		return cell;
	}

	public FacesCell getFacesCellFromBodyRow(int bodyrow, int bodycol,
			List<FacesRow> bodyRows) {
		FacesCell cell = null;
		try {
			if (bodyRows.get(bodyrow).getCells().size() > bodycol)
				cell = bodyRows.get(bodyrow).getCells().get(bodycol);
		} catch (Exception e) {
			log.severe("Web Form WebFormHelper getFacesCellFromBodyRow Error bodyrow = "
					+ bodyrow
					+ " bodycol = "
					+ bodycol
					+ "; error = "
					+ e.getLocalizedMessage());
		}
		return cell;
	}

	public Cell getPoiCellWithRowColFromCurrentPage(int rowIndex,
			int colIndex) {
		if (parent.getWb() != null) {
			return getPoiCellFromSheet(rowIndex, colIndex, parent.getWb()
					.getSheetAt(parent.getWb().getActiveSheetIndex()));
		}
		return null;
	}

	public Cell getPoiCellWithRowColFromTab(int rowIndex, int colIndex,
			String tabName) {
		if (parent.getWb() != null) {
			return getPoiCellFromSheet(
					rowIndex,
					colIndex,
					parent.getWb().getSheet(
							parent.getSheetConfigMap().get(tabName)
									.getSheetName()));
		}
		return null;
	}

	private Cell getPoiCellFromSheet(int rowIndex, int colIndex,
			Sheet sheet1) {
		if ((sheet1 != null) && (sheet1.getRow(rowIndex) != null))
			return sheet1.getRow(rowIndex).getCell(colIndex);
		return null;
	}

	public FacesCell getFacesCellWithRowColFromCurrentPage(int rowIndex,
			int colIndex) {
		if (parent.getBodyRows() != null) {
			int top = parent.getCurrentTopRow();
			int left = parent.getCurrentLeftColumn();
			List<FacesCell> cellList = parent.getBodyRows()
					.get(rowIndex - top).getCells();
			return parent.getBodyRows().get(rowIndex - top).getCells()
					.get(colIndex - left);
		}
		return null;
	}

}
