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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;







import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;

import com.tiefaces.components.websheet.dataobjects.SheetConfiguration;


public class TieWebSheetDataHandler {

	private TieWebSheetBean parent = null;
	
	private static boolean debug=false;
    private static void debug(String msg)
    {
        if (debug)
        {
            System.out.println("debug: " + msg);
        }
    }		
	
	
	public TieWebSheetDataHandler(TieWebSheetBean parent) {
		super();
		this.parent = parent;
	}

//	public  SRIForm buildRequestForSaving(String formid,Workbook wb, Map<String, SheetConfiguration> sheetConfigMap, FormulaEvaluator formulaEvaluator, DataFormatter dataFormatter) {
//		
//	}
	

	
	private SheetConfiguration getsheetConfigByPageTypeId(Map<String, SheetConfiguration> sheetConfigMap, BigDecimal pageTypeId) {
		
		for ( SheetConfiguration sheetConfig: sheetConfigMap.values()) {
			try {
				debug("Web Form FormDataHandler getFormPageTypeId = "+sheetConfig.getFormPageTypeId()+" pageTypeId  = "+ pageTypeId);
				if ( sheetConfig.getFormPageTypeId().equalsIgnoreCase(pageTypeId.toString())) return sheetConfig;
			} catch (Exception ex) {
				debug("Web Form FormDataHandler getsheetConfigByPageTypeId error = "+ ex.getLocalizedMessage());
				return null;
			}
		}
		debug("Web Form FormDataHandler getsheetConfigByPageTypeId cannot find sheetConfig in template with pageTypeId = "+ pageTypeId);
		return null;
	}
//	public void populateDataToExcelSheet(SRIForm formobj, Map<String, SheetConfiguration> sheetConfigMap, boolean skipPageBody)  {
//		
//		for (SRIFormPage formpage: formobj.getPages() ) {
//			SheetConfiguration sheetConfig = getsheetConfigByPageTypeId(sheetConfigMap, formpage.getFormPageTypeId());
//			if (sheetConfig == null) {
//				debug("Invalid templdate");
//				return;
//			}
//			
//			sheetConfig.setFormPageId(Long.toString(formpage.getFormPageId()));
//			sheetConfig.setFormPageTypeId(formpage.getFormPageTypeId().toString());
//			if (!skipPageBody) loadPageData(sheetConfig,formpage);
//			
//		}
//	}

	
	
//	private void loadPageData(SheetConfiguration sheetConfig,SRIFormPage formpage) {
// 	
//
//	int initRows = formpage.getDataRecords().size();
//		
//	if (initRows<=0) return; // no records found
//	
//	sheetConfig.setBodyInitialRows(initRows+"");
//	
//	boolean bodyPopulated = sheetConfig.isBodyPopulated();
//	
//	int top=sheetConfig.getBodyCellRange().getTopRow();
//
//	String sheetName = sheetConfig.getSheetName();
//	Sheet sheet1 = parent.getWb().getSheet(sheetName);
//	
//	for (int i=top; i< ( top + initRows); i++) {
//		if ((!bodyPopulated)&&(i> top))	parent.getCellHelper().copyRow(sheet1,top, i);
//		assembleExcelRow( i,top, sheet1,sheetConfig,formpage);		
//	}
//	sheetConfig.setBodyPopulated(true);
//	
//	}
	
	
//	private void assembleExcelRow( int row, int topRow, Sheet sheet, SheetConfiguration sheetConfig,FormulaEvaluator formulaEvaluator,SRIFormPage formpage) {
//
//		SRIFormDataRecord formdata = formpage.getDataRecords().get(row - topRow);
//		
//		 for (Map.Entry<String, List<CellFormAttributes>> entry : sheetConfig.getCellFormAttributes().entrySet()) {
//			    String targetCell = entry.getKey();
//		    	List<CellFormAttributes> attributeList = entry.getValue();
//		    	for ( CellFormAttributes cellAttribute: attributeList) {
//			    		if (cellAttribute.getType().equalsIgnoreCase("mapping")) {
//			    			String attrValue = cellAttribute.getValue();
//			    			String cellValue = null;
//			    			boolean assemble=true;
//			    			if (attrValue.toUpperCase().startsWith("DATA")) {
//			    				int datacol = Integer.parseInt(attrValue.substring(4)) - 1;  // data array is 0 based
//			    				cellValue = formdata.getData()[datacol];
//			    			} else if (attrValue.equalsIgnoreCase("NOTES"))	{
//			    				if (row == topRow)	cellValue = formpage.getNote();  // notes only set in top row
//			    				else assemble = false;
//			    			}
//			    			if (assemble){
//				    			if (cellValue == null) cellValue = "";
//			    				String[] rowcol=parent.getCellHelper().getRowColFromExcelReferenceName(targetCell);
//			    				if (!rowcol[1].isEmpty()) {
//				    				if (rowcol[0].isEmpty()) targetCell = "$"+ rowcol[1]+ "$"+ (row+1);  
//									Cell cell = parent.getCellHelper().getCellByReference(targetCell, sheet);
//									if (cell != null) parent.getCellHelper().setCellValue(cell, cellValue);
//			    				}
//			    			}	
//			    		}
//				 }
//	   }		
//	}
	
	public void setUnsavedStatus(RequestContext requestContext, Boolean statusFlag) {
	    //requestContext.execute("setUnsavedState("+statusFlag+")");
	}
	
	public Boolean isUnsavedStatus() {
		Map<String, Object>  viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
		Boolean flag = (Boolean) viewMap.get("unSaved");
		if (flag == null) return false;
		return flag;
	}
	
}
