/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.CellRange;

/**
 * Configuration object hold all the attributes defined in Configuration tab
 * Also some attributes hold runtime value for each web sheet.
 *
 * @author Jason Jiang
 */
public class SheetConfiguration {

	/** The form name. */
	private String formName; // corresponds to tab name of configuration tab

	/** The sheet name. */
	private String sheetName; // corresponds to sheet name of configuration tab

	/** The form header range. */
	private String formHeaderRange; // corresponds to formHeaderRange of

	/** The form body range. */
	// configuration tab
	private String formBodyRange; // corresponds to formBodyRange of

	/** The form footer range. */
	// configuration tab
	private String formFooterRange; // corresponds to formFooterRange of

	/** The form page type id. */
	// configuration tab
	private String formPageTypeId; // corresponds to formPageType of

	/** The form page id. */
	// configuration tab
	private String formPageId; // runtime holder

	/** The form fiscal year. */
	private String formFiscalYear; // runtime holder

	/** The header cell range. */
	private CellRange headerCellRange; // transfer formHeaderRange to CellRange

	/** The body cell range. */
	// object
	private CellRange bodyCellRange; // transfer formBodyRange to CellRange

	/** The footer cell range. */
	// object
	private CellRange footerCellRange; // transfer formFooterRange to CellRange

	/** The form body type. */
	// object
	private String formBodyType; // formBodyType: Repeat or Free

	/** The body allow add rows. */
	private boolean bodyAllowAddRows; // whether allow dynamic insert row in

	/** The body initial rows. */
	// form body
	private int bodyInitialRows; // initial rows number for form body. support

	/** The body populated. */
	// EL.
	private boolean bodyPopulated; // runtime holder

	/** The form width. */
	private String formWidth; // formWidth Style

	/** The max row per page. */
	private int maxRowPerPage; // max rows per page

	/** The cell form attributes. */
	private Map<String, List<CellFormAttributes>> cellFormAttributes;
	/**
	 * The form command.
	 */
	private FormCommand formCommand;

	/** The shift map. */
	private TreeMap<String, ConfigRangeAttrs> shiftMap;

	/** The command index map. */
	private Map<String, Command> commandIndexMap;

	/** The collection obj name map. */
	private Map<String, String> collectionObjNameMap;

	/** The watch list. */
	private List<Integer> watchList;

	/**
	 * due to poi bug. cannot set comment during evaluate cell time. have to
	 * output comments following sequence. i.e. row by row. hold comments for
	 * the final sheet config form.
	 * 
	 * key is cell. value is the comments.
	 */
	private Map<Cell, String> finalCommentMap = new HashMap<Cell, String>();

	/** The saved rows before. */
	private int savedRowsBefore = 0; // Saved Rows before repeat row

	/** The saved rows after. */
	private int savedRowsAfter = 0; // Saved Rows after repeat row

	/** The hidden. */
	private boolean hidden = false; // in some case e.g. prepop, we choose to
									// hide the sheet.

	/** The cached origin formulas. */
	private Map<Cell, String> cachedOriginFormulas = new HashMap<Cell, String>();

	/**
	 * Gets the sheet name.
	 *
	 * @return the sheet name
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Sets the sheet name.
	 *
	 * @param psheetName
	 *            the new sheet name
	 */
	public void setSheetName(final String psheetName) {
		this.sheetName = psheetName;
	}

	/**
	 * Gets the form name.
	 *
	 * @return the form name
	 */
	public String getFormName() {
		return formName;
	}

	/**
	 * Sets the form name.
	 *
	 * @param pformName
	 *            the new form name
	 */
	public void setFormName(final String pformName) {
		this.formName = pformName;
	}

	/**
	 * Gets the form header range.
	 *
	 * @return the form header range
	 */
	public String getFormHeaderRange() {
		return formHeaderRange;
	}

	/**
	 * Sets the form header range.
	 *
	 * @param pformHeaderRange
	 *            the new form header range
	 */
	public void setFormHeaderRange(final String pformHeaderRange) {
		this.formHeaderRange = pformHeaderRange;
	}

	/**
	 * Gets the form body range.
	 *
	 * @return the form body range
	 */
	public String getFormBodyRange() {
		return formBodyRange;
	}

	/**
	 * Sets the form body range.
	 *
	 * @param pformBodyRange
	 *            the new form body range
	 */
	public void setFormBodyRange(final String pformBodyRange) {
		this.formBodyRange = pformBodyRange;
	}

	/**
	 * Gets the form footer range.
	 *
	 * @return the form footer range
	 */
	public String getFormFooterRange() {
		return formFooterRange;
	}

	/**
	 * Sets the form footer range.
	 *
	 * @param pformFooterRange
	 *            the new form footer range
	 */
	public void setFormFooterRange(final String pformFooterRange) {
		this.formFooterRange = pformFooterRange;
	}

