/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tiefaces.components.websheet;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.tiefaces.common.FacesUtility;
import com.tiefaces.components.websheet.TieWebSheetView.tabModel;
import com.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import com.tiefaces.components.websheet.dataobjects.FacesCell;
import com.tiefaces.components.websheet.dataobjects.FacesRow;
import com.tiefaces.components.websheet.dataobjects.HeaderCell;
import com.tiefaces.components.websheet.dataobjects.RowInfo;
import com.tiefaces.components.websheet.dataobjects.SheetConfiguration;

public class TieWebSheetLoader implements Serializable {

	private TieWebSheetBean parent = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("DEBUG: " + msg);
		}
	}

	public TieWebSheetLoader(TieWebSheetBean parent) {
		this.parent = parent;
		debug("TieWebSheetBean Constructor");
	}

	private void loadHeaderRows(SheetConfiguration sheetConfig,
			Map<String, CellRangeAddress> cellRangeMap,
			List<String> skippedRegionCells) {

		int top = sheetConfig.getHeaderCellRange().getTopRow();
		int bottom = sheetConfig.getHeaderCellRange().getBottomRow();
		int left = sheetConfig.getHeaderCellRange().getLeftCol();
		int right = sheetConfig.getHeaderCellRange().getRightCol();

		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		int totalWidth = parent.getCellHelper().calcTotalWidth(sheet1, left,
				right);
		debug("totalwidth = " + totalWidth);
		String formWidthStyle = sheetConfig.getFormWidth();
		if ((formWidthStyle == null) || (formWidthStyle.isEmpty()))		
			parent.setTableWidthStyle(TieWebSheetUtility
				.widthUnits2Pixel(totalWidth) + "px;");
		else
			parent.setTableWidthStyle("100%;");
			
		debug("change to pixel = " + parent.getTableWidthStyle());
		parent.setHeaderRows(new ArrayList<List<HeaderCell>>());

		if (top < 0) {
			// this is blank configuration. set column letter as header
			parent.getHeaderRows().add(
					loadHeaderRowWithoutConfigurationTab(sheet1, left, right,
							totalWidth));
		} else {
			for (int i = top; i <= bottom; i++) {
				parent.getHeaderRows().add(
						loadHeaderRowWithConfigurationTab(sheetConfig, sheet1, sheetName, i, top, left,
								right, totalWidth, cellRangeMap,
								skippedRegionCells));
				
			}
		}
	}
	
	
	private List<HeaderCell> loadHeaderRowWithoutConfigurationTab(Sheet sheet1,
			int firstCol, int lastCol, double totalWidth) {
		
		List<HeaderCell> headercells = new ArrayList<HeaderCell>();
		for (int i = firstCol; i <= lastCol; i++) {
			if (!sheet1.isColumnHidden(i)) {
			String style = getHeaderColumnStyle(parent.getWb(), null,
					sheet1.getColumnWidth(i), totalWidth, 12); 	
			headercells.add(new HeaderCell("1", "1",style,style,
					TieWebSheetUtility.GetExcelColumnName(i)));
			}
		}
		return headercells;

	}

	private String getHeaderColumnStyle(Workbook wb, Cell cell,
			double colWidth, double totalWidth, float rowHeight) {

		String columnstyle = "";
		if (cell != null)
			columnstyle += parent.getCellHelper().getCellStyle(wb, cell, "")
					+ parent.getCellHelper().getCellFontStyle(wb, cell,"", rowHeight); // +
		// "background-image: none ;color: #000000;";
		double percentage = FacesUtility.round(100 * colWidth / totalWidth, 2);
		columnstyle = columnstyle + " width : " + percentage + "%;";
		return columnstyle;
	}
	
	private List<HeaderCell> loadHeaderRowWithConfigurationTab(SheetConfiguration sheetConfig, Sheet sheet1, String sheetName,
			int currentRow, int top,  int left, int right, double totalWidth,
			Map<String, CellRangeAddress> cellRangeMap,
			List<String> skippedRegionCells) {

		Row row = sheet1.getRow(currentRow);
		List<HeaderCell> headercells = new ArrayList<HeaderCell>();
		for (int cindex = left; cindex <= right; cindex++) {
			String cellindex = "$" + cindex + "$" + currentRow;
			if ((cindex >= left) && (cindex <= right)) {
				if (!skippedRegionCells.contains(cellindex) && !sheet1.isColumnHidden(cindex) ) {
					Cell cell = null;
					if (row != null)
						cell = row.getCell(cindex, Row.CREATE_NULL_AS_BLANK);
					if (cell != null) {
						FacesCell fcell = new FacesCell();
						parent.getCellHelper().convertCell(sheetConfig, fcell,cell,
								(currentRow - top), 1, top, false,
								cellRangeMap);
						parent.getPicHelper().setupFacesCellPictures(sheet1, fcell, sheetName+cellindex);
						parent.getCellHelper().setupCellStyle(parent.getWb(),
								sheet1, fcell,cell, row.getHeightInPoints());
						fcell.setColumnStyle(fcell.getColumnStyle()+getColumnWidthStyle(sheet1,cellRangeMap,cellindex,cindex,totalWidth));
						fcell.setColumnIndex(cindex);
						
						headercells.add(new HeaderCell(fcell.getRowspan()+"", fcell.getColspan()+"",fcell.getStyle(),fcell.getColumnStyle(),
								parent.getCellHelper().getCellValueWithFormat(cell)));						
					}
				}	
					
			}		
		}
		return headercells;
	}

	private String getColumnWidthStyle(Sheet sheet1, Map<String, CellRangeAddress> cellRangeMap,  String cellindex, int cindex,  double totalWidth ) {

		CellRangeAddress caddress = cellRangeMap.get(cellindex);
		double colWidth = 0;
		// check whether the cell has rowspan or colspan
		if (caddress != null) 
			colWidth = parent.getCellHelper()
				.calcTotalWidth(sheet1,
						caddress.getFirstColumn(),
						caddress.getLastColumn());
		else
			colWidth = sheet1.getColumnWidth(cindex);
		
		double percentage = FacesUtility.round(100 * colWidth / totalWidth, 2);
		return " width : " + percentage + "%;";
		
	}


	// return 0 -- No template
	// return -1 -- error in open form
	// return 1 -- success
	
	private void clearWorkbook() {
		parent.setWb(null);
		parent.setFormulaEvaluator(null);
		parent.setDataFormatter(null);
		parent.setHeaderRows(null);
		parent.setBodyRows(null);
		parent.setSheetConfigMap(null);
		parent.setTabs(null);		
	}
	
	public int loadWorkbook(InputStream fis) {

		clearWorkbook();
		try {
			parent.setWb(WorkbookFactory.create(fis));
			if (parent.getWb() instanceof XSSFWorkbook) {
				parent.setExcelType(TieWebSheetConstants.EXCEL_2007_TYPE);
			} else if (parent.getWb() instanceof HSSFWorkbook) {
				parent.setExcelType(TieWebSheetConstants.EXCEL_2003_TYPE);
			}
			debug(" load excel type = " + parent.getExcelType());
			parent.setFormulaEvaluator(parent.getWb().getCreationHelper()
					.createFormulaEvaluator());
			parent.setDataFormatter(new DataFormatter());
			parent.setSheetConfigMap(new TieWebSheetConfigurationHandler(parent)
					.buildConfiguration());
			parent.getPicHelper().loadPictureMap();
			parent.loadData();
			initSheet();
			initTabs();
			if (parent.getTabs().size() > 0) {
				loadWorkSheet(parent.getTabs().get(0).getTitle());
			}
			fis.close();
			// remove configuration sheet
			if (parent.getWb().getSheet(
					parent.getConfigurationTab()) != null)
				parent.getWb().removeSheetAt(
						parent.getWb().getSheetIndex(
								parent.getConfigurationTab()));
		} catch (Exception e) {
			e.printStackTrace();
			debug("Web Form loadWorkbook Error Exception = "
					+ e.getLocalizedMessage());
			return -1;
		}
		return 1;

	}

	private void initTabs() {
		parent.setTabs(new ArrayList<tabModel>());
		if (parent.getSheetConfigMap() != null) {
			for (String key : parent.getSheetConfigMap().keySet()) {
				parent.getTabs().add(new tabModel("form_" + key, key, "form"));
			}
		}
	}

	private void initSheet() {
		for (SheetConfiguration sheetConfig : parent.getSheetConfigMap()
				.values()) {
			initPageData(sheetConfig, parent.getWb(),
					parent.getFormulaEvaluator());
		}
		parent.getCellHelper().reCalc();
	}

	private void initPageData(SheetConfiguration sheetConfig, Workbook wb,
			FormulaEvaluator formulaEvaluator) {

		boolean bodyPopulated = sheetConfig.isBodyPopulated();

		if (bodyPopulated)
			return; // do nothing if already populated.

		int initRows = parent.getCellHelper()
				.getInitRowsFromConfig(sheetConfig);
		int top = sheetConfig.getBodyCellRange().getTopRow();

		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = wb.getSheet(sheetName);

		for (int i = top; i < (top + initRows); i++) {
			if ((!bodyPopulated) && (i > top))
				parent.getCellHelper().copyRow(wb, sheet1, top, i);
			initExcelRow(i, top, sheet1, sheetConfig);
		}
		if (initRows > 0)
			sheetConfig.setBodyPopulated(true);
	}

	private void initExcelRow(int row, int topRow, Sheet sheet,
			SheetConfiguration sheetConfig) {

		for (Map.Entry<String, List<CellFormAttributes>> entry : sheetConfig
				.getCellFormAttributes().entrySet()) {
			String targetCell = entry.getKey();
			List<CellFormAttributes> attributeList = entry.getValue();
			for (CellFormAttributes cellAttribute : attributeList) {
				if (cellAttribute.getType().equalsIgnoreCase("load")) {
					String[] rowcol = parent.getCellHelper()
							.getRowColFromExcelReferenceName(targetCell);
					if (rowcol[0].isEmpty()) {
						targetCell = "$" + rowcol[1] + "$" + (row + 1);
					}
					Cell cell = TieWebSheetUtility.getCellByReference(
							targetCell, sheet);
					// if has full target cell i.e. $B$9 only apply when row ==
					// topRow
					if ((cell != null)
							&& (rowcol[0].isEmpty() || (row == topRow))) {
						String attrValue = cellAttribute.getValue();
						attrValue = attrValue.replace("$rowIndex",
								(row - topRow) + "");
						if (attrValue.contains("#{"))
							attrValue = FacesUtility.evaluateExpression(
									attrValue, String.class);
						parent.getCellHelper().setCellValue(cell, attrValue);
					}
				}
			}
		}
	}

	public int findTabIndexWithName(String tabname) {

		for (int i = 0; i < parent.getTabs().size(); i++) {
			if (parent.getTabs().get(i).getTitle().equalsIgnoreCase(tabname))
				return i;
		}
		return -1;

	}
	
	
	public void loadWorkSheet(String tabName) {

		int tabIndex = findTabIndexWithName(tabName);
		if (parent.getWebFormTabView() != null) {
			parent.getWebFormTabView().setActiveIndex(tabIndex);
		}	
		parent.setCurrentTabName(tabName);
		String sheetName = parent.getSheetConfigMap().get(tabName)
				.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);
		parent.getWb().setActiveSheet(parent.getWb().getSheetIndex(sheet1));

		SheetConfiguration sheetConfig = parent.getSheetConfigMap()
				.get(tabName);

		
		parent.setMaxRowsPerPage(parent.getSheetConfigMap().get(tabName).getMaxRowPerPage());
		parent.setBodyAllowAddRows(parent.getSheetConfigMap().get(tabName).isBodyAllowAddRows());

		// populate repeat rows before setup cell range map
		populateBodyRepeatRows(sheetConfig);
		Map<String, CellRangeAddress> cellRangeMap = parent.getCellHelper()
				.indexMergedRegion(sheet1);
		List<String> skippedRegionCells = parent.getCellHelper()
				.skippedRegionCells(sheet1);
		loadHeaderRows(sheetConfig, cellRangeMap, skippedRegionCells);
		loadBodyRows(sheetConfig, cellRangeMap, skippedRegionCells);
		parent.getValidationHandler().validateCurrentPage();
		createDynamicColumns(tabName);
		//reset datatable current page to 1 
		setDataTablePage(0);
		saveObjs();
		RequestContext.getCurrentInstance().update(parent.getClientId()+ ":websheettab");
	}
	
	private void setDataTablePage(int first) {
		if (parent.getWebFormClientId()!=null) {
		   final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
	       .findComponent(parent.getWebFormClientId());
		   if (d != null) {
			   d.setFirst(first);
		   }
		}
	}
	private void saveObjs() {

		Map<String, Object> viewMap = FacesContext.getCurrentInstance()
				.getViewRoot().getViewMap();
		// viewMap.put("wb", wb);
		// viewMap.put("formulaEvaluator", formulaEvaluator);
		// viewMap.put("dataFormatter", dataFormatter);
		// viewMap.put("headerRows", headerRows);
		// viewMap.put("bodyRows", bodyRows);
		// viewMap.put("sheetConfigMap", sheetConfigMap);
		// viewMap.put("engine",engine);
		// viewMap.put("tabs", tabs);
		viewMap.put("currentTabName", parent.getCurrentTabName());
		// viewMap.put("templateName", templateName);
		viewMap.put("fullValidation", parent.getFullValidation());

		debug("************ saveobjs viewMap = " + viewMap);

	}

	private void populateBodyRepeatRows(SheetConfiguration sheetConfig) {

		boolean repeatbody = parent.getCellHelper().getRepeatBodyFromConfig(
				sheetConfig);
		boolean bodyPopulated = sheetConfig.isBodyPopulated();
		if (!repeatbody)
			return;
		if (bodyPopulated)
			return;

		int initRows = parent.getCellHelper()
				.getInitRowsFromConfig(sheetConfig);
		int top = sheetConfig.getBodyCellRange().getTopRow();
		int bottom = parent.getCellHelper().getBodyBottomFromConfig(
				sheetConfig, initRows);
		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		for (int i = top; i <= bottom; i++) {
			if ((i >= top) && (i < (top + initRows))) {
				if (i > top)
					parent.getCellHelper().copyRow(parent.getWb(), sheet1, top, i);
				debug("Web Form populateBodyRepeatRows copy row = " + i);
			}
		}
	}

	private void setupRowInfo(FacesRow facesRow, Sheet sheet1, Row row, int rowIndex, boolean repeatZone) {

		facesRow.setRepeatZone(repeatZone);
		if (row != null) {
			facesRow.setRendered(!row.getZeroHeight());
			facesRow.setRowheight(row.getHeight());
		} else {
			facesRow.setRendered(true);
			facesRow.setRowheight(sheet1.getDefaultRowHeight());
		}

	}

	private void loadBodyRows(SheetConfiguration sheetConfig,
			Map<String, CellRangeAddress> cellRangeMap,
			List<String> skippedRegionCells) {

		boolean repeatbody = parent.getCellHelper().getRepeatBodyFromConfig(
				sheetConfig);
		int initRows = parent.getCellHelper()
				.getInitRowsFromConfig(sheetConfig);

		boolean bodyPopulated = sheetConfig.isBodyPopulated();
		
		int top = sheetConfig.getBodyCellRange().getTopRow();
		int bottom = parent.getCellHelper().getBodyBottomFromConfig(
				sheetConfig, initRows);
		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();

		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		parent.setBodyRows(new ArrayList<FacesRow>());
		boolean repeatZone = false;
		for (int i = top; i <= bottom; i++) {
			repeatZone = false;
			if ((repeatbody) && (i >= top) && (i < (top + initRows))) {
				repeatZone = true;
			}
			parent.getBodyRows().add(assembleFacesBodyRow(i,sheet1,repeatZone,top,left,right,initRows,sheetConfig,cellRangeMap,skippedRegionCells));
		}
		sheetConfig.setBodyPopulated(true);
		parent.setCurrentTopRow(top);
		parent.setCurrentLeftColumn(left);
		debug("Web Form loading bodyRows = " + parent.getBodyRows());
	}

	
	private FacesRow assembleFacesBodyRow(int rowIndex, Sheet sheet1, boolean repeatZone, int top, int left, int right, int initRows,SheetConfiguration sheetConfig, Map<String, CellRangeAddress> cellRangeMap, List<String> skippedRegionCells) {
		
		
		FacesRow facesRow = new FacesRow(rowIndex);
		Row row = sheet1.getRow(rowIndex);
		setupRowInfo(facesRow, sheet1, row, rowIndex, repeatZone);
		List<FacesCell> bodycells = new ArrayList<FacesCell>();
		debug(" loder row number = " + rowIndex + " row = " + row);
		for (int cindex = left; cindex <= right; cindex++) {
			String cellindex = "$" + cindex + "$" + rowIndex;
			if (!skippedRegionCells.contains(cellindex) && !sheet1.isColumnHidden(cindex)) {
				Cell cell = null;
				// if (i < (top + initRows)) {
				if (row != null) {
					cell = row.getCell(cindex, Row.CREATE_NULL_AS_BLANK);
					//cell = row.getCell(cindex);
				}
				if (cell != null) {
					FacesCell fcell = new FacesCell();
					parent.getCellHelper().convertCell(sheetConfig, fcell,cell,
							(rowIndex - top), initRows, top, repeatZone,
							cellRangeMap);
					parent.getPicHelper().setupFacesCellPictures(sheet1, fcell, sheet1.getSheetName()+cellindex);
					parent.getCellHelper().setupCellStyle(parent.getWb(),
							sheet1, fcell,cell, row.getHeightInPoints());
					fcell.setColumnIndex(cindex) ;
					bodycells.add(fcell);
				} else {
					bodycells.add(null);
				}
			} else {
				bodycells.add(null);
			}
		}
		facesRow.setCells(bodycells);
		return facesRow;
	}
	
	private void createDynamicColumns(String tabName) {

		SheetConfiguration sheetConfig = parent.getSheetConfigMap()
				.get(tabName);

		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();

		parent.getColumns().clear();

		for (int i = left; i <= right; i++) {
			parent.getColumns().add("column" + (i - left));
		}

	}

	public void loadAllFields() {
		if (parent.getSheetConfigMap() != null) {
			for (SheetConfiguration sheetConfig : parent.getSheetConfigMap()
					.values()) {
				Sheet sheet = parent.getWb().getSheet(
						sheetConfig.getSheetName());
				for (Map.Entry<String, List<CellFormAttributes>> entry : sheetConfig
						.getCellFormAttributes().entrySet()) {
					String targetCell = entry.getKey();
					String cellAddr = parent.getCellHelper()
							.findCellAddressAfterBodyPopulated(targetCell,
									sheetConfig);
					Cell cell = null;
					if (cellAddr != null)
						cell = TieWebSheetUtility.getCellByReference(cellAddr,
								sheet);
					if (cell != null) {
						List<CellFormAttributes> attributeList = entry
								.getValue();
						for (CellFormAttributes cellAttribute : attributeList) {
							if (cellAttribute.getType()
									.equalsIgnoreCase("load")) {
								String attrValue = cellAttribute.getValue();
								attrValue = FacesUtility.evaluateExpression(
										attrValue, String.class);
								parent.getCellHelper().setCellValue(
										cell,
										FacesUtility.evaluateExpression(
												cellAttribute.getValue(),
												String.class));
							}
						}
					}

				}
			}
		}
	}

	// commented below is another way to do export excel file
	// public void doExport() throws IOException {
	//
	// loadAllFields();
	// String filename =
	// parent.getCellHelper().evaluateExpression("#{submissionEdit.submission.submissionTypeCd}_#{submissionEdit.submissionRes.submission.organizationCode}_#{submissionEdit.submission.reportingPeriod}_#{submissionEdit.reportingYearCode}",
	// String.class)+".xls";
	//
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// ExternalContext externalContext = facesContext.getExternalContext();
	// externalContext.setResponseContentType("application/vnd.ms-excel");
	// externalContext.setResponseHeader("Content-Disposition",
	// "attachment; filename=\""+filename+"\"");
	// wb.write(externalContext.getResponseOutputStream());
	// facesContext.responseComplete();
	// log.info("Web Form Exported file finished filename = "+filename);
	// }
	
	// return current page facesCell according to row and colindex


	public void addRepeatRow(int rowIndex) {
		
		
	  	String tabName = parent.getCurrentTabName();
    	String sheetName = parent.getSheetConfigMap().get(tabName).getSheetName(); 
    	Sheet sheet1 = parent.getWb().getSheet(sheetName);		
		parent.getCellHelper().copyRow(parent.getWb(), sheet1, rowIndex, rowIndex + 1);
		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(tabName);
		int initRows = parent.getCellHelper().getInitRowsFromConfig(sheetConfig) + 1;
		sheetConfig.setBodyInitialRows(initRows);
		int top=sheetConfig.getBodyCellRange().getTopRow();
		int left=sheetConfig.getBodyCellRange().getLeftCol();
		int right=sheetConfig.getBodyCellRange().getRightCol();
		Map<String, CellRangeAddress> cellRangeMap = parent.getCellHelper().indexMergedRegion(sheet1);
		List<String> skippedRegionCells = parent.getCellHelper().skippedRegionCells(sheet1);
		
		parent.getBodyRows().add(rowIndex + 1 - top , assembleFacesBodyRow(  rowIndex + 1 ,sheet1, true ,top, left, right, initRows, sheetConfig, cellRangeMap, skippedRegionCells));
		
		for (int irow = rowIndex + 2 - top; irow < parent.getBodyRows().size(); irow++) {
			FacesRow facesrow = parent.getBodyRows().get(irow); 
			facesrow.setRowIndex(facesrow.getRowIndex() + 1);
		}
		parent.getCellHelper().reCalc();
	}
	
	public void deleteRepeatRow(int rowIndex) {
	  	String tabName = parent.getCurrentTabName();
    	String sheetName = parent.getSheetConfigMap().get(tabName).getSheetName(); 
		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(tabName);
    	Sheet sheet1 = parent.getWb().getSheet(sheetName);

		int initRows = parent.getCellHelper().getInitRowsFromConfig(sheetConfig) - 1;
		int top=sheetConfig.getBodyCellRange().getTopRow();
		if (initRows < 1) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "System Error", "Cannot delete the last row"));
			return ;
		}
		parent.getCellHelper().removeRow(sheet1, rowIndex);
		parent.getBodyRows().remove(rowIndex - top);
		for (int irow = rowIndex - top; irow < parent.getBodyRows().size(); irow++) {
			FacesRow facesrow = parent.getBodyRows().get(irow); 
			facesrow.setRowIndex(facesrow.getRowIndex() - 1);
		}
		
		sheetConfig.setBodyInitialRows(initRows);
		parent.getCellHelper().reCalc();
		
	}	
	
}
