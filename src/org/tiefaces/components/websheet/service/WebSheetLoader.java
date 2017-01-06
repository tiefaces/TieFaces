/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.TieWebSheetView.tabModel;
import org.tiefaces.components.websheet.configuration.ConfigBuildRef;
import org.tiefaces.components.websheet.configuration.ConfigRangeAttrs;
import org.tiefaces.components.websheet.configuration.ConfigurationHandler;
import org.tiefaces.components.websheet.configuration.ConfigurationHelper;
import org.tiefaces.components.websheet.configuration.ExpressionEngine;
import org.tiefaces.components.websheet.configuration.RowsMapping;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.HeaderCell;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

/**
 * The Class WebSheetLoader.
 */
public class WebSheetLoader implements Serializable {

	/** The parent. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger log = Logger.getLogger(Thread
			.currentThread().getStackTrace()[0].getClassName());

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
		log.fine("TieWebSheetLoader Constructor");
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

		int totalWidth = CellUtility.calcTotalWidth(sheet1, left, right,
				TieWebSheetUtility.pixel2WidthUnits(parent
						.getLineNumberColumnWidth()
						+ parent.getAddRowColumnWidth()));
		log.fine("totalwidth = " + totalWidth);
		String formWidthStyle = sheetConfig.getFormWidth();
		if ((formWidthStyle == null) || (formWidthStyle.isEmpty())) {
			parent.setTableWidthStyle(TieWebSheetUtility
					.widthUnits2Pixel(totalWidth) + "px;");
		} else {
			parent.setTableWidthStyle(formWidthStyle);
		}

		parent.setLineNumberColumnWidthStyle(getWidthStyle(
				TieWebSheetUtility.pixel2WidthUnits(parent
						.getLineNumberColumnWidth()), totalWidth));
		parent.setAddRowColumnWidthStyle(getWidthStyle(TieWebSheetUtility
				.pixel2WidthUnits(parent.getAddRowColumnWidth()),
				totalWidth));

		log.fine("tableWidthStyle = " + parent.getTableWidthStyle()
				+ " lineNumberColumnWidthStyle= "
				+ parent.getLineNumberColumnWidthStyle()
				+ " addRowColumnWidthStyle= "
				+ parent.getAddRowColumnWidthStyle());

		parent.getHeaderRows().clear();

		if (top < 0) {
			// this is blank configuration. set column letter as header
			parent.getHeaderRows().add(
					loadHeaderRowWithoutConfigurationTab(sheet1, left,
							right, totalWidth, true));
			// set showlinenumber to true as default
			parent.setShowLineNumber(true);
		} else {
			parent.getHeaderRows().add(
					loadHeaderRowWithoutConfigurationTab(sheet1, left,
							right, totalWidth, false));
			for (int i = top; i <= bottom; i++) {
				parent.getHeaderRows().add(
						loadHeaderRowWithConfigurationTab(sheetConfig,
								sheet1, sheetName, i, top, left, right,
								totalWidth, cellRangeMap,
								skippedRegionCells));

			}
			// set showlinenumber to false as default
			parent.setShowLineNumber(false);

		}

	}

	/**
	 * Load header row without configuration tab.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param firstCol
	 *            the first col
	 * @param lastCol
	 *            the last col
	 * @param totalWidth
	 *            the total width
	 * @param rendered
	 *            the rendered
	 * @return the list
	 */
	private List<HeaderCell> loadHeaderRowWithoutConfigurationTab(
			final Sheet sheet1, final int firstCol, final int lastCol,
			final double totalWidth, final boolean rendered) {

		List<HeaderCell> headercells = new ArrayList<HeaderCell>();
		for (int i = firstCol; i <= lastCol; i++) {
			if (!sheet1.isColumnHidden(i)) {
				String style = getHeaderColumnStyle(parent.getWb(), null,
						sheet1.getColumnWidth(i), totalWidth, 12);
				headercells.add(new HeaderCell("1", "1", style, style,
						TieWebSheetUtility.GetExcelColumnName(i), rendered,
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
				headercells.add(new HeaderCell("1", "1", "", "", "", false,
						false));
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
	 * @param rowHeight
	 *            the row height
	 * @return the header column style
	 */
	private String getHeaderColumnStyle(final Workbook wb, final Cell cell,
			final double colWidth, final double totalWidth,
			final float rowHeight) {

		String columnstyle = "";
		if (cell != null) {
			columnstyle += CellUtility.getCellStyle(wb, cell, "")
					+ CellUtility.getCellFontStyle(wb, cell, "", rowHeight); // +
		}
		// "background-image: none ;color: #000000;";
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
		double percentage = FacesUtility.round(100 * colWidth / totalWidth,
				2);
		return "width:" + percentage + "%;";
	}

	/**
	 * Load header row with configuration tab.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param sheet1
	 *            the sheet 1
	 * @param sheetName
	 *            the sheet name
	 * @param currentRow
	 *            the current row
	 * @param top
	 *            the top
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param totalWidth
	 *            the total width
	 * @param cellRangeMap
	 *            the cell range map
	 * @param skippedRegionCells
	 *            the skipped region cells
	 * @return the list
	 */
	private List<HeaderCell> loadHeaderRowWithConfigurationTab(
			SheetConfiguration sheetConfig, Sheet sheet1, String sheetName,
			int currentRow, int top, int left, int right,
			double totalWidth, Map<String, CellRangeAddress> cellRangeMap,
			List<String> skippedRegionCells) {

		Row row = sheet1.getRow(currentRow);
		List<HeaderCell> headercells = new ArrayList<HeaderCell>();
		for (int cindex = left; cindex <= right; cindex++) {
			String cellindex = "$" + cindex + "$" + currentRow;
			if ((cindex >= left) && (cindex <= right)) {
				if (!skippedRegionCells.contains(cellindex)
						&& !sheet1.isColumnHidden(cindex)) {
					Cell cell = null;
					if (row != null)
						cell = row.getCell(cindex,
								MissingCellPolicy.CREATE_NULL_AS_BLANK);
					int originRowIndex = ConfigurationHelper
							.getOriginalRowNumInHiddenColumn(row);
					if (cell != null) {
						FacesCell fcell = new FacesCell();
						CellUtility.convertCell(sheetConfig, fcell, cell,
								(currentRow - top), 1, top, false,
								cellRangeMap, originRowIndex,
								parent.getCellAttributesMap(), null);
						parent.getPicHelper().setupFacesCellPictureCharts(
								sheet1,
								fcell,
								TieWebSheetUtility.getFullCellRefName(
										sheet1, cell));
						CellUtility.setupCellStyle(parent.getWb(), sheet1,
								fcell, cell, row.getHeightInPoints());
						fcell.setColumnStyle(fcell.getColumnStyle()
								+ getColumnWidthStyle(sheet1, cellRangeMap,
										cellindex, cindex, totalWidth));
						fcell.setColumnIndex(cindex);

						headercells.add(new HeaderCell(fcell.getRowspan()
								+ "", fcell.getColspan() + "", fcell
								.getStyle(), fcell.getColumnStyle(),
								CellUtility.getCellValueWithFormat(cell,
										parent.getFormulaEvaluator(),
										parent.getDataFormatter()), true,
								true));
					}
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
		double colWidth = 0;
		// check whether the cell has rowspan or colspan
		if (caddress != null) {
			colWidth = CellUtility.calcTotalWidth(sheet1,
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
		parent.setWb(null);
		parent.setFormulaEvaluator(null);
		parent.setDataFormatter(null);
		parent.setHeaderRows(null);
		parent.setBodyRows(null);
		parent.setSheetConfigMap(null);
		parent.setTabs(null);
		parent.setDataContext(null);
		parent.getChartsMap().clear();
		parent.getChartDataMap().clear();
		parent.getChartAnchorsMap().clear();
		parent.getChartPositionMap().clear();
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
	public int loadWorkbook(final InputStream fis,
			final Map<String, Object> dataContext) {

		try {
			Workbook wb = WorkbookFactory.create(fis);
			int ireturn = loadWorkbook(wb, dataContext);
			fis.close();
			return ireturn;
		} catch (Exception e) {
			e.printStackTrace();
			log.fine("Web Form loadWorkbook Error Exception = "
					+ e.getLocalizedMessage());
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
	public int loadWorkbook(final Workbook wb,
			final Map<String, Object> dataContext) {

		try {
			clearWorkbook();
			// only support xssf workbook now since 2016 July
			if (!(wb instanceof XSSFWorkbook)) {
				log.fine("Web Form loadWorkbook Error: Not supported format. Only support xlsx now.");
				return -1;
			}
			parent.setWb(wb);
			parent.setDataContext(dataContext);
			parent.setFormulaEvaluator(parent.getWb().getCreationHelper()
					.createFormulaEvaluator());
			parent.setDataFormatter(new DataFormatter());
			parent.setSheetConfigMap(new ConfigurationHandler(parent)
					.buildConfiguration());
			parent.reCalcMaxColCounts();
			parent.setPicturesMap(parent.getPicHelper().getPictruesMap(
					parent.getWb()));
			parent.getChartHelper().loadChartsMap();
			initSheet();
			initTabs();
			if (parent.getTabs().size() > 0) {
				loadWorkSheet(parent.getTabs().get(0).getTitle());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.fine("Web Form loadWorkbook Error Exception = "
					+ e.getLocalizedMessage());
			return -1;
		}
		return 1;

	}

	/**
	 * Inits the tabs.
	 */
	private void initTabs() {
		parent.setTabs(new ArrayList<tabModel>());
		if (parent.getSheetConfigMap() != null) {
			for (String key : parent.getSheetConfigMap().keySet()) {
				parent.getTabs().add(
						new tabModel("form_" + key, key, "form"));
			}
		}
	}

	/**
	 * Inits the sheet.
	 */
	private void initSheet() {
		loadData();
		/*
		 * for (SheetConfiguration sheetConfig : parent.getSheetConfigMap()
		 * .values()) { initPageData(sheetConfig, parent.getWb(),
		 * parent.getWbWrapper(), parent.getFormulaEvaluator()); }
		 * CellUtility.reCalc();
		 */
	}

	/**
	 * load data process. unfinished.
	 */
	private void loadData() {

		if (parent.getDataContext() == null) {
			// no data objects available.
			return;
		}

		for (SheetConfiguration sheetConfig : parent.getSheetConfigMap()
				.values()) {
			List<RowsMapping> currentRowsMappingList = null;
			ConfigBuildRef configBuildRef = new ConfigBuildRef(
					parent.getWbWrapper(), parent.getWb().getSheet(
							sheetConfig.getSheetName()),
					parent.getExpEngine(), parent.getCellHelper(),
					sheetConfig.getCachedOriginFormulas(),
					parent.getCellAttributesMap(),
					sheetConfig.getFinalCommentMap());
			int length = sheetConfig.getFormCommand().buildAt(null,
					configBuildRef,
					sheetConfig.getFormCommand().getTopRow(),
					parent.getDataContext(), currentRowsMappingList);
			sheetConfig.setShiftMap(configBuildRef.getShiftMap());
			sheetConfig.setCollectionObjNameMap(configBuildRef
					.getCollectionObjNameMap());
			sheetConfig.setCommandIndexMap(configBuildRef
					.getCommandIndexMap());
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
	public int findTabIndexWithName(final String tabname) {

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
	public void loadWorkSheet(final String tabName) {

		int tabIndex = findTabIndexWithName(tabName);
		if (parent.getWebFormTabView() != null) {
			parent.getWebFormTabView().setActiveIndex(tabIndex);
		}
		parent.setCurrentTabName(tabName);
		String sheetName = parent.getSheetConfigMap().get(tabName)
				.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);
		parent.getWb().setActiveSheet(parent.getWb().getSheetIndex(sheet1));

		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(
				tabName);

		parent.setMaxRowsPerPage(parent.getSheetConfigMap().get(tabName)
				.getMaxRowPerPage());
		parent.setBodyAllowAddRows(parent.getSheetConfigMap().get(tabName)
				.isBodyAllowAddRows());

		// populate repeat rows before setup cell range map

		Map<String, CellRangeAddress> cellRangeMap = CellUtility
				.indexMergedRegion(sheet1);
		List<String> skippedRegionCells = CellUtility
				.skippedRegionCells(sheet1);
		loadHeaderRows(sheetConfig, cellRangeMap, skippedRegionCells);
		loadBodyRows(sheetConfig, cellRangeMap, skippedRegionCells);
		parent.getValidationHandler().validateCurrentPage();
		createDynamicColumns(tabName);
		// reset datatable current page to 1
		setDataTablePage(0);
		saveObjs();
		if (RequestContext.getCurrentInstance() != null) {
			RequestContext.getCurrentInstance().update(
					parent.getClientId() + ":websheettab");
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
			log.fine("saveobjs in viewMap = " + viewMap);
		} catch (Exception ex) {
			log.fine("saveobjs in viewMap error = " + ex.getMessage());

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
	 * @param repeatZone
	 *            the repeat zone
	 * @param allowAdd
	 *            the allow add
	 */
	private void setupRowInfo(final FacesRow facesRow, final Sheet sheet1,
			final Row row, final int rowIndex, final boolean repeatZone,
			final boolean allowAdd) {

		facesRow.setRepeatZone(repeatZone);
		facesRow.setAllowAdd(allowAdd);
		if (row != null) {
			facesRow.setRendered(!row.getZeroHeight());
			facesRow.setRowheight(row.getHeight());
			int rowNum = ConfigurationHelper
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

		int initRows = CellUtility.getInitRowsFromConfig(sheetConfig);

		int top = sheetConfig.getBodyCellRange().getTopRow();
		int bottom = CellUtility.getBodyBottomFromConfig(sheetConfig,
				initRows);
		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();

		String sheetName = sheetConfig.getSheetName();
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		parent.setBodyRows(new ArrayList<FacesRow>());
		clearCache();

		for (int i = top; i <= bottom; i++) {

			parent.getBodyRows().add(
					assembleFacesBodyRow(i, sheet1, false, top, left,
							right, initRows, sheetConfig, cellRangeMap,
							skippedRegionCells));
		}
		sheetConfig.setBodyPopulated(true);
		parent.setCurrentTopRow(top);
		parent.setCurrentLeftColumn(left);
		log.fine("Web Form loading bodyRows = " + parent.getBodyRows());
	}

	/**
	 * Assemble faces body row.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param sheet1
	 *            the sheet 1
	 * @param repeatZone
	 *            the repeat zone
	 * @param top
	 *            the top
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param initRows
	 *            the init rows
	 * @param sheetConfig
	 *            the sheet config
	 * @param cellRangeMap
	 *            the cell range map
	 * @param skippedRegionCells
	 *            the skipped region cells
	 * @return the faces row
	 */
	private FacesRow assembleFacesBodyRow(int rowIndex, Sheet sheet1,
			boolean repeatZone, int top, int left, int right, int initRows,
			SheetConfiguration sheetConfig,
			Map<String, CellRangeAddress> cellRangeMap,
			List<String> skippedRegionCells) {

		FacesRow facesRow = new FacesRow(rowIndex);
		Row row = sheet1.getRow(rowIndex);
		setupRowInfo(facesRow, sheet1, row, rowIndex, repeatZone,
				ConfigurationHelper.isRowAllowAdd(row, sheetConfig));
		String saveAttrList = ConfigurationHelper
				.getSaveAttrListFromRow(row);
		List<FacesCell> bodycells = new ArrayList<FacesCell>();
		log.fine(" loder row number = " + rowIndex + " row = " + row);
		for (int cindex = left; cindex <= right; cindex++) {
			String cellindex = "$" + cindex + "$" + rowIndex;
			if (!skippedRegionCells.contains(cellindex)
					&& !sheet1.isColumnHidden(cindex)) {
				Cell cell = null;
				// if (i < (top + initRows)) {
				if (row != null) {
					cell = row.getCell(cindex,
							MissingCellPolicy.CREATE_NULL_AS_BLANK);
					// cell = row.getCell(cindex);
				}
				if (cell != null) {
					FacesCell fcell = new FacesCell();
					CellUtility.convertCell(sheetConfig, fcell, cell,
							(rowIndex - top), initRows, top, repeatZone,
							cellRangeMap, facesRow.getOriginRowIndex(),
							parent.getCellAttributesMap(), saveAttrList);
					parent.getPicHelper().setupFacesCellPictureCharts(
							sheet1,
							fcell,
							TieWebSheetUtility.getFullCellRefName(sheet1,
									cell));
					CellUtility.setupCellStyle(parent.getWb(), sheet1,
							fcell, cell, row.getHeightInPoints());
					fcell.setColumnIndex(cindex);
					bodycells.add(fcell);
					addCache(sheet1, cell);
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
	 * @param sheet1
	 *            the sheet 1
	 * @param cell
	 *            the cell
	 */
	private void addCache(final Sheet sheet1, final Cell cell) {
		parent.getCachedCells().put(cell, Cell.CELL_TYPE_FORMULA);
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
	 * @param sheet1
	 *            the sheet 1
	 * @param cell
	 *            the cell
	 * @param fcell
	 *            the fcell
	 */
	public void refreshCachedCell(final String tblName, final int i,
			final int index, final Sheet sheet1, final Cell cell,
			final FacesCell fcell) {

		if ((cell != null)
				&& (cell.getCellType() == Cell.CELL_TYPE_FORMULA)) {
			String newValue = CellUtility
					.getCellValueWithFormat(cell,
							parent.getFormulaEvaluator(),
							parent.getDataFormatter());
			if (parent.getCachedCells().isValueChanged(sheet1, cell,
					newValue)) {

				if (fcell.isHasSaveAttr()) {
					parent.getCellHelper()
							.saveDataInContext(cell, newValue);
				}

				log.fine("refresh obj name =" + tblName + ":" + i
						+ ":cocalc" + index + " formula = "
						+ cell.getCellFormula() + "newValue = " + newValue);

				RequestContext.getCurrentInstance().update(
						tblName + ":" + i + ":cocalc" + index);
				parent.getCachedCells().put(cell, Cell.CELL_TYPE_FORMULA);
			}
		}
	}

	/**
	 * Creates the dynamic columns.
	 *
	 * @param tabName
	 *            the tab name
	 */
	private void createDynamicColumns(final String tabName) {

		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(
				tabName);

		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();

		parent.getColumns().clear();

		for (int i = left; i <= right; i++) {
			parent.getColumns().add("column" + (i - left));
		}

	}

	/**
	 * Load all fields.
	 */
	public void loadAllFields() {
		if (parent.getSheetConfigMap() != null) {
			for (SheetConfiguration sheetConfig : parent
					.getSheetConfigMap().values()) {
				Sheet sheet = parent.getWb().getSheet(
						sheetConfig.getSheetName());
				for (Map.Entry<String, List<CellFormAttributes>> entry : sheetConfig
						.getCellFormAttributes().entrySet()) {
					String targetCell = entry.getKey();
					String cellAddr = CellUtility
							.findCellAddressAfterBodyPopulated(targetCell,
									sheetConfig);
					Cell cell = null;
					if (cellAddr != null) {
						cell = TieWebSheetUtility.getCellByReference(
								cellAddr, sheet);
					}
					if (cell != null) {
						List<CellFormAttributes> attributeList = entry
								.getValue();
						for (CellFormAttributes cellAttribute : attributeList) {
							if (cellAttribute.getType().equalsIgnoreCase(
									"load")) {
								String attrValue = cellAttribute.getValue();
								attrValue = FacesUtility
										.evaluateExpression(attrValue,
												String.class);
								CellUtility.setCellValue(cell, FacesUtility
										.evaluateExpression(
												cellAttribute.getValue(),
												String.class));
							}
						}
					}

				}
			}
		}
	}

	/**
	 * Adds the repeat row.
	 *
	 * @param rowIndex
	 *            the row index
	 */
	public void addRepeatRow(final int rowIndex) {

		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(
				parent.getCurrentTabName());
		Sheet sheet = parent.getWb().getSheet(sheetConfig.getSheetName());
		ConfigBuildRef configBuildRef = new ConfigBuildRef(
				parent.getWbWrapper(), sheet, parent.getExpEngine(),
				parent.getCellHelper(),
				sheetConfig.getCachedOriginFormulas(),
				parent.getCellAttributesMap(),
				sheetConfig.getFinalCommentMap());
		// set add mode
		configBuildRef.setAddMode(true);
		configBuildRef.setCollectionObjNameMap(sheetConfig
				.getCollectionObjNameMap());
		configBuildRef.setCommandIndexMap(sheetConfig.getCommandIndexMap());
		configBuildRef.setShiftMap(sheetConfig.getShiftMap());
		configBuildRef.setWatchList(sheetConfig.getWatchList());
		int length = ConfigurationHelper.addRow(configBuildRef, rowIndex,
				sheetConfig, parent.getDataContext());
		if (length <= 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"System Error", "Cannot add row"));
		}

		refreshBodyRowsInRange(configBuildRef.getInsertPosition(), length,
				sheet, sheetConfig);
		parent.getCellHelper().reCalc();

		/*
		 * CellUtility.copyRow(parent.getWb(), parent.getWbWrapper(), sheet1,
		 * sheet1, rowIndex, rowIndex + 1); int initRows =
		 * CellUtility.getInitRowsFromConfig( sheetConfig) + 1;
		 * sheetConfig.setBodyInitialRows(initRows); int top =
		 * sheetConfig.getBodyCellRange().getTopRow(); int left =
		 * sheetConfig.getBodyCellRange().getLeftCol(); int right =
		 * sheetConfig.getBodyCellRange().getRightCol(); Map<String,
		 * CellRangeAddress> cellRangeMap = parent
		 * .getCellHelper().indexMergedRegion(sheet1); List<String>
		 * skippedRegionCells = CellUtility .skippedRegionCells(sheet1);
		 * 
		 * parent.getBodyRows().add( rowIndex + 1 - top,
		 * assembleFacesBodyRow(rowIndex + 1, sheet1, true, top, left, right,
		 * initRows, sheetConfig, cellRangeMap, skippedRegionCells));
		 * 
		 * for (int irow = rowIndex + 2 - top; irow < parent.getBodyRows()
		 * .size(); irow++) { FacesRow facesrow =
		 * parent.getBodyRows().get(irow);
		 * facesrow.setRowIndex(facesrow.getRowIndex() + 1); }
		 */
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
		Map<String, CellRangeAddress> cellRangeMap = CellUtility
				.indexMergedRegion(sheet);
		List<String> skippedRegionCells = CellUtility
				.skippedRegionCells(sheet);
		int top = sheetConfig.getBodyCellRange().getTopRow();
		int left = sheetConfig.getBodyCellRange().getLeftCol();
		int right = sheetConfig.getBodyCellRange().getRightCol();
		int initRows = 0;
		for (int irow = insertPosition; irow < insertPosition + length; irow++) {
			parent.getBodyRows().add(
					irow - top,
					assembleFacesBodyRow(irow, sheet, true, top, left,
							right, initRows, sheetConfig, cellRangeMap,
							skippedRegionCells));
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
	public void deleteRepeatRow(final int rowIndex) {
		String tabName = parent.getCurrentTabName();
		String sheetName = parent.getSheetConfigMap().get(tabName)
				.getSheetName();
		SheetConfiguration sheetConfig = parent.getSheetConfigMap().get(
				tabName);
		Sheet sheet1 = parent.getWb().getSheet(sheetName);

		int initRows = CellUtility.getInitRowsFromConfig(sheetConfig) - 1;
		int top = sheetConfig.getBodyCellRange().getTopRow();
		if (initRows < 1) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"System Error", "Cannot delete the last row"));
			return;
		}
		CellUtility.removeRow(sheet1, rowIndex);
		parent.getBodyRows().remove(rowIndex - top);
		for (int irow = rowIndex - top; irow < parent.getBodyRows().size(); irow++) {
			FacesRow facesrow = parent.getBodyRows().get(irow);
			facesrow.setRowIndex(facesrow.getRowIndex() - 1);
		}

		sheetConfig.setBodyInitialRows(initRows);
		parent.getCellHelper().reCalc();

	}

}
