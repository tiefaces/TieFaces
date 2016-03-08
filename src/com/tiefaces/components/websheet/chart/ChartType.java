package com.tiefaces.components.websheet.chart;

public enum ChartType {
	Area("Area"),
	AreaStacked("AreaStacked"),
	Bar3D("Bar3D"),
	Bar3DStacked("Bar3DStacked"),
	Column3D("Column3D"),
	Column3DStacked("Column3DStacked"),
	Bar("Bar"),
	BarStacked("BarStacked"),
	Column("Column"),
	ColumnStacked("ColumnStacked"),
	Bubble("Bubble"),
	Doughnut("Doughnut"),
	Line3D("Line3D"),
	Line("Line"),
	OfPie("OfPie"),
	Pie3D("Pie3D"),
	Pie("Pie"),
	Radar("Radar"),
	Scatter("Scatter"),
	Stock("Stock"),
	Surface3D("Surface3D"),
	Surface("Surface");
	
	private String string;

	private ChartType(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
}
