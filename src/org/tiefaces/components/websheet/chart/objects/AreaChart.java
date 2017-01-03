/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

/**
 * area chart class.
 * 
 * @author JASON JIANG
 *
 */
public class AreaChart implements ChartObject {

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getChartListFromCtChart(org.openxmlformats.schemas.drawingml.x2006.chart.CTChart)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getAreaChartList();
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getSerListFromCtObjChart(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final List getSerListFromCtObjChart(final Object ctObjChart) {

		if (ctObjChart instanceof CTAreaChart) {
			return ((CTAreaChart) ctObjChart).getSerList();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getCtAxDataSourceFromSerList(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final CTAxDataSource getCtAxDataSourceFromSerList(
			final List serList) {

		if ((serList != null) && (serList.size() > 0)
				&& (serList.get(0) instanceof CTAreaSer)) {
			return ((CTAreaSer) serList.get(0)).getCat();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getSeriesLabelFromCTSer(java.lang.Object)
	 */
	@Override
	public final String getSeriesLabelFromCTSer(final Object ctObjSer) {
		if (ctObjSer instanceof CTAreaSer) {
			return ((CTAreaSer) ctObjSer).getTx().getStrRef().getF();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getShapePropertiesFromCTSer(java.lang.Object)
	 */
	@Override
	public final CTShapeProperties getShapePropertiesFromCTSer(
			final Object ctObjSer) {
		if (ctObjSer instanceof CTAreaSer) {
			return ((CTAreaSer) ctObjSer).getSpPr();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getCTNumDataSourceFromCTSer(java.lang.Object)
	 */
	@Override
	public final CTNumDataSource getCTNumDataSourceFromCTSer(
			final Object ctObjSer) {
		if (ctObjSer instanceof CTAreaSer) {
			return ((CTAreaSer) ctObjSer).getVal();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getDPtListFromCTSer(java.lang.Object)
	 */
	@Override
	public final List<CTDPt> getDPtListFromCTSer(final Object ctObjSer) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#isLineColor()
	 */
	@Override
	public final boolean isLineColor() {
		return false;
	}

}
