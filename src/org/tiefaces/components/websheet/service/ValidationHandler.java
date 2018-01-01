/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.TieCell;
import org.tiefaces.components.websheet.utility.CellControlsUtility;
import org.tiefaces.components.websheet.utility.CellUtility;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;
import org.tiefaces.components.websheet.utility.SaveAttrsUtility;


/**
 * The Class ValidationHandler.
 */
public class ValidationHandler {

	/** The parent. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger LOG = Logger.getLogger(ValidationHandler.class.getName());

	/**
	 * Instantiates a new validation handler.
	 *
	 * @param pparent
	 *            the parent
	 */
	public ValidationHandler(final TieWebSheetBean pparent) {
		super();
		this.parent = pparent;

	}

	/**
	 * Refresh after status changed.
	 *
	 * @param oldStatus
	 *            the old status
	 * @param newStatus
	 *            the new status
	 * @param formRow
	 *            the form row
	 * @param formCol
	 *            the form col
	 * @param cell
	 *            the cell
	 * @param updateGui
	 *            update gui flag.
	 *            if true then update gui.
	 */
	private void refreshAfterStatusChanged(final boolean oldStatus, final boolean newStatus, final int formRow,
			final int formCol, final FacesCell cell, final boolean updateGui) {

		if (!newStatus) {
			cell.setErrormsg("");
		}
		cell.setInvalid(newStatus);
		if (updateGui && (oldStatus != newStatus) && (parent.getWebFormClientId() != null)) {
			RequestContext.getCurrentInstance()
					.update(parent.getWebFormClientId() + ":" + (formRow) + ":group" + (formCol));
		}

	}

	/**
	 * Validate with row col in current page.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @param updateGui
	 * 			  if true then update gui. 
	 * @return true, if successful
	 */
	public boolean validateWithRowColInCurrentPage(final int row, final int col, boolean updateGui) {

		//until now passEmptyCheck has one to one relation to submitMode
		//e.g. when passEmptyCheck = false, then submitMode = true.
		
		boolean submitMode = parent.getSubmitMode();
		boolean passEmptyCheck = !submitMode;
		
		int topRow = parent.getCurrent().getCurrentTopRow();
		int leftCol = parent.getCurrent().getCurrentLeftColumn();
		boolean pass = true;

		FacesRow fRow = CellUtility.getFacesRowFromBodyRow(row, parent.getBodyRows(), topRow);
		if (fRow == null) {
			return pass;
		}

		FacesCell cell = CellUtility.getFacesCellFromBodyRow(row, col, parent.getBodyRows(), topRow, leftCol);
		if (cell == null) {
			return pass;
		}

		Cell poiCell = parent.getCellHelper().getPoiCellWithRowColFromCurrentPage(row, col);
		boolean oldStatus = cell.isInvalid();

		String value = CellUtility.getCellValueWithoutFormat(poiCell);
		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}

		if (passEmptyCheck && value.isEmpty()) {
			refreshAfterStatusChanged(oldStatus, false, row - topRow, col - leftCol, cell, updateGui);
			return pass;
		}

		if (((parent.isOnlyValidateInSubmitMode() && submitMode ) || !parent.isOnlyValidateInSubmitMode())
		&& !validateByTieWebSheetValidationBean(poiCell, topRow, leftCol, cell, value, updateGui)) {
			return false;
		}
		

		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(parent.getCurrent().getCurrentTabName());
		List<CellFormAttributes> cellAttributes = CellControlsUtility.findCellValidateAttributes(
				parent.getCellAttributesMap().getCellValidateAttributes(), fRow.getOriginRowIndex(), poiCell);

		if (parent.isAdvancedContext() && parent.getConfigAdvancedContext().getErrorSuffix() != null
				&& !checkErrorMessageFromObjectInContext(row - topRow, col - leftCol, cell, poiCell, value,
						sheetConfig, updateGui)) {
			return false;
		}

		if (cellAttributes != null) {
			pass = validateAllRulesForSingleCell(row - topRow, col - leftCol, cell, poiCell, value, sheetConfig,
					cellAttributes, updateGui);
		}

