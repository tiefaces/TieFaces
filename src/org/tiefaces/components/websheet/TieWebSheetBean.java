/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.chart.ChartHelper;
import org.tiefaces.components.websheet.chart.ChartsData;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CachedCells;
import org.tiefaces.components.websheet.dataobjects.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.CellMap;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.HeaderCell;
import org.tiefaces.components.websheet.serializable.SerialDataContext;
import org.tiefaces.components.websheet.serializable.SerialWorkbook;
import org.tiefaces.components.websheet.service.CellHelper;
import org.tiefaces.components.websheet.service.PicturesHelper;
import org.tiefaces.components.websheet.service.ValidationHandler;
import org.tiefaces.components.websheet.service.WebSheetLoader;
import org.tiefaces.components.websheet.utility.CellControlsUtility;
import org.tiefaces.components.websheet.utility.CellUtility;

/**
 * Main class for web sheet.
 * 
 * @author Jason Jiang
 *
 */
public class TieWebSheetBean extends TieWebSheetView
		implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3495468356246589276L;

	/** hold instance for columns in current display sheet. */
	private List<String> columns = new ArrayList<>();
	/** hold instance for each body rows in current display sheet. */
	private List<FacesRow> bodyRows;
	/** hold instance for each header rows in current display sheet. */
	private List<List<HeaderCell>> headerRows;
	/** current workbook. */
	private SerialWorkbook serialWb;
	/** current workbook wrapper for formula parser. */
	private transient XSSFEvaluationWorkbook wbWrapper;
	/** current formula evaluator. */
	private transient FormulaEvaluator formulaEvaluator;
	/** current dataFormatter. */
	private transient DataFormatter dataFormatter;
	/** hold data object context. */
	private SerialDataContext serialDataContext;
	/** hold pictures for current display sheet. */
	private transient Map<String, Picture> picturesMap;
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
	private transient ChartsData chartsData;

	/** hold cached cells in current display sheet. */
	private CachedCells cachedCells;
	/**
	 * The max column counts across sheets of this workbook. e.g. sheet1 has 3
	 * columns, sheet2 has 5 columns. maxColCounts = 5;
	 */
	private int maxColCounts = 0;

	/** hold expressionEngine instance. */
	private transient ExpressionEngine expEngine = null;
	/** hold current objects. */
	private TieWebSheetBeanCurrent current;

	/** weather process full a validation. */
	private Boolean fullValidation = false;
	/** create bean's this.getHelper(). */
	private transient TieWebSheetBeanHelper helper = null;
	/**
	 * Client id for whole websheet component. This is the top level client id.
	 * There're tabs and web forms under this top level.
	 */
	private String clientId = null;
	/** Client id for web forms. */
	private transient String webFormClientId = null;

	/** skip configuration. show the excel form as is. */
	private boolean skipConfiguration = false;

	/**
	 * cell default control.
	 */
	private Map<String, Map<String, String>> cellDefaultControl = new HashMap<>();

	/** for download file. */
	private transient StreamedContent exportFile;

	/**
	 * cells map for current display sheet.
	 */

	private CellMap cellsMap = new CellMap(this);
	
	/**
	 * workaround for rendered attributes.
	 * True -- work sheet will be rendered.
	 * False -- work sheet not rendered.
	 */
	private boolean rendered = true;	
	
	private Locale defaultLocale = null;
	
	private String defaultDatePattern = null;
	

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(TieWebSheetBean.class.getName());

	/** constructor. Allow for extension. */
	public TieWebSheetBean() {
		LOG.fine("TieWebSheetBean Constructor");
	}

	/** initialize. */
	@PostConstruct
	public void init() {
		initialLoad();
	}

	/**
	 * assign web form client id.
	 * 
	 * @param pWebFormClientId
	 *            String client id name.
	 */
	public void setWebFormClientId(final String pWebFormClientId) {
		this.webFormClientId = pWebFormClientId;
	}

	/**
	 * Gets the web form client id.
	 *
	 * @return web form client Id.
	 */
	public String getWebFormClientId() {
		return webFormClientId;
	}

	/**
	 * Gets the client id.
	 *
	 * @return client id.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets the client id.
	 *
	 * @param pClientId
	 *            client Id.
	 */
	public void setClientId(final String pClientId) {
		this.clientId = pClientId;
	}

	/**
	 * get body rows.
	 * 
	 * @return body rows.
	 */
	public List<FacesRow> getBodyRows() {
		if (this.bodyRows == null) {
			this.bodyRows = new ArrayList<>();
		}
		return bodyRows;
	}

	/**
	 * set body rows.
	 * 
	 * @param pBodyRows
	 *            body rows list.
	 */
	public void setBodyRows(final List<FacesRow> pBodyRows) {
		this.bodyRows = pBodyRows;
	}

	/**
	 * Gets the header rows.
	 *
	 * @return return header row list.
	 */
	public List<List<HeaderCell>> getHeaderRows() {
		if (this.headerRows == null) {
			this.headerRows = new ArrayList<>();
		}
		return headerRows;
	}

	/**
	 * set header rows.
	 * 
	 * @param pHeaderRows
	 *            header rows list.
	 */
	public void setHeaderRows(final List<List<HeaderCell>> pHeaderRows) {
		this.headerRows = pHeaderRows;
	}

	/**
	 * set columns.
	 * 
	 * @param pColumns
	 *            column list.
	 */
	public void setColumns(final List<String> pColumns) {
		this.columns = pColumns;
	}

	/**
	 * Gets the serial wb.
	 *
	 * @return the serial_wb
	 */
	public SerialWorkbook getSerialWb() {
		if (serialWb == null) {
			serialWb = new SerialWorkbook();
		}
		return serialWb;
	}

	/**
	 * Sets the serial wb.
	 *
	 * @param pserialWb
	 *            the serial_wb to set
	 */
	public void setSerialWb(final SerialWorkbook pserialWb) {
		this.serialWb = pserialWb;
	}

	/**
	 * Gets the serial data context.
	 *
	 * @return the serialDataContext
	 */
	public SerialDataContext getSerialDataContext() {
		if (serialDataContext == null) {
			serialDataContext = new SerialDataContext();
		}
		return serialDataContext;
	}

	/**
	 * Sets the serial data context.
	 *
	 * @param pserialDataContext
	 *            the serialDataContext to set
	 */
	public void setSerialDataContext(
			final SerialDataContext pserialDataContext) {
		this.serialDataContext = pserialDataContext;
	}

	/**
	 * get workbook.
	 * 
	 * @return workbook.
	 */
	public Workbook getWb() {
		return this.getSerialWb().getWb();
	}

	/**
	 * Set up workbook. Also create evaluation wrapper.
	 * 
	 * @param pWb
	 *            workbook.
	 */

	public void setWb(final Workbook pWb) {

		this.getSerialWb().setWb(pWb);
		this.wbWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) pWb);
	}

	/**
	 * Return evaluation wrapper if needed.
	 * 
	 * @return wbwrapper.
	 */
	public XSSFEvaluationWorkbook getWbWrapper() {
		if ((this.wbWrapper == null) && (this.getWb() != null)) {
			this.wbWrapper = XSSFEvaluationWorkbook
					.create((XSSFWorkbook) this.getWb());
		}
		return wbWrapper;
	}

	/**
	 * get formulaevaluator.
	 * 
	 * @return formulaevaluator.
	 */
	public FormulaEvaluator getFormulaEvaluator() {
		if ((this.formulaEvaluator == null) && (this.getWb() != null)) {
			this.formulaEvaluator = this.getWb().getCreationHelper()
					.createFormulaEvaluator();
		}
		return formulaEvaluator;
	}

	/**
	 * set formulaevaluator.
	 * 
	 * @param pFormulaEvaluator
	 *            formulaevaluator.
	 */
	public void setFormulaEvaluator(
			final FormulaEvaluator pFormulaEvaluator) {
		this.formulaEvaluator = pFormulaEvaluator;
	}

	/**
	 * get data formatter.
	 * 
	 * @return dataformatter.
	 */
	public DataFormatter getDataFormatter() {
		if (this.dataFormatter == null) {
			this.dataFormatter = new DataFormatter(this.getDefaultLocale());
		}
		return dataFormatter;
	}

	/**
	 * set dataformatter.
	 * 
	 * @param pDataFormatter
	 *            dataformatter.
	 */
	public void setDataFormatter(final DataFormatter pDataFormatter) {
		this.dataFormatter = pDataFormatter;
	}

	/**
	 * get columns.
	 * 
	 * @return list of columns.
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * Gets the current.
	 *
	 * @return the current
	 */
	public TieWebSheetBeanCurrent getCurrent() {
		if (current == null) {
			current = new TieWebSheetBeanCurrent();
		}
		return current;
	}

	/**
	 * get full validation.
	 * 
	 * @return true if it's fullvalidation.
	 */
	public Boolean getFullValidation() {
		return fullValidation;
	}

	/**
	 * set full validation.
	 * 
	 * @param pFullValidation
	 *            full validation flag.
	 */
	public void setFullValidation(final Boolean pFullValidation) {
		this.fullValidation = pFullValidation;
	}

	/**
	 * get bean helper.
	 * 
	 * @return helper.
	 */

	public TieWebSheetBeanHelper getHelper() {
		if (this.helper == null) {
			this.helper = new TieWebSheetBeanHelper(this);
		}
		return this.helper;
	}

	/**
	 * get cell this.getHelper().
	 * 
	 * @return cell this.getHelper().
	 */
	public CellHelper getCellHelper() {
		return this.getHelper().getCellHelper();
	}

	/**
	 * get expression engine.
	 * 
	 * @return exp engine.
	 */
	public ExpressionEngine getExpEngine() {
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
	public WebSheetLoader getWebSheetLoader() {
		return this.getHelper().getWebSheetLoader();
	}

	/**
	 * get validationhandler.
	 * 
	 * @return validation handler.
	 */
	public ValidationHandler getValidationHandler() {
		return this.getHelper().getValidationHandler();
	}

	/**
	 * get picthis.getHelper().
	 * 
	 * @return picHelper.
	 */
	public PicturesHelper getPicHelper() {
		return this.getHelper().getPicHelper();
	}

	/**
	 * get chartthis.getHelper().
	 * 
	 * @return chartthis.getHelper().
	 */
	public ChartHelper getChartHelper() {
		return this.getHelper().getChartHelper();
	}

	/**
	 * get pictures map.
	 * 
	 * @return pictures map.
	 */
	public Map<String, Picture> getPicturesMap() {
		if (this.picturesMap == null) {
			this.picturesMap = new HashMap<>();
		}
		return picturesMap;
	}

	/**
	 * set pictures map.
	 * 
	 * @param pPicturesMap
	 *            pictures map.
	 */
	public void setPicturesMap(final Map<String, Picture> pPicturesMap) {
		this.picturesMap = pPicturesMap;
	}

	/**
	 * charts data.
	 * 
	 * @return charts data
	 */
	public ChartsData getCharsData() {
		if (this.chartsData == null) {
			this.chartsData = new ChartsData();
		}
		return this.chartsData;
	}

	/**
	 * Gets the cached cells.
	 *
	 * @return cached cells.
	 */
	public CachedCells getCachedCells() {
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
	public void setCachedCells(final CachedCells pCachedCells) {
		this.cachedCells = pCachedCells;
	}

	/**
	 * Gets the max col counts.
	 *
	 * @return max column counts.
	 */
	public int getMaxColCounts() {
		if (this.maxColCounts == 0) {
			reCalcMaxColCounts();
		}
		return maxColCounts;
	}

	/**
	 * Checks if is skip configuration.
	 *
	 * @return the skipConfiguration
	 */
	public boolean isSkipConfiguration() {
		return skipConfiguration;
	}

	/**
	 * Sets the skip configuration.
	 *
	 * @param pskipConfiguration
	 *            the skipConfiguration to set
	 */
	public void setSkipConfiguration(final boolean pskipConfiguration) {
		this.skipConfiguration = pskipConfiguration;
	}

	/**
	 * recalculate max coulumn count across sheets in the workbook.
	 */
	public void reCalcMaxColCounts() {
		if ((this.getSheetConfigMap() == null)
				|| (this.getSheetConfigMap().isEmpty())) {
			this.maxColCounts = 0;
			return;
		}
		int maxColumns = 0;
		for (SheetConfiguration sheetConfig : this.getSheetConfigMap()
				.values()) {
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
	public int loadWebSheet(final InputStream inputStream) {
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
	public int loadWebSheet(final InputStream inputStream,
			final Map<String, Object> pDataContext) {
		return this.getHelper().getWebSheetLoader()
				.loadWorkbook(inputStream, pDataContext);
	}

	/**
	 * load web sheet from giving workbook.
	 * 
	 * @param pWb
	 *            workbook.
	 * @return 1 (success) -1 (failed)
	 */

	public int loadWebSheet(final Workbook pWb) {
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

	public int loadWebSheet(final Workbook pWb,
			final Map<String, Object> pDataContext) {
		return this.getHelper().getWebSheetLoader().loadWorkbook(pWb,
				pDataContext);
	}

	/**
	 * Triggered when user switch the tab. This will load different tab(sheet)
	 * as the current sheet.
	 * 
	 * @param event
	 *            tabchange event.
	 */
	public void onTabChange(final TabChangeEvent event) {
		String tabName = event.getTab().getTitle();
		loadWorkSheetByTabName(tabName);
	}

	/**
	 * load worksheet by tab name.
	 * 
	 * @param tabName
	 *            tab name.
	 * @return 1 success. -1 failed.
	 */

	public int loadWorkSheetByTabName(final String tabName) {

		try {
			int sheetId = this.getHelper().getWebSheetLoader()
					.findTabIndexWithName(tabName);
			if ((getSheetConfigMap() != null)
					&& (sheetId < getSheetConfigMap().size())) {
				this.getHelper().getWebSheetLoader().loadWorkSheet(tabName);
				setActiveTabIndex(sheetId);
			}
			return 1;
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "loadWorkSheetByTabName failed. error = "
					+ ex.getMessage(), ex);

		}
		return -1;
	}

	/**
	 * get export file.
	 * 
	 * @return exportfile.
	 */

	public StreamedContent getExportFile() {
		return exportFile;
	}

	/** download current workbook. */
	public void doExport() {
		try {

			String fileName = "WebSheetTemplate" + "."
					+ TieConstants.EXCEL_2007_TYPE;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			this.getWb().write(out);
			InputStream stream = new BufferedInputStream(
					new ByteArrayInputStream(out.toByteArray()));
			exportFile = new DefaultStreamedContent(stream,
					"application/force-download", fileName);

		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error in export file : " + e.getLocalizedMessage(), e);
		}
		return;
	}

	/**
	 * Save the current workbooks.
	 */
	public void doSave() {

		fullValidation = false;
		this.getHelper().getValidationHandler()
				.setFullValidationInView(fullValidation);
		if (!this.getHelper().getValidationHandler().preValidation(true)) {
			LOG.fine("Validation failded before saving");
			return;
		}

		processSave();
	}

	/**
	 * save process. User need override this method to save into db.
	 * 
	 */
	public void processSave() {
		this.getHelper().getWebSheetLoader().setUnsavedStatus(
				RequestContext.getCurrentInstance(), false);
		return;
	}

	/**
	 * Triggered when value in cells changed. e.g. user edit cell.
	 * 
	 * @param event
	 *            ajax event.
	 */
	public void valueChangeEvent(final AjaxBehaviorEvent event) {
		this.getHelper().getValidationHandler().valueChangeEvent(event);
	}

	/**
	 * check whether current workbook contain multiple pages.
	 * 
	 * @return true (multiple pages) false ( single page).
	 */
	public boolean isMultiplePage() {
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
	public void finish() {
		if (FacesContext.getCurrentInstance() == null) {
			LOG.info("session has gone");
		}

	}

	/**
	 * get sheet config map.
	 * 
	 * @return sheet config map.
	 */
	public Map<String, SheetConfiguration> getSheetConfigMap() {
		return this.getSerialWb().getSheetConfigMap();
	}

	/**
	 * set sheet config map.
	 * 
	 * @param pSheetConfigMap
	 *            sheet config map.
	 */
	public void setSheetConfigMap(
			final Map<String, SheetConfiguration> pSheetConfigMap) {
		this.getSerialWb().setSheetConfigMap(pSheetConfigMap);
	}

	/**
	 * Gets the cells map.
	 *
	 * @return the cells map
	 */
	@SuppressWarnings("rawtypes")
	public Map getCellsMap() {
		return cellsMap;
	}

	/**
	 * initial load process. designed for extension.
	 */
	public void initialLoad() {
		// designed for extension.
	}

	/**
	 * Triggered when user click add row button.
	 * 
	 * @param rowIndex
	 *            row index.
	 */
	public void addRepeatRow(final int rowIndex) {
		this.getHelper().getWebSheetLoader().addRepeatRow(rowIndex);
	}

	/**
	 * Triggered when user click delete row button.
	 * 
	 * @param rowIndex
	 *            row index.
	 */
	public void deleteRepeatRow(final int rowIndex) {
		this.getHelper().getWebSheetLoader().deleteRepeatRow(rowIndex);
	}

	/**
	 * get cell attributes map.
	 * 
	 * @return cell attributes map.
	 */
	public CellAttributesMap getCellAttributesMap() {
		return cellAttributesMap;
	}

	/**
	 * get cell default control.
	 * 
	 * @return cell default control.
	 */
	public Map<String, Map<String, String>> getCellDefaultControl() {
		return cellDefaultControl;
	}

	/**
	 * populate component.
	 * 
	 * @param event
	 *            component system event.
	 */
	public void populateComponent(final ComponentSystemEvent event) {
		UIComponent component = event.getComponent();
		int[] rowcol = CellUtility
				.getRowColFromComponentAttributes(component);
		int row = rowcol[0];
		int col = rowcol[1];
		FacesCell fcell = CellUtility.getFacesCellFromBodyRow(row, col,
				this.getBodyRows(), this.getCurrent().getCurrentTopRow(),
				this.getCurrent().getCurrentLeftColumn());
		CellControlsUtility.populateAttributes(component, fcell,
				this.getCellDefaultControl());
	}

	/**
	 * Gets the current sheet config.
	 *
	 * @return the currentSheetConfig
	 */
	public SheetConfiguration getCurrentSheetConfig() {
		String currentTabName = this.getCurrent().getCurrentTabName();
		if (currentTabName == null) {
			return null;
		}
		return this.getSheetConfigMap().get(currentTabName);
	}

	/**
	 * load the bean from saving.
	 * 
	 * @param in
	 *            inputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void readObject(final java.io.ObjectInputStream in)
			throws IOException {
		try {
			in.defaultReadObject();
			recover();
		} catch (EncryptedDocumentException | ClassNotFoundException e) {
			LOG.log(Level.SEVERE,
					" error in readObject of serialWorkbook : "
							+ e.getLocalizedMessage(),
					e);
		}
	}

	/**
	 * recover objects after deserilize.
	 */
	private void recover() {
		if (this.getWb() != null) {
			this.getChartHelper().loadChartsMap();
			this.getPicHelper().loadPicturesMap();
		}
	}

	/**
	 * Checks if is show tab view.
	 *
	 * @return true, if is show tab view
	 */
	public boolean isShowTabView() {
		if (this.getTabs() == null) {
			return false;
		}
		return !this.isHideSingleSheetTabTitle()
				|| (this.getTabs().size() > 1);

	}
	

	/**
	 * @return the rendered
	 */
	public boolean isRendered() {
		return rendered;
	}

	/**
	 * @param rendered the rendered to set
	 */
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	

	public Locale getDefaultLocale() {
	    if (defaultLocale== null) {
		defaultLocale = Locale.getDefault();
	    }
	    return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public String getDefaultDatePattern() {
	    if (defaultDatePattern == null) {
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT,
			Locale.getDefault());
		defaultDatePattern = ((SimpleDateFormat) formatter).toLocalizedPattern();
	    }
	    return defaultDatePattern;
	}

	public void setDefaultDatePattern(String defaultDatePattern) {
		this.defaultDatePattern = defaultDatePattern;
	}

	public String getDecimalSeparatorByDefaultLocale() {
		final DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(getDefaultLocale());
		return "" + nf.getDecimalFormatSymbols().getDecimalSeparator();
	}

	public String getThousandSeparatorByDefaultLocale() {
		final DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(getDefaultLocale());
		return "" + nf.getDecimalFormatSymbols().getGroupingSeparator();
	}

}
