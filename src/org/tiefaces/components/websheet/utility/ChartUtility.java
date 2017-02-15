/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.utility;

import java.awt.BasicStroke;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import org.tiefaces.common.AppUtils;
import org.tiefaces.components.websheet.chart.ChartAxis;
import org.tiefaces.components.websheet.chart.ChartData;
import org.tiefaces.components.websheet.chart.ChartType;
import org.tiefaces.components.websheet.chart.ChartsData;
import org.tiefaces.components.websheet.chart.objects.ChartObject;
import org.tiefaces.exception.IllegalChartException;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility Class for Chart.
 *
 * @author Jason Jiang
 */
public final class ChartUtility {

	/** Line style: line. */
	public static final String STYLE_LINE = "line";
	/** Line style: dashed. */
	public static final String STYLE_DASH = "dash";
	/** Line style: dotted. */
	public static final String STYLE_DOT = "dot";
	/** STROKE_DEFAULT_LINE_WIDTH. */
	public static final float STROKE_DEFAULT_LINE_WIDTH = 0.2f;
	/** STROKE_DEFAULT_DASH_WIDTH. */
	public static final float STROKE_DEFAULT_DASH_WIDTH = 5.0f;
	/** STROKE_MITER_LIMIT_STYLE_DOT. */
	public static final float STROKE_MITER_LIMIT_STYLE_DOT = 2.0f;
	/** STROKE_MITER_LIMIT_STYLE_DASH. */
	public static final float STROKE_MITER_LIMIT_STYLE_DASH = 10.0f;
	/** STROKE_DEFAULT_DASHPHASE. */
	public static final float STROKE_DEFAULT_DASHPHASE = 0.0f;

	/**
	 * hide constructor.
	 */
	private ChartUtility() {
		super();
	}

	/**
	 * return chart type from CTChart object.
	 * 
	 * @param ctChart
	 *            object.
	 * @return ChartType.
	 */
	public static ChartType getChartType(final CTChart ctChart) {
		CTPlotArea plotArea = ctChart.getPlotArea();

		for (ChartType chartType : ChartType.values()) {
			if (chartType.isThisType(plotArea)) {
				return chartType;
			}
		}
		return null;
	}

