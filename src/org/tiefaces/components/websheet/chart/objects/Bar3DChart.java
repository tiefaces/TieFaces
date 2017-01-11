/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
/**
 * Bar 3d chart.
 * @author JASON JIANG
 *
 */
public class Bar3DChart extends BarChart {


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.BarChart#
	 * getChartListFromCtChart
	 * (org.openxmlformats.schemas.drawingml.x2006.chart.CTChart)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getBar3DChartList();
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.BarChart#getSerListFromCtObjChart(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final List getSerListFromCtObjChart(final Object ctObjChart) {
		
		if (ctObjChart instanceof CTBar3DChart) {
			return ((CTBar3DChart) ctObjChart).getSerList();
		}
		return null;
	}


}
