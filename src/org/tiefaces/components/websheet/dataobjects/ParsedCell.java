/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import org.apache.poi.ss.util.CellReference;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

/**
 * The Class ParsedCell.
 */
public class ParsedCell {

	/** The sheet name. */
	private String sheetName;

	/** The row. */
	private int row;

	/** The col. */
	private int col;

	/**
	 * Instantiates a new parsed cell.
	 *
	 * @param psheetName
	 *            the sheet name
	 * @param prow
	 *            the row
	 * @param pcol
	 *            the col
	 */
	public ParsedCell(final String psheetName, final int prow, final int pcol) {
		super();
		this.sheetName = psheetName;
		this.row = prow;
		this.col = pcol;
	}

	/**
	 * Instantiates a new parsed cell.
	 *
	 * @param fullName
	 *            the full name
	 */
	public ParsedCell(final String fullName) {
		super();
		try {
			this.sheetName = TieWebSheetUtility
					.getSheetNameFromFullCellRefName(fullName);
			String cellrefName = TieWebSheetUtility
					.removeSheetNameFromFullCellRefName(fullName);
			CellReference ref = new CellReference(cellrefName);
			this.row = ref.getRow();
			this.col = ref.getCol();
		} catch (Exception ex) {
			throw new RuntimeException("Cannot parse fullname " + fullName
					+ " to ParsedCell. Error = "
					+ ex.getLocalizedMessage());
		}
	}

	/**
	 * Gets the sheet name.
	 *
	 * @return the sheet name
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Sets the sheet name.
	 *
	 * @param psheetName
	 *            the new sheet name
	 */
	public void setSheetName(final String psheetName) {
		this.sheetName = psheetName;
	}

	/**
	 * Gets the row.
	 *
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the row.
	 *
	 * @param prow
	 *            the new row
	 */
	public void setRow(final int prow) {
		this.row = prow;
	}

	/**
	 * Gets the col.
	 *
	 * @return the col
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Sets the col.
	 *
	 * @param pcol
	 *            the new col
	 */
	public void setCol(final int pcol) {
		this.col = pcol;
	}

}
