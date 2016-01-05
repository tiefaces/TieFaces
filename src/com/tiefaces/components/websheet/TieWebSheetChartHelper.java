/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.*;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import com.tiefaces.common.AppUtils;
import com.tiefaces.common.FacesUtility;
import com.tiefaces.components.websheet.dataobjects.AnchorSize;
import com.tiefaces.components.websheet.dataobjects.ChartAxis;
import com.tiefaces.components.websheet.dataobjects.ChartData;
import com.tiefaces.components.websheet.dataobjects.ChartSeries;
import com.tiefaces.components.websheet.dataobjects.ChartType;
import com.tiefaces.components.websheet.dataobjects.ParsedCell;
import com.tiefaces.components.websheet.dataobjects.XColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;

public class TieWebSheetChartHelper {

	private TieWebSheetBean parent = null;

	private static boolean debug = true;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("TieWebSheetChartHelper: " + msg);
		}
	}

	public TieWebSheetChartHelper(TieWebSheetBean parent) {
		this.parent = parent;
		debug("TieWebSheetBean Constructor");
	}

	public void loadChartsMap() {

		this.initChartsMap(parent.getWb());
	}

	private void initAnchorsMap(Workbook wb) {
		try {
			if (wb instanceof XSSFWorkbook) {
				initXSSFAnchorsMap((XSSFWorkbook) wb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			debug("Web Form getAnchorsMap Error Exception = "
					+ e.getLocalizedMessage());
		}
	}

	private void initChartsMap(Workbook wb) {
		try {
			if (wb instanceof XSSFWorkbook) {
				initXSSFChartsMap((XSSFWorkbook) wb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			debug("Web Form getChartsMap Error Exception = "
					+ e.getLocalizedMessage());
		}
	}

	public void setUpLineChartData(ChartData chartData, CTChart ctChart,
			ThemesTable themeTable) {
		// System.out.println(" setup line chart data ctChart  = "+ctChart);

		// below code demo how to read theme
		// ThemeDocument theme;
		// try {
		// theme = ThemeDocument.Factory.parse(
		// themeTable.getPackagePart().getInputStream());
		// CTColorScheme colorScheme =
		// theme.getTheme().getThemeElements().getClrScheme();
		// System.out.println(" colorScheme = "+colorScheme);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		CTLineChart ctLineChart = null;
		CTPlotArea plotArea = ctChart.getPlotArea();
		// Line
		List<CTLineChart> plotCharts = plotArea.getLineChartList();
		if (plotCharts != null && plotCharts.size() > 0) {
			ctLineChart = plotCharts.get(0);
		}
		if (ctLineChart != null) {
			List<CTLineSer> bsers = ctLineChart.getSerList();
			if (!AppUtils.emptyList(bsers)) {
				chartData.buildCategoryList(bsers.get(0));
				chartData.buildSeriesList(bsers, themeTable);
			}
		}
	}

	private void initXSSFChartsMap(XSSFWorkbook wb) {

		initAnchorsMap(parent.getWb());
		Map<String, ClientAnchor> anchorMap = parent.getChartAnchorsMap();

		Map<String, BufferedImage> chartMap = parent.getChartsMap();
		Map<String, ChartData> chartDataMap = parent.getChartDataMap();
		chartMap.clear();
		chartDataMap.clear();

		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();
			List<XSSFChart> charts = drawing.getCharts();
			if ((charts != null) && (charts.size() > 0)) {
				for (XSSFChart chart : charts) {
					String chartId = sheet.getSheetName() + "!"
							+ chart.getPackageRelationship().getId();
					ChartLegend legend = chart.getOrCreateLegend();
					ClientAnchor anchor = null;
					if (chartId != null) {
						anchor = anchorMap.get(chartId);
						if (anchor != null) {
							ChartData chartData = initChartDataFromXSSFChart(
									chartId, chart);
							chartDataMap.put(chartId, chartData);
							JFreeChart jchart = createLineChart(chartData);
							AnchorSize anchorSize = parent.getPicHelper()
									.getAnchorSize(sheet, anchor);
							BufferedImage img = jchart.createBufferedImage(
									anchorSize.getWidth(),
									anchorSize.getHeight());
							chartMap.put(chartId, img);
						}
					}
				}
			}
		}

	}

	private XSSFClientAnchor getClientAnchorFromCTDrawing(CTDrawing ctDrawing) {

		if ((ctDrawing != null) && (ctDrawing.sizeOfTwoCellAnchorArray() > 0)) {
			List<CTTwoCellAnchor> alist = ctDrawing.getTwoCellAnchorList();
			for (int j = 0; j < alist.size(); j++) {
				CTTwoCellAnchor ctanchor = alist.get(j);
				String chartId = getAnchorAssociateChartId(ctanchor
						.getGraphicFrame().getGraphic().getGraphicData()
						.getDomNode());
				if (chartId != null) {
					int dx1 = (int) ctanchor.getFrom().getColOff();
					int dy1 = (int) ctanchor.getFrom().getRowOff();
					int dx2 = (int) ctanchor.getTo().getColOff();
					int dy2 = (int) ctanchor.getTo().getRowOff();
					int col1 = ctanchor.getFrom().getCol();
					int row1 = ctanchor.getFrom().getRow();
					int col2 = ctanchor.getTo().getCol();
					int row2 = ctanchor.getTo().getRow();
					return new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1,
							col2, row2);
				}
			}
		}
		return null;
	}

	public String getParsedCellValue(ParsedCell pCell) {

		String result = "";
		try {
			Cell poiCell = parent.getWb().getSheet(pCell.getSheetName())
					.getRow(pCell.getRow()).getCell(pCell.getCol());
			result = parent.getCellHelper().getCellValueWithoutFormat(poiCell);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;

	}

	private DefaultCategoryDataset createDataset(ChartData chartData) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<ParsedCell> categoryList = chartData.getCategoryList();
		for (ChartSeries chartSeries : chartData.getSeriesList()) {
			String seriesLabel = getParsedCellValue(chartSeries
					.getSeriesLabel());
			List<ParsedCell> valueList = chartSeries.getValueList();
			for (int i = 0; i < categoryList.size(); i++) {
				try {
					String sCategory = getParsedCellValue(categoryList.get(i));
					String sValue = getParsedCellValue(valueList.get(i));
					dataset.addValue(Double.parseDouble(sValue), seriesLabel,
							sCategory);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return dataset;

	}

	private JFreeChart createLineChart(ChartData chartData) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createLineChart(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				PlotOrientation.VERTICAL, true, // include legend
				false, // tooltips
				false // urls
				);

		setupStyle(chart, chartData);

		return chart;

	}

	private void setupStyle(JFreeChart chart, ChartData chartData) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		List<ChartSeries> seriesList = chartData.getSeriesList();
		BasicStroke bLine = new BasicStroke(2.0f);
		for (int i = 0; i < seriesList.size(); i++) {
			Color cColor = TieWebSheetUtility.xssfClrToClr(seriesList.get(i)
					.getSeriesColor().getXssfColor());
			plot.getRenderer().setSeriesPaint(i, cColor);
			plot.getRenderer().setSeriesStroke(i, bLine);
		}
		plot.setBackgroundPaint(TieWebSheetUtility.xssfClrToClr(chartData
				.getBgColor().getXssfColor()));

		// below are modifications for default setting in excel chart
		// to-do: need read setting from xml in future
		plot.setOutlineVisible(false);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setRangeGridlineStroke(new BasicStroke(0.1f));
		chart.setBackgroundPaint(Color.WHITE);
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setFrame(BlockBorder.NONE);
		
	}

	private ChartData initChartDataFromXSSFChart(String chartId, XSSFChart chart) {

		XSSFWorkbook wb = (XSSFWorkbook) parent.getWb();
		ThemesTable themeTable = wb.getStylesSource().getTheme();

		ChartData chartData = new ChartData();
		XSSFRichTextString chartTitle = chart.getTitle();
		CTChart ctChart = chart.getCTChart();
		ChartType chartType = getChartType(ctChart);
		chartData.setBgColor(getBgColor(ctChart.getPlotArea(), themeTable));
		debug("initChartDataFromXSSFChart chart id = " + chartId + " title = "
				+ chartTitle + " chart type = " + chartType);

		chartData.setId(chartId);
		if (chartTitle != null) {
			chartData.setTitle(chartTitle.toString());
		}
		chartData.setType(chartType);

		List<CTCatAx> ctCatAxList = ctChart.getPlotArea().getCatAxList();
		if ((ctCatAxList != null) && (ctCatAxList.size() > 0)) {
			chartData.setCatAx(new ChartAxis(ctCatAxList.get(0)));
		}
		List<CTValAx> ctValAxList = ctChart.getPlotArea().getValAxList();
		if ((ctValAxList != null) && (ctValAxList.size() > 0)) {
			chartData.setValAx(new ChartAxis(ctValAxList.get(0)));
		}
		switch (chartType) {
		case Area3D:
			break;
		case Line:
			setUpLineChartData(chartData, ctChart, themeTable);
			break;
		}
		// XSSFChartLegend legend = chart.getOrCreateLegend();
		// System.out.println("******* legend = "+ legend);
		return chartData;
	}

	private void initXSSFAnchorsMap(XSSFWorkbook wb) {

		Map<String, ClientAnchor> anchortMap = parent.getChartAnchorsMap();
		Map<String, String> positionMap = parent.getChartPositionMap();
		anchortMap.clear();
		positionMap.clear();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();
			CTDrawing ctDrawing = drawing.getCTDrawing();
			if (ctDrawing.sizeOfTwoCellAnchorArray() > 0) {
				List<CTTwoCellAnchor> alist = ctDrawing.getTwoCellAnchorList();
				for (int j = 0; j < alist.size(); j++) {
					CTTwoCellAnchor ctanchor = alist.get(j);
					String chartId = sheet.getSheetName()
							+ "!"
							+ getAnchorAssociateChartId(ctanchor
									.getGraphicFrame().getGraphic()
									.getGraphicData().getDomNode());
					if (chartId != null) {
						int dx1 = (int) ctanchor.getFrom().getColOff();
						int dy1 = (int) ctanchor.getFrom().getRowOff();
						int dx2 = (int) ctanchor.getTo().getColOff();
						int dy2 = (int) ctanchor.getTo().getRowOff();
						int col1 = ctanchor.getFrom().getCol();
						int row1 = ctanchor.getFrom().getRow();
						int col2 = ctanchor.getTo().getCol();
						int row2 = ctanchor.getTo().getRow();
						anchortMap.put(chartId, new XSSFClientAnchor(dx1, dy1,
								dx2, dy2, col1, row1, col2, row2));
						positionMap.put(
								TieWebSheetUtility.getFullCellRefName(
										sheet.getSheetName(), row1, col1),
								chartId);
					}
				}
			}
		}
	}

	private String getAnchorAssociateChartId(Node parentNode) {
		NodeList childNodes = parentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if ((childNode != null)
					&& (childNode.getNodeName().equalsIgnoreCase("c:chart"))
					&& (childNode.hasAttributes())) {
				NamedNodeMap attrs = childNode.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Attr attribute = (Attr) attrs.item(j);
					if (attribute.getName().equalsIgnoreCase("r:id")) {
						return attribute.getValue();
					}
				}
			}
		}
		return null;
	}

	public static XColor getBgColor(CTPlotArea ctPlot, ThemesTable themeTable) {

		CTSolidColorFillProperties colorFill = null;

		try {
			colorFill = ctPlot.getSpPr().getSolidFill();
		} catch (Exception ex) {
			debug("No entry in bgcolor for solidFill");
		}
		// if there's no solidFill, then use white color
		if (colorFill != null) {
			CTSchemeColor ctsColor = colorFill.getSchemeClr();
			if (ctsColor != null) {
				debug("get background color from scheme");
				return getXColorFromSchemeClr(ctsColor, themeTable);
			} else {
				CTSRgbColor ctrColor = colorFill.getSrgbClr();
				if (ctrColor != null) {
					debug("get background color from rgb");
					return getXColorFromRgbClr(ctrColor, themeTable);
				}
			}
		}
		return new XColor(new XSSFColor(Color.WHITE));

	}

	private static XColor assembleXcolor(XSSFColor bcolor, double preTint, int lumOff,
			int lumMod, int alphaInt) {

		if (bcolor == null) {
			return null;
		}

		double tint = preTint;
		if (tint == 0 ) {
			// no preTint
			if (lumOff>0) {
					tint = (lumOff / 100000.00);
			} else {
				if (lumMod>0) {
					tint = -1 * (lumMod / 100000.00);
				}
			}
		}	

		bcolor.setTint(tint);

		double alpha = 0;
		if (alphaInt>0) {
			alpha = alphaInt / 100000.00;
		}

		debug("assmebleXcolor lumOff = "+lumOff+" lumMod = "+lumMod+" tint = "+tint+" alpha = "+alpha+" xssfcolor = "+bcolor.getRgbWithTint());
		
		return new XColor(bcolor, alpha);

	}

	private static XColor getXColorFromSchemeClr(CTSchemeColor ctsColor,
			ThemesTable themeTable) {
		if (ctsColor.getVal() != null) {
			return getXColorFromSchemeWithSchema(ctsColor.getVal().toString(),
					0, ctsColor, themeTable);
		}
		return null;
	}

	private static XColor getXColorFromSchemeWithSchema(String colorSchema,
			double preTint, CTSchemeColor ctsColor, ThemesTable themeTable) {
		int colorIndex = TieWebSheetUtility.getThemeIndexFromName(colorSchema);
		if (colorIndex >= 0) {
			XSSFColor bcolor = themeTable.getThemeColor(colorIndex);
			if (bcolor != null) {
				int lumOff = 0;
				int lumMod = 0;
				int alphaInt = 0;
				if (ctsColor != null) {
					try {
						lumOff = ctsColor.getLumOffArray(0).getVal();
					} catch (Exception ex) {
						debug("No lumOff entry");
					}
					try {
						lumMod = ctsColor.getLumModArray(0).getVal();
					} catch (Exception ex) {
						debug("No lumMod entry");
					}
					try {
							alphaInt = ctsColor.getAlphaArray(0).getVal();
						} catch (Exception ex) {
							debug("No alpha entry");
					}
				}
				return assembleXcolor(bcolor, preTint, lumOff, lumMod, alphaInt);
			}
		}
		return null;
	}

	private static XColor getXColorFromRgbClr(CTSRgbColor ctrColor,
			ThemesTable themeTable) {
		XSSFColor bcolor = null;
		try {
			byte[] rgb = ctrColor.getVal();
			bcolor = new XSSFColor(rgb);
		} catch (Exception ex) {
			debug("Cannot get rgb color error = " + ex.getLocalizedMessage());
			return null;
		}
		int lumOff = 0;
		int lumMod = 0;
		int alphaStr = 0;
		try {
			lumOff = ctrColor.getLumOffArray(0).getVal();
		} catch (Exception ex) {
			debug("No lumOff entry");
		}
		try {
			lumMod = ctrColor.getLumModArray(0).getVal();
		} catch (Exception ex) {
			debug("No lumMod entry");
		}
		try {
			alphaStr = ctrColor.getAlphaArray(0).getVal();
		} catch (Exception ex) {
			debug("No alpha entry");
		}
		return assembleXcolor(bcolor,0, lumOff, lumMod, alphaStr);
	}

	public static XColor getLineColor(int index, CTLineSer ctLineSer,
			ThemesTable themeTable) {

		CTSolidColorFillProperties colorFill = null;

		try {
			colorFill = ctLineSer.getSpPr().getLn().getSolidFill();
		} catch (Exception ex) {
			debug("No entry for solidFill");
		}
		// if there's no solidFill, then use automaticFill color
		if (colorFill != null) {
			CTSchemeColor ctsColor = colorFill.getSchemeClr();
			if (ctsColor != null) {
				return getXColorFromSchemeClr(ctsColor, themeTable);
			} else {
				CTSRgbColor ctrColor = colorFill.getSrgbClr();
				if (ctrColor != null) {
					return getXColorFromRgbClr(ctrColor, themeTable);
				}
			}
		}
		return getXColorWithAutomaticFill(index, themeTable);

	}

	private static XColor getXColorWithAutomaticFill(int index,
			ThemesTable themeTable) {

		int reminder = (index + 1) % 6;
		if (reminder == 0) {
			reminder = 6;
		}
		String schema = "accent" + reminder;
		double tint = TieWebSheetUtility.getAutomaticTint(index);
		debug(" getXColor automaic index = " + index + " schema = " + schema
				+ " tint = " + tint);
		return getXColorFromSchemeWithSchema(schema, tint, null, themeTable);
	}

	@SuppressWarnings("rawtypes")
	public ChartType getChartType(CTChart ctChart) {
		CTPlotArea plotArea = ctChart.getPlotArea();

		// Area3D
		if (!AppUtils.emptyList((List) plotArea.getArea3DChartList())) {
			return ChartType.Area3D;
		}

		// Area
		if (!AppUtils.emptyList((List) plotArea.getAreaChartList())) {
			return ChartType.Area;
		}

		// Bar3D or Column3D
		final List<CTBar3DChart> bar3ds = plotArea.getBar3DChartList();
		if (!AppUtils.emptyList((List) bar3ds)) {
			switch (bar3ds.get(0).getBarDir().getVal().intValue()) {
			case STBarDir.INT_BAR:
				return ChartType.Bar3D;
			default:
			case STBarDir.INT_COL:
				return ChartType.Column3D;
			}
		}

		// Bar or Column
		final List<CTBarChart> bars = plotArea.getBarChartList();
		if (!AppUtils.emptyList((List) bars)) {
			switch (bars.get(0).getBarDir().getVal().intValue()) {
			case STBarDir.INT_BAR:
				return ChartType.Bar;
			default:
			case STBarDir.INT_COL:
				return ChartType.Column;
			}
		}

		// Bubble
		if (!AppUtils.emptyList((List) plotArea.getBubbleChartList())) {
			return ChartType.Bubble;
		}

		// Doughnut
		if (!AppUtils.emptyList((List) plotArea.getDoughnutChartList())) {
			return ChartType.Doughnut;
		}

		// Line3D
		if (!AppUtils.emptyList((List) plotArea.getLine3DChartList())) {
			return ChartType.Line3D;
		}

		// Line
		if (!AppUtils.emptyList((List) plotArea.getLineChartList())) {
			return ChartType.Line;
		}

		// OfPie
		if (!AppUtils.emptyList((List) plotArea.getOfPieChartList())) {
			return ChartType.OfPie;
		}

		// Pie3D
		if (!AppUtils.emptyList((List) plotArea.getPie3DChartList())) {
			return ChartType.Pie3D;
		}

		// Pie
		if (!AppUtils.emptyList((List) plotArea.getPieChartList())) {
			return ChartType.Pie;
		}

		// Radar
		if (!AppUtils.emptyList((List) plotArea.getRadarChartList())) {
			return ChartType.Radar;
		}

		// Scatter
		if (!AppUtils.emptyList((List) plotArea.getScatterChartList())) {
			return ChartType.Scatter;
		}

		// Stock
		if (!AppUtils.emptyList((List) plotArea.getStockChartList())) {
			return ChartType.Stock;
		}

		// Surface3D
		if (!AppUtils.emptyList((List) plotArea.getSurface3DChartList())) {
			return ChartType.Surface3D;
		}

		// Surface
		if (!AppUtils.emptyList((List) plotArea.getSurfaceChartList())) {
			return ChartType.Surface;
		}

		return null;
	}

	/** Line style: line */
	public static final String STYLE_LINE = "line";
	/** Line style: dashed */
	public static final String STYLE_DASH = "dash";
	/** Line style: dotted */
	public static final String STYLE_DOT = "dot";

	/**
	 * Convert style string to stroke object.
	 * 
	 * @param style
	 *            One of STYLE_xxx.
	 * @return Stroke for <i>style</i> or null if style not supported.
	 */
	private BasicStroke toStroke(String style) {
		BasicStroke result = null;

		if (style != null) {
			float lineWidth = 0.2f;
			float dash[] = { 5.0f };
			float dot[] = { lineWidth };

			if (style.equalsIgnoreCase(STYLE_LINE)) {
				result = new BasicStroke(lineWidth);
			} else if (style.equalsIgnoreCase(STYLE_DASH)) {
				result = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
			} else if (style.equalsIgnoreCase(STYLE_DOT)) {
				result = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 2.0f, dot, 0.0f);
			}
		}// else: input unavailable

		return result;
	}// toStroke()

	/**
	 * Set color of series.
	 * 
	 * @param chart
	 *            JFreeChart.
	 * @param seriesIndex
	 *            Index of series to set color of (0 = first series)
	 * @param style
	 *            One of STYLE_xxx.
	 */
	public void setSeriesStyle(JFreeChart chart, int seriesIndex, String style) {
		if (chart != null && style != null) {
			BasicStroke stroke = toStroke(style);

			Plot plot = chart.getPlot();
			if (plot instanceof CategoryPlot) {
				CategoryPlot categoryPlot = chart.getCategoryPlot();
				CategoryItemRenderer cir = categoryPlot.getRenderer();
				try {
					cir.setSeriesStroke(seriesIndex, stroke); // series line
																// style
				} catch (Exception e) {
					System.err.println("Error setting style '" + style
							+ "' for series '" + seriesIndex + "' of chart '"
							+ chart + "': " + e);
				}
			} else if (plot instanceof XYPlot) {
				XYPlot xyPlot = chart.getXYPlot();
				XYItemRenderer xyir = xyPlot.getRenderer();
				try {
					xyir.setSeriesStroke(seriesIndex, stroke); // series line
																// style
				} catch (Exception e) {
					System.err.println("Error setting style '" + style
							+ "' for series '" + seriesIndex + "' of chart '"
							+ chart + "': " + e);
				}
			} else {
				System.out
						.println("setSeriesColor() unsupported plot: " + plot);
			}
		}// else: input unavailable
	}// setSeriesStyle()

	/**
	 * Set color of series.
	 * 
	 * @param chart
	 *            JFreeChart.
	 * @param seriesIndex
	 *            Index of series to set color of (0 = first series)
	 * @param color
	 *            New color to set.
	 */
	public void setSeriesColor(JFreeChart chart, int seriesIndex, Color color) {
		if (chart != null) {
			Plot plot = chart.getPlot();
			try {
				if (plot instanceof CategoryPlot) {
					CategoryPlot categoryPlot = chart.getCategoryPlot();
					CategoryItemRenderer cir = categoryPlot.getRenderer();
					cir.setSeriesPaint(seriesIndex, color);
				} else if (plot instanceof PiePlot) {
					PiePlot piePlot = (PiePlot) chart.getPlot();
					piePlot.setSectionPaint(seriesIndex, color);
				} else if (plot instanceof XYPlot) {
					XYPlot xyPlot = chart.getXYPlot();
					XYItemRenderer xyir = xyPlot.getRenderer();
					xyir.setSeriesPaint(seriesIndex, color);
				} else {
					System.out.println("setSeriesColor() unsupported plot: "
							+ plot);
				}
			} catch (Exception e) { // e.g. invalid seriesIndex
				System.err.println("Error setting color '" + color
						+ "' for series '" + seriesIndex + "' of chart '"
						+ chart + "': " + e);
			}
		}// else: input unavailable
	}// setSeriesColor()

}
