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
	 * @param palpha
	 *            the alpha
	 */
	public XColor(final XSSFColor seriesColor, final double palpha) {
		super();
		this.xssfColor = seriesColor;
		this.alpha = palpha;
	}


	/**
	 * Gets the xssf color.
	 *
	 * @return the xssf color
	 */
	public final XSSFColor getXssfColor() {
		return xssfColor;
	}
	
	/**
	 * Sets the series color.
	 *
	 * @param pxssfColor
	 *            the new series color
	 */
	public final void setSeriesColor(final XSSFColor pxssfColor) {
		this.xssfColor = pxssfColor;
	}
	
	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public final double getAlpha() {
		return alpha;
	}
	
	/**
	 * Sets the alpha.
	 *
	 * @param palpha
	 *            the new alpha
	 */
	public final void setAlpha(final double palpha) {
		this.alpha = palpha;
	}
	
}
