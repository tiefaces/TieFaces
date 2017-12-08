/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.TieWebSheetView.TabModel;
import org.tiefaces.components.websheet.configuration.ConfigBuildRef;
import org.tiefaces.components.websheet.configuration.ConfigurationHandler;
import org.tiefaces.components.websheet.configuration.RangeBuildRef;
import org.tiefaces.components.websheet.configuration.RowsMapping;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.HeaderCell;
import org.tiefaces.components.websheet.dataobjects.TieCell;
import org.tiefaces.components.websheet.utility.CellStyleUtility;
import org.tiefaces.components.websheet.utility.CellUtility;
import org.tiefaces.components.websheet.utility.CommandUtility;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;
import org.tiefaces.components.websheet.utility.SaveAttrsUtility;
import org.tiefaces.components.websheet.utility.WebSheetUtility;
import org.tiefaces.exception.AddRowException;
import org.tiefaces.exception.DeleteRowException;

/**
 * The Class WebSheetLoader.
 */
public class WebSheetLoader implements Serializable {

	/** The parent. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(WebSheetLoader.class.getName());

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new web sheet loader.
	 *
	 * @param pParent
	 *            the parent
	 */
	public WebSheetLoader(final TieWebSheetBean pParent) {
		this.parent = pParent;
		LOG.fine("TieWebSheetLoader Constructor");
	}

	/**
	 * Load header rows.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param cellRangeMap
	 *            the cell range map
	 * @param skippedRegionCells
	 *            the skipped region cells
	 */
	private void loadHeaderRows(final SheetConfiguration sheetConfig,
			final Map<String, CellRangeAddress> cellRangeMap,
			final List<String> skippedRegionCells) {

		int top = sheetConfig.getHeaderCellRange().getTopRow();
		int bottom = sheetConfig.getHeaderCellRange().getBottomRow();
		int left = sheetConfig.getHeaderCellRange().getLeftCol();
		int right = sheetConfig.getHeaderCellRange().getRightCol();

		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		int totalWidth = CellStyleUtility.calcTotalWidth(sheet1, left,
				right,
				WebSheetUtility
						.pixel2WidthUnits(parent.getLineNumberColumnWidth()
								+ parent.getAddRowColumnWidth()));

		RangeBuildRef rangeBuildRef = new RangeBuildRef(left, right,
				totalWidth, sheet1);

		if (sheetConfig.isFixedWidthStyle()) {
			parent.setTableWidthStyle("table-layout: fixed; width:"
					+ WebSheetUtility.widthUnits2Pixel(totalWidth) + "px;");
		}

		parent.setLineNumberColumnWidthStyle(
				getWidthStyle(
						WebSheetUtility.pixel2WidthUnits(
								parent.getLineNumberColumnWidth()),
						totalWidth));
		parent.setAddRowColumnWidthStyle(
				"width:" + parent.getAddRowColumnWidth() + "px;");

		parent.getHeaderRows().clear();

		if (top < 0) {
			// this is blank configuration. set column letter as header
			parent.getHeaderRows().add(loadHeaderRowWithoutConfigurationTab(
					rangeBuildRef, true));
			// set showlinenumber to true as default
			parent.setShowLineNumber(true);
		} else {
			parent.getHeaderRows().add(loadHeaderRowWithoutConfigurationTab(
					rangeBuildRef, false));
			for (int i = top; i <= bottom; i++) {
				parent.getHeaderRows()
						.add(loadHeaderRowWithConfigurationTab(sheetConfig,
								rangeBuildRef, i, cellRangeMap,
								skippedRegionCells));

			}
			// set showlinenumber to false as default
			parent.setShowLineNumber(false);

		}

	}

	/**
	 * Load header row without configuration tab.
	 *
	 * @param rangeBuildRef
	 *            the range build ref
	 * @param rendered
	 *            the rendered
	 * @return the list
	 */
	private List<HeaderCell> loadHeaderRowWithoutConfigurationTab(
			final RangeBuildRef rangeBuildRef, final boolean rendered) {

		int firstCol = rangeBuildRef.getLeft();
		int lastCol = rangeBuildRef.getRight();
		double totalWidth = (double) rangeBuildRef.getTotalWidth();
		Sheet sheet1 = rangeBuildRef.getSheet();
		List<HeaderCell> headercells = new ArrayList<>();
		for (int i = firstCol; i <= lastCol; i++) {
			if (!sheet1.isColumnHidden(i)) {
				String style = getHeaderColumnStyle(parent.getWb(), null,
						sheet1.getColumnWidth(i), totalWidth);
				headercells.add(new HeaderCell("1", "1", style, style,
						WebSheetUtility.getExcelColumnName(i), rendered,
						true));
			}
		}
		fillToMaxColumns(headercells);
		return headercells;

	}

