/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.context.RequestContext;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.configuration.CellControlsHelper;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;

/**
 * The Class ValidationHandler.
 */
public class ValidationHandler {

	/** The parent. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(ValidationHandler.class.getName());

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

	// / <summary>
	// / Description: validate single cell
	// / </summary>
	// / <param name="component">UIComponent which be validated</param>
	// / <param name="value">value will be validated</param>
	// / <param name="wb">Workbook reference</param>
	// / <param name="formulaEvaluator">FormulaEvaluator reference</param>
	// / <param name="sheetConfigMap"> Map which contain configuration</param>
	// / <param name="bodyRows">manage bean for datatable</param>
	// / <param name="engine">ScriptEngine for validation</param>
	// / <param name="currentTabName">current tab</param>
	// / <param name="useExistingValue"> if true, use cell value to replace the
	// value. </param>
	// / <param name="passEmptyCheck"> if true and value is empty, then pass the
	/**
	 * Validate.
	 *
	 * @param component
	 *            the component
	 * @param value
	 *            the value
	 * @param useExistingValue
	 *            the use existing value
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @throws ValidatorException
	 *             the validator exception
	 */
	// validation</param>
	public void validate(final UIComponent component, final String value,
			final boolean useExistingValue, final boolean passEmptyCheck)
			throws ValidatorException {
		// UIComponent client id = frmSubmissionWebform:webformtables:0:column2
		// frmSubmissionWebform:webformtables:0:j_idt107:2:column
		int[] rowcol = CellUtility
				.getRowColFromComponentAttributes(component);
		int row = rowcol[0];
		int col = rowcol[1];
		validateWithRowColInCurrentPage(row, col, value, useExistingValue,
				passEmptyCheck);
	}

	/**
	 * Refresh after status changed.
	 *
	 * @param oldStatus
	 *            the old status
	 * @param newStatus
	 *            the new status
	 * @param irow
	 *            the irow
	 * @param topRow
	 *            the top row
	 * @param icol
	 *            the icol
	 * @param leftCol
	 *            the left col
	 * @param cell
	 *            the cell
	 */
	private void refreshAfterStatusChanged(final boolean oldStatus,
			final boolean newStatus, final int irow, final int topRow,
			final int icol, final int leftCol, final FacesCell cell) {

		if (!newStatus) {
			cell.setErrormsg("");
		}
		cell.setInvalid(newStatus);
		if (oldStatus != newStatus) {
			RequestContext.getCurrentInstance().update(
					parent.getWebFormClientId() + ":" + (irow - topRow)
							+ ":group" + (icol - leftCol));
		}

	}

	/**
	 * Validate with row col in current page.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @param value
	 *            the value
	 * @param useExistingValue
	 *            the use existing value
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @throws ValidatorException
	 *             the validator exception
	 */
	public void validateWithRowColInCurrentPage(int row, int col,
			String value, boolean useExistingValue, boolean passEmptyCheck)
			throws ValidatorException {

		LOG.fine("validationwithrowcolincurrentpage row = " + row
				+ " col = " + col + " value = " + value);

		int topRow = parent.getCurrentTopRow();
		int leftCol = parent.getCurrentLeftColumn();

		FacesCell cell = CellUtility.getFacesCellFromBodyRow(row - topRow,
				col - leftCol, parent.getBodyRows());
		if (cell == null) {
			return;
		}

		Cell poiCell = parent.getCellHelper()
				.getPoiCellWithRowColFromCurrentPage(row, col);
		boolean oldStatus = cell.isInvalid();
		if (useExistingValue) {
			value = CellUtility.getCellValueWithoutFormat(poiCell);
		}

		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}

