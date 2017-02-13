/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.serializable;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SerialCellMap implements Serializable {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(SerialCellMap.class.getName());

	/**
	 * cell is transient.
	 */
	private transient Map<Cell, String> map;

	/**
	 * use for saving cell address when serialize.
	 */
	private List<SerialKey> saveList;

	/**
	 * Instantiates a new serial cell map.
	 */
	public SerialCellMap() {
		super();
		LOG.log(Level.INFO, "serial cell map constructor");
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
		LOG.log(Level.INFO, "before cell map write, save cell address");
		saveList = new ArrayList<>();
		for (Map.Entry<Cell, String> entry : this.getMap().entrySet()) {
			saveList.add(
					new SerialKey(new SerialCellAddress(entry.getKey()),
							entry.getValue()));
		}
		LOG.log(Level.INFO, "serial cell map start default write objects");
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
	 * Gets the map.
	 *
	 * @return the map
	 */
	public Map<Cell, String> getMap() {
		if (this.map == null) {
			this.map = new HashMap<>();
		}
		return map;
	}

	/**
	 * Sets the map.
	 *
	 * @param pmap
	 *            the map to set
	 */
	public void setMap(final Map<Cell, String> pmap) {
		this.map = pmap;
	}

	/**
	 * recover the cell reference to the sheet.
	 * @param sheet sheet.
	 */
	public void recover(final Sheet sheet) {
		if (!this.getMap().isEmpty()) {
			map.clear();
		}
		for (SerialKey entry : this.saveList) {
			SerialCellAddress skey = entry.getKey();
			map.put(sheet.getRow(skey.getRow()).getCell(skey.getColumn()),
					entry.getValue());
		}

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
		sb.append("map = " + this.map);
		sb.append("}");
		return sb.toString();
	}	

}