	/**
	 * Fill to max columns.
	 *
	 * @param headercells
	 *            the headercells
	 */
	private void fillToMaxColumns(final List<HeaderCell> headercells) {
		if (headercells.size() < parent.getMaxColCounts()) {
			int fills = parent.getMaxColCounts() - headercells.size();
			for (int s = 0; s < fills; s++) {
				headercells.add(
						new HeaderCell("1", "1", "", "", "", false, false));
			}
		}
	}

	/**
	 * Gets the header column style.
	 *
	 * @param wb
	 *            the wb
	 * @param cell
	 *            the cell
	 * @param colWidth
	 *            the col width
	 * @param totalWidth
	 *            the total width
	 * @return the header column style
	 */
	private String getHeaderColumnStyle(final Workbook wb, final Cell cell,
			final double colWidth, final double totalWidth) {

		String columnstyle = "";
		if (cell != null) {
			columnstyle += CellStyleUtility.getCellStyle(wb, cell, "")
					+ CellStyleUtility.getCellFontStyle(wb, cell); // +
		}

		columnstyle = columnstyle + getWidthStyle(colWidth, totalWidth);
		return columnstyle;
	}

	/**
	 * Gets the width style.
	 *
	 * @param colWidth
	 *            the col width
	 * @param totalWidth
	 *            the total width
	 * @return the width style
	 */
	private String getWidthStyle(final double colWidth,
			final double totalWidth) {
		double percentage = FacesUtility
				.round(TieConstants.CELL_FORMAT_PERCENTAGE_VALUE * colWidth
						/ totalWidth, 2);
		return "width:" + percentage
				+ TieConstants.CELL_FORMAT_PERCENTAGE_SYMBOL + ";";
	}

	/**
	 * Load header row with configuration tab.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param rangeBuildRef
	 *            the range build ref
	 * @param currentRow
	 *            the current row
	 * @param cellRangeMap
	 *            the cell range map
	 * @param skippedRegionCells
	 *            the skipped region cells
	 * @return the list
	 */
	private List<HeaderCell> loadHeaderRowWithConfigurationTab(
			final SheetConfiguration sheetConfig,
			final RangeBuildRef rangeBuildRef, final int currentRow,
			final Map<String, CellRangeAddress> cellRangeMap,
			final List<String> skippedRegionCells) {

		Sheet sheet1 = rangeBuildRef.getSheet();
		int left = rangeBuildRef.getLeft();
		int right = rangeBuildRef.getRight();
		double totalWidth = (double) rangeBuildRef.getTotalWidth();
		Row row = sheet1.getRow(currentRow);
		List<HeaderCell> headercells = new ArrayList<>();
		for (int cindex = left; cindex <= right; cindex++) {
			String cellindex = CellUtility.getCellIndexNumberKey(cindex,
					currentRow);

			if (!skippedRegionCells.contains(cellindex)
					&& !sheet1.isColumnHidden(cindex)) {
				Cell cell = null;
				if (row != null) {
					cell = row.getCell(cindex,
							MissingCellPolicy.CREATE_NULL_AS_BLANK);
				}
				int originRowIndex = ConfigurationUtility
						.getOriginalRowNumInHiddenColumn(row);
				if (cell != null) {
					FacesCell fcell = new FacesCell();

					CellUtility.convertCell(sheetConfig, fcell, cell,
							cellRangeMap, originRowIndex,
							parent.getCellAttributesMap(), null);
					parent.getPicHelper().setupFacesCellPictureCharts(
							sheet1, fcell, cell, WebSheetUtility
									.getFullCellRefName(sheet1, cell));
					CellStyleUtility.setupCellStyle(parent.getWb(), fcell,
							cell, row.getHeightInPoints());
					fcell.setColumnStyle(fcell.getColumnStyle()
							+ getColumnWidthStyle(sheet1, cellRangeMap,
									cellindex, cindex, totalWidth));
					fcell.setColumnIndex(cindex);

					headercells.add(new HeaderCell(
							Integer.toString(fcell.getRowspan()),
							Integer.toString(fcell.getColspan()),
							fcell.getStyle(), fcell.getColumnStyle(),
							CellUtility.getCellValueWithFormat(cell,
									parent.getFormulaEvaluator(),
									parent.getDataFormatter()),
							true, true));
				}
			}

		}
		fillToMaxColumns(headercells);
		return headercells;
	}

