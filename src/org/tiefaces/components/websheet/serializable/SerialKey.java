/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.serializable;

import java.io.Serializable;

/**
 * serialize workbook.
 * 
 * @author Jason Jiang
 *
 */
public class SerialKey implements Serializable {

	/**
	 * serial key.
	 */
	private static final long serialVersionUID = -2866059313706406662L;
	/** key. */
	private SerialCellAddress key;
	/** The value. */
	private String value;

	/**
	 * Instantiates a new serial key.
	 *
	 * @param pkey
	 *            the key
	 * @param pvalue
	 *            the pvalue
	 */
	public SerialKey(final SerialCellAddress pkey, final String pvalue) {
		super();
		this.key = pkey;
		this.value = pvalue;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public SerialCellAddress getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param pkey
	 *            the key to set
	 */
	public void setKey(final SerialCellAddress pkey) {
		this.key = pkey;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param pvalue
	 *            the value to set
	 */
	public void setValue(final String pvalue) {
		this.value = pvalue;
	}

}
