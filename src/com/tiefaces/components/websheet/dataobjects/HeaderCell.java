/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.dataobjects;

/**
 * Header cell object used for Primefaces datatable header.
 * 
 * @author Jason Jiang
 */
public class HeaderCell {

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("debug: " + msg);
		}
	}

	private String rowspan; // cell row span attribute
	private String colspan; // cell column span attribute
	private String cellValue; // header text label
	private String style; // cell web css style attriubte
	private String columnStyle; // cell web css style attriubte
	private boolean rendered = true;;

	/**
	 * Constructor.
	 * 
	 * @param rowspan
	 *            cell row span attribute
	 * @param colspan
	 *            cell column span attribute
	 * @param style
	 *            cell web css style attriubte
	 * @param headertext
	 *            header text label
	 */
	public HeaderCell(String rowspan, String colspan, String style,
			String columnStyle, String cellValue, boolean rendered) {
		super();
		this.rowspan = rowspan;
		this.colspan = colspan;
		this.style = style;
		this.columnStyle = columnStyle;
		this.cellValue = cellValue;
		this.rendered = rendered;
		debug("header cell construction: rowspan = " + rowspan + " colspan="
				+ colspan + " style=" + style + " columnStyle=" + columnStyle
				+ " cellValue=" + cellValue);
	}

	public String getRowspan() {
		return rowspan;
	}

	public void setRowspan(String rowspan) {
		this.rowspan = rowspan;
	}

	public String getColspan() {
		return colspan;
	}

	public void setColspan(String colspan) {
		this.colspan = colspan;
	}

	public String getCellValue() {
		return cellValue;
	}

	public void setCellValue(String cellValue) {
		this.cellValue = cellValue;
	}

	public String getColumnStyle() {
		return columnStyle;
	}

	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("rowspan = " + rowspan);
		sb.append(",");
		sb.append("colspan = " + colspan);
		sb.append(",");
		sb.append("style = " + style);
		sb.append(",");
		sb.append("columnStyle = " + columnStyle);
		sb.append(",");
		sb.append("cellValue = " + cellValue);
		sb.append("}");
		return sb.toString();
	}

}
