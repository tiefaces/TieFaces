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
 
package com.tiefaces.components.websheet.dataobjects;

import java.util.List;
import java.util.Map;

/**
 * Configuration object hold all the attributes defined in Configuration tab
 * Also some attributes hold runtime value for each web sheet
 * 
 * @author Jason Jiang
 * @note This object corresponds to configuration tab.
 */
public class SheetConfiguration {
	
	private String formName;  // corresponds to tab name of configuration tab
	private String sheetName; // corresponds to sheet name of configuration tab
	private String formHeaderRange; // corresponds to formHeaderRange of configuration tab
	private String formBodyRange; // corresponds to formBodyRange of configuration tab
	private String formFooterRange; // corresponds to formFooterRange of configuration tab
	private String formPageTypeId; // corresponds to formPageType of configuration tab
	private String formPageId; // runtime holder
	private String formFiscalYear; // runtime holder
	private CellRange headerCellRange; // transfer formHeaderRange to CellRange object 
	private CellRange bodyCellRange; // transfer formBodyRange to CellRange object
	private CellRange footerCellRange; // transfer formFooterRange to CellRange object
	private String formBodyType; // formBodyType: Repeat or Free
	private boolean bodyAllowAddRows; // whether allow dynamic insert row in form body 
	private int bodyInitialRows; // initial rows number for form body. support EL.
	private boolean bodyPopulated; // runtime holder
	private String formWidth;  // formWidth Style
	private int maxRowPerPage; // max rows per page
	private Map<String, List<CellFormAttributes>> cellFormAttributes; // Map collection for form attributes
	private int savedRowsBefore=0; // Saved Rows before repeat row
	private int savedRowsAfter=0;  // Saved Rows after repeat row
	
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getTabName() {
		return formName;
	}
	public void setTabName(String tabName) {
		this.formName = tabName;
	}
	public String getFormHeaderRange() {
		return formHeaderRange;
	}
	public void setFormHeaderRange(String formHeaderRange) {
		this.formHeaderRange = formHeaderRange;
	}
	public String getFormBodyRange() {
		return formBodyRange;
	}
	public void setFormBodyRange(String formBodyRange) {
		this.formBodyRange = formBodyRange;
	}
	public String getFormFooterRange() {
		return formFooterRange;
	}
	public void setFormFooterRange(String formFooterRange) {
		this.formFooterRange = formFooterRange;
	}
	public Map<String, List<CellFormAttributes>> getCellFormAttributes() {
		return cellFormAttributes;
	}
	public void setCellFormAttributes(
			Map<String, List<CellFormAttributes>> cellFormAttributes) {
		this.cellFormAttributes = cellFormAttributes;
	}
	public CellRange getHeaderCellRange() {
		return headerCellRange;
	}
	public void setHeaderCellRange(CellRange headerCellRange) {
		this.headerCellRange = headerCellRange;
	}
	public CellRange getBodyCellRange() {
		return bodyCellRange;
	}
	public void setBodyCellRange(CellRange bodyCellRange) {
		this.bodyCellRange = bodyCellRange;
	}
	public CellRange getFooterCellRange() {
		return footerCellRange;
	}
	public void setFooterCellRange(CellRange footerCellRange) {
		this.footerCellRange = footerCellRange;
	}
	public String getFormBodyType() {
		return formBodyType;
	}
	public void setFormBodyType(String formBodyType) {
		this.formBodyType = formBodyType;
	}
	
	public boolean isBodyAllowAddRows() {
		return bodyAllowAddRows;
	}
	public void setBodyAllowAddRows(boolean bodyAllowAddRows) {
		this.bodyAllowAddRows = bodyAllowAddRows;
	}
	public int getBodyInitialRows() {
		return bodyInitialRows;
	}
	public void setBodyInitialRows(int bodyInitialRows) {
		this.bodyInitialRows = bodyInitialRows;
	}
	
	public boolean isBodyPopulated() {
		return bodyPopulated;
	}
	public void setBodyPopulated(boolean bodyPopulated) {
		this.bodyPopulated = bodyPopulated;
	}
	public String getFormPageTypeId() {
		return formPageTypeId;
	}
	public void setFormPageTypeId(String formPageTypeId) {
		this.formPageTypeId = formPageTypeId;
	}
	public String getFormPageId() {
		return formPageId;
	}
	public void setFormPageId(String formPageId) {
		this.formPageId = formPageId;
	}
	
	public String getFormFiscalYear() {
		return formFiscalYear;
	}
	public void setFormFiscalYear(String formFiscalYear) {
		this.formFiscalYear = formFiscalYear;
	}
	public String getFormWidth() {
		return formWidth;
	}
	public void setFormWidth(String formWidth) {
		this.formWidth = formWidth;
	}
	public int getMaxRowPerPage() {
		return maxRowPerPage;
	}
	public void setMaxRowPerPage(int maxRowPerPage) {
		this.maxRowPerPage = maxRowPerPage;
	}
	public int getSavedRowsBefore() {
		return savedRowsBefore;
	}
	public void setSavedRowsBefore(int savedRowsBefore) {
		this.savedRowsBefore = savedRowsBefore;
	}
	public int getSavedRowsAfter() {
		return savedRowsAfter;
	}
	public void setSavedRowsAfter(int savedRowsAfter) {
		this.savedRowsAfter = savedRowsAfter;
	}	
	/**
	 * Obtain a human readable representation.
	 * @return String Human readable label
	 */			
	public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("formName = " + formName);
        sb.append(",");
        sb.append("sheetName = " + sheetName);
        sb.append(",");
        sb.append("formHeaderRange = " + formHeaderRange);
        sb.append(",");        
        sb.append("formBodyRange = " + formBodyRange);
        sb.append(",");        
        sb.append("formFooterRange = " + formFooterRange);
        sb.append(",");        
        sb.append("formBodyType = " + formBodyType);
        sb.append(",");        
        sb.append("bodyAllowAddRows = " + bodyAllowAddRows);
        sb.append(",");        
        sb.append("bodyInitialRows = " + bodyInitialRows);
        sb.append(",");        
        sb.append("formPageId = " + formPageId);
        sb.append(",");        
        sb.append("formPageTypeId = " + formPageTypeId);
        sb.append(",");        
        sb.append("formFiscalYear = " + formFiscalYear);
        sb.append(",");        
        sb.append("HeaderCellRange = " + headerCellRange);
        sb.append(",");        
        sb.append("BodyCellRange = " + bodyCellRange);
        sb.append(",");        
        sb.append("BodyPopulated = " + bodyPopulated);
        sb.append(",");        
        sb.append("FooterCellRange = " + footerCellRange);
        sb.append(",");        
        sb.append("cellFormAttributes = " + cellFormAttributes);
        sb.append("}");
        return sb.toString();
    }	

	

}
