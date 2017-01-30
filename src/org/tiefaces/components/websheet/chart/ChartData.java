/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.ThemesTable;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.tiefaces.components.websheet.chart.objects.ChartObject;
import org.tiefaces.components.websheet.dataobjects.ParsedCell;
import org.tiefaces.components.websheet.dataobjects.XColor;
import org.tiefaces.components.websheet.utility.ColorUtility;
import org.tiefaces.components.websheet.utility.WebSheetUtility;

/**
 * Chart Data class.
 * 
 * @author Jason Jiang.
 *
 */
public class ChartData {

	/** id. */
	private String id;

	/** title. */
	private String title;

	/** bgcolor. */
	private XColor bgColor;

	/** type. */
	private ChartType type;

	/** catAx. */
	private ChartAxis catAx;

	/** valAx. */
	private ChartAxis valAx;

	/** categoryList. */
	private List<ParsedCell> categoryList;

	/** seriesList. */
	private List<ChartSeries> seriesList;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(ChartData.class.getName());

	/**
	 * get category list.
	 * 
	 * @return category list.
	 */
	public final List<ParsedCell> getCategoryList() {
		return categoryList;
	}

	/**
	 * set categorylist.
	 * 
	 * @param pcategoryList
	 *            category list.
	 */
	public final void setCategoryList(
			final List<ParsedCell> pcategoryList) {
		this.categoryList = pcategoryList;
	}

	/**
	 * get serieslist.
	 * 
	 * @return serieslist.
	 */
	public final List<ChartSeries> getSeriesList() {
		return seriesList;
	}

	/**
	 * set series list.
	 * 
	 * @param pseriesList
	 *            series list.
	 */
	public final void setSeriesList(final List<ChartSeries> pseriesList) {
		this.seriesList = pseriesList;
	}

	/**
	 * get bg color.
	 * 
	 * @return bg color.
	 */
	public final XColor getBgColor() {
		return bgColor;
	}

	/**
	 * set bg color.
	 * 
	 * @param pbgColor
	 *            bgcolor.
	 */
	public final void setBgColor(final XColor pbgColor) {
		this.bgColor = pbgColor;
	}

	/**
	 * build categotry list.
	 * 
	 * @param ctAxDs
	 *            ctaxdatasource.
	 */
	public final void buildCategoryList(final CTAxDataSource ctAxDs) {

		List<ParsedCell> cells = new ArrayList<>();
		try {
			String fullRangeName = ctAxDs.getStrRef().getF();
			String sheetName = WebSheetUtility
					.getSheetNameFromFullCellRefName(fullRangeName);
			CellRangeAddress region = CellRangeAddress.valueOf(
					WebSheetUtility.removeSheetNameFromFullCellRefName(
							fullRangeName));
			for (int row = region.getFirstRow(); row <= region
					.getLastRow(); row++) {
				for (int col = region.getFirstColumn(); col <= region
						.getLastColumn(); col++) {
					cells.add(new ParsedCell(sheetName, row, col));
					LOG.fine(" add category sheetName = " + sheetName
							+ " row = " + row + " col = " + col);
				}
			}

		} catch (Exception ex) {
			LOG.log(Level.FINE, "failed in buildCategoryList", ex);
		}
		this.setCategoryList(cells);
	}

