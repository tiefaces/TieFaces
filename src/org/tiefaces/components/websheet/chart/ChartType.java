/*
 * Copyright 2015 TieFaces.
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
	Area("Area"),
	/** area stacked. */
	AreaStacked("AreaStacked"),
	/** bar 3d. */
	Bar3D("Bar3D"),
	/** bar 3d stacked. */
	Bar3DStacked("Bar3DStacked"),
	/** column 3d. */
	Column3D("Column3D"),
	/** column 3d stacked. */
	Column3DStacked("Column3DStacked"),
	/** bar. */
	Bar("Bar"),
	/** bar stacked. */
	BarStacked("BarStacked"),
	/** column. */
	Column("Column"),
	/** column stacked. */
	ColumnStacked("ColumnStacked"),
	/** bubble. */
	Bubble("Bubble"),
	/** dough nut. */
	Doughnut("Doughnut"),
	/** line 3d. */
	Line3D("Line3D"),
	/** line. */
	Line("Line"),
	/** of pie. */
	OfPie("OfPie"),
	/** pie 3d. */
	Pie3D("Pie3D"),
	/** pie. */
	Pie("Pie"),
	/** radar. */
	Radar("Radar"),
	/** scatter. */
	Scatter("Scatter"),
	/** stock. */
	Stock("Stock"),
	/** surface 3d. */
	Surface3D("Surface3D"),
	/** surface. */
	Surface("Surface");
	
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
