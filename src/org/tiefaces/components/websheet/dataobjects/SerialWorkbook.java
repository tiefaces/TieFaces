/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * serialize workbook.
 * 
 * @author Jason Jiang
 *
 */
public class SerialWorkbook implements Serializable {

	/** logger. */
	private static final Logger LOG = Logger.getLogger(SerialWorkbook.class
			.getName());

	/**
	 * serial id.
	 */
	private static final long serialVersionUID = 4711304873642901753L;

	/**
	 * workbook is transient.
	 */
	private transient Workbook wb;

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
			wb = WorkbookFactory.create(in);
		} catch (EncryptedDocumentException e) {
			LOG.log(Level.SEVERE,
					" error in readObject of serialWorkbook : "
							+ e.getLocalizedMessage(), e);
		} catch (InvalidFormatException e) {
			LOG.log(Level.SEVERE,
					" error in readObject of serialWorkbook : "
							+ e.getLocalizedMessage(), e);
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

}
