package com.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class Bar3DChart extends BarChart {

	@SuppressWarnings("rawtypes")
	@Override
	public List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getBar3DChartList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getSerListFromCtObjChart(Object ctObjChart) {
		
		if (ctObjChart instanceof CTBar3DChart) {
			return ((CTBar3DChart) ctObjChart).getSerList();
		}
		return null;
	}


}
