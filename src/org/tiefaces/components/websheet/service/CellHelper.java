/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.dataobjects.CollectionObject;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.utility.CellUtility;
import org.tiefaces.components.websheet.utility.CommandUtility;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;
import org.tiefaces.components.websheet.utility.SaveAttrsUtility;

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
	private static final Logger LOG = Logger
			.getLogger(CellHelper.class.getName());

	/**
	 * Instantiates a new cell helper.
	 */
	public CellHelper() {
		super();
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

		String saveAttrList = SaveAttrsUtility
				.getSaveAttrListFromRow(poiCell.getRow());
		if (saveAttrList != null) {
			String saveAttr = SaveAttrsUtility.getSaveAttrFromList(
					poiCell.getColumnIndex(), saveAttrList);
			if (saveAttr != null) {
				String fullName = ConfigurationUtility
						.getFullNameFromRow(poiCell.getRow());
				if (fullName != null) {
					restoreDataContext(fullName);
					SaveAttrsUtility.saveDataToObjectInContext(
							parent.getSerialDataContext().getDataContext(), saveAttr, strValue,
							parent.getExpEngine());
				}
			}
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
			LOG.log(Level.SEVERE,
					" recalc formula error : " + ex.getLocalizedMessage(),
					ex);
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
		return CommandUtility.evalBoolExpression(parent.getExpEngine(),
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
	public final FacesCell getFacesCellWithRowColFromCurrentPage(
			final int rowIndex, final int colIndex) {
		if (parent.getBodyRows() != null) {
			int top = parent.getCurrent().getCurrentTopRow();
			int left = parent.getCurrent().getCurrentLeftColumn();
			return parent.getBodyRows().get(rowIndex - top).getCells()
					.get(colIndex - left);
		}
		return null;
	}

	/**
	 * Restore data context.
	 *
	 * @param fullName
	 *            the full name
	 * @return the collection object
	 */
	public final CollectionObject restoreDataContext(
			final String fullName) {

		CollectionObject collect = new CollectionObject();
		
		String[] parts = fullName.split(":");
		
		if (!isNeedRestore(fullName, parts)) {
			return collect;
		}

		boolean stopSkip = false;
		List<String> list = parent.getCurrent().getCurrentDataContextNameList();
		int listSize = list.size();

		// prepare collection data in context.
		// must loop through the full name which may have multiple
		// layer.
		// i.e. E.department.1:E.employee.0
		// need prepare department.1 and employee.0

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			boolean skip = false;
			if ((!stopSkip) && (i<listSize)) {
				String listPart = list.get(i);
				if (part.equalsIgnoreCase(listPart)) {
					skip = true;
				}
			}
			if (!skip) {
				stopSkip = true;
				startRestoreDataContext(collect, part);
			}	
		}
		if (stopSkip) {
			parent.getCurrent().setCurrentDataContextName(fullName);
		}	

		return collect;
	}

	/**
	 * Checks if is need restore.
	 *
	 * @param fullName
	 *            the full name
	 * @param parts
	 *            the parts
	 * @return true, if is need restore
	 */
	private boolean isNeedRestore(final String fullName,String[] parts) {
		if (fullName == null) {
			return false;
		}
		if ((parent.getCurrent().getCurrentDataContextName() != null)
				&& (parent.getCurrent().getCurrentDataContextName().toLowerCase().startsWith(fullName.toLowerCase()))) {
			return false;
		}

		if ((parts == null)||(parts.length<=1)) {
			return false;
		}
		return true;
	}

	/**
	 * Start restore data context.
	 *
	 * @param collect
	 *            the collect
	 * @param part
	 *            the part
	 */
	private void startRestoreDataContext(CollectionObject collect,
			String part) {
		if (part.startsWith(
				TieConstants.EACH_COMMAND_FULL_NAME_PREFIX)) {
			String[] varparts = part.split("\\.");
			collect.setEachCommand(CommandUtility.getEachCommandFromPartsName(
					parent.getCurrentSheetConfig().getCommandIndexMap(), varparts));
			collect.setLastCollection(ConfigurationUtility
					.transformToCollectionObject(parent.getExpEngine(),
							collect.getEachCommand().getItems(),
							parent.getSerialDataContext().getDataContext()));
			collect.setLastCollectionIndex(
					CommandUtility.prepareCollectionDataInContext(
							varparts, collect.getLastCollection(),
							parent.getSerialDataContext().getDataContext()));
		}
	}
	
}
