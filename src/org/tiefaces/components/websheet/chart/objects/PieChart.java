/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart.objects;

import java.util.ArrayList;
import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

/**
 * Pie Chart.
 * 
 * @author JASON JIANG
 *
 */
public class PieChart implements ChartObject {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getChartListFromCtChart
	 * (org.openxmlformats.schemas.drawingml.x2006.chart.CTChart)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getPieChartList();
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getSerListFromCtObjChart(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getSerListFromCtObjChart(final Object ctObjChart) {

		if (ctObjChart instanceof CTPieChart) {
			return ((CTPieChart) ctObjChart).getSerList();
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
				&& (serList.get(0) instanceof CTPieSer)) {
			return ((CTPieSer) serList.get(0)).getCat();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getSeriesLabelFromCTSer(java.lang.Object)
	 */
	@Override
	public final String getSeriesLabelFromCTSer(final Object ctObjSer) {
		if (ctObjSer instanceof CTPieSer) {
			return ((CTPieSer) ctObjSer).getTx().getStrRef().getF();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getShapePropertiesFromCTSer(java.lang.Object)
	 */
	@Override
	public final CTShapeProperties getShapePropertiesFromCTSer(
			final Object ctObjSer) {
		if (ctObjSer instanceof CTPieSer) {
			return ((CTPieSer) ctObjSer).getSpPr();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getCTNumDataSourceFromCTSer(java.lang.Object)
	 */
	@Override
	public final CTNumDataSource getCTNumDataSourceFromCTSer(
			final Object ctObjSer) {
		if (ctObjSer instanceof CTPieSer) {
			return ((CTPieSer) ctObjSer).getVal();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#getDPtListFromCTSer(java.lang.Object)
	 */
	@Override
	public final List<CTDPt> getDPtListFromCTSer(final Object ctObjSer) {
		if (ctObjSer instanceof CTPieSer) {
			List<CTDPt> dptList = ((CTPieSer) ctObjSer).getDPtList();
			if (dptList == null) {
				// return empty list instead of null for pie.
				// this will ensure pie create valueColorList in serial object.
				dptList = new ArrayList<CTDPt>();
			}
			return dptList;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#isLineColor()
	 */
	@Override
	public final boolean isLineColor() {
		// TODO Auto-generated method stub
		return false;
	}

}
