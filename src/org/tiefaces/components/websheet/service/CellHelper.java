/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.util.List;
import java.util.logging.Logger;


import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.configuration.ConfigRangeAttrs;
import org.tiefaces.components.websheet.configuration.ConfigurationHelper;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.MapObject;

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
	private static final Logger LOG = Logger.getLogger(CellHelper.class
			.getName());

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
	public final void restoreDataContext(final String fullName) {

		if ((parent.getCurrentDataContextName() == null)
				|| (!parent.getCurrentDataContextName().equalsIgnoreCase(
						fullName))) {
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
			LOG.severe(" recalc formula error : "
					+ ex.getLocalizedMessage());
		}

	}

	/**
	 * evaluate boolean express.
	 *
	 * @param pscript
	 *            express.
	 * @return true (express is true) false ( express is false or invalid).
	 */
	public final boolean evalBoolExpression(final String pscript) {
		return CellUtility.evalBoolExpression(parent.getExpEngine(),
				pscript);
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
	public final Cell getPoiCellWithRowColFromCurrentPage(
			final int rowIndex, final int colIndex) {
		return CellUtility.getPoiCellWithRowColFromCurrentPage(rowIndex,
				colIndex, parent.getWb());
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
	public final Cell getPoiCellWithRowColFromTab(final int rowIndex,
			final int colIndex, final String tabName) {
		if (parent.getWb() != null) {

			return CellUtility.getPoiCellFromSheet(
					rowIndex,
					colIndex,
					parent.getWb().getSheet(
							parent.getSheetConfigMap().get(tabName)
									.getSheetName()));
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
	public final FacesCell getFacesCellWithRowColFromCurrentPage(
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
