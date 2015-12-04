/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.tiefaces.common.FacesUtility;
import com.tiefaces.components.websheet.dataobjects.CellMap;
import com.tiefaces.components.websheet.dataobjects.FacesRow;
import com.tiefaces.components.websheet.dataobjects.HeaderCell;
import com.tiefaces.components.websheet.dataobjects.SheetConfiguration;

import javax.script.ScriptEngine;

public class TieWebSheetBean extends TieWebSheetView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3495468356246589276L;

	private List<String> columns;
	private List<FacesRow> bodyRows;
	private List<List<HeaderCell>> headerRows;
	private Workbook wb;
	private FormulaEvaluator formulaEvaluator;
	private DataFormatter dataFormatter;

	private Map<String, Picture> picturesMap;
	private Map<String, SheetConfiguration> sheetConfigMap;
	private ScriptEngine engine;
	private String currentTabName;
	private int currentTopRow;
	private int currentLeftColumn;
	private Boolean fullValidation = false;

	private TieWebSheetLoader webSheetLoader = null;
	private TieWebSheetCellHelper cellHelper = null;
	private TieWebSheetPicturesHelper picHelper = null;
	private TieWebSheetDataHandler dataHandler = null;
	private TieWebSheetValidationHandler validationHandler = null;

	private String clientId = null;
	private String webFormClientId = null;
	private String excelType = null;
	private String configurationTab = TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SHEET;

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("DEBUG: " + msg);
		}
	}

	public TieWebSheetBean() {
		debug("TieWebSheetBean Constructor");
	}

	@PostConstruct
	public void init() {
		debug("TieWebSheetBean into postConstructor");
		columns = new ArrayList<String>();
		engine = (ScriptEngine) FacesUtility
				.evaluateExpressionGet("#{tieWebSheetApp.engine}");
		webSheetLoader = new TieWebSheetLoader(this);
		cellHelper = new TieWebSheetCellHelper(this);
		dataHandler = new TieWebSheetDataHandler(this);
		validationHandler = new TieWebSheetValidationHandler(this);
		picHelper = new TieWebSheetPicturesHelper(this);
		initialLoad();
	}

	public void setWebFormClientId(String webFormClientId) {
		this.webFormClientId = webFormClientId;
	}

	public String getWebFormClientId() {
		return webFormClientId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public List<FacesRow> getBodyRows() {
		return bodyRows;
	}

	public void setBodyRows(List<FacesRow> bodyRows) {
		this.bodyRows = bodyRows;
	}

	public List<List<HeaderCell>> getHeaderRows() {
		return headerRows;
	}

	public void setHeaderRows(List<List<HeaderCell>> headerRows) {
		this.headerRows = headerRows;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public Workbook getWb() {
		return wb;
	}

	public void setWb(Workbook wb) {
		this.wb = wb;
	}

	public FormulaEvaluator getFormulaEvaluator() {
		return formulaEvaluator;
	}

	public void setFormulaEvaluator(FormulaEvaluator formulaEvaluator) {
		this.formulaEvaluator = formulaEvaluator;
	}

	public DataFormatter getDataFormatter() {
		return dataFormatter;
	}

	public void setDataFormatter(DataFormatter dataFormatter) {
		this.dataFormatter = dataFormatter;
	}

	public List<String> getColumns() {
		return columns;
	}

	public String getCurrentTabName() {
		return currentTabName;
	}

	public void setCurrentTabName(String currentTabName) {
		this.currentTabName = currentTabName;
	}

	public Boolean getFullValidation() {
		return fullValidation;
	}

	public void setFullValidation(Boolean fullValidation) {
		this.fullValidation = fullValidation;
	}

	public TieWebSheetCellHelper getCellHelper() {
		return cellHelper;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public TieWebSheetLoader getWebSheetLoader() {
		return webSheetLoader;
	}

	public TieWebSheetDataHandler getDataHandler() {
		return dataHandler;
	}

	public TieWebSheetValidationHandler getValidationHandler() {
		return validationHandler;
	}

	public TieWebSheetPicturesHelper getPicHelper() {
		return picHelper;
	}

	public String getExcelType() {
		return excelType;
	}

	public void setExcelType(String excelType) {
		this.excelType = excelType;
	}

	public int getCurrentTopRow() {
		return currentTopRow;
	}

	public void setCurrentTopRow(int currentTopRow) {
		this.currentTopRow = currentTopRow;
	}

	public int getCurrentLeftColumn() {
		return currentLeftColumn;
	}

	public void setCurrentLeftColumn(int currentLeftColumn) {
		this.currentLeftColumn = currentLeftColumn;
	}

	public Map<String, Picture> getPicturesMap() {
		return picturesMap;
	}

	public void setPicturesMap(Map<String, Picture> picturesMap) {
		this.picturesMap = picturesMap;
	}

	public String getConfigurationTab() {
		return configurationTab;
	}

	public void setConfigurationTab(String configurationTab) {
		this.configurationTab = configurationTab;
	}

	public int loadWebSheet(InputStream inputStream) {
		return webSheetLoader.loadWorkbook(inputStream);
	}

	public void onTabChange(TabChangeEvent event) {
		String tabName = event.getTab().getTitle();

		int sheetId = webSheetLoader.findTabIndexWithName(tabName);

		if ((getSheetConfigMap() != null)
				&& (sheetId < getSheetConfigMap().size()))
			webSheetLoader.loadWorkSheet(tabName);
	}

	private StreamedContent exportFile;

	public StreamedContent getExportFile() {
		return exportFile;
	}

	public void doExport() {
		try {

			webSheetLoader.loadAllFields();
			String fileName = "WebSheetTemplate" + "." + this.getExcelType();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			wb.write(out);
			InputStream stream = new BufferedInputStream(
					new ByteArrayInputStream(out.toByteArray()));
			exportFile = new DefaultStreamedContent(stream,
					"application/force-download", fileName);

		} catch (Exception e) {
			e.printStackTrace();
			debug(e.getLocalizedMessage());
		}
		return;
	}

	private boolean preValidation(boolean passEmptyCheck) {

		String tabName = validationHandler
				.findFirstInvalidSheet(passEmptyCheck);
		if (tabName != null) {
			// if (!currentTabName.equalsIgnoreCase(tabName)) {
			// int tabIndex = findTabIndexWithName(tabName);
			// webFormTabView.setActiveIndex(tabIndex);
			// //
			// RequestContext.getCurrentInstance().update("frmSubmissionWebform:tabview");
			// }
			webSheetLoader.loadWorkSheet(tabName);
			// RequestContext.getCurrentInstance().update(WebFormHelper.getWebFormClientId());
			return false;
		}
		return true;
	}

	private void setFullValidationInView(Boolean fullflag) {

		Map<String, Object> viewMap = FacesContext.getCurrentInstance()
				.getViewRoot().getViewMap();
		Boolean flag = (Boolean) viewMap.get("fullValidation");
		if ((flag == null) || (!flag.equals(fullflag))) {
			viewMap.put("fullValidation", fullflag);
		}
	}

	public void doSave() {

		fullValidation = false;
		setFullValidationInView(fullValidation);
		if (!preValidation(true)) {
			debug("Validation failded before saving");
			return;
		}
		processSave();

	}

	private int processSave() {

		// String formid =
		// WebFormHelper.evaluateExpression("#{submissionEdit.formId}",
		// String.class);
		// SRIForm formobj = FormDataHandler.buildRequestForSaving(formid, wb,
		// sheetConfigMap, formulaEvaluator, dataFormatter);
		//
		// try {
		// SRISubmissionDataSaveRequest request = new
		// SRISubmissionDataSaveRequest(SRIWebUtils.getRemoteUserId(),SRIWebUtils.getLocale(),
		// SRIWebUtils.getRequestId());
		// request.setForm(formobj);
		// log.debug("WebFormBean doSave request formobj="+formobj);
		// SRISubmissionDataSaveResponse response =
		// SRIWebFacade.getSubmissionFacade().saveSubmissionData(request);
		// if(response.isResponseInError()) {
		// log.error("Web Form Saving Error response = "+ response);
		// FacesContext.getCurrentInstance().addMessage(null,
		// AppMessageHelper.getErrorMessage(SRIWebConstants.SRI_MESSAGE_ERROR_WEBFORM_SAVE));
		// } else {
		//
		// SRIForm formobj_response = response.getForm();
		// if (formobj_response != null)
		// FormDataHandler.populateDataToExcelSheet(formobj_response, wb,
		// sheetConfigMap, formulaEvaluator, true);
		//
		// FacesContext.getCurrentInstance().addMessage(null,
		// AppMessageHelper.getInfoMessage(SRIWebConstants.SRI_MESSAGE_INFO_WEBFORM_SAVE));
		// FormDataHandler.setUnsavedStatus(RequestContext.getCurrentInstance(),
		// false);
		// }
		// resetInvalidMsgInBodyRows();
		// } catch (Exception e) {
		// e.printStackTrace();
		// log.error("Web Form Saving Error Exception = "+
		// e.getLocalizedMessage());
		// FacesContext.getCurrentInstance().addMessage(null,
		// AppMessageHelper.getErrorMessage(SRIWebConstants.SRI_MESSAGE_ERROR_WEBFORM_SAVE));
		// return -1;
		// }
		return 1;
	}

	public void loadData() {

		// try {
		// String formid =
		// WebFormHelper.evaluateExpression("#{submissionEdit.formId}",
		// String.class);
		// SRISubmissionDataFetchRequest fetchRequest = new
		// SRISubmissionDataFetchRequest(SRIWebUtils.getRemoteUserId(),SRIWebUtils.getLocale(),
		// SRIWebUtils.getRequestId());
		// fetchRequest.setFormId(Long.parseLong(formid));
		// SRISubmissionDataFetchResponse fetchResponse =
		// SRIWebFacade.getSubmissionFacade().getSubmissionData(fetchRequest);
		// SRIForm formobj = null;
		// if (fetchResponse != null ){
		// formobj= fetchResponse.getForm();
		// log.debug("WebFormBean loadData formobj = "+formobj);
		// if(fetchResponse.isResponseInError()) {
		// log.error("Web Form Loading Error response = "+ fetchResponse);
		// FacesContext.getCurrentInstance().addMessage(null,
		// AppMessageHelper.getErrorMessage(SRIWebConstants.SRI_MESSAGE_ERROR_WEBFORM_LOAD));
		// return;
		// }
		// }
		// if (formobj != null)
		// FormDataHandler.populateDataToExcelSheet(formobj, wb, sheetConfigMap,
		// formulaEvaluator, false);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// log.error("Web Form Loading Error Exception = "+
		// e.getLocalizedMessage());
		// FacesContext.getCurrentInstance().addMessage(null,
		// AppMessageHelper.getErrorMessage(SRIWebConstants.SRI_MESSAGE_ERROR_WEBFORM_LOAD));
		// }
		//

	}

	/*
	 * Needed a change handler on the note field, which doesn't need all the
	 * other code below
	 */
	public void noteChangeEvent(AjaxBehaviorEvent event) {
		dataHandler.setUnsavedStatus(RequestContext.getCurrentInstance(), true);
	}

	public void valueChangeEvent(AjaxBehaviorEvent event) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String tblName = getWebFormClientId();
		UIComponent target = event.getComponent();

		boolean pass = validationHandler.validateCell(target);
		if (pass) {
			// to improve performance, re-validate current row only
			// page validation take times. will happen when change tab(page) or
			// reload page.
			int[] rowcol = cellHelper.getRowColFromComponentAttributes(target);
			validationHandler.validateRowInCurrentPage(rowcol[0], true);
			// refresh current page calculation fields
			UIComponent s = facesContext.getViewRoot().findComponent(tblName);
			if (s != null) {
				DataTable webDataTable = (DataTable) s;
				int first = webDataTable.getFirst();
				int rowsToRender = webDataTable.getRowsToRender();
				int rowCounts = webDataTable.getRowCount();
				int top = this.getCurrentTopRow();
				int left = this.getCurrentLeftColumn();
				for (int i = first; i <= (first + rowsToRender); i++) {
					if (i < rowCounts) {
						FacesRow dataRow = bodyRows.get(i);
						for (int index = 0; index < dataRow.getCells().size(); index++) {
							Cell poiCell = this.getCellHelper()
									.getPoiCellWithRowColFromCurrentPage(
											i + top, index + left);
							if ((poiCell != null)
									&& (poiCell.getCellType() == Cell.CELL_TYPE_FORMULA)) {
								debug("refresh obj name =" + tblName + ":" + i
										+ ":cocalc" + index + " formula = "
										+ poiCell.getCellFormula());
								RequestContext.getCurrentInstance().update(
										tblName + ":" + i + ":cocalc" + index);
							}
						}
					}

				}
			}
		}
		dataHandler.setUnsavedStatus(RequestContext.getCurrentInstance(), true);
	}

	public boolean isMultiplePage() {
		if ((bodyRows != null) && (bodyRows.size() > this.getMaxRowsPerPage()))
			return true;
		return false;
	}

	@PreDestroy
	public void finish() {
		debug("finishing view webformbean");
		if (FacesContext.getCurrentInstance() == null) {
			debug("session has gone");
		}

	}

	public Map<String, SheetConfiguration> getSheetConfigMap() {
		return sheetConfigMap;
	}

	public void setSheetConfigMap(Map<String, SheetConfiguration> sheetConfigMap) {
		this.sheetConfigMap = sheetConfigMap;
	}

	private Map cellsMap = new CellMap(this);

	public Map getCellsMap() {
		return cellsMap;
	}

	public void initialLoad() {
		// no initialload
		return;
	}


	public void addRepeatRow(int rowIndex) {
		this.webSheetLoader.addRepeatRow(rowIndex);
	}

	public void deleteRepeatRow(int rowIndex) {
		this.webSheetLoader.deleteRepeatRow(rowIndex);
	}
	

}