	/**
	 * build series list.
	 * 
	 * @param bsers
	 *            bsers.
	 * @param themeTable
	 *            themetable.
	 * @param ctObj
	 *            ctobj.
	 */
	@SuppressWarnings("rawtypes")
	public final void buildSeriesList(final List bsers,
			final ThemesTable themeTable, final ChartObject ctObj) {

		List<ChartSeries> lseriesList = new ArrayList<>();
		try {
			for (int index = 0; index < bsers.size(); index++) {
				Object ctObjSer = bsers.get(index);
				ChartSeries ctSer = new ChartSeries();
				ctSer.setSeriesLabel(new ParsedCell(
						ctObj.getSeriesLabelFromCTSer(ctObjSer)));
				ctSer.setSeriesColor(ColorUtility.geColorFromSpPr(index,
						ctObj.getShapePropertiesFromCTSer(ctObjSer),
						themeTable, ctObj.isLineColor()));
				List<ParsedCell> cells = new ArrayList<>();
				String fullRangeName = (ctObj
						.getCTNumDataSourceFromCTSer(ctObjSer)).getNumRef()
								.getF();
				String sheetName = WebSheetUtility
						.getSheetNameFromFullCellRefName(fullRangeName);
				CellRangeAddress region = CellRangeAddress
						.valueOf(WebSheetUtility
								.removeSheetNameFromFullCellRefName(
										fullRangeName));
				for (int row = region.getFirstRow(); row <= region
						.getLastRow(); row++) {
					for (int col = region.getFirstColumn(); col <= region
							.getLastColumn(); col++) {
						cells.add(new ParsedCell(sheetName, row, col));
						LOG.fine(" add serial value sheetName = "
								+ sheetName + " row = " + row + " col = "
								+ col);
					}
				}
				ctSer.setValueList(cells);
				ctSer.setValueColorList(getColorListFromDPTWithValueList(
						ctObj.getDPtListFromCTSer(ctObjSer), cells,
						themeTable, ctObj));
				lseriesList.add(ctSer);
			}
		} catch (Exception ex) {
			LOG.log(Level.FINE, "failed in buildSerialList", ex);
		}
		this.setSeriesList(lseriesList);
	}

	/**
	 * get color list from dpt.
	 * 
	 * @param dptList
	 *            dpt.
	 * @param cells
	 *            cells.
	 * @param themeTable
	 *            themetable.
	 * @param ctObj
	 *            ctobj.
	 * @return list of xcolor.
	 */
	private List<XColor> getColorListFromDPTWithValueList(
			final List<CTDPt> dptList, final List<ParsedCell> cells,
			final ThemesTable themeTable, final ChartObject ctObj) {
		List<XColor> colors = new ArrayList<>();
		if ((dptList != null) && (cells != null)) {

			for (int index = 0; index < cells.size(); index++) {
				CTDPt dpt = getDPtFromListWithIndex(dptList, index);
				CTShapeProperties ctSpPr = null;
				if (dpt != null) {
					ctSpPr = dpt.getSpPr();
				}
				colors.add(ColorUtility.geColorFromSpPr(index, ctSpPr,
						themeTable, ctObj.isLineColor()));
			}
		}
		return colors;

	}

	/**
	 * get dpt from list.
	 * 
	 * @param dptList
	 *            dptlist.
	 * @param index
	 *            index.
	 * @return ctdpt.
	 */
	private CTDPt getDPtFromListWithIndex(final List<CTDPt> dptList,
			final int index) {

		if (dptList != null) {
			for (CTDPt dpt : dptList) {
				if (dpt.getIdx().getVal() == index) {
					return dpt;
				}
			}
		}
		return null;
	}

	/**
	 * get id.
	 * 
	 * @return id.
	 */
	public final String getId() {
		return id;
	}

	/**
	 * set id.
	 * 
	 * @param pid
	 *            id.
	 */
	public final void setId(final String pid) {
		id = pid;
	}

	/**
	 * get title.
	 * 
	 * @return title.
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * set title.
	 * 
	 * @param ptitle
	 *            title.
	 */
	public final void setTitle(final String ptitle) {
		title = ptitle;
	}

	/**
	 * get type.
	 * 
	 * @return type.
	 */
	public final ChartType getType() {
		return type;
	}

	/**
	 * set chart type.
	 * 
	 * @param ptype
	 *            type.
	 */
	public final void setType(final ChartType ptype) {
		type = ptype;
	}

	/**
	 * get cat ax.
	 * 
	 * @return chartaxis.
	 */
	public final ChartAxis getCatAx() {
		return catAx;
	}

	/**
	 * set catax.
	 * 
	 * @param pcatAx
	 *            catax.
	 */
	public final void setCatAx(final ChartAxis pcatAx) {
		this.catAx = pcatAx;
	}

	/**
	 * get valax.
	 * 
	 * @return chartaxis.
	 */
	public final ChartAxis getValAx() {
		return valAx;
	}

	/**
	 * set valax.
	 * 
	 * @param pvalAx
	 *            valax.
	 */
	public final void setValAx(final ChartAxis pvalAx) {
		this.valAx = pvalAx;
	}

}
