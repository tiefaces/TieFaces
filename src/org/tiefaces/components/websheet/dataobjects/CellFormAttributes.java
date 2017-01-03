/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

/**
 * Form attributes object defined in configuration tab.
 * 
 * @author Jason Jiang
 * This object corresponds to attributes columns ( type, value, message)
 *       in configuration tab.
 */

public class CellFormAttributes {

	/** The type. */
	private String type;
	
	/** The value. */
	private String value;
	
	/** The message. */
	private String message;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param ptype
	 *            the new type
	 */
	public void setType(final String ptype) {
		this.type = ptype;
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
	 *            the new value
	 */
	public void setValue(final String pvalue) {
		this.value = pvalue;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param pmessage
	 *            the new message
	 */
	public void setMessage(final String pmessage) {
		this.message = pmessage;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("type = " + type);
		sb.append(",");
		sb.append("value = " + value);
		sb.append(",");
		sb.append("message = " + message);
		sb.append("}");
		return sb.toString();
	}

}
