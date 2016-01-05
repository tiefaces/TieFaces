package com.tiefaces.components.websheet.dataobjects;

public enum ChartType {
	Area3D("Area3D"),
	Area("Area"),
	Bar3D("Bar3D"),
	Column3D("Column3D"),
	Bar("Bar"),
	Column("Column"),
	Bubble("Bubble"),
	Doughnut("Doughnut"),
	Line3D("Line3D"),
	Line("Line"),
	OfPie("OfPie"),
	Pie3D("Pie3D"),
	Pie("Pie"),
	Radar("Radar"),
	Scatter("Scatter"),
	Stock(""),
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