	/**
	 * Gets the cell form attributes.
	 *
	 * @return the cell form attributes
	 */
	public Map<String, List<CellFormAttributes>> getCellFormAttributes() {
		return cellFormAttributes;
	}

	/**
	 * Sets the cell form attributes.
	 *
	 * @param pcellFormAttributes
	 *            the cell form attributes
	 */
	public void setCellFormAttributes(
			final Map<String, List<CellFormAttributes>> pcellFormAttributes) {
		this.cellFormAttributes = pcellFormAttributes;
	}

	/**
	 * Gets the header cell range.
	 *
	 * @return the header cell range
	 */
	public CellRange getHeaderCellRange() {
		return headerCellRange;
	}

	/**
	 * Sets the header cell range.
	 *
	 * @param pheaderCellRange
	 *            the new header cell range
	 */
	public void setHeaderCellRange(final CellRange pheaderCellRange) {
		this.headerCellRange = pheaderCellRange;
	}

	/**
	 * Gets the body cell range.
	 *
	 * @return the body cell range
	 */
	public CellRange getBodyCellRange() {
		return bodyCellRange;
	}

	/**
	 * Sets the body cell range.
	 *
	 * @param pbodyCellRange
	 *            the new body cell range
	 */
	public void setBodyCellRange(final CellRange pbodyCellRange) {
		this.bodyCellRange = pbodyCellRange;
	}

	/**
	 * Gets the footer cell range.
	 *
	 * @return the footer cell range
	 */
	public CellRange getFooterCellRange() {
		return footerCellRange;
	}

	/**
	 * Sets the footer cell range.
	 *
	 * @param pfooterCellRange
	 *            the new footer cell range
	 */
	public void setFooterCellRange(final CellRange pfooterCellRange) {
		this.footerCellRange = pfooterCellRange;
	}

	/**
	 * Gets the form body type.
	 *
	 * @return the form body type
	 */
	public String getFormBodyType() {
		return formBodyType;
	}

	/**
	 * Sets the form body type.
	 *
	 * @param pformBodyType
	 *            the new form body type
	 */
	public void setFormBodyType(final String pformBodyType) {
		this.formBodyType = pformBodyType;
	}

	/**
	 * Checks if is body allow add rows.
	 *
	 * @return true, if is body allow add rows
	 */
	public boolean isBodyAllowAddRows() {
		return bodyAllowAddRows;
	}

	/**
	 * Sets the body allow add rows.
	 *
	 * @param pbodyAllowAddRows
	 *            the new body allow add rows
	 */
	public void setBodyAllowAddRows(final boolean pbodyAllowAddRows) {
		this.bodyAllowAddRows = pbodyAllowAddRows;
	}

	/**
	 * Gets the body initial rows.
	 *
	 * @return the body initial rows
	 */
	public int getBodyInitialRows() {
		return bodyInitialRows;
	}

	/**
	 * Sets the body initial rows.
	 *
	 * @param pbodyInitialRows
	 *            the new body initial rows
	 */
	public void setBodyInitialRows(final int pbodyInitialRows) {
		this.bodyInitialRows = pbodyInitialRows;
	}

	/**
	 * Checks if is body populated.
	 *
	 * @return true, if is body populated
	 */
	public boolean isBodyPopulated() {
		return bodyPopulated;
	}

	/**
	 * Sets the body populated.
	 *
	 * @param pbodyPopulated
	 *            the new body populated
	 */
	public void setBodyPopulated(final boolean pbodyPopulated) {
		this.bodyPopulated = pbodyPopulated;
	}

	/**
	 * Gets the form page type id.
	 *
	 * @return the form page type id
	 */
	public String getFormPageTypeId() {
		return formPageTypeId;
	}

	/**
	 * Sets the form page type id.
	 *
	 * @param pformPageTypeId
	 *            the new form page type id
	 */
	public void setFormPageTypeId(final String pformPageTypeId) {
		this.formPageTypeId = pformPageTypeId;
	}

	/**
	 * Gets the form page id.
	 *
	 * @return the form page id
	 */
	public String getFormPageId() {
		return formPageId;
	}

	/**
	 * Sets the form page id.
	 *
	 * @param pformPageId
	 *            the new form page id
	 */
	public void setFormPageId(final String pformPageId) {
		this.formPageId = pformPageId;
	}

	/**
	 * Gets the form fiscal year.
	 *
	 * @return the form fiscal year
	 */
	public String getFormFiscalYear() {
		return formFiscalYear;
	}

	/**
	 * Sets the form fiscal year.
	 *
	 * @param pformFiscalYear
	 *            the new form fiscal year
	 */
	public void setFormFiscalYear(final String pformFiscalYear) {
		this.formFiscalYear = pformFiscalYear;
	}

	/**
	 * Gets the form width.
	 *
	 * @return the form width
	 */
	public String getFormWidth() {
		return formWidth;
	}

