/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.serializable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;

/**
 * serialize workbook.
 * 
 * @author Jason Jiang
 *
 */
public class SerialWorkbook implements Serializable {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(SerialWorkbook.class.getName());

	/**
	 * serial id.
	 */
	private static final long serialVersionUID = 4711304873642901753L;

	/**
	 * workbook is transient.
	 */
	private transient Workbook wb;

	/** hold configuration for each sheet. */
	private Map<String, SheetConfiguration> sheetConfigMap;



	/**
	 * save the workbook before serialize.
	 * 
	 * @param out
	 *            outputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void writeObject(final java.io.ObjectOutputStream out)
			throws IOException {
		out.defaultWriteObject();
		if (wb != null) {
			wb.write(out);
		}
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
			in.defaultReadObject();
			wb = WorkbookFactory.create(in);
			recover();
		} catch (EncryptedDocumentException | InvalidFormatException
				| ClassNotFoundException e) {
			LOG.log(Level.SEVERE,
					" error in readObject of serialWorkbook : "
							+ e.getLocalizedMessage(),
					e);
		}
	}

	/**
	 * Gets the wb.
	 *
	 * @return the wb
	 */
	public final Workbook getWb() {
		return wb;
	}

	/**
	 * set wb.
	 * 
	 * @param pwb
	 *            the wb to set.
	 */
	public final void setWb(final Workbook pwb) {
		this.wb = pwb;
	}

	/**
	 * Gets the sheet config map.
	 *
	 * @return the sheetConfigMap
	 */
	public final Map<String, SheetConfiguration> getSheetConfigMap() {
		return sheetConfigMap;
	}

	/**
	 * Sets the sheet config map.
	 *
	 * @param psheetConfigMap
	 *            the sheetConfigMap to set
	 */
	public void setSheetConfigMap(
			final Map<String, SheetConfiguration> psheetConfigMap) {
		this.sheetConfigMap = psheetConfigMap;
	}

	/**
	 * recover the cell reference to the sheet.
	 */
	public void recover() {
		Map<String, SheetConfiguration> map = this.getSheetConfigMap();
		for (Entry<String, SheetConfiguration> entry : map.entrySet()) {
			entry.getValue().recover(this.getWb());
		}
	}

}
