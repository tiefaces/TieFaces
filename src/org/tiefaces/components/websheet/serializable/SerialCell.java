/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.serializable;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * serialize workbook.
 * 
 * @author Jason Jiang
 *
 */
public class SerialCell implements Serializable {

	/**
	 * serial key.
	 */
	private static final long serialVersionUID = 1L;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(SerialCell.class.getName());

	/**
	 * cell is transient.
	 */
	private transient Cell cell;

	/**
	 * use for saving cell address when serialize.
	 */
	private SerialCellAddress cellAddr;

	/**
	 * 
	 */
	public SerialCell() {
		super();
		LOG.log(Level.INFO, "serial cell constructor");
	}

	/**
	 * Constructor.
	 * 
	 * @param pcell
	 *            cell.
	 * @param pcellAddr
	 *            cell address.
	 */
	public SerialCell(final Cell pcell, final SerialCellAddress pcellAddr) {
		super();
		this.cell = pcell;
		this.cellAddr = pcellAddr;
	}

	/**
	 * save the cell before serialize.
	 * 
	 * @param out
	 *            outputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void writeObject(final java.io.ObjectOutputStream out)
			throws IOException {
		LOG.log(Level.INFO, "before cell write, save cell address");
		this.cellAddr = new SerialCellAddress(this.cell);
		LOG.log(Level.INFO, "serial cell start default write objects");
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
			LOG.log(Level.INFO, "serial cell start default read objects");
			in.defaultReadObject();
		} catch (EncryptedDocumentException | ClassNotFoundException e) {
			LOG.log(Level.SEVERE,
					" error in readObject of serialWorkbook : "
							+ e.getLocalizedMessage(),
					e);
		}
	}

	/**
	 * @return the cell
	 */
	public final Cell getCell() {
		return cell;
	}

	/**
	 * @param pcell
	 *            the cell to set
	 */
	public final void setCell(final Cell pcell) {
		this.cell = pcell;
	}

	/**
	 * @return the cellAddr
	 */
	public final SerialCellAddress getCellAddr() {
		return cellAddr;
	}

	/**
	 * @param pcellAddr
	 *            the cellAddr to set
	 */
	public final void setCellAddr(final SerialCellAddress pcellAddr) {
		this.cellAddr = pcellAddr;
	}

	/**
	 * recover cell by using it's address.
	 * 
	 * @param sheet
	 *            sheet.
	 */
	public final void recover(final Sheet sheet) {

		if (this.cellAddr != null) {
			this.setCell(sheet.getRow(this.cellAddr.getRow())
					.getCell(this.cellAddr.getColumn()));
		}

	}

}
