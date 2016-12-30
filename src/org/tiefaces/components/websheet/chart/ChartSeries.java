package org.tiefaces.components.websheet.chart;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFColor;
import org.tiefaces.components.websheet.dataobjects.ParsedCell;
import org.tiefaces.components.websheet.dataobjects.XColor;

public class ChartSeries {
	
	private ParsedCell seriesLabel;
	private XColor seriesColor = null;
	private List<ParsedCell> valueList;
	private List<XColor> valueColorList;
	
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
	public List<XColor> getValueColorList() {
		return valueColorList;
	}
	public void setValueColorList(List<XColor> valueColorList) {
		this.valueColorList = valueColorList;
	}

	
	
}
