package com.tiefaces.components.websheet.dataobjects;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class ChartSeries {
	
	private ParsedCell seriesLabel;
	private XColor seriesColor = null;
	private List<ParsedCell> valueList;
	
	public ParsedCell getSeriesLabel() {
		return seriesLabel;
	}
	public void setSeriesLabel(ParsedCell seriesLabel) {
		this.seriesLabel = seriesLabel;
	}
	public List<ParsedCell> getValueList() {
		return valueList;
	}
	public void setValueList(List<ParsedCell> valueList) {
		this.valueList = valueList;
	}
	public XColor getSeriesColor() {
		return seriesColor;
	}
	public void setSeriesColor(XColor seriesColor) {
		this.seriesColor = seriesColor;
	}

	
	
}
