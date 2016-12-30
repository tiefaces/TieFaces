package org.tiefaces.components.websheet.chart;

import java.awt.BasicStroke;
import java.util.List;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTArea3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;
import org.tiefaces.common.AppUtils;

/**
 * Utility Class for Chart
 * @author Jason Jiang
 *
 */
public class ChartUtility {
	/**
	 * return chart type from CTChart object.
	 * @param ctChart object.
	 * @return ChartType.
	 */
	@SuppressWarnings("rawtypes")
	public static ChartType getChartType(final CTChart ctChart) {
		CTPlotArea plotArea = ctChart.getPlotArea();

		// Area
		final List<CTAreaChart> areas = plotArea.getAreaChartList();
		if (!AppUtils.emptyList((List) areas)) {
			int grouping = areas.get(0).getGrouping().getVal().intValue();
			switch (grouping) {
			case STGrouping.INT_STACKED: 
				return ChartType.AreaStacked;
			default:
				return ChartType.Area;
		}
		}

		// Bar3D or Column3D
		final List<CTBar3DChart> bar3ds = plotArea.getBar3DChartList();
		if (!AppUtils.emptyList((List) bar3ds)) {
			int grouping = bar3ds.get(0).getGrouping().getVal().intValue();
			switch (bar3ds.get(0).getBarDir().getVal().intValue()) {
			case STBarDir.INT_BAR:
				switch (grouping) {
					case STBarGrouping.INT_STACKED: 
						return ChartType.Bar3DStacked;
					default:
						return ChartType.Bar3D;
				}
			default:
			case STBarDir.INT_COL:
				switch (grouping) {
				case STBarGrouping.INT_STACKED: 
					return ChartType.Column3DStacked;
				default:
					return ChartType.Column3D;
				}
			}
		}

		// Bar or Column
		final List<CTBarChart> bars = plotArea.getBarChartList();
		if (!AppUtils.emptyList((List) bars)) {
			int grouping = bars.get(0).getGrouping().getVal().intValue();
			switch (bars.get(0).getBarDir().getVal().intValue()) {
			case STBarDir.INT_BAR:
				switch (grouping) {
				case STBarGrouping.INT_STACKED: 
					return ChartType.BarStacked;
				default:
					return ChartType.Bar;
				}
			default:
			case STBarDir.INT_COL:
				switch (grouping) {
				case STBarGrouping.INT_STACKED: 
					return ChartType.ColumnStacked;
				default:
					return ChartType.Column;
				}
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
	
	/** Line style: line. */
	public static final String STYLE_LINE = "line";
	/** Line style: dashed. */
	public static final String STYLE_DASH = "dash";
	/** Line style: dotted. */
	public static final String STYLE_DOT = "dot";

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
		}

		return result;
	}
	
}
