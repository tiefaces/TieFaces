/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.serializable;

import java.io.Serializable;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Serial Cell Address.
 * @author Jason Jiang
 *
 */
public class SerialCellAddress implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1764470854644006400L;


	/**
	 * row index.
	 */
	private int row = 0;
	/**
	 * column index.
	 */
	private int col = 0;

	/**
	 * Create a new CellAddress object.
	 *
	 * @param prow
	 *            Row index (first row is 0)
	 * @param pcolumn
	 *            Column index (first column is 0)
	 */
	public SerialCellAddress(final int prow, final int pcolumn) {
		super();
		this.row = prow;
		this.col = pcolumn;
	}


	/**
	 * Create a new CellAddress object.
	 *
	 * @param cell
	 *            the Cell to get the location of
	 */
	public SerialCellAddress(final Cell cell) {
		if (cell != null) {
			this.row = cell.getRowIndex();
			this.col = cell.getColumnIndex();
		}
	}

	/**
	 * Get the cell address row.
	 *
	 * @return row
	 */
	public final int getRow() {
		return row;
	}

	/**
	 * Get the cell address column.
	 *
	 * @return column
	 */
	public final int getColumn() {
		return col;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("row = " + this.row);
		sb.append(",");
		sb.append("col = " + this.col);
		sb.append("}");
		return sb.toString();
	}	
	
}
