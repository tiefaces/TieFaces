/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
