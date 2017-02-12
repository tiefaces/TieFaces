/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart;

import java.util.List;
import org.tiefaces.components.websheet.dataobjects.ParsedCell;
import org.tiefaces.components.websheet.dataobjects.XColor;

/**
 * Chart series.
 * @author Jason Jiang
 *
 */
public class ChartSeries {
	
	/** seies label. */
	private ParsedCell seriesLabel;
	/** seriescolor. */
	private XColor seriesColor = null;
	/** list of cell value. */
	private List<ParsedCell> valueList;
	/** value color list. */
	private List<XColor> valueColorList;
	/**
	 * get series label.
	 * @return parsed cell.
	 */
	public final ParsedCell getSeriesLabel() {
		return seriesLabel;
	}
	/**
	 * set series label.
	 * @param pseriesLabel serieslabel.
	 */
	public final void setSeriesLabel(final ParsedCell pseriesLabel) {
		this.seriesLabel = pseriesLabel;
	}
	/**
	 * get value list.
	 * @return list of value.
	 */
	public final List<ParsedCell> getValueList() {
		return valueList;
	}
	/**
	 * set value list.
	 * @param pvalueList list of value.
	 */
	public final void setValueList(final List<ParsedCell> pvalueList) {
		this.valueList = pvalueList;
	}
	/**
	 * get series color.
	 * @return series color.
	 */
	public final XColor getSeriesColor() {
		return seriesColor;
	}
	/**
	 * set series color.
	 * @param pseriesColor series color.
	 */
	public final void setSeriesColor(final XColor pseriesColor) {
		this.seriesColor = pseriesColor;
	}
	/**
	 * get value color list.
	 * @return list of color.
	 */
	public final List<XColor> getValueColorList() {
		return valueColorList;
	}
	/**
	 * set value color list.
	 * @param pvalueColorList value color list.
	 */
	public final void setValueColorList(final List<XColor> pvalueColorList) {
		this.valueColorList = pvalueColorList;
	}

	
	
}