		if (passEmptyCheck && value.isEmpty()) {
			refreshAfterStatusChanged(oldStatus, false, row, topRow, col,
					leftCol, cell);
			return;
		}

		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(
				parent.getCurrentTabName());
		List<CellFormAttributes> cellAttributes = CellUtility
				.findCellAttributes(sheetConfig, poiCell, row, topRow);
		if (cellAttributes != null) {
			Sheet sheet1 = parent.getWb().getSheet(
					sheetConfig.getSheetName());
			for (CellFormAttributes attr : cellAttributes) {
				boolean pass = doValidation(value, attr,
						poiCell.getRowIndex(), sheet1);
				if (!pass) {
					String errmsg = attr.getMessage();
					if (errmsg == null) {
						errmsg = "Invalid input";
					}
					cell.setErrormsg(errmsg);
					refreshAfterStatusChanged(false, true, row, topRow,
							col, leftCol, cell);
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Valication",
							errmsg);
					LOG.severe("Web Form ValidationHandler validate failed = "
							+ errmsg + "; row =" + row + " col= " + col);
					throw new ValidatorException(msg);
				}

			}
		}
		refreshAfterStatusChanged(oldStatus, false, row, topRow, col,
				leftCol, cell);

	}

	/**
	 * Do validation.
	 *
	 * @param value
	 *            the value
	 * @param attr
	 *            the attr
	 * @param rowIndex
	 *            the row index
	 * @param sheet
	 *            the sheet
	 * @return true, if successful
	 * @throws ValidatorException
	 *             the validator exception
	 */
	private boolean doValidation(Object value, CellFormAttributes attr,
			int rowIndex, Sheet sheet) throws ValidatorException {
		String attrType = attr.getType().trim();
		boolean pass = true;
		if (attrType.equalsIgnoreCase("input")) {
			pass = FacesUtility.evalInputType(value.toString(),
					attr.getValue());

		} else if (attrType.equalsIgnoreCase("check")) {
			String attrValue = attr.getValue();
			attrValue = attrValue.replace("$value", value.toString() + "");
			attrValue = CellUtility.replaceExpressionWithCellValue(
					attrValue, rowIndex, sheet);
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
	public boolean validateCell(final UIComponent target) {

		try {
			validate(target, null, true, true);
		} catch (ValidatorException ex) {
			// use log.debug because mostly they are expected
			LOG.severe("Web Form ValidationHandler validateCell failed error = "
					+ ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate current page.
	 *
	 * @return true, if successful
	 */
	public boolean validateCurrentPage() {
		boolean allpass = true;
		boolean passEmptyCheck = true;

		try {
			Map<String, Object> viewMap = FacesContext.getCurrentInstance()
					.getViewRoot().getViewMap();
			Boolean fullvalidation = (Boolean) viewMap
					.get("fullValidation");
			if ((fullvalidation != null) && (fullvalidation)) {
				passEmptyCheck = false;
			}
		} catch (Exception ex) {
			LOG.fine("cannot get fullValidation from view map. error = "
					+ ex.getMessage());
		}

		int top = parent.getCurrentTopRow();
		for (int irow = 0; irow < parent.getBodyRows().size(); irow++) {
			if (!validateRowInCurrentPage(irow + top, passEmptyCheck)) {
				allpass = false;
			}
		}
		return allpass;
	}

	/**
	 * Validate row in current page.
	 *
	 * @param irow
	 *            the irow
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @return true, if successful
	 */
	public boolean validateRowInCurrentPage(final int irow,
			final boolean passEmptyCheck) {
		boolean pass = true;
		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(
				parent.getCurrentTabName());
		if (sheetConfig != null) {
			int top = sheetConfig.getBodyCellRange().getTopRow();
			int left = sheetConfig.getBodyCellRange().getLeftCol();
			List<FacesCell> cellRow = parent.getBodyRows().get(irow - top)
					.getCells();
			for (int icol = 0; icol < cellRow.size(); icol++) {
				try {
					validateWithRowColInCurrentPage(irow, icol + left,
							null, true, passEmptyCheck);
				} catch (ValidatorException ex) {
					pass = false;
				}
			}
		}
		return pass;
	}

	/**
	 * Find first invalid sheet.
	 *
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @return the string
	 */
	public String findFirstInvalidSheet(final boolean passEmptyCheck) {
		for (Map.Entry<String, SheetConfiguration> entry : parent
				.getSheetConfigMap().entrySet()) {
			SheetConfiguration sheetConfig = entry.getValue();
			String tabName = entry.getKey();
			Sheet sheet = parent.getWb().getSheet(
					sheetConfig.getSheetName());
			int initialRows = sheetConfig.getBodyInitialRows();
			int topRow = sheetConfig.getBodyCellRange().getTopRow();
			for (int datarow = 0; datarow < initialRows; datarow++) {
				if (!validateDataRow(datarow, initialRows, topRow, sheet,
						sheetConfig, passEmptyCheck)) {
					return tabName;
				}
			}
		}
		return null;
	}

	/**
	 * Validate data row.
	 *
	 * @param datarow
	 *            the datarow
	 * @param initialRows
	 *            the initial rows
	 * @param topRow
	 *            the top row
	 * @param sheet
	 *            the sheet
	 * @param sheetConfig
	 *            the sheet config
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @return true, if successful
	 */
	private boolean validateDataRow(int datarow, int initialRows,
			int topRow, Sheet sheet, SheetConfiguration sheetConfig,
			boolean passEmptyCheck) {

		boolean rowpass = true;
		for (Map.Entry<String, List<CellFormAttributes>> entry : sheetConfig
				.getCellFormAttributes().entrySet()) {
			String targetCell = entry.getKey();
			List<CellFormAttributes> attributeList = entry.getValue();
			Cell cell = null;
			for (CellFormAttributes attr : attributeList) {
				if ((attr.getType().equalsIgnoreCase("input") || attr
						.getType().equalsIgnoreCase("check"))) {
					if (cell == null) {
						cell = CellUtility.getCellReferenceWithConfig(
								targetCell, datarow, initialRows,
								sheetConfig, sheet);
					}
					if (cell != null) {
						String cellValue = CellUtility
								.getCellValueWithoutFormat(cell);
						if (!(passEmptyCheck && cellValue.isEmpty())) {
							if (!doValidation(cellValue, attr, topRow
									+ datarow, sheet)) {
								LOG.fine("Web Form ValidationHandler validateDatarow targetCell = "
										+ targetCell
										+ " validation failed; datarow="
										+ datarow);
								rowpass = false;
								break;
							}
						}
					}
				}
			}
		}
		return rowpass;
	}

}
