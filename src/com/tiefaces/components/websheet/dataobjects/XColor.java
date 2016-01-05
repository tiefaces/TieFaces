package com.tiefaces.components.websheet.dataobjects;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class XColor {

	private XSSFColor xssfColor = null;
	private double alpha = 0;
	public XColor(XSSFColor seriesColor) {
		super();
		this.xssfColor = seriesColor;
	}
	
	
	public XColor(XSSFColor seriesColor, double alpha) {
		super();
		this.xssfColor = seriesColor;
		this.alpha = alpha;
	}


	public XSSFColor getXssfColor() {
		return xssfColor;
	}
	public void setSeriesColor(XSSFColor xssfColor) {
		this.xssfColor = xssfColor;
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
}
