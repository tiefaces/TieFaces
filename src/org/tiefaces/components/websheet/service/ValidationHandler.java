/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.context.RequestContext;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
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
		if ((oldStatus != newStatus)
				&& (parent.getWebFormClientId() != null)) {
			RequestContext.getCurrentInstance()
					.update(parent.getWebFormClientId() + ":"
							+ (irow - topRow) + ":group"
							+ (icol - leftCol));
		}

	}

	/**
	 * Validate with row col in current page.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @throws ValidatorException
	 *             the validator exception
	 */
	public void validateWithRowColInCurrentPage(final int row,
			final int col, final boolean passEmptyCheck)
			throws ValidatorException {

		int topRow = parent.getCurrentTopRow();
		int leftCol = parent.getCurrentLeftColumn();

		FacesCell cell = CellUtility.getFacesCellFromBodyRow(row, col,
				parent.getBodyRows(), topRow, leftCol);
		if (cell == null) {
			return;
		}

		Cell poiCell = parent.getCellHelper()
				.getPoiCellWithRowColFromCurrentPage(row, col);
		boolean oldStatus = cell.isInvalid();

		String value = CellUtility.getCellValueWithoutFormat(poiCell);
		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}

		LOG.fine("validationwithrowcolincurrentpage row = " + row
				+ " col = " + col + " value = " + value);

		if (passEmptyCheck && value.isEmpty()) {
			refreshAfterStatusChanged(oldStatus, false, row, topRow, col,
					leftCol, cell);
			return;
		}

		SheetConfiguration sheetConfig = parent.getSheetConfigMap()
				.get(parent.getCurrentTabName());
		List<CellFormAttributes> cellAttributes = CellUtility
				.findCellValidateAttributes(
						parent.getCellAttributesMap()
								.getCellValidateAttributes(),
						poiCell);
		if (cellAttributes != null) {
			Sheet sheet1 = parent.getWb()
					.getSheet(sheetConfig.getSheetName());
			for (CellFormAttributes attr : cellAttributes) {
				boolean pass = doValidation(value, attr,
						poiCell.getRowIndex(), sheet1);
				if (!pass) {
					String errmsg = attr.getMessage();
					if (errmsg == null) {
						errmsg = TieConstants.DEFALT_MSG_INVALID_INPUT;
					}
					cell.setErrormsg(errmsg);
					refreshAfterStatusChanged(false, true, row, topRow, col,
							leftCol, cell);
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR, errmsg, errmsg);
					LOG.log(Level.FINE,
							"Web sheet validationHandler validate failed = "
									+ errmsg + "; row =" + row + " col= "
									+ col);
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
	private boolean doValidation(final Object value,
			final CellFormAttributes attr, final int rowIndex,
			final Sheet sheet) throws ValidatorException {
		boolean pass;

		String attrValue = attr.getValue();
		attrValue = attrValue.replace("$value", value.toString() + "");
		attrValue = CellUtility.replaceExpressionWithCellValue(attrValue,
				rowIndex, sheet);
		pass = parent.getCellHelper().evalBoolExpression(attrValue);
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

		try {
			int[] rowcol = CellUtility
					.getRowColFromComponentAttributes(target);
			int row = rowcol[0];
			int col = rowcol[1];
			validateWithRowColInCurrentPage(row, col, true);
		} catch (ValidatorException ex) {
			// use log.debug because mostly they are expected
			LOG.log(Level.SEVERE,
					"Web Form ValidationHandler validateCell failed error = "
							+ ex.getLocalizedMessage(),
					ex);
			return false;
		}
		return true;
	}

	/**
	 * Validate current page.
	 *
	 * @return true, if successful
	 */
	public final boolean validateCurrentPage() {
		boolean allpass = true;
		boolean passEmptyCheck = true;

		try {
			if (FacesContext.getCurrentInstance() != null) {
				Map<String, Object> viewMap = FacesContext
						.getCurrentInstance().getViewRoot().getViewMap();
				Boolean fullvalidation = (Boolean) viewMap
						.get("fullValidation");
				if ((fullvalidation != null) && (fullvalidation)) {
					passEmptyCheck = false;
				}
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"cannot get fullValidation from view map. error = "
							+ ex.getMessage(),
					ex);
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
	public final boolean validateRowInCurrentPage(final int irow,
			final boolean passEmptyCheck) {
		SheetConfiguration sheetConfig = parent.getSheetConfigMap()
				.get(parent.getCurrentTabName());
		return this.validateRow(irow, passEmptyCheck, sheetConfig);
	}

	/**
	 * Find first invalid sheet.
	 *
	 * @param passEmptyCheck
	 *            the pass empty check
	 * @return the string
	 */
	public final String findFirstInvalidSheet(
			final boolean passEmptyCheck) {
		for (Map.Entry<String, SheetConfiguration> entry : parent
				.getSheetConfigMap().entrySet()) {
			SheetConfiguration sheetConfig = entry.getValue();
			String tabName = entry.getKey();
			int topRow = sheetConfig.getBodyCellRange().getTopRow();
			for (int irow = 0; irow < parent.getBodyRows().size(); irow++) {
				if (!validateRow(irow + topRow, passEmptyCheck,
						sheetConfig)) {
					return tabName;
				}
			}
		}
		return null;
	}

	/**
	 * Validate data row.
	 * 
	 * @param irow
	 *            row number.
	 * @param passEmptyCheck
	 *            whether pass empty cell.
	 * @param sheetConfig
	 *            sheet config.
	 * @return true if passed validation.
	 */
	private boolean validateRow(final int irow,
			final boolean passEmptyCheck,
			final SheetConfiguration sheetConfig) {
		boolean pass = true;
		if (sheetConfig != null) {
			int top = sheetConfig.getBodyCellRange().getTopRow();
			List<FacesCell> cellRow = parent.getBodyRows().get(irow - top)
					.getCells();
			for (int index = 0; index < cellRow.size(); index++) {
				try {
					FacesCell fcell = cellRow.get(index);
					if (fcell != null) {
						int colIndex = cellRow.get(index).getColumnIndex();
						validateWithRowColInCurrentPage(irow, colIndex,
								passEmptyCheck);
					}
				} catch (ValidatorException ex) {
					LOG.log(Level.SEVERE, "failed validateRow error = "
							+ ex.getLocalizedMessage(), ex);
					pass = false;
				}
			}
		}
		return pass;
	}

}
