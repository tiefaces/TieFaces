package com.tiefaces.components.websheet.dataobjects;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

import com.tiefaces.common.AppUtils;
import com.tiefaces.components.websheet.TieWebSheetChartHelper;
import com.tiefaces.components.websheet.TieWebSheetUtility;

public class ChartData {
	
	private String Id;
	
	private String Title;
	
	private XColor bgColor;
	
	private ChartType Type;
	
	private ChartAxis catAx;
	
	private ChartAxis valAx;
	
	private List<ParsedCell> categoryList;
	
	private List<ChartSeries> seriesList;

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("debug chartData: " + msg);
		}
	}
	
	public List<ParsedCell> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<ParsedCell> categoryList) {
		this.categoryList = categoryList;
	}

	public List<ChartSeries> getSeriesList() {
		return seriesList;
	}

	public void setSeriesList(List<ChartSeries> seriesList) {
		this.seriesList = seriesList;
	} 
	
	
	public XColor getBgColor() {
		return bgColor;
	}

	public void setBgColor(XColor bgColor) {
		this.bgColor = bgColor;
	}

	public void buildCategoryList(CTLineSer ctLineSer) {
		
		List<ParsedCell> cells = new ArrayList<ParsedCell>();
		try {
			String fullRangeName = ctLineSer.getCat().getStrRef().getF();
			String sheetName = TieWebSheetUtility.getSheetNameFromFullCellRefName(fullRangeName);
			CellRangeAddress region = CellRangeAddress.valueOf(TieWebSheetUtility.removeSheetNameFromFullCellRefName(fullRangeName));
			for ( int row = region.getFirstRow(); row<= region.getLastRow(); row++) {
				for ( int col = region.getFirstColumn(); col<= region.getLastColumn(); col++) {					
					cells.add( new ParsedCell(sheetName, row, col));
					debug(" add category sheetName = "+sheetName+ " row = "+row+" col = "+col);					
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.setCategoryList(cells);
	}
	
	public void buildSeriesList(List<CTLineSer> bsers, ThemesTable themeTable) {

		List<ChartSeries> seriesList = new ArrayList<ChartSeries>();
		try {
       	 for (int index=0; index< bsers.size(); index++) {
       		CTLineSer ctLineSer = bsers.get(index); 
       		ChartSeries ctSer = new ChartSeries();
       		ctSer.setSeriesLabel(new ParsedCell(ctLineSer.getTx().getStrRef().getF()));
			ctSer.setSeriesColor(TieWebSheetChartHelper.getLineColor(index, ctLineSer, themeTable));
    		List<ParsedCell> cells = new ArrayList<ParsedCell>();
       		String fullRangeName = ctLineSer.getVal().getNumRef().getF();
       		String sheetName = TieWebSheetUtility.getSheetNameFromFullCellRefName(fullRangeName);
			CellRangeAddress region = CellRangeAddress.valueOf(TieWebSheetUtility.removeSheetNameFromFullCellRefName(fullRangeName));
			for ( int row = region.getFirstRow(); row<= region.getLastRow(); row++) {
				for ( int col = region.getFirstColumn(); col<= region.getLastColumn(); col++) {
					cells.add( new ParsedCell(sheetName, row, col));
					debug(" add serial value sheetName = "+sheetName+ " row = "+row+" col = "+col);					
				}
			}
			ctSer.setValueList(cells);
			seriesList.add(ctSer);
       	 }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.setSeriesList(seriesList);
	}


	
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public ChartType getType() {
		return Type;
	}

	public void setType(ChartType type) {
		Type = type;
	}

	public ChartAxis getCatAx() {
		return catAx;
	}

	public void setCatAx(ChartAxis catAx) {
		this.catAx = catAx;
	}

	public ChartAxis getValAx() {
		return valAx;
	}

	public void setValAx(ChartAxis valAx) {
		this.valAx = valAx;
	}

	
	
	
}
