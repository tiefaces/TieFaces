/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

/**
 * Interface used for return each different objects from different charts.
 * 
 * @author Jason Jiang
 *
 */
public interface ChartObject {

	/**
	 * return chart list.
	 * 
	 * @param ctChart
	 *            ctchart.
	 * @return List.
	 */
	@SuppressWarnings("rawtypes")
	List getChartListFromCtChart(CTChart ctChart);

	/**
	 * get serlist from ctobject chart.
	 * 
	 * @param ctObjChart
	 *            ctObjChart
	 * @return list of ser.
	 */
	@SuppressWarnings("rawtypes")
	List getSerListFromCtObjChart(Object ctObjChart);

	/**
	 * get ct ax datasource from serlist.
	 * 
	 * @param serList
	 *            serlist.
	 * @return ctaxdatasource.
	 */
	@SuppressWarnings("rawtypes")
	CTAxDataSource getCtAxDataSourceFromSerList(List serList);

	/**
	 * get series label from ctser.
	 * 
	 * @param ctObjSer
	 *            ctobjser.
	 * @return series label.
	 */
	String getSeriesLabelFromCTSer(Object ctObjSer);

	/**
	 * get shape properties from ctser.
	 * 
	 * @param ctObjSer
	 *            ctobjser.
	 * @return shapeperoperties.
	 */
	CTShapeProperties getShapePropertiesFromCTSer(Object ctObjSer);

	/**
	 * get ctnum data source from ctser.
	 * 
	 * @param ctObjSer
	 *            ctobjser.
	 * @return ctnum datasource.
	 */
	CTNumDataSource getCTNumDataSourceFromCTSer(Object ctObjSer);

	/**
	 * is line color.
	 * 
	 * @return true if linecolor.
	 */
	boolean isLineColor();

	/**
	 * get dpt list from ctser.
	 * 
	 * @param ctObjSer
	 *            ctobjser.
	 * @return list of ctdpt.
	 */
	List<CTDPt> getDPtListFromCTSer(Object ctObjSer);

}
