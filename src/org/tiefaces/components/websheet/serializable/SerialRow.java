/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.serializable;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * serialize workbook.
 * 
 * @author Jason Jiang
 *
 */
public class SerialRow implements Serializable {

	/**
	 * serial key.
	 */
	private static final long serialVersionUID = 1L;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(SerialRow.class.getName());

	/**
	 * cell is transient.
	 */
	private transient Row row;

	/**
	 * use for saving cell address when serialize.
	 */
	private int rowIndex = -1;

	/**
	 * 
	 */
	public SerialRow() {
		this(null);
		LOG.log(Level.INFO, "serial row constructor");
	}

	/**
	 * constructor.
	 * 
	 * @param row
	 *            row.
	 */

	public SerialRow(Row row) {
		this(row, -1);
	}

	/**
	 * constructor.
	 * 
	 * @param row
	 *            row.
	 * @param rowIndex
	 *            row index.
	 */

	public SerialRow(final Row row, final int rowIndex) {
		super();
		this.row = row;
		this.rowIndex = rowIndex;
	}

	/**
	 * save the row before serialize.
	 * 
	 * @param out
	 *            outputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void writeObject(final java.io.ObjectOutputStream out)
			throws IOException {
		LOG.log(Level.INFO, "before row write, save row address");
		this.rowIndex = this.getRow().getRowNum();
		LOG.log(Level.INFO, "serial row start default write objects");
		out.defaultWriteObject();
	}

	/**
	 * load the workbook from saving.
	 * 
	 * @param in
	 *            inputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void readObject(final java.io.ObjectInputStream in)
			throws IOException {
		try {
			LOG.log(Level.INFO, "serial row start default read objects");
			in.defaultReadObject();
		} catch (EncryptedDocumentException | ClassNotFoundException e) {
			LOG.log(Level.SEVERE, " error in readObject of serial row : "
					+ e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @return the row
	 */
	public final Row getRow() {
		return row;
	}

	/**
	 * @param prow
	 *            the row to set
	 */
	public final void setRow(final Row prow) {
		this.row = prow;
	}

	/**
	 * @return the rowIndex
	 */
	public final int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param prowIndex
	 *            the rowIndex to set
	 */
	public final void setRowIndex(final int prowIndex) {
		this.rowIndex = prowIndex;
	}

	/**
	 * recover row by using it's address.
	 * 
	 * @param sheet
	 *            sheet.
	 */
	public final void recover(final Sheet sheet) {

		if (rowIndex >= 0) {
			this.setRow(sheet.getRow(rowIndex));
			this.setRowIndex(-1);
		}

	}
	
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if (this.getRow() != null) {
		sb.append("row = " + this.getRow().getRowNum());
		}
		sb.append("}");
		return sb.toString();
	}
	

}