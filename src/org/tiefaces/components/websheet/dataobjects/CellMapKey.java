/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import org.tiefaces.common.TieConstants;

/**
 * Form attributes object defined in configuration tab.
 * 
 * @author Jason Jiang This object corresponds to attributes columns ( type,
 *         value, message) in configuration tab.
 */

public class CellMapKey {

	/** The row index. */
	private int rowIndex = -1;

	/** The col index. */
	private int colIndex = -1;

	/** The formatted. */
	private boolean formatted = false;

	/** The percented. */
	private boolean percented = false;

	/** The pictured. */
	private boolean pictured = false;

	/** The charted. */
	private boolean charted = false;

	/** The parse success. */
	private boolean parseSuccess = false;

	/**
	 * Instantiates a new cell map key.
	 *
	 * @param skey
	 *            the skey
	 */
	public CellMapKey(final String skey) {

		try {
			String[] keyList = skey.split(":");
			int keylength = keyList.length;
			if ((keylength >= 2) && (!keyList[0].isEmpty())
					&& (!keyList[1].isEmpty())) {
				this.rowIndex = Integer.parseInt(keyList[0]);
				this.colIndex = Integer.parseInt(keyList[1]);

				if (keylength > 2) {
					String key = keyList[2].toLowerCase();
					if (key.equalsIgnoreCase(
							TieConstants.CELL_MAP_KEY_CHART)) {
						this.charted = true;
					} else if (key.equalsIgnoreCase(
							TieConstants.CELL_MAP_KEY_PICTURE)) {

						this.pictured = true;
					} else if (key.equalsIgnoreCase(
							TieConstants.CELL_MAP_KEY_FORMAT)) {

						this.formatted = true;
					} else if (key.equalsIgnoreCase(
							TieConstants.CELL_MAP_KEY_PERCENT)) {
						this.percented = true;
					}
				}
				this.parseSuccess = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Gets the row index.
	 *
	 * @return the row index
	 */
	public final int getRowIndex() {
		return rowIndex;
	}

	/**
	 * Gets the col index.
	 *
	 * @return the col index
	 */
	public final int getColIndex() {
		return colIndex;
	}

	/**
	 * Checks if is formatted.
	 *
	 * @return true, if is formatted
	 */
	public final boolean isFormatted() {
		return formatted;
	}

	/**
	 * Checks if is percented.
	 *
	 * @return true, if is percented
	 */
	public final boolean isPercented() {
		return percented;
	}

	/**
	 * Checks if is pictured.
	 *
	 * @return true, if is pictured
	 */
	public final boolean isPictured() {
		return pictured;
	}

	/**
	 * Checks if is parses the success.
	 *
	 * @return true, if is parses the success
	 */
	public final boolean isParseSuccess() {
		return parseSuccess;
	}

	/**
	 * Checks if is charted.
	 *
	 * @return true, if is charted
	 */
	public final boolean isCharted() {
		return charted;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */

	public final String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("parseSuccess= " + parseSuccess);
		sb.append(",");
		sb.append("rowIndex = " + rowIndex);
		sb.append(",");
		sb.append("colIndex = " + colIndex);
		sb.append(",");
		sb.append("formatted = " + formatted);
		sb.append(",");
		sb.append("percented = " + percented);
		sb.append(",");
		sb.append("pictured = " + pictured);
		sb.append(",");
		sb.append("charted = " + charted);
		sb.append("}");
		return sb.toString();
	}

}
