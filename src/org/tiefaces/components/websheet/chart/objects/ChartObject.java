package org.tiefaces.components.websheet.chart.objects;

import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
/**
 * Interface used for return each different objects from different charts.
 * @author Jason Jiang
 *
 */
public interface ChartObject {

	/**
	 * return chart list.
	 * @param ctChart
	 * @return List. Could be List<CtLineChart>, List<CtBarChart>, ... 
	 */
	@SuppressWarnings("rawtypes")
	public List getChartListFromCtChart(CTChart ctChart);
	
	@SuppressWarnings("rawtypes")
	public List getSerListFromCtObjChart(Object ctObjChart);
	
	@SuppressWarnings("rawtypes")
	public CTAxDataSource getCtAxDataSourceFromSerList(List serList);
	
	public String getSeriesLabelFromCTSer(Object ctObjSer);
	
	public CTShapeProperties getShapePropertiesFromCTSer(Object ctObjSer);
	
	public CTNumDataSource getCTNumDataSourceFromCTSer(Object ctObjSer);
	
	public boolean isLineColor();

	List<CTDPt> getDPtListFromCTSer(Object ctObjSer);
	

}
