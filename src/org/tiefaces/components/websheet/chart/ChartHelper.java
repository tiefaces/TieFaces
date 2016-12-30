/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import org.tiefaces.common.AppUtils;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.chart.objects.AreaChart;
import org.tiefaces.components.websheet.chart.objects.Bar3DChart;
import org.tiefaces.components.websheet.chart.objects.BarChart;
import org.tiefaces.components.websheet.chart.objects.ChartObject;
import org.tiefaces.components.websheet.chart.objects.LineChart;
import org.tiefaces.components.websheet.chart.objects.Pie3DChart;
import org.tiefaces.components.websheet.chart.objects.PieChart;
import org.tiefaces.components.websheet.dataobjects.AnchorSize;
import org.tiefaces.components.websheet.dataobjects.ParsedCell;
import org.tiefaces.components.websheet.dataobjects.XColor;
import org.tiefaces.components.websheet.utility.ColorUtility;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;

/**
 * 
 * @author Jason Jiang
 *
 */
public class ChartHelper {

	/** instance to parent websheet bean. */
	private TieWebSheetBean parent = null;
	/** log instance. */
	private final Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());

	/**
	 * Constructor. Pass in websheet bean, So this helper can access related
	 * instance class.
	 * 
	 * @param pParent
	 *            parent websheet bean
	 */
	public ChartHelper(final TieWebSheetBean pParent) {
		this.parent = pParent;
		log.fine("ChartHelper Constructor");
	}

	/**
	 * initial charts map for current workbook.
	 */
	public final void loadChartsMap() {

		this.initChartsMap(parent.getWb());
	}

	/**
	 * initial anchors map for specified workbook. Excel put the chart position
	 * information in draw.xml instead of chart.xml. anchors map contains the
	 * information getting from draw.xml.
	 * 
	 * @param wb
	 *            specified workbook.
	 */
	private void initAnchorsMap(final Workbook wb) {
		try {
			if (wb instanceof XSSFWorkbook) {
				initXSSFAnchorsMap((XSSFWorkbook) wb);
			}
		} catch (Exception e) {
			log.severe("Web Form getAnchorsMap Error Exception = "
					+ e.getLocalizedMessage());
		}
	}

	/**
	 * initial chart map for specified workbook.
	 * 
	 * @param wb
	 *            specified workbook.
	 */
	private void initChartsMap(final Workbook wb) {
		try {
			if (wb instanceof XSSFWorkbook) {
				initXSSFChartsMap((XSSFWorkbook) wb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.severe("Web Form getChartsMap Error Exception = "
					+ e.getLocalizedMessage());
		}
	}


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
	
	/**
	 * build chartData for line chart. chartData include categoryList and
	 * seriesList which used for generate jfreechart.
	 * 
	 * @param chartData chart data.
	 * @param ctChart ct chart.
	 * @param themeTable
	 *            themeTable used for get color with theme name.
	 * @param ctObj ct object.
	 */
	
	public final void setUpChartData(final ChartData chartData,
			final CTChart ctChart, final ThemesTable themeTable,
			final ChartObject ctObj) {

		Object chartObj = null;
		@SuppressWarnings("rawtypes")
		List plotCharts = ctObj.getChartListFromCtChart(ctChart);

		// chart object
		if (plotCharts != null && plotCharts.size() > 0) {
			chartObj = plotCharts.get(0);
		}
		if (chartObj != null) {
			@SuppressWarnings("rawtypes")
			List bsers = ctObj.getSerListFromCtObjChart(chartObj);
			if (!AppUtils.emptyList(bsers)) {
				chartData.buildCategoryList(ctObj
						.getCtAxDataSourceFromSerList(bsers));
				chartData.buildSeriesList(bsers, themeTable, ctObj);
			}
		}
	}

	/**
	 * initial chart map for XSSF format file. XSSF file is actually the only
	 * format in POI support chart object.
	 * 
	 * @param wb
	 *            xssf workbook.
	 */
	private void initXSSFChartsMap(final XSSFWorkbook wb) {

		initAnchorsMap(wb);
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
					// ChartLegend legend = chart.getOrCreateLegend();
					ClientAnchor anchor = null;
					if (chartId != null) {
						anchor = anchorMap.get(chartId);
						if (anchor != null) {
							ChartData chartData = initChartDataFromXSSFChart(
									chartId, chart);
							chartDataMap.put(chartId, chartData);
							JFreeChart jchart = createChart(chartData);
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

	/**
	 * return cell Value for paredCell. ParsedCell contain sheetName/row/col
	 * which normally is parsed from String like: Sheet1!B2 .
	 * 
	 * @param pCell
	 *            parsed cell.
	 * @return cell string value without format.
	 */
	public final String getParsedCellValue(final ParsedCell pCell) {

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

	/**
	 * create default category dataset for JfreeChart with giving chartData.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @return DefaultCategoryDataset for jfreechart.
	 */

	private DefaultCategoryDataset createDataset(final ChartData chartData) {
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

	/**
	 * create default category dataset for JfreeChart with giving chartData.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @return DefaultCategoryDataset for jfreechart.
	 */

	private DefaultPieDataset createPieDataset(final ChartData chartData) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		List<ParsedCell> categoryList = chartData.getCategoryList();
		for (ChartSeries chartSeries : chartData.getSeriesList()) {
			List<ParsedCell> valueList = chartSeries.getValueList();
			for (int i = 0; i < categoryList.size(); i++) {
				try {
					String sCategory = getParsedCellValue(categoryList.get(i));
					String sValue = getParsedCellValue(valueList.get(i));
					dataset.setValue(sCategory, Double.parseDouble(sValue));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return dataset;

	}

	/**
	 * return pie chart title from chartData
	 * 
	 * @param chartData
	 *            ChartData object.
	 * @return title (String).
	 */
	private String getPieTitle(final ChartData chartData) {
		for (ChartSeries chartSeries : chartData.getSeriesList()) {
			String seriesLabel = getParsedCellValue(chartSeries
					.getSeriesLabel());
			return seriesLabel;
		}
		return "";

	}

	/**
	 * Create jfree chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @return jfree line chart.
	 */

	private JFreeChart createChart(final ChartData chartData) {

		switch (chartData.getType()) {
		case Area:
			return createAreaChart(chartData);
		case AreaStacked:
			return createStackedAreaChart(chartData);
		case Line:
			return createLineChart(chartData);
		case Column:
			return createBarChart(chartData, true);
		case ColumnStacked:
			return createStackedBarChart(chartData, true);
		case Column3D:
			return createBarChart3D(chartData, true);
		case Column3DStacked:
			return createStackedBarChart3D(chartData, true);
		case Bar:
			return createBarChart(chartData, false);
		case Bar3D:
			return createBarChart3D(chartData, false);
		case BarStacked:
			return createStackedBarChart(chartData, false);
		case Bar3DStacked:
			return createStackedBarChart3D(chartData, false);
		case Pie:
			return createPieChart(chartData);
		case Pie3D:
			return createPie3DChart(chartData);
		default:
			break;
		}

		return null;
	}

	/**
	 * Create jfree line chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @return jfree line chart.
	 */
	private JFreeChart createLineChart(final ChartData chartData) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createLineChart(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				false, // tooltips
				false // urls
				);

		setupStyle(chart, chartData);

		return chart;

	}

	/**
	 * Create jfree bar chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @param vertical
	 *            chart orientation.
	 * @return jfree line chart.
	 */
	private JFreeChart createAreaChart(final ChartData chartData) 
	{
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		// create the chart...
		final JFreeChart chart = ChartFactory.createAreaChart(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				orientation, true, // include legend
				false, // tooltips
				false // urls
				);

		setupStyle(chart, chartData);

		return chart;

	}
	
	private JFreeChart createStackedAreaChart(final ChartData chartData) 
	{
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		// create the chart...
		final JFreeChart chart = ChartFactory.createStackedAreaChart(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				orientation, true, // include legend
				false, // tooltips
				false // urls
				);

		setupStyle(chart, chartData);

		return chart;

	}
	
	/**
	 * Create jfree bar chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @param vertical
	 *            chart orientation.
	 * @return jfree line chart.
	 */
	private JFreeChart createBarChart(final ChartData chartData,
			final boolean vertical) {

		PlotOrientation orientation = PlotOrientation.VERTICAL;
		if (!vertical) {
			orientation = PlotOrientation.HORIZONTAL;
		}
		// create the chart...
		final JFreeChart chart = ChartFactory.createBarChart(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				orientation, true, // include legend
				false, // tooltips
				false // urls
				);

		setupBarStyle(chart, chartData);

		return chart;

	}

	/**
	 * Create jfree bar chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @param vertical
	 *            chart orientation.
	 * @return jfree line chart.
	 */
	private JFreeChart createStackedBarChart(final ChartData chartData,
			final boolean vertical) {

		PlotOrientation orientation = PlotOrientation.VERTICAL;
		if (!vertical) {
			orientation = PlotOrientation.HORIZONTAL;
		}
		// create the chart...
		final JFreeChart chart = ChartFactory.createStackedBarChart(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				orientation, true, // include legend
				false, // tooltips
				false // urls
				);

		setupBarStyle(chart, chartData);

		return chart;

	}

	/**
	 * Create jfree bar chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @param vertical
	 *            chart orientation.
	 * @return jfree line chart.
	 */
	private JFreeChart createPieChart(final ChartData chartData) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createPieChart(
				getPieTitle(chartData), // chart title
				createPieDataset(chartData), // data
				true, // include legend
				false, // tooltips
				false // urls
				);

		setupPieStyle(chart, chartData);

		return chart;

	}
	private JFreeChart createPie3DChart(final ChartData chartData) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createPieChart3D(
				getPieTitle(chartData), // chart title
				createPieDataset(chartData), // data
				true, // include legend
				false, // tooltips
				false // urls
				);

		setupPieStyle(chart, chartData);

		return chart;

	}

	/**
	 * Create jfree bar chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @param vertical
	 *            chart orientation.
	 * @return jfree line chart.
	 */
	private JFreeChart createBarChart3D(final ChartData chartData,
			final boolean vertical) {

		PlotOrientation orientation = PlotOrientation.VERTICAL;
		if (!vertical) {
			orientation = PlotOrientation.HORIZONTAL;
		}
		// create the chart...
		final JFreeChart chart = ChartFactory.createBarChart3D(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				orientation, true, // include legend
				false, // tooltips
				false // urls
				);

		setupBarStyle(chart, chartData);

		return chart;

	}

	/**
	 * Create jfree bar chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @param vertical
	 *            chart orientation.
	 * @return jfree line chart.
	 */
	private JFreeChart createStackedBarChart3D(final ChartData chartData,
			final boolean vertical) {

		PlotOrientation orientation = PlotOrientation.VERTICAL;
		if (!vertical) {
			orientation = PlotOrientation.HORIZONTAL;
		}
		// create the chart...
		final JFreeChart chart = ChartFactory.createStackedBarChart3D(
				chartData.getTitle(), // chart title
				chartData.getCatAx().getTitle(), // x axis label
				chartData.getValAx().getTitle(), // y axis label
				createDataset(chartData), // data
				orientation, true, // include legend
				false, // tooltips
				false // urls
				);

		setupBarStyle(chart, chartData);

		return chart;

	}

	/**
	 * finalize the style for jfreechart. The default setting is different from
	 * jfreechart and Excel. We try to minimize the difference.
	 * 
	 * @param chart
	 *            jfreechart.
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 */

	private void setupStyle(final JFreeChart chart, final ChartData chartData) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		List<ChartSeries> seriesList = chartData.getSeriesList();
		BasicStroke bLine = new BasicStroke(2.0f);
		for (int i = 0; i < seriesList.size(); i++) {
			Color cColor = ColorUtility.xssfClrToClr(seriesList.get(i)
					.getSeriesColor().getXssfColor());
			plot.getRenderer().setSeriesPaint(i, cColor);
			plot.getRenderer().setSeriesStroke(i, bLine);
		}
		plot.setBackgroundPaint(ColorUtility.xssfClrToClr(chartData
				.getBgColor().getXssfColor()));

		// below are modifications for default setting in excel chart
		// to-do: need read setting from xml in future
		plot.setOutlineVisible(false);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setRangeGridlineStroke(new BasicStroke(0.1f));
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		chart.setBackgroundPaint(Color.WHITE);
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setFrame(BlockBorder.NONE);

	}

	/**
	 * finalize the style for jfreechart. The default setting is different from
	 * jfreechart and Excel. We try to minimize the difference.
	 * 
	 * @param chart
	 *            jfreechart.
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 */

	private void setupPieStyle(final JFreeChart chart, final ChartData chartData) {
		PiePlot plot = (PiePlot) chart.getPlot();
		List<ChartSeries> seriesList = chartData.getSeriesList();
		List<ParsedCell> categoryList = chartData.getCategoryList();
		BasicStroke bLine = new BasicStroke(2.0f);
		for (int i = 0; i < seriesList.size(); i++) {
			ChartSeries chartSeries = seriesList.get(i);
			List<XColor> valueColorList = chartSeries.getValueColorList();
			for (int index = 0; index < categoryList.size(); index++) {
				try {
					String sCategory = getParsedCellValue(categoryList
							.get(index));
					Color cColor = ColorUtility.xssfClrToClr(valueColorList
							.get(index).getXssfColor());
					plot.setSectionPaint(sCategory, cColor);
					plot.setSectionOutlineStroke(sCategory, bLine);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
		plot.setBackgroundPaint(ColorUtility.xssfClrToClr(chartData
				.getBgColor().getXssfColor()));

		// below are modifications for default setting in excel chart
		// to-do: need read setting from xml in future

		plot.setOutlineVisible(false);
		plot.setLegendItemShape(new Rectangle(8, 8));
		chart.setBackgroundPaint(Color.WHITE);
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setFrame(BlockBorder.NONE);

	}

	/**
	 * finalize the style for barchart. This will call setupStyle common first.
	 * 
	 * @param chart
	 *            jfreechart.
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 */

	private void setupBarStyle(final JFreeChart chart, final ChartData chartData) {
		setupStyle(chart, chartData);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		// ((BarRenderer) plot.getRenderer()).setBarPainter(new
		// StandardBarPainter());
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setItemMargin(.02);

		plot.setForegroundAlpha(1);
	}

	/**
	 * initial chartData from POI XSSF Chart object.
	 * 
	 * @param chartId
	 *            usually as Sheet1!ref1 etc.
	 * @param chart
	 *            POI XSSF chart.
	 * @return chartData object.
	 */
	private ChartData initChartDataFromXSSFChart(final String chartId,
			final XSSFChart chart) {

		XSSFWorkbook wb = (XSSFWorkbook) parent.getWb();
		ThemesTable themeTable = wb.getStylesSource().getTheme();

		ChartData chartData = new ChartData();
		XSSFRichTextString chartTitle = chart.getTitle();
		CTChart ctChart = chart.getCTChart();
		ChartType chartType = ChartUtility.getChartType(ctChart);
		chartData.setBgColor(ColorUtility.getBgColor(ctChart.getPlotArea(),
				themeTable));
		log.fine("initChartDataFromXSSFChart chart id = " + chartId
				+ " title = " + chartTitle + " chart type = " + chartType);

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

		ChartObject ctObj = null;

		switch (chartType) {
		case Area: 
		case AreaStacked:
			ctObj = new AreaChart();
			break;
		case Line:
			ctObj = new LineChart();
			break;
		case Column:
		case ColumnStacked:
		case Bar:
		case BarStacked:
			ctObj = new BarChart();
			break;
		case Column3D:
		case Column3DStacked:
		case Bar3D:
		case Bar3DStacked:
			ctObj = new Bar3DChart();
			break;
		case Pie:
			ctObj = new PieChart();
			break;
		case Pie3D:
			ctObj = new Pie3DChart();
			break;
		default:
			break;
		}

		if (ctObj != null) {
			setUpChartData(chartData, ctChart, themeTable, ctObj);
		}

		// XSSFChartLegend legend = chart.getOrCreateLegend();
		// System.out.println("******* legend = "+ legend);
		return chartData;
	}

	/**
	 * retrieve anchor information from draw.xml for all the charts in the
	 * workbook. then save them to anchors map.
	 * 
	 * @param wb
	 *            workbook.
	 */
	private void initXSSFAnchorsMap(final XSSFWorkbook wb) {

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

	/**
	 * get anchor information from draw.xml.
	 * 
	 * @param ctDrawing
	 * @return clientAnchor which include row/col and dx information.
	 */
	// private XSSFClientAnchor getClientAnchorFromCTDrawing(final CTDrawing
	// ctDrawing) {
	//
	// if ((ctDrawing != null) && (ctDrawing.sizeOfTwoCellAnchorArray() > 0)) {
	// List<CTTwoCellAnchor> alist = ctDrawing.getTwoCellAnchorList();
	// for (int j = 0; j < alist.size(); j++) {
	// CTTwoCellAnchor ctanchor = alist.get(j);
	// String chartId = getAnchorAssociateChartId(ctanchor
	// .getGraphicFrame().getGraphic().getGraphicData()
	// .getDomNode());
	// if (chartId != null) {
	// int dx1 = (int) ctanchor.getFrom().getColOff();
	// int dy1 = (int) ctanchor.getFrom().getRowOff();
	// int dx2 = (int) ctanchor.getTo().getColOff();
	// int dy2 = (int) ctanchor.getTo().getRowOff();
	// int col1 = ctanchor.getFrom().getCol();
	// int row1 = ctanchor.getFrom().getRow();
	// int col2 = ctanchor.getTo().getCol();
	// int row2 = ctanchor.getTo().getRow();
	// return new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1,
	// col2, row2);
	// }
	// }
	// }
	// return null;
	// }

	/**
	 * Navigate through xml node to get the chartId. This is a workaround as
	 * there's no direct method in the api.
	 * 
	 * @param parentNode
	 *            root node to search rid.
	 * @return rid in the giving node tree.
	 */
	private String getAnchorAssociateChartId(final Node parentNode) {
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
			BasicStroke stroke = ChartUtility.toStroke(style);

			Plot plot = chart.getPlot();
			if (plot instanceof CategoryPlot) {
				CategoryPlot categoryPlot = chart.getCategoryPlot();
				CategoryItemRenderer cir = categoryPlot.getRenderer();
				try {
					cir.setSeriesStroke(seriesIndex, stroke); // series line
																// style
				} catch (Exception e) {
					log.severe("Error setting style '" + style
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
					log.severe("Error setting style '" + style
							+ "' for series '" + seriesIndex + "' of chart '"
							+ chart + "': " + e);
				}
			} else {
				log.fine("setSeriesColor() unsupported plot: " + plot);
			}
		}// else: input unavailable
	}// setSeriesStyle()



}
