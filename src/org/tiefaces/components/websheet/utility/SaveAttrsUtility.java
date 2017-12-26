/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.utility;

import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;

/**
 * Helper class for save attributes.
 */
public final class SaveAttrsUtility {

	/**
	 * hide constructor.
	 */
	private SaveAttrsUtility() {
		// not called
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
				) {
			String saveAttr = SaveAttrsUtility.parseSaveAttrString(
					cell.getStringCellValue());
			if (!saveAttr.isEmpty()) {
				return TieConstants.CELL_ADDR_PRE_FIX + cell.getColumnIndex() + "=" + saveAttr + ",";
			}
		}
		return "";
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
			strObject = TieConstants.METHOD_PREFIX + strObject + TieConstants.METHOD_END;
			Object object = CommandUtility.evaluate(strObject, context, engine);
			CellControlsUtility.setObjectProperty(object, strMethod,
					strValue, true);
		}
	}
	
	/**
	 * reload the data from context to websheet row.
	 * @param context context.
	 * @param fullSaveAttr full saveattr.
	 * @param row row.
	 * @param engine engine.
	 */

	public static void refreshSheetRowFromContext(
			final Map<String, Object> context, final String fullSaveAttr,
			final Row row, final ExpressionEngine engine) {
		if (!fullSaveAttr.startsWith(TieConstants.CELL_ADDR_PRE_FIX)) {
			return;
		}
		int ipos = fullSaveAttr.indexOf('=');
		if (ipos>0) {
			String columnIndex = fullSaveAttr.substring(1, ipos);
			String saveAttr = fullSaveAttr.substring(ipos+1);
			Cell cell = row.getCell(Integer.parseInt(columnIndex));
			CommandUtility.evaluateNormalCells(cell,TieConstants.METHOD_PREFIX+saveAttr+TieConstants.METHOD_END, context, engine);
		}
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
			String str = TieConstants.CELL_ADDR_PRE_FIX + columnIndex + "=";
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
	 * get the columnIndex from saveAttr.
	 * saveAttr format as: $columnIndex=xxxxxxx
	 * 
	 * @param saveAttr saveAttr
	 * @return columnIndex String
	 */
	public static String getColumnIndexFromSaveAttr(final String saveAttr) {
		if ((saveAttr != null) && (!saveAttr.isEmpty())) {
			int iend = saveAttr.indexOf('=');
			if (iend > 0) {
				int istart = saveAttr.indexOf('$');
				if (iend > istart) {
					return saveAttr.substring(istart + 1, iend);
	
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
		Cell saveAttrCell = cell.getRow()
				.getCell(TieConstants.HIDDEN_SAVE_OBJECTS_COLUMN);
		if (saveAttrCell != null) {
			return isHasSaveAttr(cell,
					saveAttrCell.getStringCellValue());
		}
		return false;
	}

	/**
	 * Checks if is checks for save attr.
	 *
	 * @param cell
	 *            the cell
	 * @param saveAttrs
	 *            the save attrs
	 * @return true, if is checks for save attr
	 */
	public static boolean isHasSaveAttr(final Cell cell,
			final String saveAttrs) {
		
		if (cell != null) {
			int columnIndex = cell.getColumnIndex();
			String str = TieConstants.CELL_ADDR_PRE_FIX + columnIndex + "=";
			if ((saveAttrs != null) && (saveAttrs.indexOf(str) >= 0)) {
				return true;
			}
		}	
		return false;
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
				setSaveAttrsForRow(row);
			}
		}
	}

	/**
	 * set SaveAttrs For Row.
	 * 
	 * @param row
	 *            row.
	 */
	private static void setSaveAttrsForRow(final Row row) {
		StringBuilder saveAttr = new StringBuilder();
		for (Cell cell : row) {
			String sAttr = parseSaveAttr(cell);
			if (!sAttr.isEmpty()) {
				saveAttr.append(sAttr);
			}
		}
		if (saveAttr.length() > 0) {
			SaveAttrsUtility.setSaveObjectsInHiddenColumn(row,
					saveAttr.toString());
		}
	}



}
