/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.common;

import java.io.Serializable;

import javax.faces.context.FacesContext;

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
		if (header != null && header.startsWith("#{")
				&& FacesContext.getCurrentInstance() != null) {
			header = FacesContext
					.getCurrentInstance()
					.getApplication()
					.evaluateExpressionGet(FacesContext.getCurrentInstance(),
							header, String.class);
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
