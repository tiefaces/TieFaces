/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart;

/**
 * Chart Type class (enum).
 * @author Jason Jiang
 *
 */
public enum ChartType {
	/** area. */
	AREA("Area"),
	/** area stacked. */
	AREASTACKED("AreaStacked"),
	/** bar 3d. */
	BAR3D("Bar3D"),
	/** bar 3d stacked. */
	BAR3DSTACKED("Bar3DStacked"),
	/** column 3d. */
	COLUMN3D("Column3D"),
	/** column 3d stacked. */
	COLUMN3DSTACKED("Column3DStacked"),
	/** bar. */
	BAR("Bar"),
	/** bar stacked. */
	BARSTACKED("BarStacked"),
	/** column. */
	COLUMN("Column"),
	/** column stacked. */
	COLUMNSTACKED("ColumnStacked"),
	/** bubble. */
	BUBBLE("Bubble"),
	/** dough nut. */
	DOUGHNUT("Doughnut"),
	/** line 3d. */
	LINE3D("Line3D"),
	/** line. */
	LINE("Line"),
	/** of pie. */
	OFPIE("OfPie"),
	/** pie 3d. */
	PIE3D("Pie3D"),
	/** pie. */
	PIE("Pie"),
	/** radar. */
	RADAR("Radar"),
	/** scatter. */
	SCATTER("Scatter"),
	/** stock. */
	STOCK("Stock"),
	/** surface 3d. */
	SURFACE3D("Surface3D"),
	/** surface. */
	SURFACE("Surface");
	
	/**
	 * string.
	 */
	private String string;

	/**
	 * constructor.
	 * @param pstring string.
	 */
	ChartType(final String pstring) {
		this.string = pstring;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return string;
	}
	
}
