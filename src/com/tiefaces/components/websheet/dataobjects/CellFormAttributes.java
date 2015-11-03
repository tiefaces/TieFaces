/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.dataobjects;

/**
 * Form attributes object defined in configuration tab.
 * @author Jason Jiang
 * @note This object corresponds to attributes columns ( type, value, message) in configuration tab.
 */

public class CellFormAttributes {
	
	private String type;
	private String value;
	private String message;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * Obtain a human readable representation.
	 * @return String Human readable label
	 */	
	public String toString(){
    	
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
