/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart.objects;

import java.util.ArrayList;
import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

/**
 * Bar Chart.
 * 
 * @author JASON JIANG
 *
 */
public class BarChart implements ChartObject {
	
	/** empty list. */
	private List<CTBarSer> emptySerlist = new ArrayList<>();

	/** empty list. */
	private List<CTDPt> emptyDPlist = new ArrayList<>();
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
		return ctChart.getPlotArea().getBarChartList();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getSerListFromCtObjChart(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getSerListFromCtObjChart(final Object ctObjChart) {

		if (ctObjChart instanceof CTBarChart) {
			return ((CTBarChart) ctObjChart).getSerList();
		}
		return emptySerlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getCtAxDataSourceFromSerList(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final CTAxDataSource getCtAxDataSourceFromSerList(
			final List serList) {

		if ((serList != null) && (!serList.isEmpty())
				&& (serList.get(0) instanceof CTBarSer)) {
			return ((CTBarSer) serList.get(0)).getCat();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getSeriesLabelFromCTSer(java.lang.Object)
	 */
	@Override
	public final String getSeriesLabelFromCTSer(final Object ctObjSer) {
		if (ctObjSer instanceof CTBarSer) {
			return ((CTBarSer) ctObjSer).getTx().getStrRef().getF();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getShapePropertiesFromCTSer(java.lang.Object)
	 */
	@Override
	public final CTShapeProperties getShapePropertiesFromCTSer(
			final Object ctObjSer) {
		if (ctObjSer instanceof CTBarSer) {
			return ((CTBarSer) ctObjSer).getSpPr();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getCTNumDataSourceFromCTSer(java.lang.Object)
	 */
	@Override
	public final CTNumDataSource getCTNumDataSourceFromCTSer(
			final Object ctObjSer) {
		if (ctObjSer instanceof CTBarSer) {
			return ((CTBarSer) ctObjSer).getVal();
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.chart.objects.ChartObject#
	 * getDPtListFromCTSer(java.lang.Object)
	 */
	@Override
	public final List<CTDPt> getDPtListFromCTSer(final Object ctObjSer) {
		return emptyDPlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.chart.objects.ChartObject#isLineColor()
	 */
	@Override
	public final boolean isLineColor() {
		return false;
	}


	/**
	 * Gets the empty serlist.
	 *
	 * @return the emptySerlist
	 */
	public final List<CTBarSer> getEmptySerlist() {
		return emptySerlist;
	}

	
	
}
