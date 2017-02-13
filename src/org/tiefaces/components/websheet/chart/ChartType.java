/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart;

import java.util.List;

import org.jfree.chart.JFreeChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;
import org.tiefaces.common.AppUtils;
import org.tiefaces.components.websheet.chart.objects.AreaChart;
import org.tiefaces.components.websheet.chart.objects.Bar3DChart;
import org.tiefaces.components.websheet.chart.objects.BarChart;
import org.tiefaces.components.websheet.chart.objects.ChartObject;
import org.tiefaces.components.websheet.chart.objects.LineChart;
import org.tiefaces.components.websheet.chart.objects.Pie3DChart;
import org.tiefaces.components.websheet.chart.objects.PieChart;

/**
 * Chart Type class (enum).
 * 
 * @author Jason Jiang
 *
 */
public enum ChartType {
	/** area. */
	AREA("Area") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			final List<CTAreaChart> areas = plotArea.getAreaChartList();
			if (!AppUtils.emptyList((List) areas)) {
				int grouping = areas.get(0).getGrouping().getVal()
						.intValue();
				if (grouping != STGrouping.INT_STACKED) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new AreaChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createAreaChart(chartData);
		}
	},
	/** area stacked. */
	AREASTACKED("AreaStacked") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			final List<CTAreaChart> areas = plotArea.getAreaChartList();
			if (!AppUtils.emptyList((List) areas)) {
				int grouping = areas.get(0).getGrouping().getVal()
						.intValue();
				if (grouping == STGrouping.INT_STACKED) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new AreaChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createStackedAreaChart(chartData);
		}
	},
	/** bar 3d. */
	BAR3D("Bar3D") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar3D or Column3D
			final List<CTBar3DChart> bar3ds = plotArea.getBar3DChartList();
			if (!AppUtils.emptyList((List) bar3ds)) {
				int grouping = bar3ds.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bar3ds.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_BAR)
						&& (grouping != STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new Bar3DChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createBarChart3D(chartData, false);
		}
	},
	/** bar 3d stacked. */
	BAR3DSTACKED("Bar3DStacked") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar3D or Column3D
			final List<CTBar3DChart> bar3ds = plotArea.getBar3DChartList();
			if (!AppUtils.emptyList((List) bar3ds)) {
				int grouping = bar3ds.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bar3ds.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_BAR)
						&& (grouping == STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new Bar3DChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createStackedBarChart3D(chartData, false);
		}
	},
	/** column 3d. */
	COLUMN3D("Column3D") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar3D or Column3D
			final List<CTBar3DChart> bar3ds = plotArea.getBar3DChartList();
			if (!AppUtils.emptyList((List) bar3ds)) {
				int grouping = bar3ds.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bar3ds.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_COL)
						&& (grouping != STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new Bar3DChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createBarChart3D(chartData, true);
		}
	},
	/** column 3d stacked. */
	COLUMN3DSTACKED("Column3DStacked") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar3D or Column3D
			final List<CTBar3DChart> bar3ds = plotArea.getBar3DChartList();
			if (!AppUtils.emptyList((List) bar3ds)) {
				int grouping = bar3ds.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bar3ds.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_COL)
						&& (grouping == STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new Bar3DChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createStackedBarChart3D(chartData, true);
		}
	},
	/** bar. */
	BAR("Bar") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar or Column
			final List<CTBarChart> bars = plotArea.getBarChartList();
			if (!AppUtils.emptyList((List) bars)) {
				int grouping = bars.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bars.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_BAR)
						&& (grouping != STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;

		}

		@Override
		public ChartObject createChartObject() {
			return new BarChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createBarChart(chartData, false);
		}
	},
	/** bar stacked. */
	BARSTACKED("BarStacked") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar or Column
			final List<CTBarChart> bars = plotArea.getBarChartList();
			if (!AppUtils.emptyList((List) bars)) {
				int grouping = bars.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bars.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_BAR)
						&& (grouping == STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {

			return new BarChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createStackedBarChart(chartData, false);
		}
	},
	/** column. */
	COLUMN("Column") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar or Column
			final List<CTBarChart> bars = plotArea.getBarChartList();
			if (!AppUtils.emptyList((List) bars)) {
				int grouping = bars.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bars.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_COL)
						&& (grouping != STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new BarChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createBarChart(chartData, true);
		}
	},
	/** column stacked. */
	COLUMNSTACKED("ColumnStacked") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Bar or Column
			final List<CTBarChart> bars = plotArea.getBarChartList();
			if (!AppUtils.emptyList((List) bars)) {
				int grouping = bars.get(0).getGrouping().getVal()
						.intValue();
				int bardir = bars.get(0).getBarDir().getVal().intValue();
				if ((bardir == STBarDir.INT_COL)
						&& (grouping == STBarGrouping.INT_STACKED)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new BarChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createStackedBarChart(chartData, true);
		}
	},
	/** bubble. */
	BUBBLE("Bubble") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getBubbleChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** dough nut. */
	DOUGHNUT("Doughnut") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils
					.emptyList((List) plotArea.getDoughnutChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** line 3d. */
	LINE3D("Line3D") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getLine3DChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** line. */
	LINE("Line") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getLineChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new LineChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createLineChart(chartData);
		}
	},
	/** of pie. */
	OFPIE("OfPie") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getOfPieChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new PieChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** pie 3d. */
	PIE3D("Pie3D") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getPie3DChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new Pie3DChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createPie3DChart(chartData);
		}
	},
	/** pie. */
	PIE("Pie") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getPieChartList())) {
				return true;
			}

			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return new PieChart();
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return chartHelper.createPieChart(chartData);
		}
	},
	/** radar. */
	RADAR("Radar") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils.emptyList((List) plotArea.getRadarChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** scatter. */
	SCATTER("Scatter") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils
					.emptyList((List) plotArea.getScatterChartList())) {
				return true;
			}

			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** stock. */
	STOCK("Stock") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			// Stock
			if (!AppUtils.emptyList((List) plotArea.getStockChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** surface 3d. */
	SURFACE3D("Surface3D") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils
					.emptyList((List) plotArea.getSurface3DChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	},
	/** surface. */
	SURFACE("Surface") {
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isThisType(final CTPlotArea plotArea) {
			if (!AppUtils
					.emptyList((List) plotArea.getSurfaceChartList())) {
				return true;
			}
			return false;
		}

		@Override
		public ChartObject createChartObject() {
			return null;
		}

		@Override
		public JFreeChart createChart(ChartHelper chartHelper,
				ChartData chartData) {
			return null;
		}
	};

	/**
	 * string.
	 */
	private String string;

	/**
	 * constructor.
	 * 
	 * @param pstring
	 *            string.
	 */
	ChartType(final String pstring) {
		this.string = pstring;
	}

	/**
	 * interface.
	 * 
	 * @param plotArea
	 *            plot area.
	 * @return true if it's the type.
	 */

	public abstract boolean isThisType(CTPlotArea plotArea);

	/**
	 * create chart object.
	 * 
	 * @return chart object.
	 */
	public abstract ChartObject createChartObject();

	/**
	 * create jfree chart.
	 * 
	 * @param chartHelper
	 *          
	 * @param chartData
	 *            chart data.
	 * @return jfree chart.
	 */
	public abstract JFreeChart createChart(ChartHelper chartHelper,
			ChartData chartData);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return string;
	}

}
