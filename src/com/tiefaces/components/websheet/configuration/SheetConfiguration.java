/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;

import com.tiefaces.components.websheet.dataobjects.CachedCells;
import com.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import com.tiefaces.components.websheet.dataobjects.CellRange;
import com.tiefaces.components.websheet.dataobjects.FormulaMapping;

/**
 * Configuration object hold all the attributes defined in Configuration tab
 * Also some attributes hold runtime value for each web sheet
 * 
 * @author Jason Jiang
 * @note This object corresponds to configuration tab.
 */
public class SheetConfiguration {

	private String formName; // corresponds to tab name of configuration tab
	private String sheetName; // corresponds to sheet name of configuration tab
	private String formHeaderRange; // corresponds to formHeaderRange of
									// configuration tab
	private String formBodyRange; // corresponds to formBodyRange of
									// configuration tab
	private String formFooterRange; // corresponds to formFooterRange of
									// configuration tab
	private String formPageTypeId; // corresponds to formPageType of
									// configuration tab
	private String formPageId; // runtime holder
	private String formFiscalYear; // runtime holder
	private CellRange headerCellRange; // transfer formHeaderRange to CellRange
										// object
	private CellRange bodyCellRange; // transfer formBodyRange to CellRange
										// object
	private CellRange footerCellRange; // transfer formFooterRange to CellRange
										// object
	private String formBodyType; // formBodyType: Repeat or Free
	private boolean bodyAllowAddRows; // whether allow dynamic insert row in
										// form body
	private int bodyInitialRows; // initial rows number for form body. support
									// EL.
	private boolean bodyPopulated; // runtime holder
	private String formWidth; // formWidth Style
	private int maxRowPerPage; // max rows per page
	private Map<String, List<CellFormAttributes>> cellFormAttributes; // Map
																		// collection
																		// for
																		// form
																		// attributes
	
	private FormCommand formCommand;
	
	private TreeMap<String, ConfigRangeAttrs> shiftMap;
	
	private Map<String, Command> commandIndexMap;
	private Map<String, String> collectionObjNameMap;	
	private List<Integer> watchList;
	
	/** due to poi bug. cannot set comment during evaluate cell time.
	 *  have to output comments following sequence. i.e. row by row.
	 *  hold comments for the final sheet config form.
	 *  
	 *  key is cell.
	 *  value is the comments.
	 */
	private Map<Cell, String> finalCommentMap = new HashMap<Cell, String>();
	
	
	
	
	private int savedRowsBefore = 0; // Saved Rows before repeat row
	private int savedRowsAfter = 0; // Saved Rows after repeat row
	
	private boolean hidden = false; // in some case e.g. prepop, we choose to hide the sheet.
	
	private Map<Cell, String> cachedOriginFormulas= new HashMap<Cell, String>();

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
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

	
	
/*	
	public ConfigRange getConfigRange() {
		if (this.configRange == null) {
			configRange = new ConfigRange();
		}
		return configRange;
	}

	public void setConfigRange(ConfigRange configRange) {
		this.configRange = configRange;
	}

*/
	
	public List<Integer> getWatchList() {
		return watchList;
	}

	public void setWatchList(List<Integer> watchList) {
		this.watchList = watchList;
	}

	public TreeMap<String, ConfigRangeAttrs> getShiftMap() {
		return shiftMap;
	}

	public void setShiftMap(TreeMap<String, ConfigRangeAttrs> shiftMap) {
		this.shiftMap = shiftMap;
	}

	
	
	public Map<String, Command> getCommandIndexMap() {
		return commandIndexMap;
	}

	public void setCommandIndexMap(Map<String, Command> commandIndexMap) {
		this.commandIndexMap = commandIndexMap;
	}

	public Map<String, String> getCollectionObjNameMap() {
		return collectionObjNameMap;
	}

	public void setCollectionObjNameMap(
			Map<String, String> collectionObjNameMap) {
		this.collectionObjNameMap = collectionObjNameMap;
	}

	public FormCommand getFormCommand() {
		if (this.formCommand == null) {
			this.formCommand = new FormCommand();
		}
		return formCommand;
	}

	public void setFormCommand(FormCommand formCommand) {
		this.formCommand = formCommand;
	}

	
	
	public boolean isHidden() {
		return hidden;
	}


	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	


	public Map<Cell, String> getCachedOriginFormulas() {
		return cachedOriginFormulas;
	}

	public void setCachedOriginFormulas(
			Map<Cell, String> cachedOriginFormulas) {
		this.cachedOriginFormulas = cachedOriginFormulas;
	}

	
	

	public Map<Cell, String> getFinalCommentMap() {
		return finalCommentMap;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {
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
//		sb.append("ConfigRange = " + configRange);
//		sb.append(",");
		sb.append("cellFormAttributes = " + cellFormAttributes);
		sb.append(",");
		sb.append("hidden = " + hidden);
		sb.append("}");
		return sb.toString();
	}

}
