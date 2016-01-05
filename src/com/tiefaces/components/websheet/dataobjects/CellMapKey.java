/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.dataobjects;

/**
 * Form attributes object defined in configuration tab.
 * 
 * @author Jason Jiang
 * @note This object corresponds to attributes columns ( type, value, message)
 *       in configuration tab.
 */

public class CellMapKey {

	private int rowIndex = -1;
	private int colIndex = -1;
	private boolean formatted = false;
	private boolean percented = false;
	private boolean pictured = false;
	private boolean charted = false;
	private boolean parseSuccess = false;

	public CellMapKey(String skey) {
		// TODO Auto-generated constructor stub
		try {
			String[] keyList = skey.split(":");
			int keylength = keyList.length;
			if ((keylength >= 2) && (!keyList[0].isEmpty())
					&& (!keyList[1].isEmpty())) {
				this.rowIndex = Integer.parseInt(keyList[0]);
				this.colIndex = Integer.parseInt(keyList[1]);

				if (keylength > 2) {
					switch (keyList[2].toLowerCase()) {
					case "chart":
						this.charted = true;
						break;
					case "picture":
						this.pictured = true;
						break;
					case "format":
						this.formatted = true;
						break;
					case "percent":
						this.percented = true;
						break;
					}
				}
				this.parseSuccess = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColIndex() {
		return colIndex;
	}

	public boolean isFormatted() {
		return formatted;
	}

	public boolean isPercented() {
		return percented;
	}

	public boolean isPictured() {
		return pictured;
	}

	public boolean isParseSuccess() {
		return parseSuccess;
	}
	public boolean isCharted() {
		return charted;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */

	public String toString() {

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
