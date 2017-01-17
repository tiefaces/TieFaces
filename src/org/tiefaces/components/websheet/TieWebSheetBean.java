/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.chart.ChartData;
import org.tiefaces.components.websheet.chart.ChartHelper;
import org.tiefaces.components.websheet.configuration.CellControlsHelper;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CachedCells;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.CellMap;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.HeaderCell;
import org.tiefaces.components.websheet.service.CellHelper;
import org.tiefaces.components.websheet.service.CellUtility;
import org.tiefaces.components.websheet.service.PicturesHelper;
import org.tiefaces.components.websheet.service.ValidationHandler;
import org.tiefaces.components.websheet.service.WebSheetLoader;

/**
 * Main class for web sheet.
 * 
 * @author Jason Jiang
 *
 */
public class TieWebSheetBean extends TieWebSheetView implements
		Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3495468356246589276L;

	/** hold instance for columns in current display sheet. */
	private List<String> columns = new ArrayList<String>();;
	/** hold instance for each body rows in current display sheet. */
	private List<FacesRow> bodyRows;
	/** hold instance for each header rows in current display sheet. */
	private List<List<HeaderCell>> headerRows;
	/** current workbook. */
	private Workbook wb;
	/** current workbook wrapper for formula parser. */
	private XSSFEvaluationWorkbook wbWrapper;
	/** current formula evaluator. */
	private FormulaEvaluator formulaEvaluator;
	/** current dataFormatter. */
	private DataFormatter dataFormatter;
	/** hold data object context. */
	private Map<String, Object> dataContext;
	/** hold pictures for current display sheet. */
	private Map<String, Picture> picturesMap;
	/**
	 * cell attributes map.
	 */
	private CellAttributesMap cellAttributesMap = new CellAttributesMap(
			new HashMap<String, Map<String, String>>(),
			new HashMap<String, String>(),
			new HashMap<String, List<CellFormAttributes>>(),
			new HashMap<String, Map<String, String>>(),
			new HashMap<String, String>(),
			new HashMap<String, List<CellFormAttributes>>());

	/**
	 * chars data.
	 */
	private ChartsData charsData = new ChartsData();

	/** hold cached cells in current display sheet. */
	private CachedCells cachedCells;
	/**
	 * The max column counts across sheets of this workbook. e.g. sheet1 has 3
	 * columns, sheet2 has 5 columns. maxColCounts = 5;
	 */
	private int maxColCounts = 0;
	/** hold configuration for each sheet. */
	private Map<String, SheetConfiguration> sheetConfigMap;
	/** hold expressionEngine instance. */
	private ExpressionEngine expEngine = null;
	/** hold current objects. */
	private TieWebSheetBeanCurrent current = new TieWebSheetBeanCurrent();

	/** weather process full a validation. */
	private Boolean fullValidation = false;
	/** create bean's helper. */
	private TieWebSheetBeanHelper helper = new TieWebSheetBeanHelper(this);
	/**
	 * Client id for whole websheet component. This is the top level client id.
	 * There're tabs and web forms under this top level.
	 */
	private String clientId = null;
	/** Client id for web forms. */
	private String webFormClientId = null;
	/** skip configuration. show the excel form as is. */
	private boolean skipConfiguration = false;
	
	/** logger. */
	private static final Logger LOG 
	= Logger.getLogger(TieWebSheetBean.class.getName());

	/** constructor. Allow for extension. */
	public TieWebSheetBean() {
		LOG.fine("TieWebSheetBean Constructor");
	}

	/** initialize. */
	@PostConstruct
	public final void init() {
		initialLoad();
	}

	/**
	 * assign web form client id.
	 * 
	 * @param pWebFormClientId
	 *            String client id name.
	 */
	public final void setWebFormClientId(final String pWebFormClientId) {
		this.webFormClientId = pWebFormClientId;
	}

	/**
	 * Gets the web form client id.
	 *
	 * @return web form client Id.
	 */
	public final String getWebFormClientId() {
		return webFormClientId;
	}

	/**
	 * Gets the client id.
	 *
	 * @return client id.
	 */
	public final String getClientId() {
		return clientId;
	}

	/**
	 * Sets the client id.
	 *
	 * @param pClientId
	 *            client Id.
	 */
	public final void setClientId(final String pClientId) {
		this.clientId = pClientId;
	}

	/**
	 * get body rows.
	 * 
	 * @return body rows.
	 */
	public final List<FacesRow> getBodyRows() {
		return bodyRows;
	}

	/**
	 * set body rows.
	 * 
	 * @param pBodyRows
	 *            body rows list.
	 */
	public final void setBodyRows(final List<FacesRow> pBodyRows) {
		this.bodyRows = pBodyRows;
	}

	/**
	 * Gets the header rows.
	 *
	 * @return return header row list.
	 */
	public final List<List<HeaderCell>> getHeaderRows() {
		if (headerRows == null) {
			headerRows = new ArrayList<List<HeaderCell>>();
		}
		return headerRows;
	}

	/**
	 * set header rows.
	 * 
	 * @param pHeaderRows
	 *            header rows list.
	 */
	public final void setHeaderRows(final List<List<HeaderCell>> pHeaderRows) {
		this.headerRows = pHeaderRows;
	}

	/**
	 * set columns.
	 * 
	 * @param pColumns
	 *            column list.
	 */
	public final void setColumns(final List<String> pColumns) {
		this.columns = pColumns;
	}

	/**
	 * get workbook.
	 * 
	 * @return workbook.
	 */
	public final Workbook getWb() {
		return wb;
	}

	/**
	 * Set up workbook. Also create evaluation wrapper.
	 * 
	 * @param pWb
	 *            workbook.
	 */

	public final void setWb(final Workbook pWb) {
		this.wb = pWb;
		this.wbWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) wb);
	}

	/**
	 * Return evaluation wrapper if needed.
	 * 
	 * @return wbwrapper.
	 */
	public final XSSFEvaluationWorkbook getWbWrapper() {
		if ((this.wbWrapper == null) && (this.wb != null)) {
			this.wbWrapper = XSSFEvaluationWorkbook
					.create((XSSFWorkbook) wb);
		}
		return wbWrapper;
	}

	/**
	 * get formulaevaluator.
	 * 
	 * @return formulaevaluator.
	 */
	public final FormulaEvaluator getFormulaEvaluator() {
		return formulaEvaluator;
	}

	/**
	 * set formulaevaluator.
	 * 
	 * @param pFormulaEvaluator
	 *            formulaevaluator.
	 */
	public final void setFormulaEvaluator(
			final FormulaEvaluator pFormulaEvaluator) {
		this.formulaEvaluator = pFormulaEvaluator;
	}

	/**
	 * get data formatter.
	 * 
	 * @return dataformatter.
	 */
	public final DataFormatter getDataFormatter() {
		return dataFormatter;
	}

	/**
	 * set dataformatter.
	 * 
	 * @param pDataFormatter
	 *            dataformatter.
	 */
	public final void setDataFormatter(final DataFormatter pDataFormatter) {
		this.dataFormatter = pDataFormatter;
	}

	/**
	 * get columns.
	 * 
	 * @return list of columns.
	 */
	public final List<String> getColumns() {
		return columns;
	}

	/**
	 * get current tab name.
	 * 
	 * @return current tab name.
	 */
	public final String getCurrentTabName() {
		return current.getCurrentTabName();
	}

	/**
	 * set current tab name.
	 * 
	 * @param pCurrentTabName
	 *            current tab name.
	 */
	public final void setCurrentTabName(final String pCurrentTabName) {
		this.current.setCurrentTabName(pCurrentTabName);
	}

	/**
	 * get full validation.
	 * 
	 * @return true if it's fullvalidation.
	 */
	public final Boolean getFullValidation() {
		return fullValidation;
	}

	/**
	 * set full validation.
	 * 
	 * @param pFullValidation
	 *            full validation flag.
	 */
	public final void setFullValidation(final Boolean pFullValidation) {
		this.fullValidation = pFullValidation;
	}

	/**
	 * get cell helper.
	 * 
	 * @return cell helper.
	 */
	public final CellHelper getCellHelper() {
		return helper.getCellHelper();
	}

	/**
	 * get expression engine.
	 * 
	 * @return exp engine.
	 */
	public final ExpressionEngine getExpEngine() {
		if (this.expEngine == null) {
			this.expEngine = new ExpressionEngine();
		}
		return expEngine;
	}

	/**
	 * get websheet loader.
	 * 
	 * @return websheetloader.
	 */
	public final WebSheetLoader getWebSheetLoader() {
		return helper.getWebSheetLoader();
	}


	/**
	 * get validationhandler.
	 * 
	 * @return validation handler.
	 */
	public final ValidationHandler getValidationHandler() {
		return helper.getValidationHandler();
	}

	/**
	 * get pichelper.
	 * 
	 * @return picHelper.
	 */
	public final PicturesHelper getPicHelper() {
		return helper.getPicHelper();
	}

	/**
	 * get charthelper.
	 * 
	 * @return charthelper.
	 */
	public final ChartHelper getChartHelper() {
		return helper.getChartHelper();
	}

	/**
	 * get current top row.
	 * 
	 * @return current top row.
	 */
	public final int getCurrentTopRow() {
		return current.getCurrentTopRow();
	}

	/**
	 * set current top row.
	 * 
	 * @param pCurrentTopRow
	 *            current top row.
	 */
	public final void setCurrentTopRow(final int pCurrentTopRow) {
		this.current.setCurrentTopRow(pCurrentTopRow);
	}

	/**
	 * get current left column.
	 * 
	 * @return current left column.
	 */
	public final int getCurrentLeftColumn() {
		return current.getCurrentLeftColumn();
	}

	/**
	 * set current left column.
	 * 
	 * @param pCurrentLeftColumn
	 *            current left column.
	 */
	public final void setCurrentLeftColumn(final int pCurrentLeftColumn) {
		this.current.setCurrentLeftColumn(pCurrentLeftColumn);
	}

	/**
	 * get pictures map.
	 * 
	 * @return pictures map.
	 */
	public final Map<String, Picture> getPicturesMap() {
		return picturesMap;
	}

	/**
	 * set pictures map.
	 * 
	 * @param pPicturesMap
	 *            pictures map.
	 */
	public final void setPicturesMap(final Map<String, Picture> pPicturesMap) {
		this.picturesMap = pPicturesMap;
	}

	/**
	 * Gets the charts map.
	 *
	 * @return charts map.
	 */
	public final Map<String, BufferedImage> getChartsMap() {
		if (charsData.getChartsMap() == null) {
			charsData.setChartsMap(new HashMap<String, BufferedImage>());
		}
		return charsData.getChartsMap();
	}

	/**
	 * set charts map.
	 * 
	 * @param pChartsMap
	 *            chartsmap.
	 */
	public final void setChartsMap(
			final Map<String, BufferedImage> pChartsMap) {
		this.charsData.setChartsMap(pChartsMap);
	}

	/**
	 * Gets the chart data map.
	 *
	 * @return chart data map.
	 */
	public final Map<String, ChartData> getChartDataMap() {
		if (charsData.getChartDataMap() == null) {
			charsData.setChartDataMap(new HashMap<String, ChartData>());
		}
		return charsData.getChartDataMap();
	}

	/**
	 * set charts data map.
	 * 
	 * @param pChartDataMap
	 *            chart data map.
	 */
	public final void setChartDataMap(
			final Map<String, ChartData> pChartDataMap) {
		this.charsData.setChartDataMap(pChartDataMap);
	}

	/**
	 * Gets the chart anchors map.
	 *
	 * @return chart anchors map.
	 */
	public final Map<String, ClientAnchor> getChartAnchorsMap() {
		if (charsData.getChartAnchorsMap() == null) {
			charsData
					.setChartAnchorsMap(new HashMap<String, ClientAnchor>());
		}
		return charsData.getChartAnchorsMap();
	}

	/**
	 * set chart anchors map.
	 * 
	 * @param pChartAnchorsMap
	 *            chart anchors map.
	 */
	public final void setChartAnchorsMap(
			final Map<String, ClientAnchor> pChartAnchorsMap) {
		this.charsData.setChartAnchorsMap(pChartAnchorsMap);
	}

	/**
	 * chart position map.
	 * 
	 * @return chartPositionMap.
	 */
	public final Map<String, String> getChartPositionMap() {
		if (charsData.getChartPositionMap() == null) {
			charsData.setChartPositionMap(new HashMap<String, String>());
		}
		return charsData.getChartPositionMap();
	}

	/**
	 * set chart position map.
	 *
	 * @param pChartPositionMap
	 *            the chart position map
	 */
	public final void setChartPositionMap(
			final Map<String, String> pChartPositionMap) {
		this.charsData.setChartPositionMap(pChartPositionMap);
	}

	/**
	 * Gets the cached cells.
	 *
	 * @return cached cells.
	 */
	public final CachedCells getCachedCells() {
		if (cachedCells == null) {
			cachedCells = new CachedCells(this);
		}
		return cachedCells;
	}

	/**
	 * set cached cells.
	 * 
	 * @param pCachedCells
	 *            cached cells.
	 */
	public final void setCachedCells(final CachedCells pCachedCells) {
		this.cachedCells = pCachedCells;
	}

	/**
	 * create datacontext map if needed.
	 * 
	 * @return map.
	 */
	public final Map<String, Object> getDataContext() {
		return dataContext;
	}

	/**
	 * set data context.
	 * 
	 * @param pDataContext
	 *            data context.
	 */
	public final void setDataContext(final Map<String, Object> pDataContext) {
		this.dataContext = pDataContext;

	}

	/**
	 * Gets the max col counts.
	 *
	 * @return max column counts.
	 */
	public final int getMaxColCounts() {
		if (this.maxColCounts == 0) {
			reCalcMaxColCounts();
		}
		return maxColCounts;
	}

	
	
	/**
	 * @return the skipConfiguration
	 */
	public final boolean isSkipConfiguration() {
		return skipConfiguration;
	}

	/**
	 * @param pskipConfiguration the skipConfiguration to set
	 */
	public final void setSkipConfiguration(final boolean pskipConfiguration) {
		this.skipConfiguration = pskipConfiguration;
	}

	/**
	 * recalculate max coulumn count across sheets in the workbook.
	 */
	public final void reCalcMaxColCounts() {
		if ((this.sheetConfigMap == null)
				|| (this.sheetConfigMap.size() == 0)) {
			this.maxColCounts = 0;
			return;
		}
		int maxColumns = 0;
		for (SheetConfiguration sheetConfig : this.sheetConfigMap.values()) {
			int counts = sheetConfig.getHeaderCellRange().getRightCol()
					- sheetConfig.getHeaderCellRange().getLeftCol() + 1;
			if (maxColumns < counts) {
				maxColumns = counts;
			}
		}
		this.maxColCounts = maxColumns;
	}

	/**
	 * load web sheet from inputStream file.
	 * 
	 * @param inputStream
	 *            input stream file.
	 * @return 1 (success) -1 (failed)
	 */
	public final int loadWebSheet(final InputStream inputStream) {
		return loadWebSheet(inputStream, null);
	}

	/**
	 * load web sheet from inputStream file with data object.
	 * 
	 * @param inputStream
	 *            input stream file.
	 * @param pDataContext
	 *            data object.
	 * @return 1 (success) -1 (failed)
	 */
	public final int loadWebSheet(final InputStream inputStream,
			final Map<String, Object> pDataContext) {
		return helper.getWebSheetLoader().loadWorkbook(inputStream, pDataContext);
	}

	/**
	 * load web sheet from giving workbook.
	 * 
	 * @param pWb
	 *            workbook.
	 * @return 1 (success) -1 (failed)
	 */

	public final int loadWebSheet(final Workbook pWb) {
		return loadWebSheet(pWb, null);
	}

	/**
	 * load web sheet from giving workbook with data object.
	 * 
	 * @param pWb
	 *            workbook.
	 * @param pDataContext
	 *            data object.
	 * @return 1 (success) -1 (failed)
	 */

	public final int loadWebSheet(final Workbook pWb,
			final Map<String, Object> pDataContext) {
		return helper.getWebSheetLoader().loadWorkbook(pWb, pDataContext);
	}

	/**
	 * Triggered when user switch the tab. This will load different tab(sheet)
	 * as the current sheet.
	 * 
	 * @param event
	 *            tabchange event.
	 */
	public final void onTabChange(final TabChangeEvent event) {
		String tabName = event.getTab().getTitle();
		loadWorkSheetByTabName(tabName);
	}
	/**
	 * load worksheet by tab name.
	 * @param tabName tab name.
	 * @return 1 success. -1 failed.
	 */

	public final int loadWorkSheetByTabName(final String tabName) {

		try {
			int sheetId = helper.getWebSheetLoader().findTabIndexWithName(tabName);
			if ((getSheetConfigMap() != null)
					&& (sheetId < getSheetConfigMap().size())) {
				helper.getWebSheetLoader().loadWorkSheet(tabName);
			}
			return 1;
		} catch (Exception ex) {
			LOG.fine("loadWorkSheetByTabName failed. error = "
					+ ex.getMessage());
		}
		return -1;
	}

	/** for download file. */
	private StreamedContent exportFile;

	/**
	 * get export file.
	 * 
	 * @return exportfile.
	 */

	public final StreamedContent getExportFile() {
		return exportFile;
	}

	/** download current workbook. */
	public final void doExport() {
		try {

			String fileName = "WebSheetTemplate" + "."
					+ TieConstants.EXCEL_2007_TYPE;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			wb.write(out);
			InputStream stream = new BufferedInputStream(
					new ByteArrayInputStream(out.toByteArray()));
			exportFile = new DefaultStreamedContent(stream,
					"application/force-download", fileName);

		} catch (Exception e) {
			LOG.severe("Error in export file : " + e.getLocalizedMessage());
		}
		return;
	}

	/**
	 * triggered before validation process.
	 * 
	 * @param passEmptyCheck
	 *            true(allow pass empty fields) false ( not allow pass empty
	 *            fields).
	 * @return true (pass) false (failed)
	 */
	private boolean preValidation(final boolean passEmptyCheck) {

		String tabName = helper.getValidationHandler()
				.findFirstInvalidSheet(passEmptyCheck);
		if (tabName != null) {
			helper.getWebSheetLoader().loadWorkSheet(tabName);
			return false;
		}
		return true;
	}

	/**
	 * set full validation flag with javascript for holding in client side.
	 * 
	 * @param fullflag
	 *            true or false
	 */
	private void setFullValidationInView(final Boolean fullflag) {

		Map<String, Object> viewMap = FacesContext.getCurrentInstance()
				.getViewRoot().getViewMap();
		Boolean flag = (Boolean) viewMap.get("fullValidation");
		if ((flag == null) || (!flag.equals(fullflag))) {
			viewMap.put("fullValidation", fullflag);
		}
	}

	/**
	 * Save the current workbooks.
	 */
	public final void doSave() {

		fullValidation = false;
		setFullValidationInView(fullValidation);
		if (!preValidation(true)) {
			LOG.info("Validation failded before saving");
			return;
		}
		processSave();

	}

	/**
	 * save process. unfinished.
	 * 
	 * @return 1 (success) -1 (failed).
	 */
	private int processSave() {

		return 1;
	}

	/**
	 * Needed a change handler on the note field which doesn't need all the
	 * other code below.
	 * 
	 * @param event
	 *            ajax event.
	 */
	public final void noteChangeEvent(final AjaxBehaviorEvent event) {
		helper.getWebSheetLoader().setUnsavedStatus(RequestContext.getCurrentInstance(),
				true);
	}

	/**
	 * Triggered when value in cells changed. e.g. user edit cell.
	 * 
	 * @param event
	 *            ajax event.
	 */
	public final void valueChangeEvent(final AjaxBehaviorEvent event) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String tblName = getWebFormClientId();
		UIComponent target = event.getComponent();

		boolean pass = helper.getValidationHandler().validateCell(target);
		if (pass) {
			// to improve performance, re-validate current row only
			// page validation take times. will happen when change tab(page) or
			// reload page.
			int[] rowcol = CellUtility
					.getRowColFromComponentAttributes(target);
			helper.getValidationHandler().validateRowInCurrentPage(rowcol[0], true);
			// refresh current page calculation fields
			UIComponent s = facesContext.getViewRoot().findComponent(
					tblName);
			if (s != null) {
				DataTable webDataTable = (DataTable) s;
				int first = webDataTable.getFirst();
				int rowsToRender = webDataTable.getRowsToRender();
				int rowCounts = webDataTable.getRowCount();
				int top = this.getCurrentTopRow();
				int left = this.getCurrentLeftColumn();

				String sheetName = getSheetConfigMap().get(
						this.getCurrentTabName()).getSheetName();
				Sheet sheet1 = getWb().getSheet(sheetName);
				for (int i = first; i <= (first + rowsToRender); i++) {
					if (i < rowCounts) {
						FacesRow dataRow = bodyRows.get(i);
						int isize = dataRow.getCells().size();
						for (int index = 0; index < isize; index++) {
							FacesCell fcell = dataRow.getCells().get(index);
							Cell poiCell = this.getCellHelper()
									.getPoiCellWithRowColFromCurrentPage(
											i + top, index + left);
							if (poiCell != null) {
								getWebSheetLoader().refreshCachedCell(
										tblName, i, index, sheet1, poiCell,
										fcell);
							}
						}
					}

				}
			}
		}
		helper.getWebSheetLoader().setUnsavedStatus(RequestContext.getCurrentInstance(),
				true);
	}

	/**
	 * check whether current workbook contain multiple pages.
	 * 
	 * @return true (multiple pages) false ( single page).
	 */
	public final boolean isMultiplePage() {
		if ((bodyRows != null)
				&& (bodyRows.size() > this.getMaxRowsPerPage())) {
			return true;
		}
		return false;
	}

	/**
	 * called before bean gone.
	 */
	@PreDestroy
	public final void finish() {
		LOG.fine("finishing view webformbean");
		if (FacesContext.getCurrentInstance() == null) {
			LOG.info("session has gone");
		}

	}

	/**
	 * get sheet config map.
	 * 
	 * @return sheet config map.
	 */
	public final Map<String, SheetConfiguration> getSheetConfigMap() {
		return sheetConfigMap;
	}

	/**
	 * set sheet config map.
	 * 
	 * @param pSheetConfigMap
	 *            sheet config map.
	 */
	public final void setSheetConfigMap(
			final Map<String, SheetConfiguration> pSheetConfigMap) {
		this.sheetConfigMap = pSheetConfigMap;
	}

	/**
	 * cells map for current display sheet.
	 */
	@SuppressWarnings("rawtypes")
	private Map cellsMap = new CellMap(this);

	/**
	 * Gets the cells map.
	 *
	 * @return the cells map
	 */
	@SuppressWarnings("rawtypes")
	public final Map getCellsMap() {
		return cellsMap;
	}

	/**
	 * initial load process. designed for extension.
	 */
	public void initialLoad() {
	}

	/**
	 * Triggered when user click add row button.
	 * 
	 * @param rowIndex
	 *            row index.
	 */
	public final void addRepeatRow(final int rowIndex) {
		this.helper.getWebSheetLoader().addRepeatRow(rowIndex);
	}

	/**
	 * Triggered when user click delete row button.
	 * 
	 * @param rowIndex
	 *            row index.
	 */
	public final void deleteRepeatRow(final int rowIndex) {
		this.helper.getWebSheetLoader().deleteRepeatRow(rowIndex);
	}

	/**
	 * get cell attributes map.
	 * 
	 * @return cell attributes map.
	 */
	public final CellAttributesMap getCellAttributesMap() {
		return cellAttributesMap;
	}

	/**
	 * cell default control.
	 */
	private Map<String, Map<String, String>> 
	cellDefaultControl = new HashMap<String, Map<String, String>>();

	/**
	 * get cell default control.
	 * 
	 * @return cell default control.
	 */
	public final Map<String, Map<String, String>> getCellDefaultControl() {
		return cellDefaultControl;
	}

	/**
	 * populate component.
	 * 
	 * @param event
	 *            component system event.
	 */
	public final void populateComponent(final ComponentSystemEvent event) {
		UIComponent component = event.getComponent();
		int[] rowcol = CellUtility
				.getRowColFromComponentAttributes(component);
		int row = rowcol[0];
		int col = rowcol[1];
		FacesCell fcell = CellUtility.getFacesCellFromBodyRow(row, col,
				bodyRows);
		CellControlsHelper.populateAttributes(component, fcell,
				this.getCellDefaultControl());
	}

	/**
	 * get current data context name.
	 * 
	 * @return current data context name.
	 */
	public final String getCurrentDataContextName() {
		return current.getCurrentDataContextName();
	}

	/**
	 * set current data context name.
	 * 
	 * @param pcurrentDataContextName
	 *            current data context name.
	 */
	public final void setCurrentDataContextName(
			final String pcurrentDataContextName) {
		this.current.setCurrentDataContextName(pcurrentDataContextName);
	}

}
