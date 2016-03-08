package com.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class AreaChart implements ChartObject {

	@SuppressWarnings("rawtypes")
	@Override
	public List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getAreaChartList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getSerListFromCtObjChart(Object ctObjChart) {
		
		if (ctObjChart instanceof CTAreaChart) {
			return ((CTAreaChart) ctObjChart).getSerList();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public final CTAxDataSource getCtAxDataSourceFromSerList(List serList) {
		
		if ((serList != null) && ( serList.size() > 0) && ( serList.get(0) instanceof CTAreaSer) ) {
			return ((CTAreaSer) serList.get(0)).getCat();
		}
		return null;
	}

	@Override
	public final String getSeriesLabelFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTAreaSer)  {
			return ((CTAreaSer) ctObjSer).getTx().getStrRef().getF();
		}
		return null;
	}

	@Override
	public CTShapeProperties getShapePropertiesFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTAreaSer)  {
			return ((CTAreaSer) ctObjSer).getSpPr();
		}
		return null;
	}

	@Override
	public CTNumDataSource getCTNumDataSourceFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTAreaSer)  {
			return ((CTAreaSer) ctObjSer).getVal();
		}
		return null;
	}

	@Override
	public List<CTDPt> getDPtListFromCTSer(Object ctObjSer) {
		return null;
	}

	@Override
	public boolean isLineColor() {
		return false;
	}

}
