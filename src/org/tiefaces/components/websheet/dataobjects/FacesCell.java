/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.util.List;
import java.util.Map;


/**
 * Cell object used for JSF datatable. This object hold an reference to POI cell
 * object
 * 
 * @author Jason Jiang
 */
public class FacesCell {

	/** cell web css style. */
	private String style = "";
	/** column css style. */
	private String columnStyle = "";
	/** cell column span default set to 1. */
	private int colspan = 1; //
	/** row span default set to 1. */
	private int rowspan = 1; //
	/** column index in the datatable. */
	private int columnIndex; //
	/** indicate the cell hold invalid data when. */
	private boolean invalid = false; //
	/** hold error message when the cell is invalid. */
	private String errormsg;
	/** data type for input cell. could be text/text area/number etc. */
	private String inputType = "";
	/** indicate the cell hold picture when set to true. */
	private boolean containPic = false;
	/** picture Id for retrieve picture when containPic = true. */
	private String pictureId;
	/** cell web css style. */
	private String pictureStyle = "";
	/** decimalPlaces for number. default is 2. */
	private short decimalPlaces = 2;
	/** symbol. default is null. */
	private String symbol;
	/** symbolPosition. default is prefix */
	private String symbolPosition = "p";
	/** indicate the cell hold chart when set to true. */
	private boolean containChart = false;
	/** chart Id for retrieve picture when containPic = true. */
	private String chartId;
	/** cell web css style. */
	private String chartStyle = "";

	/** The control. */
	private String control = "";

	/** The input attrs. */
	private List<CellFormAttributes> inputAttrs;

	/** The select item attrs. */
	private Map<String, String> selectItemAttrs;

	/** The date pattern. */
	private String datePattern = "";

	/** The has save attr. */
	private boolean hasSaveAttr = false;

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public final String getStyle() {
		return style;
	}

	/**
	 * Sets the style.
	 *
	 * @param pstyle
	 *            the new style
	 */
	public final void setStyle(final String pstyle) {
		this.style = pstyle;
	}

	/**
	 * Gets the valid style.
	 *
	 * @return the valid style
	 */
	public String getValidStyle() {
		if (invalid) {
			return style + "border-color: red;";
		} else {
			return style;
		}
	}

	/**
	 * Gets the input type.
	 *
	 * @return the input type
	 */
	public String getInputType() {
		return inputType;
	}

	/**
	 * Sets the input type.
	 *
	 * @param pinputType
	 *            the new input type
	 */
	public void setInputType(final String pinputType) {
		this.inputType = pinputType;
	}

	/**
	 * Gets the column index.
	 *
	 * @return the column index
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * Sets the column index.
	 *
	 * @param pcolumnIndex
	 *            the new column index
	 */
	public void setColumnIndex(final int pcolumnIndex) {
		this.columnIndex = pcolumnIndex;
	}

	/**
	 * Checks if is invalid.
	 *
	 * @return true, if is invalid
	 */
	public boolean isInvalid() {
		return invalid;
	}

	/**
	 * Sets the invalid.
	 *
	 * @param pinvalid
	 *            the new invalid
	 */
	public void setInvalid(final boolean pinvalid) {
		this.invalid = pinvalid;
	}

	/**
	 * Checks if is contain pic.
	 *
	 * @return true, if is contain pic
	 */
	public boolean isContainPic() {
		return containPic;
	}

	/**
	 * Sets the contain pic.
	 *
	 * @param pcontainPic
	 *            the new contain pic
	 */
	public void setContainPic(final boolean pcontainPic) {
		this.containPic = pcontainPic;
	}

	/**
	 * Gets the picture style.
	 *
	 * @return the picture style
	 */
	public String getPictureStyle() {
		return pictureStyle;
	}

	/**
	 * Sets the picture style.
	 *
	 * @param ppictureStyle
	 *            the new picture style
	 */
	public void setPictureStyle(final String ppictureStyle) {
		this.pictureStyle = ppictureStyle;
	}

	/**
	 * Gets the picture id.
	 *
	 * @return the picture id
	 */
	public String getPictureId() {
		return pictureId;
	}

	/**
	 * Sets the picture id.
	 *
	 * @param ppictureId
	 *            the new picture id
	 */
	public void setPictureId(final String ppictureId) {
		this.pictureId = ppictureId;
	}

	/**
	 * Gets the errormsg.
	 *
	 * @return the errormsg
	 */
	public String getErrormsg() {
		return errormsg;
	}

	/**
	 * Sets the errormsg.
	 *
	 * @param perrormsg
	 *            the new errormsg
	 */
	public void setErrormsg(final String perrormsg) {
		this.errormsg = perrormsg;
	}

	/**
	 * Gets the colspan.
	 *
	 * @return the colspan
	 */
	public int getColspan() {
		return colspan;
	}

	/**
	 * Sets the colspan.
	 *
	 * @param pcolspan
	 *            the new colspan
	 */
	public void setColspan(final int pcolspan) {
		this.colspan = pcolspan;
	}

	/**
	 * Gets the rowspan.
	 *
	 * @return the rowspan
	 */
	public int getRowspan() {
		return rowspan;
	}

