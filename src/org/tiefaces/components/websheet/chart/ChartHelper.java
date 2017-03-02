/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.POIXMLDocumentPart.RelationPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.dataobjects.AnchorSize;
import org.tiefaces.components.websheet.dataobjects.ParsedCell;
import org.tiefaces.components.websheet.dataobjects.XColor;
import org.tiefaces.components.websheet.utility.CellUtility;
import org.tiefaces.components.websheet.utility.ChartUtility;
import org.tiefaces.components.websheet.utility.ColorUtility;
import org.tiefaces.components.websheet.utility.PicturesUtility;
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
 * The Class ChartHelper.
 *
 * @author Jason Jiang
 */
public class ChartHelper {

	/** instance to parent websheet bean. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(ChartHelper.class.getName());

	/**
	 * Instantiates a new chart helper.
	 *
	 * @param pParent
	 *            parent websheet bean
	 */
	public ChartHelper(final TieWebSheetBean pParent) {
		this.parent = pParent;
		LOG.fine("ChartHelper Constructor");
	}

	/**
	 * initial charts map for current workbook.
	 */
	public final void loadChartsMap() {

		this.initChartsMap(parent.getWb());
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
				initXSSFChartsMap((XSSFWorkbook) wb, parent.getCharsData());
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "getChartsMap Error Exception = "
					+ e.getLocalizedMessage(), e);
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
			result = CellUtility.getCellValueWithoutFormat(poiCell);

		} catch (Exception ex) {
			LOG.log(Level.FINE,
					"error getParsedCellValue :" + ex.getLocalizedMessage(),
					ex);
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

	private DefaultPieDataset createPieDataset(final ChartData chartData) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		List<ParsedCell> categoryList = chartData.getCategoryList();
		for (ChartSeries chartSeries : chartData.getSeriesList()) {
			List<ParsedCell> valueList = chartSeries.getValueList();
			for (int i = 0; i < categoryList.size(); i++) {
				try {
					String sCategory = getParsedCellValue(
							categoryList.get(i));
					String sValue = getParsedCellValue(valueList.get(i));
					dataset.setValue(sCategory, Double.parseDouble(sValue));
				} catch (Exception ex) {
					LOG.log(Level.FINE, "error in creatPieDataset : "
							+ ex.getLocalizedMessage(), ex);
				}
			}
		}
		return dataset;

	}

	/**
	 * return pie chart title from chartData.
	 *
	 * @param chartData
	 *            ChartData object.
	 * @return title (String).
	 */
	private String getPieTitle(final ChartData chartData) {
		for (ChartSeries chartSeries : chartData.getSeriesList()) {
			return getParsedCellValue(chartSeries.getSeriesLabel());
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

		return chartData.getType().createChart(this, chartData);

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
	public final void setSeriesStyle(final JFreeChart chart,
			final int seriesIndex, final String style) {
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
					LOG.log(Level.SEVERE,
							"Error setting style '" + style
									+ "' for series '" + Integer.toString(seriesIndex)
									+ "' of chart '" + chart.toString() + "': "
									+ e.getLocalizedMessage(),
							e);
				}
			} else if (plot instanceof XYPlot) {
				XYPlot xyPlot = chart.getXYPlot();
				XYItemRenderer xyir = xyPlot.getRenderer();
				try {
					xyir.setSeriesStroke(seriesIndex, stroke); // series line
																// style
				} catch (Exception e) {
					LOG.log(Level.SEVERE,
							"Error setting style '" + style
									+ "' for series '" + Integer.toString(seriesIndex)
									+ "' of chart '" + chart.toString() + "': "
									+ e.getLocalizedMessage(),
							e);
				}
			} else {
				LOG.log(Level.FINE,"setSeriesColor() unsupported plot: " + plot.toString());
			}
		}
	}

	/**
	 * Create jfree line chart.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @return jfree line chart.
	 */
	public JFreeChart createLineChart(final ChartData chartData) {

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
	 * @return jfree line chart.
	 */
	public JFreeChart createAreaChart(final ChartData chartData) {
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

	/**
	 * create stacked area chart.
	 * 
	 * @param chartData
	 *            chart data.
	 * @return jfree chart.
	 */
	public JFreeChart createStackedAreaChart(final ChartData chartData) {
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
	public JFreeChart createBarChart(final ChartData chartData,
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
	public JFreeChart createStackedBarChart(final ChartData chartData,
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
	 * @return jfree line chart.
	 */
	public JFreeChart createPieChart(final ChartData chartData) {

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

	/**
	 * create pie 3d chart.
	 * 
	 * @param chartData
	 *            chart data.
	 * @return jfreechart.
	 */
	public JFreeChart createPie3DChart(final ChartData chartData) {

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
	public JFreeChart createBarChart3D(final ChartData chartData,
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
	public JFreeChart createStackedBarChart3D(final ChartData chartData,
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

	private void setupStyle(final JFreeChart chart,
			final ChartData chartData) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		List<ChartSeries> seriesList = chartData.getSeriesList();
		BasicStroke bLine = new BasicStroke(2.0f);
		for (int i = 0; i < seriesList.size(); i++) {
			Color cColor = ColorUtility.xssfClrToClr(
					seriesList.get(i).getSeriesColor().getXssfColor());
			plot.getRenderer().setSeriesPaint(i, cColor);
			plot.getRenderer().setSeriesStroke(i, bLine);
		}
		plot.setBackgroundPaint(ColorUtility
				.xssfClrToClr(chartData.getBgColor().getXssfColor()));

		// below are modifications for default setting in excel chart
		// to-do: need read setting from xml in future
		plot.setOutlineVisible(false);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setRangeGridlineStroke(
				new BasicStroke(TieConstants.DEFAULT_BASIC_STROKE));
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

	private void setupPieStyle(final JFreeChart chart,
			final ChartData chartData) {
		PiePlot plot = (PiePlot) chart.getPlot();
		List<ChartSeries> seriesList = chartData.getSeriesList();
		List<ParsedCell> categoryList = chartData.getCategoryList();
		BasicStroke bLine = new BasicStroke(2.0f);
		for (int i = 0; i < seriesList.size(); i++) {
			ChartSeries chartSeries = seriesList.get(i);
			List<XColor> valueColorList = chartSeries.getValueColorList();
			for (int index = 0; index < categoryList.size(); index++) {
				try {
					String sCategory = getParsedCellValue(
							categoryList.get(index));
					Color cColor = ColorUtility.xssfClrToClr(
							valueColorList.get(index).getXssfColor());
					plot.setSectionPaint(sCategory, cColor);
					plot.setSectionOutlineStroke(sCategory, bLine);
				} catch (Exception ex) {
					LOG.log(Level.FINE, "SetupPieStyle error = "
							+ ex.getLocalizedMessage(), ex);
				}
			}

		}
		plot.setBackgroundPaint(ColorUtility
				.xssfClrToClr(chartData.getBgColor().getXssfColor()));

		// below are modifications for default setting in excel chart
		// to-do: need read setting from xml in future

		plot.setOutlineVisible(false);
		plot.setLegendItemShape(
				new Rectangle(TieConstants.DEFAULT_LEGENT_ITEM_SHAPE_WIDTH,
						TieConstants.DEFAULT_LEGENT_ITEM_SHAPE_HEIGHT));
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

	private void setupBarStyle(final JFreeChart chart,
			final ChartData chartData) {
		setupStyle(chart, chartData);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setItemMargin(TieConstants.DEFAULT_BAR_STYLE_ITEM_MARGIN);

		plot.setForegroundAlpha(
				TieConstants.DEFAULT_BARSTYLE_FOREGROUND_ALPHA);
	}

	/**
	 * create default category dataset for JfreeChart with giving chartData.
	 * 
	 * @param chartData
	 *            contain information gathered from excel chart object.
	 * @return DefaultCategoryDataset for jfreechart.
	 */

	private DefaultCategoryDataset createDataset(
			final ChartData chartData) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<ParsedCell> categoryList = chartData.getCategoryList();
		for (ChartSeries chartSeries : chartData.getSeriesList()) {
			String seriesLabel = getParsedCellValue(
					chartSeries.getSeriesLabel());
			List<ParsedCell> valueList = chartSeries.getValueList();
			for (int i = 0; i < categoryList.size(); i++) {
				try {
					String sCategory = getParsedCellValue(
							categoryList.get(i));
					String sValue = getParsedCellValue(valueList.get(i));
					dataset.addValue(Double.parseDouble(sValue),
							seriesLabel, sCategory);
				} catch (Exception ex) {
					LOG.log(Level.FINE, "error in creatDataset : "
							+ ex.getLocalizedMessage(), ex);
				}
			}
		}
		return dataset;

	}

	/**
	 * initial chart map for XSSF format file. XSSF file is actually the only
	 * format in POI support chart object.
	 *
	 * @param wb
	 *            xssf workbook.
	 * @param chartsData
	 *            the charts data
	 */
	private void initXSSFChartsMap(final XSSFWorkbook wb,
			final ChartsData chartsData) {

		initAnchorsMap(wb, chartsData);
		Map<String, ClientAnchor> anchorMap = chartsData
				.getChartAnchorsMap();

		Map<String, BufferedImage> chartMap = chartsData.getChartsMap();
		Map<String, ChartData> chartDataMap = chartsData.getChartDataMap();
		chartMap.clear();
		chartDataMap.clear();

		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();
			List<XSSFChart> charts = drawing.getCharts();
			if ((charts != null) && (!charts.isEmpty())) {
				for (XSSFChart chart : charts) {
					generateSingleXSSFChart(chart,
							getChartIdFromParent(chart,
									sheet.getSheetName()),
							sheet, anchorMap, chartMap, chartDataMap);
				}
			}
		}

	}

	/**
	 * Gets the chart id from parent.
	 *
	 * @param chart
	 *            the chart
	 * @param sheetName
	 *            the sheet name
	 * @return the chart id from parent
	 */
	private final String getChartIdFromParent(XSSFChart chart,
			String sheetName) {
		if (chart.getParent() != null) {
			for (RelationPart rp : chart.getParent().getRelationParts()) {
				if (rp.getDocumentPart() == chart) {
					return sheetName + "!" + rp.getRelationship().getId();
				}
			}
		}
		return null;
	}

	/**
	 * initial anchors map for specified workbook. Excel put the chart position
	 * information in draw.xml instead of chart.xml. anchors map contains the
	 * information getting from draw.xml.
	 *
	 * @param wb
	 *            specified workbook.
	 * @param chartsData
	 *            the charts data
	 */
	private void initAnchorsMap(final Workbook wb,
			final ChartsData chartsData) {
		try {
			if (wb instanceof XSSFWorkbook) {
				ChartUtility.initXSSFAnchorsMap((XSSFWorkbook) wb,
						chartsData);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Web Form getAnchorsMap Error Exception = "
							+ e.getLocalizedMessage(),
					e);
		}
	}

	/**
	 * Generate single XSSF chart.
	 * 
	 * @param chart
	 *            the chart
	 * @param chartId
	 *            the chart id
	 * @param sheet
	 *            the sheet
	 * @param anchorMap
	 *            the anchor map
	 * @param chartMap
	 *            the chart map
	 * @param chartDataMap
	 *            the chart data map
	 */
	private void generateSingleXSSFChart(final XSSFChart chart,
			final String chartId, final XSSFSheet sheet,
			final Map<String, ClientAnchor> anchorMap,
			final Map<String, BufferedImage> chartMap,
			final Map<String, ChartData> chartDataMap) {
		ClientAnchor anchor;
		try {
			anchor = anchorMap.get(chartId);
			if (anchor != null) {
				ChartData chartData = ChartUtility
						.initChartDataFromXSSFChart(chartId, chart,
								(XSSFWorkbook) parent.getWb());
				chartDataMap.put(chartId, chartData);
				JFreeChart jchart = createChart(chartData);
				if (jchart != null) {
					AnchorSize anchorSize = PicturesUtility
							.getAnchorSize(sheet, null, null, anchor);
					BufferedImage img = jchart.createBufferedImage(
							anchorSize.getWidth(), anchorSize.getHeight());
					chartMap.put(chartId, img);
				}
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "generate chart for " + chartId
					+ " error = " + ex.getLocalizedMessage(), ex);
		}
	}

}
