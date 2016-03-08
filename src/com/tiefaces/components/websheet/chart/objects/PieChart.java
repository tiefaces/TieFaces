package com.tiefaces.components.websheet.chart.objects;

import java.util.ArrayList;
import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class PieChart implements ChartObject {

	@SuppressWarnings("rawtypes")
	@Override
	public List getChartListFromCtChart(final CTChart ctChart) {
		return ctChart.getPlotArea().getPieChartList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getSerListFromCtObjChart(Object ctObjChart) {
		
		if (ctObjChart instanceof CTPieChart) {
			return ((CTPieChart) ctObjChart).getSerList();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public CTAxDataSource getCtAxDataSourceFromSerList(List serList) {
		
		if ((serList != null) && ( serList.size() > 0) && ( serList.get(0) instanceof CTPieSer) ) {
			return ((CTPieSer) serList.get(0)).getCat();
		}
		return null;
	}

	@Override
	public final String getSeriesLabelFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTPieSer)  {
			return ((CTPieSer) ctObjSer).getTx().getStrRef().getF();
		}
		return null;
	}

	@Override
	public CTShapeProperties getShapePropertiesFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTPieSer)  {
			return ((CTPieSer) ctObjSer).getSpPr();
		}
		return null;
	}

	@Override
	public CTNumDataSource getCTNumDataSourceFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTPieSer)  {
			return ((CTPieSer) ctObjSer).getVal();
		}
		return null;
	}

	@Override
	public List<CTDPt> getDPtListFromCTSer(Object ctObjSer) {
		if ( ctObjSer instanceof CTPieSer)  {
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

	@Override
	public boolean isLineColor() {
		// TODO Auto-generated method stub
		return false;
	}

}
