/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * The Class XColor.
 */
public class XColor {

	/** The xssf color. */
	private XSSFColor xssfColor = null;
	
	/** The alpha. */
	private double alpha = 0;
	
	/**
	 * Instantiates a new x color.
	 *
	 * @param seriesColor
	 *            the series color
	 */
	public XColor(final XSSFColor seriesColor) {
		super();
		this.xssfColor = seriesColor;
	}
	
	
	/**
	 * Instantiates a new x color.
	 *
	 * @param seriesColor
	 *            the series color
	 * @param alpha
	 *            the alpha
	 */
	public XColor(final XSSFColor seriesColor, final double alpha) {
		super();
		this.xssfColor = seriesColor;
		this.alpha = alpha;
	}


	/**
	 * Gets the xssf color.
	 *
	 * @return the xssf color
	 */
	public XSSFColor getXssfColor() {
		return xssfColor;
	}
	
	/**
	 * Sets the series color.
	 *
	 * @param xssfColor
	 *            the new series color
	 */
	public void setSeriesColor(final XSSFColor xssfColor) {
		this.xssfColor = xssfColor;
	}
	
	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}
	
	/**
	 * Sets the alpha.
	 *
	 * @param palpha
	 *            the new alpha
	 */
	public void setAlpha(final double palpha) {
		this.alpha = palpha;
	}
	
}
