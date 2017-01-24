/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.tiefaces.common.TieConstants;

/**
 * Cell object used for JSF datatable. This object hold an reference to POI cell
 * object
 * 
 * @author Jason Jiang
 */
public class FacesCell implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6811957210518221928L;
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
	public final String getValidStyle() {
		if (invalid) {
			return style + TieConstants.CELL_INVALID_STYLE;
		} else {
			return style;
		}
	}

	/**
	 * Gets the input type.
	 *
	 * @return the input type
	 */
	public final String getInputType() {
		return inputType;
	}

	/**
	 * Sets the input type.
	 *
	 * @param pinputType
	 *            the new input type
	 */
	public final void setInputType(final String pinputType) {
		this.inputType = pinputType;
	}

	/**
	 * Gets the column index.
	 *
	 * @return the column index
	 */
	public final int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * Sets the column index.
	 *
	 * @param pcolumnIndex
	 *            the new column index
	 */
	public final void setColumnIndex(final int pcolumnIndex) {
		this.columnIndex = pcolumnIndex;
	}

	/**
	 * Checks if is invalid.
	 *
	 * @return true, if is invalid
	 */
	public final boolean isInvalid() {
		return invalid;
	}

	/**
	 * Sets the invalid.
	 *
	 * @param pinvalid
	 *            the new invalid
	 */
	public final void setInvalid(final boolean pinvalid) {
		this.invalid = pinvalid;
	}

	/**
	 * Checks if is contain pic.
	 *
	 * @return true, if is contain pic
	 */
	public final boolean isContainPic() {
		return containPic;
	}

	/**
	 * Sets the contain pic.
	 *
	 * @param pcontainPic
	 *            the new contain pic
	 */
	public final void setContainPic(final boolean pcontainPic) {
		this.containPic = pcontainPic;
	}

	/**
	 * Gets the picture style.
	 *
	 * @return the picture style
	 */
	public final String getPictureStyle() {
		return pictureStyle;
	}

	/**
	 * Sets the picture style.
	 *
	 * @param ppictureStyle
	 *            the new picture style
	 */
	public final void setPictureStyle(final String ppictureStyle) {
		this.pictureStyle = ppictureStyle;
	}

	/**
	 * Gets the picture id.
	 *
	 * @return the picture id
	 */
	public final String getPictureId() {
		return pictureId;
	}

	/**
	 * Sets the picture id.
	 *
	 * @param ppictureId
	 *            the new picture id
	 */
	public final void setPictureId(final String ppictureId) {
		this.pictureId = ppictureId;
	}

	/**
	 * Gets the errormsg.
	 *
	 * @return the errormsg
	 */
	public final String getErrormsg() {
		return errormsg;
	}

	/**
	 * Sets the errormsg.
	 *
	 * @param perrormsg
	 *            the new errormsg
	 */
	public final void setErrormsg(final String perrormsg) {
		this.errormsg = perrormsg;
	}

	/**
	 * Gets the colspan.
	 *
	 * @return the colspan
	 */
	public final int getColspan() {
		return colspan;
	}

	/**
	 * Sets the colspan.
	 *
	 * @param pcolspan
	 *            the new colspan
	 */
	public final void setColspan(final int pcolspan) {
		this.colspan = pcolspan;
	}

	/**
	 * Gets the rowspan.
	 *
	 * @return the rowspan
	 */
	public final int getRowspan() {
		return rowspan;
	}

	/**
	 * Sets the rowspan.
	 *
	 * @param prowspan
	 *            the new rowspan
	 */
	public final void setRowspan(final int prowspan) {
		this.rowspan = prowspan;
	}

	/**
	 * Gets the column style.
	 *
	 * @return the column style
	 */
	public final String getColumnStyle() {
		return columnStyle;
	}

	/**
	 * Sets the column style.
	 *
	 * @param pcolumnStyle
	 *            the new column style
	 */
	public final void setColumnStyle(final String pcolumnStyle) {
		this.columnStyle = pcolumnStyle;
	}

	/**
	 * Gets the decimal places.
	 *
	 * @return the decimal places
	 */
	public final short getDecimalPlaces() {
		return decimalPlaces;
	}

	/**
	 * Sets the decimal places.
	 *
	 * @param pdecimalPlaces
	 *            the new decimal places
	 */
	public final void setDecimalPlaces(final short pdecimalPlaces) {
		this.decimalPlaces = pdecimalPlaces;
	}

	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	public final String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol.
	 *
	 * @param ppsymbol
	 *            the new symbol
	 */
	public final void setSymbol(final String ppsymbol) {
		this.symbol = ppsymbol;
	}

	/**
	 * Gets the symbol position.
	 *
	 * @return the symbol position
	 */
	public final String getSymbolPosition() {
		return symbolPosition;
	}

	/**
	 * Sets the symbol position.
	 *
	 * @param psymbolPosition
	 *            the new symbol position
	 */
	public final void setSymbolPosition(final String psymbolPosition) {
		this.symbolPosition = psymbolPosition;
	}

	/**
	 * Checks if is contain chart.
	 *
	 * @return true, if is contain chart
	 */
	public final boolean isContainChart() {
		return containChart;
	}

	/**
	 * Sets the contain chart.
	 *
	 * @param pcontainChart
	 *            the new contain chart
	 */
	public final void setContainChart(final boolean pcontainChart) {
		this.containChart = pcontainChart;
	}

	/**
	 * Gets the chart id.
	 *
	 * @return the chart id
	 */
	public final String getChartId() {
		return chartId;
	}

	/**
	 * Sets the chart id.
	 *
	 * @param pchartId
	 *            the new chart id
	 */
	public final void setChartId(final String pchartId) {
		this.chartId = pchartId;
	}

	/**
	 * Gets the chart style.
	 *
	 * @return the chart style
	 */
	public final String getChartStyle() {
		return chartStyle;
	}

	/**
	 * Sets the chart style.
	 *
	 * @param pchartStyle
	 *            the new chart style
	 */
	public final void setChartStyle(final String pchartStyle) {
		this.chartStyle = pchartStyle;
	}

	/**
	 * Gets the input attrs.
	 *
	 * @return the input attrs
	 */
	public final List<CellFormAttributes> getInputAttrs() {
		return inputAttrs;
	}

	/**
	 * Sets the input attrs.
	 *
	 * @param pinputAttrs
	 *            the new input attrs
	 */
	public final void setInputAttrs(final List<CellFormAttributes> pinputAttrs) {
		this.inputAttrs = pinputAttrs;
	}

	/**
	 * Gets the select item attrs.
	 *
	 * @return the select item attrs
	 */
	public final Map<String, String> getSelectItemAttrs() {
		return selectItemAttrs;
	}

	/**
	 * Sets the select item attrs.
	 *
	 * @param pselectItemAttrs
	 *            the select item attrs
	 */
	public final void setSelectItemAttrs(
			final Map<String, String> pselectItemAttrs) {
		this.selectItemAttrs = pselectItemAttrs;
	}

	/**
	 * Gets the control.
	 *
	 * @return the control
	 */
	public final String getControl() {
		return control;
	}

	/**
	 * Sets the control.
	 *
	 * @param pcontrol
	 *            the new control
	 */
	public final void setControl(final String pcontrol) {
		this.control = pcontrol;
	}

	/**
	 * Gets the date pattern.
	 *
	 * @return the date pattern
	 */
	public final String getDatePattern() {
		return datePattern;
	}

	/**
	 * Sets the date pattern.
	 *
	 * @param pdatePattern
	 *            the new date pattern
	 */
	public final void setDatePattern(final String pdatePattern) {
		this.datePattern = pdatePattern;

	}

	/**
	 * Checks if is checks for save attr.
	 *
	 * @return true, if is checks for save attr
	 */
	public final boolean isHasSaveAttr() {
		return hasSaveAttr;
	}

	/**
	 * Sets the checks for save attr.
	 *
	 * @param phasSaveAttr
	 *            the new checks for save attr
	 */
	public final void setHasSaveAttr(final boolean phasSaveAttr) {
		this.hasSaveAttr = phasSaveAttr;
	}


}
