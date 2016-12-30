package org.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class BarChart implements ChartObject {

	@SuppressWarnings("rawtypes")
	@Override
	public List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getBarChartList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getSerListFromCtObjChart(Object ctObjChart) {
		
		if (ctObjChart instanceof CTBarChart) {
			return ((CTBarChart) ctObjChart).getSerList();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public final CTAxDataSource getCtAxDataSourceFromSerList(List serList) {
		
		if ((serList != null) && ( serList.size() > 0) && ( serList.get(0) instanceof CTBarSer) ) {
			return ((CTBarSer) serList.get(0)).getCat();
		}
		return null;
	}

	@Override
	public final String getSeriesLabelFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTBarSer)  {
			return ((CTBarSer) ctObjSer).getTx().getStrRef().getF();
		}
		return null;
	}

	@Override
	public CTShapeProperties getShapePropertiesFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTBarSer)  {
			return ((CTBarSer) ctObjSer).getSpPr();
		}
		return null;
	}

	@Override
	public CTNumDataSource getCTNumDataSourceFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTBarSer)  {
			return ((CTBarSer) ctObjSer).getVal();
		}
		return null;
	}

	@Override
	public List<CTDPt> getDPtListFromCTSer(Object ctObjSer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLineColor() {
		// TODO Auto-generated method stub
		return false;
	}

}