	/**
	 * Sets the form width.
	 *
	 * @param pformWidth
	 *            the new form width
	 */
	public void setFormWidth(final String pformWidth) {
		this.formWidth = pformWidth;
	}

	/**
	 * Gets the max row per page.
	 *
	 * @return the max row per page
	 */
	public int getMaxRowPerPage() {
		return maxRowPerPage;
	}

	/**
	 * Sets the max row per page.
	 *
	 * @param pmaxRowPerPage
	 *            the new max row per page
	 */
	public void setMaxRowPerPage(final int pmaxRowPerPage) {
		this.maxRowPerPage = pmaxRowPerPage;
	}

	/**
	 * Gets the saved rows before.
	 *
	 * @return the saved rows before
	 */
	public int getSavedRowsBefore() {
		return savedRowsBefore;
	}

	/**
	 * Sets the saved rows before.
	 *
	 * @param psavedRowsBefore
	 *            the new saved rows before
	 */
	public void setSavedRowsBefore(final int psavedRowsBefore) {
		this.savedRowsBefore = psavedRowsBefore;
	}

	/**
	 * Gets the saved rows after.
	 *
	 * @return the saved rows after
	 */
	public int getSavedRowsAfter() {
		return savedRowsAfter;
	}

	/**
	 * Sets the saved rows after.
	 *
	 * @param psavedRowsAfter
	 *            the new saved rows after
	 */
	public void setSavedRowsAfter(final int psavedRowsAfter) {
		this.savedRowsAfter = psavedRowsAfter;
	}

	/**
	 * Gets the watch list.
	 *
	 * @return the watch list
	 */
	public List<Integer> getWatchList() {
		return watchList;
	}

	/**
	 * Sets the watch list.
	 *
	 * @param pwatchList
	 *            the new watch list
	 */
	public void setWatchList(final List<Integer> pwatchList) {
		this.watchList = pwatchList;
	}

	/**
	 * Gets the shift map.
	 *
	 * @return the shift map
	 */
	public TreeMap<String, ConfigRangeAttrs> getShiftMap() {
		return shiftMap;
	}

	/**
	 * Sets the shift map.
	 *
	 * @param pshiftMap
	 *            the shift map
	 */
	public void setShiftMap(
			final TreeMap<String, ConfigRangeAttrs> pshiftMap) {
		this.shiftMap = pshiftMap;
	}

	/**
	 * Gets the command index map.
	 *
	 * @return the command index map
	 */
	public Map<String, Command> getCommandIndexMap() {
		return commandIndexMap;
	}

	/**
	 * Sets the command index map.
	 *
	 * @param pcommandIndexMap
	 *            the command index map
	 */
	public void setCommandIndexMap(
			final Map<String, Command> pcommandIndexMap) {
		this.commandIndexMap = pcommandIndexMap;
	}

	/**
	 * Gets the collection obj name map.
	 *
	 * @return the collection obj name map
	 */
	public Map<String, String> getCollectionObjNameMap() {
		return collectionObjNameMap;
	}

	/**
	 * Sets the collection obj name map.
	 *
	 * @param pcollectionObjNameMap
	 *            the collection obj name map
	 */
	public void setCollectionObjNameMap(
			final Map<String, String> pcollectionObjNameMap) {
		this.collectionObjNameMap = pcollectionObjNameMap;
	}

	/**
	 * Gets the form command.
	 *
	 * @return the form command
	 */
	public FormCommand getFormCommand() {
		if (this.formCommand == null) {
			this.formCommand = new FormCommand();
		}
		return formCommand;
	}

	/**
	 * Sets the form command.
	 *
	 * @param pformCommand
	 *            the new form command
	 */
	public void setFormCommand(final FormCommand pformCommand) {
		this.formCommand = pformCommand;
	}

	/**
	 * Checks if is hidden.
	 *
	 * @return true, if is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Sets the hidden.
	 *
	 * @param phidden
	 *            the new hidden
	 */
	public void setHidden(final boolean phidden) {
		this.hidden = phidden;
	}

	/**
	 * Gets the cached origin formulas.
	 *
	 * @return the cached origin formulas
	 */
	public Map<Cell, String> getCachedOriginFormulas() {
		return cachedOriginFormulas;
	}

	/**
	 * Sets the cached origin formulas.
	 *
	 * @param pcachedOriginFormulas
	 *            the cached origin formulas
	 */
	public void setCachedOriginFormulas(
			final Map<Cell, String> pcachedOriginFormulas) {
		this.cachedOriginFormulas = pcachedOriginFormulas;
	}

	/**
	 * Gets the final comment map.
	 *
	 * @return the final comment map
	 */
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
		// sb.append("ConfigRange = " + configRange);
		// sb.append(",");
		sb.append("cellFormAttributes = " + cellFormAttributes);
		sb.append(",");
		sb.append("hidden = " + hidden);
		sb.append("}");
		return sb.toString();
	}

}
