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
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
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
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.configuration.ConfigRangeAttrs;
import org.tiefaces.components.websheet.configuration.ConfigurationHelper;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.MapObject;
import org.tiefaces.components.websheet.utility.ColorUtility;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

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
	private static final Logger log = Logger.getLogger(
			Thread.currentThread().getStackTrace()[0].getClassName());

	/**
	 * Instantiates a new cell helper.
	 */
	public CellHelper() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new cell helper.
	 *
	 * @param pparent
	 *            parent bean.
	 */
	public CellHelper(final TieWebSheetBean pparent) {
		super();
		this.parent = pparent;
	}





	/**
	 * Save data in context.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @param strValue
	 *            the str value
	 */
	public final void saveDataInContext(final Cell poiCell,
			final String strValue) {

		String saveAttrList = ConfigurationHelper
				.getSaveAttrListFromRow(poiCell.getRow());
		if (saveAttrList != null) {
			String saveAttr = ConfigurationHelper.getSaveAttrFromList(
					poiCell.getColumnIndex(), saveAttrList);
			if (saveAttr != null) {
				String fullName = ConfigurationHelper
						.getFullNameFromRow(poiCell.getRow());
				if (fullName != null) {
					restoreDataContext(fullName);
					ConfigurationHelper.saveDataToObjectInContext(
							parent.getDataContext(), saveAttr, strValue,
							parent.getExpEngine());
				}
			}
		}
	}

	/**
	 * Restore data context.
	 *
	 * @param fullName
	 *            the full name
	 */
	private void restoreDataContext(final String fullName) {

		if ((parent.getCurrentDataContextName() == null) || (!parent
				.getCurrentDataContextName().equalsIgnoreCase(fullName))) {
			SheetConfiguration sheetConfig = parent.getSheetConfigMap()
					.get(parent.getCurrentTabName());
			ConfigRangeAttrs attrs = sheetConfig.getShiftMap()
					.get(fullName);
			if ((attrs != null) && (attrs.getContextSnap() != null)) {
				List<MapObject> mapList = attrs.getContextSnap()
						.getSnapList();
				if (mapList != null) {
					for (MapObject mObj : mapList) {
						parent.getDataContext().put((String) mObj.getKey(),
								mObj.getValue());
					}
				}
			}
			parent.setCurrentDataContextName(fullName);
		}
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
			log.severe(
					" recalc formula error : " + ex.getLocalizedMessage());
		}

	}

	/**
	 * evaluate boolean express.
	 *
	 * @param pscript
	 *            express.
	 * @return true (express is true) false ( express is false or invalid).
	 */
	public boolean evalBoolExpression(final String pscript) {
		return CellUtility.evalBoolExpression(parent.getExpEngine(), pscript);
	}





	/**
	 * Gets the poi cell with row col from current page.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @return the poi cell with row col from current page
	 */
	public Cell getPoiCellWithRowColFromCurrentPage(final int rowIndex,
			final int colIndex) {
		return CellUtility.getPoiCellWithRowColFromCurrentPage(rowIndex, colIndex, parent.getWb());
	}

	/**
	 * Gets the poi cell with row col from tab.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @param tabName
	 *            the tab name
	 * @return the poi cell with row col from tab
	 */
	public Cell getPoiCellWithRowColFromTab(final int rowIndex,
			final int colIndex, final String tabName) {
		if (parent.getWb() != null) {
			
			return CellUtility.getPoiCellFromSheet(rowIndex, colIndex,
					parent.getWb().getSheet(parent.getSheetConfigMap()
							.get(tabName).getSheetName()));
		}
		return null;
	}


	/**
	 * Gets the faces cell with row col from current page.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @return the faces cell with row col from current page
	 */
	public FacesCell getFacesCellWithRowColFromCurrentPage(
			final int rowIndex, final int colIndex) {
		if (parent.getBodyRows() != null) {
			int top = parent.getCurrentTopRow();
			int left = parent.getCurrentLeftColumn();
			return parent.getBodyRows().get(rowIndex - top).getCells()
					.get(colIndex - left);
		}
		return null;
	}

}