	/**
	 * Sets the rowspan.
	 *
	 * @param prowspan
	 *            the new rowspan
	 */
	public void setRowspan(final int prowspan) {
		this.rowspan = prowspan;
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
	 * Gets the decimal places.
	 *
	 * @return the decimal places
	 */
	public short getDecimalPlaces() {
		return decimalPlaces;
	}

	/**
	 * Sets the decimal places.
	 *
	 * @param pdecimalPlaces
	 *            the new decimal places
	 */
	public void setDecimalPlaces(final short pdecimalPlaces) {
		this.decimalPlaces = pdecimalPlaces;
	}

	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol.
	 *
	 * @param ppsymbol
	 *            the new symbol
	 */
	public void setSymbol(final String ppsymbol) {
		this.symbol = ppsymbol;
	}

	/**
	 * Gets the symbol position.
	 *
	 * @return the symbol position
	 */
	public String getSymbolPosition() {
		return symbolPosition;
	}

	/**
	 * Sets the symbol position.
	 *
	 * @param psymbolPosition
	 *            the new symbol position
	 */
	public void setSymbolPosition(final String psymbolPosition) {
		this.symbolPosition = psymbolPosition;
	}

	/**
	 * Checks if is contain chart.
	 *
	 * @return true, if is contain chart
	 */
	public boolean isContainChart() {
		return containChart;
	}

	/**
	 * Sets the contain chart.
	 *
	 * @param pcontainChart
	 *            the new contain chart
	 */
	public void setContainChart(final boolean pcontainChart) {
		this.containChart = pcontainChart;
	}

	/**
	 * Gets the chart id.
	 *
	 * @return the chart id
	 */
	public String getChartId() {
		return chartId;
	}

	/**
	 * Sets the chart id.
	 *
	 * @param pchartId
	 *            the new chart id
	 */
	public void setChartId(final String pchartId) {
		this.chartId = pchartId;
	}

	/**
	 * Gets the chart style.
	 *
	 * @return the chart style
	 */
	public String getChartStyle() {
		return chartStyle;
	}

	/**
	 * Sets the chart style.
	 *
	 * @param pchartStyle
	 *            the new chart style
	 */
	public void setChartStyle(final String pchartStyle) {
		this.chartStyle = pchartStyle;
	}

	/**
	 * Gets the input attrs.
	 *
	 * @return the input attrs
	 */
	public List<CellFormAttributes> getInputAttrs() {
		return inputAttrs;
	}

	/**
	 * Sets the input attrs.
	 *
	 * @param pinputAttrs
	 *            the new input attrs
	 */
	public void setInputAttrs(final List<CellFormAttributes> pinputAttrs) {
		this.inputAttrs = pinputAttrs;
	}

	/**
	 * Gets the select item attrs.
	 *
	 * @return the select item attrs
	 */
	public Map<String, String> getSelectItemAttrs() {
		return selectItemAttrs;
	}

	/**
	 * Sets the select item attrs.
	 *
	 * @param pselectItemAttrs
	 *            the select item attrs
	 */
	public void setSelectItemAttrs(final Map<String, String> pselectItemAttrs) {
		this.selectItemAttrs = pselectItemAttrs;
	}

	/**
	 * Gets the control.
	 *
	 * @return the control
	 */
	public String getControl() {
		return control;
	}

	/**
	 * Sets the control.
	 *
	 * @param pcontrol
	 *            the new control
	 */
	public void setControl(final String pcontrol) {
		this.control = pcontrol;
	}

	/**
	 * Gets the date pattern.
	 *
	 * @return the date pattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * Sets the date pattern.
	 *
	 * @param pdatePattern
	 *            the new date pattern
	 */
	public void setDatePattern(final String pdatePattern) {
		this.datePattern = pdatePattern;

	}

	/**
	 * Checks if is checks for save attr.
	 *
	 * @return true, if is checks for save attr
	 */
	public boolean isHasSaveAttr() {
		return hasSaveAttr;
	}

	/**
	 * Sets the checks for save attr.
	 *
	 * @param phasSaveAttr
	 *            the new checks for save attr
	 */
	public void setHasSaveAttr(final boolean phasSaveAttr) {
		this.hasSaveAttr = phasSaveAttr;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("style = " + style);
		sb.append(",");
		sb.append("columnStyle = " + columnStyle);
		sb.append(",");
		sb.append("colspan = " + colspan);
		sb.append(",");
		sb.append("rowspan = " + rowspan);
		sb.append(",");
		sb.append("columnIndex = " + columnIndex);
		sb.append(",");
		sb.append("invalid = " + invalid);
		sb.append(",");
		sb.append("errormsg = " + errormsg);
		sb.append(",");
		sb.append("inputType = " + inputType);
		sb.append(",");
		sb.append("containPic = " + containPic);
		sb.append(",");
		sb.append("pictureId = " + pictureId);
		sb.append(",");
		sb.append("pictureStyle = " + pictureStyle);
		sb.append(",");
		sb.append("decimalPlaces = " + decimalPlaces);
		sb.append(",");
		sb.append("symbol = " + symbol);
		sb.append(",");
		sb.append("symbolPosition = " + symbolPosition);
		sb.append(",");
		sb.append("containChart = " + containChart);
		sb.append(",");
		sb.append("chartId = " + chartId);
		sb.append(",");
		sb.append("chartStyle = " + chartStyle);
		sb.append(",");
		sb.append("control = " + control);
		sb.append(",");
		sb.append("saveAttr = " + hasSaveAttr);
		sb.append("}");
		return sb.toString();
	}

}
