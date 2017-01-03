/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.util.logging.Logger;

/**
 * Header cell object used for Primefaces datatable header.
 * 
 * @author Jason Jiang
 */
public class HeaderCell {

	/** log instance. */
	private static final Logger log = Logger.getLogger(
			Thread.currentThread().getStackTrace()[0].getClassName());

	/** The rowspan. */
	private String rowspan; // cell row span attribute

	/** The colspan. */
	private String colspan; // cell column span attribute

	/** The cell value. */
	private String cellValue; // header text label

	/** The style. */
	private String style; // cell web css style attriubte

	/** The column style. */
	private String columnStyle; // cell web css style attriubte

	/** The rendered. */
	private boolean rendered = true;

	/** The column rendered. */
	private boolean columnRendered = true;

	/**
	 * Constructor.
	 *
	 * @param prowspan
	 *            cell row span attribute
	 * @param pcolspan
	 *            cell column span attribute
	 * @param pstyle
	 *            cell web css style attriubte
	 * @param pcolumnStyle
	 *            the column style
	 * @param pcellValue
	 *            the cell value
	 * @param prendered
	 *            the rendered
	 * @param pcolumnRendered
	 *            the column rendered
	 */
	public HeaderCell(final String prowspan, final String pcolspan,
			final String pstyle, final String pcolumnStyle,
			final String pcellValue, final boolean prendered,
			final boolean pcolumnRendered) {
		super();
		this.rowspan = prowspan;
		this.colspan = pcolspan;
		this.style = pstyle;
		this.columnStyle = pcolumnStyle;
		this.cellValue = pcellValue;
		this.rendered = prendered;
		this.columnRendered = pcolumnRendered;
		log.fine("header cell construction: rowspan = " + prowspan
				+ " colspan=" + pcolspan + " style=" + pstyle
				+ " columnStyle=" + pcolumnStyle + " cellValue="
				+ pcellValue + " rendered = " + prendered
				+ " columnRendered = " + pcolumnRendered);
	}

	/**
	 * Gets the rowspan.
	 *
	 * @return the rowspan
	 */
	public String getRowspan() {
		return rowspan;
	}

	/**
	 * Sets the rowspan.
	 *
	 * @param prowspan
	 *            the new rowspan
	 */
	public void setRowspan(final String prowspan) {
		this.rowspan = prowspan;
	}

	/**
	 * Gets the colspan.
	 *
	 * @return the colspan
	 */
	public String getColspan() {
		return colspan;
	}

	/**
	 * Sets the colspan.
	 *
	 * @param pcolspan
	 *            the new colspan
	 */
	public void setColspan(final String pcolspan) {
		this.colspan = pcolspan;
	}

	/**
	 * Gets the cell value.
	 *
	 * @return the cell value
	 */
	public String getCellValue() {
		return cellValue;
	}

	/**
	 * Sets the cell value.
	 *
	 * @param pcellValue
	 *            the new cell value
	 */
	public void setCellValue(final String pcellValue) {
		this.cellValue = pcellValue;
	}

	/**
	 * Gets the column style.
	 *
	 * @return the column style
	 */
	public String getColumnStyle() {
		return columnStyle;
	}

	/**
	 * Sets the column style.
	 *
	 * @param pcolumnStyle
	 *            the new column style
	 */
	public void setColumnStyle(final String pcolumnStyle) {
		this.columnStyle = pcolumnStyle;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the style.
	 *
	 * @param pstyle
	 *            the new style
	 */
	public void setStyle(final String pstyle) {
		this.style = pstyle;
	}

	/**
	 * Checks if is rendered.
	 *
	 * @return true, if is rendered
	 */
	public boolean isRendered() {
		return rendered;
	}

	/**
	 * Sets the rendered.
	 *
	 * @param prendered
	 *            the new rendered
	 */
	public void setRendered(final boolean prendered) {
		this.rendered = prendered;
	}

	/**
	 * Checks if is column rendered.
	 *
	 * @return true, if is column rendered
	 */
	public boolean isColumnRendered() {
		return columnRendered;
	}

	/**
	 * Sets the column rendered.
	 *
	 * @param pcolumnRendered
	 *            the new column rendered
	 */
	public void setColumnRendered(boolean pcolumnRendered) {
		this.columnRendered = pcolumnRendered;
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
		sb.append(",");
		sb.append("rendered = " + rendered);
		sb.append(",");
		sb.append("columnRendered = " + columnRendered);
		sb.append("}");
		return sb.toString();
	}

}
