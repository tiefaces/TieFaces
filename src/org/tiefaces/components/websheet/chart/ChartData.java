package org.tiefaces.components.websheet.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.tiefaces.common.AppUtils;
import org.tiefaces.components.websheet.chart.objects.ChartObject;
import org.tiefaces.components.websheet.dataobjects.ParsedCell;
import org.tiefaces.components.websheet.dataobjects.XColor;
import org.tiefaces.components.websheet.utility.ColorUtility;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

public class ChartData {
	
	private String Id;
	
	private String Title;
	
	private XColor bgColor;
	
	private ChartType Type;
	
	private ChartAxis catAx;
	
	private ChartAxis valAx;
	
	private List<ParsedCell> categoryList;
	
	private List<ChartSeries> seriesList;

	/** log instance. */
	private static final Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());
	
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

	public void buildCategoryList(CTAxDataSource  ctAxDs) {
		
		List<ParsedCell> cells = new ArrayList<ParsedCell>();
		try {
			String fullRangeName = ctAxDs.getStrRef().getF();
			String sheetName = TieWebSheetUtility.getSheetNameFromFullCellRefName(fullRangeName);
			CellRangeAddress region = CellRangeAddress.valueOf(TieWebSheetUtility.removeSheetNameFromFullCellRefName(fullRangeName));
			for ( int row = region.getFirstRow(); row<= region.getLastRow(); row++) {
				for ( int col = region.getFirstColumn(); col<= region.getLastColumn(); col++) {					
					cells.add( new ParsedCell(sheetName, row, col));
					log.fine(" add category sheetName = "+sheetName+ " row = "+row+" col = "+col);					
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.setCategoryList(cells);
	}

	

	public void buildSeriesList(List bsers, ThemesTable themeTable, final ChartObject ctObj) {

		List<ChartSeries> seriesList = new ArrayList<ChartSeries>();
		try {
       	 for (int index=0; index< bsers.size(); index++) {
       		Object ctObjSer = bsers.get(index); 
       		ChartSeries ctSer = new ChartSeries();
       		ctSer.setSeriesLabel(new ParsedCell(ctObj.getSeriesLabelFromCTSer(ctObjSer)));
			ctSer.setSeriesColor(ColorUtility.geColorFromSpPr(index, ctObj.getShapePropertiesFromCTSer(ctObjSer), themeTable, ctObj.isLineColor()));
    		List<ParsedCell> cells = new ArrayList<ParsedCell>();
       		String fullRangeName = (ctObj.getCTNumDataSourceFromCTSer(ctObjSer)).getNumRef().getF();
       		String sheetName = TieWebSheetUtility.getSheetNameFromFullCellRefName(fullRangeName);
			CellRangeAddress region = CellRangeAddress.valueOf(TieWebSheetUtility.removeSheetNameFromFullCellRefName(fullRangeName));
			for ( int row = region.getFirstRow(); row<= region.getLastRow(); row++) {
				for ( int col = region.getFirstColumn(); col<= region.getLastColumn(); col++) {
					cells.add( new ParsedCell(sheetName, row, col));
					log.fine(" add serial value sheetName = "+sheetName+ " row = "+row+" col = "+col);					
				}
			}
			ctSer.setValueList(cells);
			ctSer.setValueColorList(  getColorListFromDPTWithValueList( ctObj.getDPtListFromCTSer(ctObjSer), cells, themeTable, ctObj  )  );
			seriesList.add(ctSer);
       	 }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.setSeriesList(seriesList);
	}	
	
	
	private List<XColor> getColorListFromDPTWithValueList( List<CTDPt> dptList, List<ParsedCell> cells, final ThemesTable themeTable, final ChartObject ctObj) {
		
		if (( dptList != null) && ( cells != null)) {
    		List<XColor> colors = new ArrayList<XColor>();
			for (int index=0; index< cells.size(); index++) {
				 CTDPt dpt = getDPtFromListWithIndex(dptList, index);
				 CTShapeProperties ctSpPr = null;
				 if ( dpt != null ) {
					 ctSpPr = dpt.getSpPr();
				 }
				 colors.add(ColorUtility.geColorFromSpPr(index, ctSpPr, themeTable, ctObj.isLineColor()));
			}
			return colors;
		}
		return null;
		
	}
	
	private CTDPt getDPtFromListWithIndex(List<CTDPt> dptList, int index) {
		
		if (dptList != null) {
			for (CTDPt dpt: dptList) {
				if (dpt.getIdx().getVal() == index ) {
					return dpt;
				}
			}
		}
		return null;
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