	/**
	 * Gets the column width style.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param cellRangeMap
	 *            the cell range map
	 * @param cellindex
	 *            the cellindex
	 * @param cindex
	 *            the cindex
	 * @param totalWidth
	 *            the total width
	 * @return the column width style
	 */
	private String getColumnWidthStyle(final Sheet sheet1,
			final Map<String, CellRangeAddress> cellRangeMap,
			final String cellindex, final int cindex,
			final double totalWidth) {

		CellRangeAddress caddress = cellRangeMap.get(cellindex);
		double colWidth;
		// check whether the cell has rowspan or colspan
		if (caddress != null) {
			colWidth = CellStyleUtility.calcTotalWidth(sheet1,
					caddress.getFirstColumn(), caddress.getLastColumn(), 0);
		} else {
			colWidth = sheet1.getColumnWidth(cindex);
		}

		return getWidthStyle(colWidth, totalWidth);

	}

	// return 0 -- No template
	// return -1 -- error in open form
	// return 1 -- success

	/**
	 * Clear workbook.
	 */
	private void clearWorkbook() {
		parent.setFormulaEvaluator(null);
		parent.setDataFormatter(null);
		parent.setSheetConfigMap(null);
		parent.setTabs(null);
		parent.getSerialDataContext().setDataContext(null);
		parent.setPicturesMap(null);
		parent.setHeaderRows(null);
		parent.setBodyRows(null);
		parent.setWb(null);
		parent.getHeaderRows().clear();
		parent.getBodyRows().clear();
		parent.getCharsData().getChartsMap().clear();
		parent.getCharsData().getChartDataMap().clear();
		parent.getCharsData().getChartAnchorsMap().clear();
		parent.getCharsData().getChartPositionMap().clear();
		parent.getCellAttributesMap().clear();
	}