		if (pass) {
			refreshAfterStatusChanged(oldStatus, false, row - topRow, col - leftCol, cell, updateGui);
		}
		return pass;

	}

	/**
	 * Validate by tie web sheet validation bean.
	 *
	 * @param poiCell the poi cell
	 * @param topRow the top row
	 * @param leftCol the left col
	 * @param cell the cell
	 * @param value the value
	 * @param updateGui the update gui
	 * @return true, if successful
	 */
	private boolean validateByTieWebSheetValidationBean(final Cell poiCell, final int topRow, final int leftCol,
			final FacesCell cell, final String value, boolean updateGui) {
		if (parent.getTieWebSheetValidationBean() != null) {
			String errormsg = null;
			String fullName = ConfigurationUtility.getFullNameFromRow(poiCell.getRow());
			String saveAttr = SaveAttrsUtility.prepareContextAndAttrsForCell(poiCell, fullName, parent.getCellHelper());
			if (saveAttr != null) {
				int row = poiCell.getRowIndex();
				int col = poiCell.getColumnIndex();
				errormsg = parent.getTieWebSheetValidationBean().validate(
						parent.getSerialDataContext().getDataContext(), saveAttr, ConfigurationUtility
								.getFullNameFromRow(poiCell.getRow()), poiCell.getSheet().getSheetName(),
						row, col, value);
				if ((errormsg != null) && (!errormsg.isEmpty())) {
					cell.setErrormsg(errormsg);
					refreshAfterStatusChanged(false, true, row - topRow, col - leftCol, cell, updateGui);
					return false;
				}
			}
		}
		return true;
	}

	
	/**
	 * Check error message from object in context.
	 *
	 * @param formRow the form row
	 * @param formCol the form col
	 * @param cell the cell
	 * @param poiCell the poi cell
	 * @param value the value
	 * @param sheetConfig the sheet config
	 * @param updateGui the update gui
	 * @return true, if successful
	 */
	private boolean checkErrorMessageFromObjectInContext(final int formRow, final int formCol, final FacesCell cell,
			final Cell poiCell, final String value, final SheetConfiguration sheetConfig, boolean updateGui) {

		@SuppressWarnings("unchecked")
		HashMap<String, TieCell> tieCells = (HashMap<String, TieCell>) parent.getSerialDataContext().getDataContext()
				.get("tiecells");

		if (tieCells != null) {

			TieCell tieCell = tieCells.get(CellUtility.getSkeyFromPoiCell(poiCell));

			if (tieCell != null && tieCell.getContextObject() != null) {

				String errorMethod = tieCell.getMethodStr() + parent.getConfigAdvancedContext().getErrorSuffix();

				String errorMessage = CellControlsUtility.getObjectPropertyValue(tieCell.getContextObject(),
						errorMethod, true);

				if (errorMessage != null && !errorMessage.isEmpty()) {
					cell.setErrormsg(errorMessage);
					LOG.log(Level.INFO, "Validation failed for sheet {0} row {1} column {2} : {3}",
							new Object[] { poiCell.getSheet().getSheetName(), poiCell.getRowIndex(),
									poiCell.getColumnIndex(), errorMessage });
					refreshAfterStatusChanged(false, true, formRow, formCol, cell, updateGui);
					return false;
				}

			}
		}

		return true;

	}

	/**
	 * Validate all rules for single cell.
	 *
	 * @param formRow            the form row
	 * @param formCol            the form col
	 * @param cell            the cell
	 * @param poiCell            the poi cell
	 * @param value            the value
	 * @param sheetConfig            the sheet config
	 * @param cellAttributes            the cell attributes
	 * @param updateGui the update gui
	 * @return true, if successful
	 */
	private boolean validateAllRulesForSingleCell(final int formRow, final int formCol, final FacesCell cell,
			final Cell poiCell, final String value, final SheetConfiguration sheetConfig,
			final List<CellFormAttributes> cellAttributes, boolean updateGui) {
		Sheet sheet1 = parent.getWb().getSheet(sheetConfig.getSheetName());
		for (CellFormAttributes attr : cellAttributes) {
			boolean pass = doValidation(value, attr, poiCell.getRowIndex(), poiCell.getColumnIndex(), sheet1);
			if (!pass) {
				String errmsg = attr.getMessage();
				if (errmsg == null) {
					errmsg = TieConstants.DEFALT_MSG_INVALID_INPUT;
				}
				cell.setErrormsg(errmsg);
				LOG.log(Level.INFO, "Validation failed for sheet {0} row {1} column {2} : {3}", new Object[] {
						poiCell.getSheet().getSheetName(), poiCell.getRowIndex(), poiCell.getColumnIndex(), errmsg });
				refreshAfterStatusChanged(false, true, formRow, formCol, cell, updateGui);
				return false;
			}

		}
		return true;
	}

	/**
	 * Do validation.
	 *
	 * @param value            the value
	 * @param attr            the attr
	 * @param rowIndex            the row index
	 * @param colIndex the col index
	 * @param sheet            the sheet
	 * @return true, if successful
	 */
	private boolean doValidation(final Object value, final CellFormAttributes attr, final int rowIndex,
			final int colIndex, final Sheet sheet) {
		boolean pass;

		String attrValue = attr.getValue();
		attrValue = attrValue.replace("$value", value.toString() + "").replace("$rowIndex", rowIndex + "")
				.replace("$colIndex", colIndex + "").replace("$sheetName", sheet.getSheetName());
		attrValue = ConfigurationUtility.replaceExpressionWithCellValue(attrValue, rowIndex, sheet);
		if (attrValue.contains(TieConstants.EL_START)) {
			Object returnObj = FacesUtility.evaluateExpression(attrValue, Object.class);
			attrValue = returnObj.toString();
			pass = Boolean.parseBoolean(attrValue);
		} else {
			pass = parent.getCellHelper().evalBoolExpression(attrValue);
		}
		return pass;

	}

	/**
	 * Validate cell.
	 *
	 * @param target
	 *            the target
	 * @return true, if successful
	 */
	public final boolean validateCell(final UIComponent target) {

		int[] rowcol = CellUtility.getRowColFromComponentAttributes(target);
		int row = rowcol[0];
		int col = rowcol[1];
		return validateWithRowColInCurrentPage(row, col, true);
	}

	/**
	 * Validate current page.
	 *
	 * @return true, if successful
	 */
	public final boolean validateCurrentPage() {
		boolean allpass = true;

		int top = parent.getCurrent().getCurrentTopRow();
		for (int irow = 0; irow < parent.getBodyRows().size(); irow++) {
			if (!validateRowInCurrentPage(irow + top, false)) {
				allpass = false;
			}
		}
		return allpass;
	}


	/**
	 * Validate row in current page.
	 *
	 * @param irow the irow
	 * @param updateGui the update gui
	 * @return true, if successful
	 */
	public final boolean validateRowInCurrentPage(final int irow, final boolean updateGui) {
		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(parent.getCurrent().getCurrentTabName());
		return this.validateRow(irow, sheetConfig, updateGui);
	}



	/**
	 * Validate row.
	 *
	 * @param irow the irow
	 * @param sheetConfig the sheet config
	 * @param updateGui the update gui
	 * @return true, if successful
	 */
	private boolean validateRow(final int irow, final SheetConfiguration sheetConfig, boolean updateGui) {
		boolean pass = true;
		if (sheetConfig == null) {
			return pass;
		}
		int top = sheetConfig.getBodyCellRange().getTopRow();
		List<FacesCell> cellRow = parent.getBodyRows().get(irow - top).getCells();
		for (int index = 0; index < cellRow.size(); index++) {
			FacesCell fcell = cellRow.get(index);
			if ((fcell != null) && (!validateWithRowColInCurrentPage(irow, fcell.getColumnIndex(), updateGui))) {
				pass = false;
			}
		}
		return pass;
	}

	/**
	 * Triggered when value in cells changed. e.g. user edit cell.
	 * 
	 * @param event
	 *            ajax event.
	 */
	public void valueChangeEvent(final AjaxBehaviorEvent event) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			String tblName = parent.getWebFormClientId();
			UIComponent target = event.getComponent();
			boolean pass = validateCell(target);
			if (pass) {
				// to improve performance, re-validate current row only
				// page validation take times. will happen when change tab(page)
				// or
				// reload page.
				int[] rowcol = CellUtility.getRowColFromComponentAttributes(target);
				validateRowInCurrentPage(rowcol[0], true);
				refreshCachedCellsInCurrentPage(facesContext, tblName);
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Validation error:" + ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Refresh cached cells in current page.
	 *
	 * @param facesContext
	 *            the faces context
	 * @param tblName
	 *            the tbl name
	 */
	private void refreshCachedCellsInCurrentPage(final FacesContext facesContext, final String tblName) {
		// refresh current page calculation fields
		UIComponent s = facesContext.getViewRoot().findComponent(tblName);
		if (s == null) {
			return;
		}
		DataTable webDataTable = (DataTable) s;
		int first = webDataTable.getFirst();
		int rowsToRender = webDataTable.getRowsToRender();
		int rowCounts = webDataTable.getRowCount();
		int top = parent.getCurrent().getCurrentTopRow();
		int left = parent.getCurrent().getCurrentLeftColumn();

		for (int i = first; i <= (first + rowsToRender); i++) {
			if (i < rowCounts) {
				refreshCachedCellsInRow(tblName, top, left, i);
			}

		}
	}

	/**
	 * Refresh cached cells in row.
	 *
	 * @param tblName
	 *            the tbl name
	 * @param top
	 *            the top
	 * @param left
	 *            the left
	 * @param i
	 *            the i
	 */
	private void refreshCachedCellsInRow(final String tblName, final int top, final int left, final int i) {
		FacesRow dataRow = parent.getBodyRows().get(i);
		int isize = dataRow.getCells().size();
		for (int index = 0; index < isize; index++) {
			FacesCell fcell = dataRow.getCells().get(index);
			Cell poiCell = parent.getCellHelper().getPoiCellWithRowColFromCurrentPage(i + top, index + left);
			if (poiCell != null) {
				parent.getHelper().getWebSheetLoader().refreshCachedCell(tblName, i, index, poiCell, fcell);
			}
		}
	}

	/**
	 * set submit mode flag with javascript for holding in client side.
	 * 
	 * @param fullflag
	 *            true or false
	 */
	public void setSubmitModeInView(final Boolean fullflag) {

		if (FacesContext.getCurrentInstance() != null) {
			Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
			if (viewMap != null) {
				Boolean flag = (Boolean) viewMap.get(TieConstants.SUBMITMODE);
				if ((flag == null) || (!flag.equals(fullflag))) {
					viewMap.put(TieConstants.SUBMITMODE, fullflag);
				}
			}
		}
	}

	/**
	 * triggered validation process before actions like save or submit.
	 * 
	 * @return true (pass) false (failed)
	 */
	public boolean preValidation() {
				
		String currentTabName = parent.getCurrent().getCurrentTabName();
		String tabName = null;
		String firstInvalidTabName = null;
		boolean reload = false;
		for (Map.Entry<String, SheetConfiguration> entry : parent.getSheetConfigMap().entrySet()) {
			tabName = entry.getKey();
			// if not reload and tabname==current then skip reloading.
			if (reload || (!tabName.equals(currentTabName))) {
				parent.getWebSheetLoader().prepareWorkShee(tabName);
				reload = true;
			}
			if (!parent.getValidationHandler().validateCurrentPage() &&
				(firstInvalidTabName == null)) {
				firstInvalidTabName = tabName; 
			}
		}		
		if (firstInvalidTabName != null)  {
			if (!tabName.equals(firstInvalidTabName)) {
				parent.getHelper().getWebSheetLoader().loadWorkSheet(firstInvalidTabName);
			}
			return false;
		}
		return true;
	}

}
