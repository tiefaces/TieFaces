/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;

/**
 * Pie 3d chart.
 * 
 * @author JASON JIANG
 *
 */
public class Pie3DChart extends PieChart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.PieChart#
	 * getChartListFromCtChart
	 * (org.openxmlformats.schemas.drawingml.x2006.chart.CTChart)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getPie3DChartList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.PieChart#
	 * getSerListFromCtObjChart(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final List getSerListFromCtObjChart(final Object ctObjChart) {

		if (ctObjChart instanceof CTPie3DChart) {
			return ((CTPie3DChart) ctObjChart).getSerList();
		}
		return this.getEmptySerlist();
	}

}
