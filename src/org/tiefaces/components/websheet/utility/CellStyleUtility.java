/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.utility;

import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.dataobjects.FacesCell;

/**
 * The Class PicturesHelper.
 */
public final class CellStyleUtility {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(CellStyleUtility.class.getName());

	/**
	 * hide constructor.
	 */
	private CellStyleUtility() {
		// not called
	}

	/**
	 * Gets the row style.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @param inputType
	 *            the input type
	 * @param rowHeight
	 *            the row height
	 * @return the row style
	 */
	public static String getRowStyle(final Workbook wb, final Cell poiCell,
			final String inputType, final float rowHeight) {
	
		CellStyle cellStyle = poiCell.getCellStyle();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			float maxHeight = rowHeight;
			if (!inputType.isEmpty()) {
				maxHeight = Math.min(font.getFontHeightInPoints() + 8f,
						rowHeight);
			}
			return "height:" + WebSheetUtility.pointsToPixels(maxHeight)
					+ "px;";
		}
		return "";
	}

	/**
	 * Gets the cell font style.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @return the cell font style
	 */
	public static String getCellFontStyle(final Workbook wb,
			final Cell poiCell) {
	
		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuilder webStyle = new StringBuilder();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			if (font.getItalic()) {
				webStyle.append("font-style: italic;");
			}
			if (font.getBold()) {
				webStyle.append("font-weight: bold;");
			}
			webStyle.append(
					"font-size: " + font.getFontHeightInPoints() + "pt;");
			String decoration = getCellFontDecoration(font);
			if (decoration.length() > 0) {
				webStyle.append("text-decoration:" + decoration + ";");
			}
			webStyle.append(getCellFontColor(font));
	
		}
		return webStyle.toString();
	
	}

	/** 
	 * get cell font color.
	 * @param font font.
	 * @return String font color.
	 */
	private static String getCellFontColor(Font font) {
		short[] rgbfix = { TieConstants.RGB_MAX, TieConstants.RGB_MAX,
				TieConstants.RGB_MAX };
		if (font instanceof XSSFFont) {
			XSSFColor color = ((XSSFFont) font).getXSSFColor();
			if (color != null) {
				rgbfix = ColorUtility.getTripletFromXSSFColor(color);
			}
		}
		if (rgbfix[0] != TieConstants.RGB_MAX) {
			return "color:rgb("+ FacesUtility.strJoin(rgbfix, ",") + ");";
		}
		return "";
	}

	/**
	 * Get font decoration.
	 * @param font font.
	 * @return font decoration.
	 */
	private static String getCellFontDecoration(Font font) {
		StringBuilder decoration = new StringBuilder();
		if (font.getUnderline() != 0) {
			decoration.append(" underline");
		}
		if (font.getStrikeout()) {
			decoration.append(" line-through");
		}
		return decoration.toString();
	}

	/**
	 * Gets the cell style.
	 *
	 * @param wb
	 *            the wb
	 * @param poiCell
	 *            the poi cell
	 * @param inputType
	 *            the input type
	 * @return the cell style
	 */
	public static String getCellStyle(final Workbook wb, final Cell poiCell,
			final String inputType) {
	
		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuilder webStyle = new StringBuilder();
		if (cellStyle != null) {
			if (!inputType.isEmpty()) {
				webStyle.append(getAlignmentFromCell(poiCell, cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(cellStyle));
			}
	
			webStyle.append(ColorUtility.getBgColorFromCell(wb, poiCell, cellStyle));
		}
		return webStyle.toString();
	
	}

	/**
	 * Gets the column style.
	 *
	 * @param wb
	 *            the wb
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param rowHeight
	 *            the row height
	 * @return the column style
	 */
	public static String getColumnStyle(final Workbook wb,
			final FacesCell fcell, final Cell poiCell,
			final float rowHeight) {
	
		String inputType = fcell.getInputType();
		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuilder webStyle = new StringBuilder();
		if (cellStyle != null) {
			if (fcell.isContainPic() || fcell.isContainChart()) {
				webStyle.append("vertical-align: top;");
			} else {
				webStyle.append(getAlignmentFromCell(poiCell, cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(cellStyle));
			}
			webStyle.append(ColorUtility.getBgColorFromCell(wb, poiCell, cellStyle));
			webStyle.append(getRowStyle(wb, poiCell, inputType, rowHeight));
		} else {
			webStyle.append(getAlignmentFromCellType(poiCell));
		}
		return webStyle.toString();
	
	}

	/**
	 * Gets the alignment from cell.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @param cellStyle
	 *            the cell style
	 * @return the alignment from cell
	 */
	private static String getAlignmentFromCell(final Cell poiCell,
			final CellStyle cellStyle) {
	
		String style = "";
		switch (cellStyle.getAlignmentEnum()) {
		case LEFT:
			style = TieConstants.TEXT_ALIGN_LEFT;
			break;
		case RIGHT:
			style = TieConstants.TEXT_ALIGN_RIGHT;
			break;
		case CENTER:
			style = TieConstants.TEXT_ALIGN_CENTER;
			break;
		case GENERAL:
			style = getAlignmentFromCellType(poiCell);
			break;
		default:
			break;
		}
		return style;
	}

	/**
	 * Gets the vertical alignment from cell.
	 * 
	 * @param cellStyle
	 *            the cell style
	 *
	 * @return the vertical alignment from cell
	 */
	private static String getVerticalAlignmentFromCell(
			final CellStyle cellStyle) {
	
		String style = "";
		switch (cellStyle.getVerticalAlignmentEnum()) {
		case TOP:
			style = TieConstants.VERTICAL_ALIGN_TOP;
			break;
		case CENTER:
			style = TieConstants.VERTICAL_ALIGN_CENTER;
			break;
		case BOTTOM:
			style = TieConstants.VERTICAL_ALIGN_BOTTOM;
			break;
		default:
			break;
		}
		return style;
	}

	// additionalWidth is to calculate extra width outside spreadsheet for
	// layout purpose
	/**
	 * Calc total width.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param firstCol
	 *            the first col
	 * @param lastCol
	 *            the last col
	 * @param additionalWidth
	 *            the additional width
	 * @return the int
	 */
	// e.g. lineNumberColumnWidth and addRowColumnWidth
	public static int calcTotalWidth(final Sheet sheet1, final int firstCol,
			final int lastCol, final int additionalWidth) {
	
		int totalWidth = additionalWidth;
		for (int i = firstCol; i <= lastCol; i++) {
			totalWidth += sheet1.getColumnWidth(i);
		}
		return totalWidth;
	}

	/**
	 * Calc total height.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param firstRow
	 *            the first row
	 * @param lastRow
	 *            the last row
	 * @param additionalHeight
	 *            the additional height
	 * @return the int
	 */
	public static int calcTotalHeight(final Sheet sheet1,
			final int firstRow, final int lastRow,
			final int additionalHeight) {
	
		int totalHeight = additionalHeight;
		for (int i = firstRow; i <= lastRow; i++) {
	
			totalHeight += sheet1.getRow(i).getHeight();
		}
		return totalHeight;
	}

	/**
	 * Setup cell style.
	 *
	 * @param wb
	 *            the wb
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param rowHeight
	 *            the row height
	 */
	public static void setupCellStyle(final Workbook wb, final FacesCell fcell,
			final Cell poiCell, final float rowHeight) {
	
		CellStyle cellStyle = poiCell.getCellStyle();
		if ((cellStyle != null) && (!cellStyle.getLocked())) {
			// not locked
			if (fcell.getInputType().isEmpty()) {
				fcell.setInputType(CellStyleUtility.getInputTypeFromCellType(poiCell));
			}
			if (fcell.getControl().isEmpty()
					&& (!fcell.getInputType().isEmpty())) {
				fcell.setControl("text");
			}
			setInputStyleBaseOnInputType(fcell, poiCell);
	
		}
		String webStyle = getCellStyle(wb, poiCell, fcell.getInputType())
				+ getCellFontStyle(wb, poiCell)
				+ getRowStyle(wb, poiCell, fcell.getInputType(), rowHeight);
		fcell.setStyle(webStyle);
		fcell.setColumnStyle(getColumnStyle(wb, fcell, poiCell, rowHeight));
	}

	/**
	 * set up Input Style parameter for input number component which need those
	 * parameters to make it work. e.g. symbol, symbol position, decimal places.
	 * 
	 * @param fcell
	 *            faces cell
	 * @param poiCell
	 *            poi cell
	 */
	static void setInputStyleBaseOnInputType(final FacesCell fcell,
			final Cell poiCell) {
	
		if ((fcell == null) || fcell.getInputType().isEmpty()) {
			return;
		}
	
		switch (fcell.getInputType()) {
		case TieConstants.CELL_INPUT_TYPE_PERCENTAGE:
			fcell.setSymbol("%");
			fcell.setSymbolPosition("p");
			fcell.setDecimalPlaces(CellStyleUtility.getDecimalPlacesFromFormat(poiCell));
			break;
	
		case TieConstants.CELL_INPUT_TYPE_INTEGER:
			fcell.setDecimalPlaces((short) 0);
			break;
	
		case TieConstants.CELL_INPUT_TYPE_DOUBLE:
			fcell.setDecimalPlaces(CellStyleUtility.getDecimalPlacesFromFormat(poiCell));
			fcell.setSymbol(CellStyleUtility.getSymbolFromFormat(poiCell));
			fcell.setSymbolPosition(CellStyleUtility.getSymbolPositionFromFormat(poiCell));
			break;
		default:
			break;
		}
	
	}

	/**
	 * Gets the alignment from cell type.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @return the alignment from cell type
	 */
	private static String getAlignmentFromCellType(final Cell poiCell) {
	
		switch (poiCell.getCellTypeEnum()) {
		case FORMULA:
		case NUMERIC:
			return "text-align: right;";
		default:
			return "";
		}
	
	}

	/**
	 * Gets the input type from cell type.
	 *
	 * @param cell
	 *            the cell
	 * @return the input type from cell type
	 */
	private static String getInputTypeFromCellType(final Cell cell) {
	
		String inputType = TieConstants.CELL_INPUT_TYPE_TEXT;
		if (cell.getCellTypeEnum() == CellType.NUMERIC) {
			inputType = TieConstants.CELL_INPUT_TYPE_DOUBLE;
		}
		CellStyle style = cell.getCellStyle();
		if (style != null) {
			int formatIndex = style.getDataFormat();
			String formatString = style.getDataFormatString();
			if (DateUtil.isADateFormat(formatIndex, formatString)) {
				inputType = TieConstants.CELL_INPUT_TYPE_DATE;
			} else {
				if (isAPercentageCell(formatString)) {
					inputType = TieConstants.CELL_INPUT_TYPE_PERCENTAGE;
				}
			}
		}
		return inputType;
	}

	/**
	 * get decimal places from format string e.g. 0.00 will return 2
	 *
	 * @param cell
	 *            the cell
	 * @return decimal places of the formatted string
	 */
	private static short getDecimalPlacesFromFormat(final Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return 0;
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return 0;
		}
		int ipos = formatString.indexOf('.');
		if (ipos < 0) {
			return 0;
		}
		short counter = 0;
		for (int i = ipos + 1; i < formatString.length(); i++) {
			if (formatString.charAt(i) == '0') {
				counter++;
			} else {
				break;
			}
		}
		return counter;
	}

	/**
	 * get symbol from format string e.g. [$CAD] #,##0.00 will return CAD. While
	 * $#,##0.00 will return $
	 *
	 * @param cell
	 *            the cell
	 * @return symbol of the formatted string
	 */
	private static String getSymbolFromFormat(final Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return null;
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return null;
		}
		if (formatString.indexOf(TieConstants.CELL_ADDR_PRE_FIX) < 0) {
			return null;
		}
		int ipos = formatString.indexOf("[$");
		if (ipos < 0) {
			// only $ found, then return default dollar symbol
			return "$";
		}
		// return specified dollar symbol
		return formatString.substring(ipos + 2,
				formatString.indexOf(']', ipos));
	}

	/**
	 * get symbol position from format string e.g. [$CAD] #,##0.00 will return
	 * p. While #,##0.00 $ will return s
	 *
	 * @param cell
	 *            the cell
	 * @return symbol position of the formatted string
	 */
	private static String getSymbolPositionFromFormat(final Cell cell) {
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			return "p";
		}
		String formatString = style.getDataFormatString();
		if (formatString == null) {
			return "p";
		}
		int symbolpos = formatString.indexOf('$');
		int numberpos = formatString.indexOf('#');
		if (numberpos < 0) {
			numberpos = formatString.indexOf('0');
		}
		if (symbolpos < numberpos) {
			return "p";
		} else {
			return "s";
		}
	}

	/**
	 * Check weather the cell is percentage formatted.
	 *
	 * @param formatString
	 *            the format string
	 * @return true if it's percentage formatted
	 */
	private static boolean isAPercentageCell(final String formatString) {
	
		if (formatString == null) {
			return false;
		}
		return formatString.indexOf('%') >= 0;
	
	}



}