	/**
	 * Load workbook.
	 *
	 * @param fis
	 *            the fis
	 * @param dataContext
	 *            the data context
	 * @return the int
	 */
	public final int loadWorkbook(final InputStream fis,
			final Map<String, Object> dataContext) {

		try {
			Workbook wb = WorkbookFactory.create(fis);
			int ireturn = loadWorkbook(wb, dataContext);
			fis.close();
			return ireturn;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Web Form loadWorkbook Error Exception = "
					+ e.getLocalizedMessage(), e);
			return -1;
		}

	}

	/**
	 * Load workbook.
	 *
	 * @param wb
	 *            the wb
	 * @param dataContext
	 *            the data context
	 * @return the int
	 */
	public final int loadWorkbook(final Workbook wb,
			final Map<String, Object> dataContext) {

		try {
			clearWorkbook();
			// only support xssf workbook now since 2016 July
			if (!(wb instanceof XSSFWorkbook)) {
				LOG.fine("Error: WebSheet only support xlsx template.");
				return -1;
			}
			LOG.fine("Begin load work book...");
			parent.setWb(wb);
			parent.getSerialDataContext().setDataContext(dataContext);
			parent.setSheetConfigMap(
					new ConfigurationHandler(parent).buildConfiguration());
			parent.reCalcMaxColCounts();
			parent.getChartHelper().loadChartsMap();
			parent.getPicHelper().loadPicturesMap();
			initSheet();
			initTabs();
			if (!parent.getTabs().isEmpty()) {
				loadWorkSheet(parent.getTabs().get(0).getTitle());
			}

		} catch (Exception e) {
			LOG.log(Level.FINE, "Web Form loadWorkbook Error Exception = "
					+ e.getLocalizedMessage(), e);
			return -1;
		}
		return 1;

	}

	/**
	 * Inits the tabs.
	 */
	private void initTabs() {
		parent.setTabs(new ArrayList<TabModel>());
		if (parent.getSheetConfigMap() != null) {
			for (String key : parent.getSheetConfigMap().keySet()) {
				parent.getTabs()
						.add(new TabModel("form_" + key, key, "form"));
			}
		}
	}

	/**
	 * Inits the sheet.
	 */
	private void initSheet() {
		loadData();
	}

	/**
	 * load data process. unfinished.
	 */
	private void loadData() {

		if (parent.getSerialDataContext().getDataContext() == null) {
			// no data objects available.
			return;
		}

		if (parent.isAdvancedContext()) {
			parent.getSerialDataContext().getDataContext().put("tiecells", new HashMap<String,TieCell>());
		}

		for (SheetConfiguration sheetConfig : parent.getSheetConfigMap()
				.values()) {
			List<RowsMapping> currentRowsMappingList = null;
			ConfigBuildRef configBuildRef = new ConfigBuildRef(
					parent.getWbWrapper(),
					parent.getWb().getSheet(sheetConfig.getSheetName()),
					parent.getExpEngine(), parent.getCellHelper(),
					sheetConfig.getCachedCells(),
					parent.getCellAttributesMap(),
					sheetConfig.getFinalCommentMap());
			int length = sheetConfig.getFormCommand().buildAt(null,
					configBuildRef,
					sheetConfig.getFormCommand().getTopRow(),
					parent.getSerialDataContext().getDataContext(),
					currentRowsMappingList);
			sheetConfig.setShiftMap(configBuildRef.getShiftMap());
			sheetConfig.setCollectionObjNameMap(
					configBuildRef.getCollectionObjNameMap());
			sheetConfig.setCommandIndexMap(
					configBuildRef.getCommandIndexMap());
			sheetConfig.setWatchList(configBuildRef.getWatchList());
			sheetConfig
					.setBodyAllowAddRows(configBuildRef.isBodyAllowAdd());
			sheetConfig.getBodyCellRange().setBottomRow(
					sheetConfig.getFormCommand().getTopRow() + length - 1);
			sheetConfig.setBodyPopulated(true);
		}
		parent.getCellHelper().reCalc();

	}

	/**
	 * Find tab index with name.
	 *
	 * @param tabname
	 *            the tabname
	 * @return the int
	 */
	public final int findTabIndexWithName(final String tabname) {

		for (int i = 0; i < parent.getTabs().size(); i++) {
			if (parent.getTabs().get(i).getTitle()
					.equalsIgnoreCase(tabname)) {
				return i;
			}
		}
		return -1;

	}

	/**
	 * Load work sheet.
	 *
	 * @param tabName
	 *            the tab name
	 */
	public final void loadWorkSheet(final String tabName) {

		int tabIndex = findTabIndexWithName(tabName);
		if (parent.getWebFormTabView() != null) {
			parent.getWebFormTabView().setActiveIndex(tabIndex);
		}
		parent.getCurrent().setCurrentTabName(tabName);
		String sheetName = parent.getSheetConfigMap().get(tabName)
				.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);
		parent.getWb().setActiveSheet(parent.getWb().getSheetIndex(sheet1));

		SheetConfiguration sheetConfig = parent.getSheetConfigMap()
				.get(tabName);

		parent.setMaxRowsPerPage(
				parent.getSheetConfigMap().get(tabName).getMaxRowPerPage());
		parent.setBodyAllowAddRows(parent.getSheetConfigMap().get(tabName)
				.isBodyAllowAddRows());

		// populate repeat rows before setup cell range map

		Map<String, CellRangeAddress> cellRangeMap = ConfigurationUtility
				.indexMergedRegion(sheet1);
		List<String> skippedRegionCells = ConfigurationUtility
				.skippedRegionCells(sheet1);
		loadHeaderRows(sheetConfig, cellRangeMap, skippedRegionCells);
		loadBodyRows(sheetConfig, cellRangeMap, skippedRegionCells);
		parent.getValidationHandler().validateCurrentPage();
		createDynamicColumns(tabName);
		// reset datatable current page to 1
		setDataTablePage(0);
		parent.getCurrent().setCurrentDataContextName(null);
		saveObjs();
		if ((RequestContext.getCurrentInstance() != null)
				&& (parent.getClientId() != null)) {
			RequestContext.getCurrentInstance()
					.update(parent.getClientId() + ":websheettab");
		}
	}

	/**
	 * Sets the data table page.
	 *
	 * @param first
	 *            the new data table page
	 */
	private void setDataTablePage(final int first) {
		if (parent.getWebFormClientId() != null) {
			final DataTable d = (DataTable) FacesContext
					.getCurrentInstance().getViewRoot()
					.findComponent(parent.getWebFormClientId());
			if (d != null) {
				d.setFirst(first);
			}
		}
	}

	/**
	 * Save objs.
	 */
	private void saveObjs() {

		try {
			if (FacesContext.getCurrentInstance() != null) {
				Map<String, Object> viewMap = FacesContext
						.getCurrentInstance().getViewRoot().getViewMap();
				viewMap.put("currentTabName",
						parent.getCurrent().getCurrentTabName());
				viewMap.put("fullValidation", parent.getFullValidation());
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"saveobjs in viewMap error = " + ex.getMessage(), ex);

		}

	}

	/**
	 * Setup row info.
	 *
	 * @param facesRow
	 *            the faces row
	 * @param sheet1
	 *            the sheet 1
	 * @param row
	 *            the row
	 * @param rowIndex
	 *            the row index
	 * @param allowAdd
	 *            the allow add
	 */
	private void setupRowInfo(final FacesRow facesRow, final Sheet sheet1,
			final Row row, final int rowIndex, final boolean allowAdd) {

		facesRow.setAllowAdd(allowAdd);
		if (row != null) {
			facesRow.setRendered(!row.getZeroHeight());
			facesRow.setRowheight(row.getHeight());
			int rowNum = ConfigurationUtility
					.getOriginalRowNumInHiddenColumn(row);
			facesRow.setOriginRowIndex(rowNum);
		} else {
			facesRow.setRendered(true);
			facesRow.setRowheight(sheet1.getDefaultRowHeight());
			facesRow.setOriginRowIndex(rowIndex);
		}

	}

	/**
	 * Load body rows.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param cellRangeMap
	 *            the cell range map
	 * @param skippedRegionCells
	 *            the skipped region cells
	 */
	private void loadBodyRows(final SheetConfiguration sheetConfig,
			final Map<String, CellRangeAddress> cellRangeMap,
			final List<String> skippedRegionCells) {

		int top = sheetConfig.getBodyCellRange().getTopRow();
		int bottom = CellUtility.getBodyBottomFromConfig(sheetConfig);
		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();

		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		parent.getBodyRows().clear();
		clearCache();

		for (int i = top; i <= bottom; i++) {

			parent.getBodyRows().add(assembleFacesBodyRow(i, sheet1, left,
					right, sheetConfig, cellRangeMap, skippedRegionCells));

		}
		sheetConfig.setBodyPopulated(true);
		parent.getCurrent().setCurrentTopRow(top);
		parent.getCurrent().setCurrentLeftColumn(left);
	}

	/**
	 * Assemble faces body row.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param sheet1
	 *            the sheet 1
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param sheetConfig
	 *            the sheet config
	 * @param cellRangeMap
	 *            the cell range map
	 * @param skippedRegionCells
	 *            the skipped region cells
	 * @return the faces row
	 */
	private FacesRow assembleFacesBodyRow(final int rowIndex,
			final Sheet sheet1, final int left, final int right,
			final SheetConfiguration sheetConfig,
			final Map<String, CellRangeAddress> cellRangeMap,
			final List<String> skippedRegionCells) {

		FacesRow facesRow = new FacesRow(rowIndex);
		Row row = sheet1.getRow(rowIndex);
		setupRowInfo(facesRow, sheet1, row, rowIndex,
				CommandUtility.isRowAllowAdd(row, sheetConfig));
		String saveAttrList = SaveAttrsUtility.getSaveAttrListFromRow(row);
		List<FacesCell> bodycells = new ArrayList<>();
		for (int cindex = left; cindex <= right; cindex++) {
			String cellindex = CellUtility.getCellIndexNumberKey(cindex,
					rowIndex);
			if (!skippedRegionCells.contains(cellindex)
					&& !sheet1.isColumnHidden(cindex)) {
				Cell cell = null;
				if (row != null) {
					cell = row.getCell(cindex,
							MissingCellPolicy.CREATE_NULL_AS_BLANK);
				}
				if (cell != null) {
					FacesCell fcell = new FacesCell();

					CellUtility.convertCell(sheetConfig, fcell, cell,
							cellRangeMap, facesRow.getOriginRowIndex(),
							parent.getCellAttributesMap(), saveAttrList);
					parent.getPicHelper().setupFacesCellPictureCharts(
							sheet1, fcell, cell, WebSheetUtility
									.getFullCellRefName(sheet1, cell));
					CellStyleUtility.setupCellStyle(parent.getWb(), fcell,
							cell, row.getHeightInPoints());
					fcell.setColumnIndex(cindex);
					bodycells.add(fcell);
					addCache(cell);
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

	/**
	 * Adds the cache.
	 * 
	 * @param cell
	 *            the cell
	 */
	private void addCache(final Cell cell) {
		parent.getCachedCells().put(cell, CellType.FORMULA);
	}

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		parent.getCachedCells().clear();

	}

	/**
	 * Refresh cached cell.
	 *
	 * @param tblName
	 *            the tbl name
	 * @param i
	 *            the i
	 * @param index
	 *            the index
	 * @param cell
	 *            the cell
	 * @param fcell
	 *            the fcell
	 */
	public final void refreshCachedCell(final String tblName, final int i,
			final int index, final Cell cell, final FacesCell fcell) {

		if ((cell != null) && (cell.getCellTypeEnum() == CellType.FORMULA)
				&& (tblName != null)) {
			try {
				processRefreshCell(tblName, i, index, cell, fcell);
			} catch (Exception ex) {
				LOG.log(Level.SEVERE, "refresh Cached Cell error : "
						+ ex.getLocalizedMessage(), ex);
			}
		}
	}

	/**
	 * Process refresh cell.
	 *
	 * @param tblName
	 *            the tbl name
	 * @param i
	 *            the i
	 * @param index
	 *            the index
	 * @param cell
	 *            the cell
	 * @param fcell
	 *            the fcell
	 */
	private void processRefreshCell(final String tblName, final int i,
			final int index, final Cell cell, final FacesCell fcell) {
		String newValue = CellUtility.getCellValueWithFormat(cell,
				parent.getFormulaEvaluator(), parent.getDataFormatter());
		if (parent.getCachedCells().isValueChanged(cell, newValue)) {
			if (fcell.isHasSaveAttr()) {
				parent.getCellHelper().saveDataInContext(cell, newValue);
			}
			RequestContext.getCurrentInstance()
					.update(tblName + ":" + i + ":cocalc" + index);
			parent.getCachedCells().put(cell, CellType.FORMULA);
		}
	}

	/**
	 * Creates the dynamic columns.
	 *
	 * @param tabName
	 *            the tab name
	 */
	private void createDynamicColumns(final String tabName) {

		SheetConfiguration sheetConfig = parent.getSheetConfigMap()
				.get(tabName);

		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();

		parent.getColumns().clear();

		for (int i = left; i <= right; i++) {
			parent.getColumns().add("column" + (i - left));
		}

	}

	/**
	 * Adds the repeat row.
	 *
	 * @param rowIndex
	 *            the row index
	 */
	public final void addRepeatRow(final int rowIndex) {

		try {
			SheetConfiguration sheetConfig = parent.getSheetConfigMap()
					.get(parent.getCurrent().getCurrentTabName());
			Sheet sheet = parent.getWb()
					.getSheet(sheetConfig.getSheetName());
			ConfigBuildRef configBuildRef = new ConfigBuildRef(
					parent.getWbWrapper(), sheet, parent.getExpEngine(),
					parent.getCellHelper(), sheetConfig.getCachedCells(),
					parent.getCellAttributesMap(),
					sheetConfig.getFinalCommentMap());
			// set add mode
			configBuildRef.setAddMode(true);
			configBuildRef.setCollectionObjNameMap(
					sheetConfig.getCollectionObjNameMap());
			configBuildRef
					.setCommandIndexMap(sheetConfig.getCommandIndexMap());
			configBuildRef.setShiftMap(sheetConfig.getShiftMap());
			configBuildRef.setWatchList(sheetConfig.getWatchList());
			int length = CommandUtility.addRow(configBuildRef, rowIndex,
					parent.getSerialDataContext().getDataContext());
			refreshBodyRowsInRange(configBuildRef.getInsertPosition(),
					length, sheet, sheetConfig);
			parent.getCellHelper().reCalc();
		} catch (AddRowException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Add Row Error", e.getMessage()));
			LOG.log(Level.SEVERE,
					"Add row error = " + e.getLocalizedMessage(), e);

		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"Add row error = " + ex.getLocalizedMessage(), ex);

		}

	}

	/**
	 * Refresh body rows in range.
	 *
	 * @param insertPosition
	 *            the insert position
	 * @param length
	 *            the length
	 * @param sheet
	 *            the sheet
	 * @param sheetConfig
	 *            the sheet config
	 */
	private void refreshBodyRowsInRange(final int insertPosition,
			final int length, final Sheet sheet,
			final SheetConfiguration sheetConfig) {
		Map<String, CellRangeAddress> cellRangeMap = ConfigurationUtility
				.indexMergedRegion(sheet);
		List<String> skippedRegionCells = ConfigurationUtility
				.skippedRegionCells(sheet);
		int top = sheetConfig.getBodyCellRange().getTopRow();
		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();
		for (int irow = insertPosition; irow < (insertPosition
				+ length); irow++) {
			parent.getBodyRows().add(irow - top,
					assembleFacesBodyRow(irow, sheet, left, right,
							sheetConfig, cellRangeMap, skippedRegionCells));
		}
		for (int irow = insertPosition + length - top; irow < parent
				.getBodyRows().size(); irow++) {
			FacesRow facesrow = parent.getBodyRows().get(irow);
			facesrow.setRowIndex(facesrow.getRowIndex() + length);
		}
	}

	/**
	 * Delete repeat row.
	 *
	 * @param rowIndex
	 *            the row index
	 */
	public final void deleteRepeatRow(final int rowIndex) {
		try {
			SheetConfiguration sheetConfig = parent.getSheetConfigMap()
					.get(parent.getCurrent().getCurrentTabName());
			Sheet sheet = parent.getWb()
					.getSheet(sheetConfig.getSheetName());
			ConfigBuildRef configBuildRef = new ConfigBuildRef(
					parent.getWbWrapper(), sheet, parent.getExpEngine(),
					parent.getCellHelper(), sheetConfig.getCachedCells(),
					parent.getCellAttributesMap(),
					sheetConfig.getFinalCommentMap());
			// set delete mode
			configBuildRef.setCollectionObjNameMap(
					sheetConfig.getCollectionObjNameMap());
			configBuildRef
					.setCommandIndexMap(sheetConfig.getCommandIndexMap());
			configBuildRef.setShiftMap(sheetConfig.getShiftMap());
			configBuildRef.setWatchList(sheetConfig.getWatchList());
			CommandUtility.deleteRow(configBuildRef, rowIndex,
					parent.getSerialDataContext().getDataContext(),
					sheetConfig, parent.getBodyRows());
			parent.getCellHelper().reCalc();
		} catch (DeleteRowException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Delete row error", e.getMessage()));
			LOG.log(Level.SEVERE,
					"Delete row error = " + e.getLocalizedMessage(), e);

		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"Delete row error = " + ex.getLocalizedMessage(), ex);

		}

	}

	/**
	 * Sets the unsaved status.
	 *
	 * @param requestContext
	 *            the request context
	 * @param statusFlag
	 *            the status flag
	 */
	public void setUnsavedStatus(final RequestContext requestContext,
			final Boolean statusFlag) {

		// in client js should have setUnsavedState method
		if (requestContext != null) {
			LOG.log(Level.FINE,
					"run setUnsavedState(" + statusFlag.toString() + ")");
			requestContext.execute(
					"setUnsavedState(" + statusFlag.toString() + ")");
		}

	}

	/**
	 * Checks if is unsaved status.
	 *
	 * @return the boolean
	 */
	public final Boolean isUnsavedStatus() {
		Map<String, Object> viewMap = FacesContext.getCurrentInstance()
				.getViewRoot().getViewMap();
		Boolean flag = (Boolean) viewMap.get("unSaved");
		if (flag == null) {
			return false;
		}
		return flag;
	}

}
