
package com.tiefaces.common;

import java.io.Serializable;

import javax.faces.context.FacesContext;

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

public class ColumnModel implements Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String header;
    private String property;
    private String style;

    public ColumnModel(String header, String property, String style) {
        this.header = header;
        this.property = property;
        this.style = style;
    }

    public String getHeader() {
    	if (header != null && header.startsWith("#{") && FacesContext.getCurrentInstance() != null) {
    		header=FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), header, String.class);
    	}
        return header;
    }

    public String getProperty() {
        return property;
    }

	public String getstyle() {
		return style;
	}
	
	

}