	/**
	 * Convert style string to stroke object.
	 * 
	 * @param style
	 *            One of STYLE_xxx.
	 * @return Stroke for <i>style</i> or null if style not supported.
	 */
	public static BasicStroke toStroke(final String style) {
		BasicStroke result = null;

		if (style != null) {
			float lineWidth = STROKE_DEFAULT_LINE_WIDTH;
			float[] dash = { STROKE_DEFAULT_DASH_WIDTH };
			float[] dot = { lineWidth };

			if (style.equalsIgnoreCase(STYLE_LINE)) {
				result = new BasicStroke(lineWidth);
			} else if (style.equalsIgnoreCase(STYLE_DASH)) {
				result = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER,
						STROKE_MITER_LIMIT_STYLE_DASH, dash,
						STROKE_DEFAULT_DASHPHASE);
			} else if (style.equalsIgnoreCase(STYLE_DOT)) {
				result = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER,
						STROKE_MITER_LIMIT_STYLE_DOT, dot,
						STROKE_DEFAULT_DASHPHASE);
			}
		}

		return result;
	}

	/**
	 * init chart data.
	 * @param chartId chartId.
	 * @param chart chart.
	 * @param wb wb.
	 * @return chartdata.
	 */
	public static ChartData initChartDataFromXSSFChart(final String chartId,
			final XSSFChart chart, final XSSFWorkbook wb) {

		ThemesTable themeTable = wb.getStylesSource().getTheme();

		ChartData chartData = new ChartData();
		XSSFRichTextString chartTitle = chart.getTitle();
		if (chartTitle != null) {
			chartData.setTitle(chartTitle.toString());
		}	
		CTChart ctChart = chart.getCTChart();
		ChartType chartType = ChartUtility.getChartType(ctChart);
		if (chartType == null) {
			throw new IllegalChartException("Unknown chart type");
		}
		
		chartData.setBgColor(
				ColorUtility.getBgColor(ctChart.getPlotArea(), themeTable));

		chartData.setId(chartId);
		chartData.setType(chartType);

		List<CTCatAx> ctCatAxList = ctChart.getPlotArea().getCatAxList();
		if ((ctCatAxList != null) && (!ctCatAxList.isEmpty())) {
			chartData.setCatAx(new ChartAxis(ctCatAxList.get(0)));
		}
		List<CTValAx> ctValAxList = ctChart.getPlotArea().getValAxList();
		if ((ctValAxList != null) && (!ctValAxList.isEmpty())) {
			chartData.setValAx(new ChartAxis(ctValAxList.get(0)));
		}

		ChartObject ctObj = chartType.createChartObject();

		if (ctObj == null) {
			throw new IllegalChartException("Cannot create chart object.");
		}
			
		setUpChartData(chartData, ctChart, themeTable, ctObj);

		return chartData;
	}

	/**
	 * build chartData for line chart. chartData include categoryList and
	 * seriesList which used for generate jfreechart.
	 * 
	 * @param chartData
	 *            chart data.
	 * @param ctChart
	 *            ct chart.
	 * @param themeTable
	 *            themeTable used for get color with theme name.
	 * @param ctObj
	 *            ct object.
	 */

	public static void setUpChartData(final ChartData chartData,
			final CTChart ctChart, final ThemesTable themeTable,
			final ChartObject ctObj) {

		Object chartObj = null;
		@SuppressWarnings("rawtypes")
		List plotCharts = ctObj.getChartListFromCtChart(ctChart);

		// chart object
		if (plotCharts != null && (!plotCharts.isEmpty())) {
			chartObj = plotCharts.get(0);
		}
		if (chartObj != null) {
			@SuppressWarnings("rawtypes")
			List bsers = ctObj.getSerListFromCtObjChart(chartObj);
			if (!AppUtils.emptyList(bsers)) {
				chartData.buildCategoryList(
						ctObj.getCtAxDataSourceFromSerList(bsers));
				chartData.buildSeriesList(bsers, themeTable, ctObj);
			}
		}
	}

	/**
	 * retrieve anchor information from draw.xml for all the charts in the
	 * workbook. then save them to anchors map.
	 * 
	 * @param wb
	 *            workbook.
	 */
	public static void initXSSFAnchorsMap(final XSSFWorkbook wb,
			final ChartsData charsData) {

		Map<String, ClientAnchor> anchortMap = charsData
				.getChartAnchorsMap();
		Map<String, String> positionMap = charsData.getChartPositionMap();
		anchortMap.clear();
		positionMap.clear();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			initXSSFAnchorsMapForSheet(anchortMap, positionMap,
					wb.getSheetAt(i));
		}
	}

	/**
	 * Inits the XSSF anchors map for sheet.
	 *
	 * @param anchortMap
	 *            the anchort map
	 * @param positionMap
	 *            the position map
	 * @param sheet
	 *            the sheet
	 */
	private static void initXSSFAnchorsMapForSheet(
			final Map<String, ClientAnchor> anchortMap,
			final Map<String, String> positionMap, final XSSFSheet sheet) {
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		CTDrawing ctDrawing = drawing.getCTDrawing();
		if (ctDrawing.sizeOfTwoCellAnchorArray() <= 0) {
			return;
		}
		List<CTTwoCellAnchor> alist = ctDrawing.getTwoCellAnchorList();
		for (int j = 0; j < alist.size(); j++) {
			CTTwoCellAnchor ctanchor = alist.get(j);
			String chartId = sheet.getSheetName() + "!"
					+ getAnchorAssociateChartId(ctanchor.getGraphicFrame()
							.getGraphic().getGraphicData().getDomNode());
			if (chartId != null) {
				int dx1 = (int) ctanchor.getFrom().getColOff();
				int dy1 = (int) ctanchor.getFrom().getRowOff();
				int dx2 = (int) ctanchor.getTo().getColOff();
				int dy2 = (int) ctanchor.getTo().getRowOff();
				int col1 = ctanchor.getFrom().getCol();
				int row1 = ctanchor.getFrom().getRow();
				int col2 = ctanchor.getTo().getCol();
				int row2 = ctanchor.getTo().getRow();
				anchortMap.put(chartId, new XSSFClientAnchor(dx1, dy1, dx2,
						dy2, col1, row1, col2, row2));
				positionMap.put(WebSheetUtility.getFullCellRefName(
						sheet.getSheetName(), row1, col1), chartId);
			}
		}
	}

	/**
	 * Navigate through xml node to get the chartId. This is a workaround as
	 * there's no direct method in the api.
	 * 
	 * @param parentNode
	 *            root node to search rid.
	 * @return rid in the giving node tree.
	 */
	private static String getAnchorAssociateChartId(final Node parentNode) {
		NodeList childNodes = parentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if ((childNode != null)
					&& ("c:chart".equalsIgnoreCase(childNode.getNodeName()))
					&& (childNode.hasAttributes())) {
				String rId = getChartIdFromChildNodeAttributes(
						childNode.getAttributes());
				if (rId != null) {
					return rId;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the chart id from child node attributes.
	 *
	 * @param attrs
	 *            the attrs
	 * @return the chart id from child node attributes
	 */
	private static String getChartIdFromChildNodeAttributes(
			final NamedNodeMap attrs) {
		for (int j = 0; j < attrs.getLength(); j++) {
			Attr attribute = (Attr) attrs.item(j);
			if ("r:id".equalsIgnoreCase(attribute.getName())) {
				return attribute.getValue();
			}
		}
		return null;
	}

}
