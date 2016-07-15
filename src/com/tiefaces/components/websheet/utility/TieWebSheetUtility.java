/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.utility;

import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

import com.tiefaces.components.websheet.configuration.ConfigCommand;
import com.tiefaces.components.websheet.configuration.ConfigRange;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TieWebSheetUtility {

	private static final Logger log = Logger.getLogger(Thread
			.currentThread().getStackTrace()[0].getClassName());

	public static String GetExcelColumnName(int number) {
		String converted = "";
		// Repeatedly divide the number by 26 and convert the
		// remainder into the appropriate letter.
		while (number >= 0) {
			int remainder = number % 26;
			converted = (char) (remainder + 'A') + converted;
			number = (number / 26) - 1;
		}

		return converted;
	}

	/**
	 * return full name for cell with sheet name and $ format e.g. Sheet1$A$1
	 * 
	 * @param sheet1
	 *            sheet
	 * @param cell
	 *            cell
	 * @return String full cell reference name
	 */

	public static String getFullCellRefName(Sheet sheet1, Cell cell) {
		if ((sheet1 != null) && (cell != null)) {
			return sheet1.getSheetName() + "!$"
					+ GetExcelColumnName(cell.getColumnIndex()) + "$"
					+ (cell.getRowIndex() + 1);
		}
		return null;
	}

	/**
	 * return full name for cell with sheet name and $ format e.g. Sheet1$A$1
	 * 
	 * @param sheet1
	 *            sheet
	 * @param cell
	 *            cell
	 * @return String full cell reference name
	 */

	public static String getFullCellRefName(String sheetName,
			int rowIndex, int colIndex) {
		if (sheetName != null) {
			return sheetName + "!$" + GetExcelColumnName(colIndex) + "$"
					+ (rowIndex + 1);
		}
		return null;
	}

	/**
	 * return sheet name from cell full name e.g. return Sheet1 from Sheet1$A$1
	 * 
	 * @param String
	 *            fullName
	 * @return String Sheet Name
	 */

	public static String getSheetNameFromFullCellRefName(String fullName) {
		if ((fullName != null) && (fullName.contains("!"))) {
			return fullName.substring(0, fullName.indexOf("!"));
		}
		return null;
	}

	/**
	 * remove sheet name from cell full name e.g. return $A$1 from Sheet1$A$1
	 * 
	 * @param String
	 *            full name
	 * @return remove Sheet Name from full name
	 */

	public static String removeSheetNameFromFullCellRefName(
			String fullName) {
		if ((fullName != null) && (fullName.contains("!"))) {
			return fullName.substring(fullName.indexOf("!") + 1);
		}
		return fullName;
	}

	// public static String convertIntToCol(int colNum) {
	// int col = colNum + 1;
	// String retVal = "";
	// int x = 0;
	// for (int n = (int)(Math.log(25*(col + 1))/Math.log(26)) - 1; n >= 0; n--)
	// {
	// x = (int)((Math.pow(26,(n + 1)) - 1) / 25 - 1);
	// if (col > x)
	// retVal += (char) ((int)((col - x - 1) / Math.pow(26, n)) % 26 + 65);
	// }
	// return retVal;
	// }

	public static int convertColToInt(String col) {
		String name = col.toUpperCase();
		int number = 0;
		int pow = 1;
		for (int i = name.length() - 1; i >= 0; i--) {
			number += (name.charAt(i) - 'A' + 1) * pow;
			pow *= 26;
		}

		return number - 1;
	}

	public static Cell getCellByReference(String cellRef, Sheet sheet) {

		// Sheet sheet =
		// wb.getSheet(sheetConfigMap.get(currentTabName).getSheetName());
		Cell c = null;
		try {
			CellReference ref = new CellReference(cellRef);
			Row r = sheet.getRow(ref.getRow());
			if (r != null) {
				c = r.getCell(ref.getCol(), Row.CREATE_NULL_AS_BLANK);
			}
		} catch (Exception ex) {
			// use log.debug because mostly it's expected
			log.severe("WebForm WebFormHelper getCellByReference cellRef = "
					+ cellRef + "; error = " + ex.getLocalizedMessage());
		}
		return c;
	}

	// Each cell conatins a fixed number of co-ordinate points; this number
	// does not vary with row height or column width or with font. These two
	// constants are defined below.
	public static final int TOTAL_COLUMN_COORDINATE_POSITIONS = 1023; // MB
	public static final int TOTAL_ROW_COORDINATE_POSITIONS = 255; // MB
	// The resoultion of an image can be expressed as a specific number
	// of pixels per inch. Displays and printers differ but 96 pixels per
	// inch is an acceptable standard to beging with.
	public static final int PIXELS_PER_INCH = 96; // MB
	// Cnstants that defines how many pixels and points there are in a
	// millimetre. These values are required for the conversion algorithm.
	public static final double PIXELS_PER_MILLIMETRES = 3.78; // MB
	public static final double POINTS_PER_MILLIMETRE = 2.83; // MB
	// The column width returned by HSSF and the width of a picture when
	// positioned to exactly cover one cell are different by almost exactly
	// 2mm - give or take rounding errors. This constant allows that
	// additional amount to be accounted for when calculating how many
	// celles the image ought to overlie.
	public static final double CELL_BORDER_WIDTH_MILLIMETRES = 2.0D; // MB
	public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
	public static final int UNIT_OFFSET_LENGTH = 7;
	public static final int[] UNIT_OFFSET_MAP = new int[] { 0, 36, 73,
			109, 146, 182, 219 };
	public static final short EXCEL_ROW_HEIGHT_FACTOR = 20;
	public static final int EMU_PER_MM = 36000;
	public static final int EMU_PER_POINTS = 12700;

	/**
	 * pixel units to excel width units(units of 1/256th of a character width)
	 * 
	 * @param pxs
	 * @return
	 */
	public static short pixel2WidthUnits(int pxs) {
		short widthUnits = (short) (EXCEL_COLUMN_WIDTH_FACTOR * (pxs / UNIT_OFFSET_LENGTH));
		widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)];
		return widthUnits;
	}

	/**
	 * excel width units(units of 1/256th of a character width) to pixel units.
	 *
	 * @param widthUnits
	 * @return
	 */
	public static int widthUnits2Pixel(int widthUnits) {
		int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR)
				* UNIT_OFFSET_LENGTH;
		int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
		pixels += Math
				.round(offsetWidthUnits
						/ ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));
		return pixels;
	}

	public static int heightUnits2Pixel(short heightUnits) {
		int pixels = (heightUnits / EXCEL_ROW_HEIGHT_FACTOR);
		int offsetHeightUnits = heightUnits % EXCEL_ROW_HEIGHT_FACTOR;
		pixels += Math
				.round((float) offsetHeightUnits
						/ ((float) EXCEL_COLUMN_WIDTH_FACTOR
								/ UNIT_OFFSET_LENGTH / 2));
		pixels += (Math.floor(pixels / 14) + 1) * 4;

		return pixels;
	}

	/**
	 * Convert Excels width units into millimetres.
	 *
	 * @param widthUnits
	 *            The width of the column or the height of the row in Excels
	 *            units.
	 * @return A primitive double that contains the columns width or rows height
	 *         in millimetres.
	 */
	public static double widthUnits2Millimetres(short widthUnits) {
		return (widthUnits2Pixel(widthUnits) / PIXELS_PER_MILLIMETRES);
	}

	/**
	 * Convert into millimetres Excels width units..
	 *
	 * @param millimetres
	 *            A primitive double that contains the columns width or rows
	 *            height in millimetres.
	 * @return A primitive int that contains the columns width or rows height in
	 *         Excels units.
	 */
	public static int millimetres2WidthUnits(double millimetres) {
		return (pixel2WidthUnits((int) (millimetres * PIXELS_PER_MILLIMETRES)));
	}

	public static int pointsToPixels(double points) {
		return (int) Math.round(points / 72D * PIXELS_PER_INCH);
	}

	public static double pointsToMillimeters(double points) {
		return points / 72D * 25.4;
	}

	// Below are utility functions for colors searched from internet. Maybe
	// could used for future.
	// public static String getColorHexString(Color color)
	// {
	// if (color instanceof HSSFColor)
	// {
	// return getHSSFColorHexString((HSSFColor) color);
	// }
	// else if (color instanceof XSSFColor)
	// {
	// return getXSSFColorHexString((XSSFColor) color);
	// }
	// else if (color == null)
	// {
	// return "null";
	// }
	// else
	// {
	// throw new IllegalArgumentException("Unexpected type of Color: " +
	// color.getClass().getName());
	// }
	// }
	//
	// /**
	// * Get the hex string for a <code>HSSFColor</code>. Moved from test code.
	// * @param hssfColor A <code>HSSFColor</code>.
	// * @return The hex string.
	// */
	// private static String getHSSFColorHexString(HSSFColor hssfColor)
	// {
	//
	// if (hssfColor == null) return "000000";
	// short[] shorts = hssfColor.getTriplet();
	// StringBuilder hexString = new StringBuilder();
	// for (short s : shorts)
	// {
	// String twoHex = Integer.toHexString(0x000000FF & s);
	// if (twoHex.length() == 1)
	// hexString.append('0');
	// hexString.append(twoHex);
	// }
	// return hexString.toString();
	// }
	//
	//
	// /**
	// * Get the hex string for a <code>XSSFColor</code>.
	// * @param xssfColor A <code>XSSFColor</code>.
	// * @return The hex string.
	// */
	// private static String getXSSFColorHexString(XSSFColor xssfColor)
	// {
	// if (xssfColor == null)
	// return "000000";
	//
	// byte[] bytes;
	// // As of Apache POI 3.8, there are Bugs 51236 and 52079 about font
	// // color where somehow black and white get switched. It appears to
	// // have to do with the fact that black and white "theme" colors get
	// // flipped. Be careful, because XSSFColor(byte[]) does NOT call
	// // "correctRGB", but XSSFColor.setRgb(byte[]) DOES call it, and so
	// // does XSSFColor.getRgb(byte[]).
	// // The private method "correctRGB" flips black and white, but no
	// // other colors. However, correctRGB is its own inverse operation,
	// // i.e. correctRGB(correctRGB(rgb)) yields the same bytes as rgb.
	// // XSSFFont.setColor(XSSFColor) calls "getRGB", but
	// // XSSFCellStyle.set[Xx]BorderColor and
	// // XSSFCellStyle.setFill[Xx]Color do NOT.
	// // Solution: Correct the font color on the way out for themed colors
	// // only. For unthemed colors, bypass the "correction".
	// if (xssfColor.getCTColor().isSetTheme())
	// bytes = xssfColor.getRgb();
	// else
	// bytes = xssfColor.getCTColor().getRgb();
	// // End of workaround for Bugs 51236 and 52079.
	// if (bytes == null)
	// {
	// // Indexed Color - like HSSF
	// HSSFColor hColor =
	// ExcelColor.getHssfColorByIndex(xssfColor.getIndexed());
	// if (hColor != null)
	// return
	// getHSSFColorHexString(ExcelColor.getHssfColorByIndex(xssfColor.getIndexed()));
	// else
	// return "000000";
	// }
	// if (bytes.length == 4)
	// {
	// // Lose the alpha.
	// bytes = new byte[] {bytes[1], bytes[2], bytes[3]};
	// }
	// StringBuilder hexString = new StringBuilder();
	// for (byte b : bytes)
	// {
	// String twoHex = Integer.toHexString(0x000000FF & b);
	// if (twoHex.length() == 1)
	// hexString.append('0');
	// hexString.append(twoHex);
	// }
	// return hexString.toString();
	// }
	//
	// /**
	// * <p>Returns a <code>String</code> formatted in the following way:</p>
	// *
	// * <code>" at " + cellReference</code>
	// *
	// * <p>e.g. <code>" at Sheet2!C3"</code>.</p>
	// * @param cell The <code>Cell</code>
	// * @return The formatted location string.
	// * @since 0.7.0
	// */
	// public static String getCellLocation(Cell cell)
	// {
	// if (cell == null)
	// return "";
	// return " at " + new CellReference(
	// cell.getSheet().getSheetName(), cell.getRowIndex(),
	// cell.getColumnIndex(), false, false).toString();
	// }
	//
	// /**
	// * Determines the proper POI <code>Color</code>, given a string value that
	// * could be a color name, e.g. "aqua", or a hex string, e.g. "#FFCCCC".
	// *
	// * @param workbook A <code>Workbook</code>, used only to determine whether
	// * to create an <code>HSSFColor</code> or an <code>XSSFColor</code>.
	// * @param value The color value, which could be one of the 48 pre-defined
	// * color names, or a hex value of the format "#RRGGBB".
	// * @return A <code>Color</code>, or <code>null</code> if an invalid color
	// * name was given.
	// */
	// public static Color getColor(Workbook workbook, String value)
	// {
	// debug("getColor: " + value);
	// Color color = null;
	// if (workbook instanceof HSSFWorkbook)
	// {
	// // Create an HSSFColor.
	// if (value.startsWith("#"))
	// {
	// ExcelColor best = ExcelColor.AUTOMATIC;
	// int minDist = 255 * 3;
	// String strRed = value.substring(1, 3);
	// String strGreen = value.substring(3, 5);
	// String strBlue = value.substring(5, 7);
	// int red = Integer.parseInt(strRed, 16);
	// int green = Integer.parseInt(strGreen, 16);
	// int blue = Integer.parseInt(strBlue, 16);
	// // Hex value. Find the closest defined color.
	// for (ExcelColor excelColor : ExcelColor.values())
	// {
	// int dist = excelColor.distance(red, green, blue);
	// if (dist < minDist)
	// {
	// best = excelColor;
	// minDist = dist;
	// }
	// }
	// color = best.getHssfColor();
	// debug("  Best HSSFColor found: " + color);
	// }
	// else
	// {
	// // Treat it as a color name.
	// try
	// {
	// ExcelColor excelColor = ExcelColor.valueOf(value);
	// if (excelColor != null)
	// color = excelColor.getHssfColor();
	// debug("  HSSFColor name matched: " + value);
	// }
	// catch (IllegalArgumentException e)
	// {
	// debug("  HSSFColor name not matched: " + e.toString());
	// }
	// }
	// }
	// else // XSSFWorkbook
	// {
	// // Create an XSSFColor.
	// if (value.startsWith("#") && value.length() == 7)
	// {
	// // Create the corresponding XSSFColor.
	// color = new XSSFColor(new byte[] {
	// Integer.valueOf(value.substring(1, 3), 16).byteValue(),
	// Integer.valueOf(value.substring(3, 5), 16).byteValue(),
	// Integer.valueOf(value.substring(5, 7), 16).byteValue()
	// });
	// debug("  XSSFColor created: " + color);
	// }
	// else
	// {
	// // Create an XSSFColor from the RGB values of the desired color.
	// try
	// {
	// ExcelColor excelColor = ExcelColor.valueOf(value);
	// if (excelColor != null)
	// {
	// color = new XSSFColor(new byte[]
	// {(byte) excelColor.getRed(), (byte) excelColor.getGreen(), (byte)
	// excelColor.getBlue()}
	// );
	// }
	// debug("  XSSFColor name matched: " + value);
	// }
	// catch (IllegalArgumentException e)
	// {
	// debug("  XSSFColor name not matched: " + e.toString());
	// }
	// }
	// }
	// return color;
	// }

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * class Helper * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * Helper methods and miscellaneous tools for
	 * Extension of Apache POI * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * To do: [ ] work on date handling [ ]
	 * work on CSV parsing to further generalize [ ] String < - > Date * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */

	public final static String DATE_REGEX_4_DIGIT_YEAR = "("
			+ "(19|20)[0-9]{2}" + "([-/.\\\\]{1})"
			+ "[0?[1-9]|[1-9]|1[012]]{1,2}" + "\\3"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + ")" + "|"
			+ "(" + "[0?[1-9]|[1-9]|1[012]]{1,2}" + "([-/.\\\\]{1})"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + "\\6"
			+ "(19|20)[0-9]{2}" + ")";
	public final static String DATE_REGEX_2_DIGIT_YEAR = "(" + "[0-9]{2}"
			+ "([-/.\\\\]{1})" + "[0?[1-9]|[1-9]|1[012]]{1,2}" + "\\3"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + ")" + "|"
			+ "(" + "[0?[1-9]|[1-9]|1[012]]{1,2}" + "([-/.\\\\]{1})"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + "\\6"
			+ "[0-9]{2}" + ")";

	/* Date Handling tools */
	public static boolean isWeekend(Calendar date) {
		return date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static Calendar getToday() {
		return Calendar.getInstance();
	}

	public static Calendar getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -1);
		return date;
	}

	public static Calendar getLastWeekday() {
		return getLastWeekday(getYesterday());
	}

	public static Calendar getLastWeekday(Calendar date) {
		Calendar _date = Calendar.getInstance();
		_date.setTime(date.getTime());
		while (isWeekend(_date)) {
			_date.add(Calendar.DATE, -1);
		}
		return _date;
	}

	public static boolean isDate(String s) {
		Pattern pattern = Pattern.compile(DATE_REGEX_4_DIGIT_YEAR);
		String[] terms = s.split(" ");
		Matcher matcher;
		for (String term : terms) {
			matcher = pattern.matcher(term);
			while (matcher.matches()) {
				return true;
			}
		}
		pattern = Pattern.compile(DATE_REGEX_2_DIGIT_YEAR);
		terms = s.split(" ");
		for (String term : terms) {
			matcher = pattern.matcher(term);
			while (matcher.matches()) {
				return true;
			}
		}
		return false;
	}

	public static String parseDate(String entry) {
		Pattern pattern = Pattern.compile(DATE_REGEX_4_DIGIT_YEAR);
		String[] terms = entry.split(" ");
		Matcher matcher;
		for (String term : terms) {
			matcher = pattern.matcher(term);
			while (matcher.matches()) {
				return matcher.group();
			}
		}
		pattern = Pattern.compile(DATE_REGEX_2_DIGIT_YEAR);
		terms = entry.split(" ");
		for (String term : terms) {
			matcher = pattern.matcher(term);
			while (matcher.matches()) {
				return matcher.group();
			}
		}
		return "";
	}

	public static int compareDates(String date1, String date2,
			String format) {
		if (format.length() == 0) {
			return 0;
		}
		char c;
		int m_start = 0, m_count = 0, d_start = 0, d_count = 0, y_start = 0, y_count = 0;
		for (int i = 0; i < format.length(); i++) {
			c = format.charAt(i);
			if (c == 'm') {
				if (m_count == 0) {
					m_start = i;
				}
				m_count++;
			}
			if (c == 'd') {
				if (d_count == 0) {
					d_start = i;
				}
				d_count++;
			}
			if (c == 'y') {
				if (y_count == 0) {
					y_start = i;
				}
				y_count++;
			}
		}
		if (y_count > 0) {
			if (Integer.parseInt(date1.substring(y_start, y_start
					+ y_count)) > Integer.parseInt(date2.substring(
					y_start, y_start + y_count))) {
				return 1;
			} else if (Integer.parseInt(date1.substring(y_start, y_start
					+ y_count)) < Integer.parseInt(date2.substring(
					y_start, y_start + y_count))) {
				return -1;
			}
		}
		if (m_count > 0) {
			if (Integer.parseInt(date1.substring(m_start, m_start
					+ m_count)) > Integer.parseInt(date2.substring(
					m_start, m_start + m_count))) {
				return 1;
			} else if (Integer.parseInt(date1.substring(m_start, m_start
					+ m_count)) < Integer.parseInt(date2.substring(
					m_start, m_start + m_count))) {
				return -1;
			}
		}
		if (d_count > 0) {
			if (Integer.parseInt(date1.substring(d_start, d_start
					+ d_count)) > Integer.parseInt(date2.substring(
					d_start, d_start + d_count))) {
				return 1;
			} else if (Integer.parseInt(date1.substring(d_start, d_start
					+ d_count)) < Integer.parseInt(date2.substring(
					d_start, d_start + d_count))) {
				return -1;
			}
		}
		return 0;
	}

	public static Calendar stringToCalendar(String date) {
		return stringToCalendar(date, "mm/dd/yy");
	}

	public static Calendar stringToCalendar(String date, String pattern) {
		return null;
	}

	public static Date stringToDate(String date) {
		return stringToDate(date, "mm/dd/yy");
	}

	public static Date stringToDate(String date, String pattern) {
		return null;
	}

	/* Parsers */
	public static ArrayList<String> parseCSVLine(String CSVLine,
			char delimChar, char quotChar) {
		char itr;
		boolean inQuotedValue = false;
		String buffer = "";
		ArrayList<String> parsedLine = new ArrayList<String>();
		for (int i = 0; i < CSVLine.length(); i++) {
			itr = CSVLine.charAt(i);
			if (itr == delimChar) {
				if (!inQuotedValue) {
					parsedLine.add(buffer);
					buffer = "";
				}
			} else if (itr == quotChar) {
				inQuotedValue = !inQuotedValue;
			} else {
				buffer += itr;
			}
		}
		if (buffer.length() > 0) {
			parsedLine.add(buffer);
		}
		String entry;
		for (int i = 0; i < parsedLine.size(); i++) {
			entry = parsedLine.get(i);
			entry.trim();
			if (entry.startsWith("(") && entry.endsWith(")")
					&& isNumeric(entry.substring(1, entry.length() - 1))) {
				parsedLine.set(i,
						"-" + entry.substring(1, entry.length() - 1));
			}
		}
		return parsedLine;
	}

	public static int max(int a, int b) {
		if (a > b) {
			return a;
		} else {
			return b;
		}
	}

	public static int min(int a, int b) {
		if (a < b) {
			return a;
		} else {
			return b;
		}
	}

	public static int minPos(int a, int b) {
		if (a >= 0 && b >= 0) {
			return min(a, b);
		} else if (a >= 0 && b < 0) {
			return a;
		} else if (b >= 0 && a < 0) {
			return b;
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public static String safeSubstring(String str, int i, int f) {
		try {
			i = max(0, i);
			i = min(i, str.length());
			// Limit to 0
			f = max(0, f);
			f = min(f, str.length());
			return str.substring(i, f);
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isNumeric(String str) {
		String s = str;
		if (s.startsWith("-")) {
			s = s.substring(1);
		}
		char c;
		int i, L = s.length(), sinceLastComma = 0;
		boolean decimalHit = false, commaHit = false;
		for (i = 0; i < L; i++) {
			c = s.charAt(i);
			if (c < '0' || c > '9') {
				if (c == '.' && !decimalHit) {
					decimalHit = true;
					if (commaHit && sinceLastComma != 3) {
						return false;
					}
					continue;
				} else if (c == ',' && !decimalHit) {
					if (commaHit) {
						if (sinceLastComma != 3) {
							return false;
						}
						sinceLastComma = 0;
					}
					commaHit = true;
					continue;
				}
				return false;
			} else {
				if (commaHit)
					sinceLastComma++;
			}
		}
		return true;
	}

	public static boolean isUpperAlpha(char c) {
		return 'A' <= c && 'Z' >= c;
	}

	public static boolean isLowerAlpha(char c) {
		return 'a' <= c && 'z' >= c;
	}

	public static boolean isAlpha(char c) {
		return isUpperAlpha(c) || isLowerAlpha(c);
	}

	public static boolean isNumeric(char c) {
		return '0' <= c && '9' >= c;
	}

	public static String formatYesterday() {
		return formatDate("MM.dd.yy", 0, 0, -1);
	}

	public static String formatYesterday(String format) {
		return formatDate(format, 0, 0, -1);
	}

	public static String formatToday() {
		return formatDate("MM.dd.yy", 0, 0, 0);
	}

	public static String formatToday(String format) {
		return formatDate(format, 0, 0, 0);
	}

	public static String formatLastWeekday() {
		return formatDate("MM.dd.yy", getLastWeekday());
	}

	public static String formatLastWeekday(String format) {
		return formatDate(format, getLastWeekday());
	}

	public static String formatDate(String format, int offset_years,
			int offset_months, int offset_days) {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.YEAR, offset_years);
		date.add(Calendar.MONTH, offset_months);
		date.add(Calendar.DATE, offset_days);
		return formatDate(format, date);
	}

	public static String formatDate(Calendar c) {
		return (new SimpleDateFormat("MM.dd.yy")).format(c.getTime());
	}

	public static String formatDate(String format, Calendar c) {
		return (new SimpleDateFormat(format)).format(c.getTime());
	}

	public static String parseToRegExcel(String raw) {
		char c;
		String buf = "", regExcel = "";
		for (int i = 0; i < raw.length(); i++) {
			c = raw.charAt(i);
			if (isNumeric(c) && i + 1 < raw.length()
					&& isNumeric(raw.charAt(i + 1))) {
				while (isNumeric(c)) {
					buf += c;
					i++;
					c = raw.charAt(i);
				}
				regExcel += parseRegExcelNumber(buf) + c;
				buf = "";
			} else {
				regExcel += c;
			}
		}
		return regExcel;
	}

	private static String parseRegExcelNumber(String number) {
		String today_d = formatToday("dd"), today_m = formatToday("MM"), today_y = formatToday("yyyy");
		String regex = "(" + today_y.substring(0, 2) + ")?"
				+ today_y.substring(2) + today_m + today_d;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(number);
		String buf = "";
		if (m.find()) {
			if (m.group().length() == 6) {
				buf = (m.start() == 0 ? "" : "<NUMBER>")
						+ "<DATE:TODAY:YYMMDD>"
						+ (m.end() == number.length() ? "" : "<NUMBER>");
			} else {
				buf = (m.start() == 0 ? "" : "<NUMBER>")
						+ "<DATE:TODAY:YYYYMMDD>"
						+ (m.end() == number.length() ? "" : "<NUMBER>");
			}
			return buf;
		}
		String yesterday_d = formatYesterday("dd"), yesterday_m = formatYesterday("MM"), yesterday_y = formatYesterday("yyyy");
		regex = "(" + yesterday_y.substring(0, 2) + ")?"
				+ yesterday_y.substring(2) + yesterday_m + yesterday_d;
		p = Pattern.compile(regex);
		m = p.matcher(number);
		buf = "";
		if (m.find()) {
			if (m.group().length() == 6) {
				buf = (m.start() == 0 ? "" : "<NUMBER>")
						+ "<DATE:YESTERDAY:YYMMDD>"
						+ (m.end() == number.length() ? "" : "<NUMBER>");
			} else {
				buf = (m.start() == 0 ? "" : "<NUMBER>")
						+ "<DATE:YESTERDAY:YYYYMMDD>"
						+ (m.end() == number.length() ? "" : "<NUMBER>");
			}
			return buf;
		}
		return "<NUMBER>";

	}

	public static String parseToRegex(String raw) {
		char c;
		int depth = 0;
		String buf = "", regex = "";
		for (int i = 0; i < raw.length(); i++) {
			c = raw.charAt(i);
			if (i > 0 && c == '.' && raw.charAt(i - 1) != '\\') {
				buf = "\\.";
			} else if (c == '<') {
				depth++;
				buf = "";
				while (depth > 0 && i < raw.length()) {
					i++;
					c = raw.charAt(i);
					if (c == '<') {
						depth++;
						buf += c;
					} else if (c == '>') {
						depth--;
						if (depth == 0) {
							break;
						}
					} else {
						buf += c;
					}
				}
				if (buf.equalsIgnoreCase("NUMBER")) {
					buf = "\\d+";
				} else if (buf.toLowerCase().contains(
						"DATE".toLowerCase())) {
					buf = "{$@" + buf + "@$}";
				} else if (buf.equalsIgnoreCase("RANDOM")) {
					buf = ".*?";
				} else {
					buf = ".*";
				}
			} else {
				buf += c;
			}
			regex += buf;
			buf = "";
		}
		int begin, end;
		String format;
		String ref_regex = regex.toUpperCase(), ref_format;
		begin = ref_regex.indexOf("{$@DATE:") + ("{$@DATE:").length();
		end = ref_regex.indexOf("@$}", begin);
		while (begin > 0 && end > 0) {
			format = regex.substring(begin, end);
			ref_format = format.toUpperCase();
			if (ref_format.startsWith("TODAY:")) {
				begin += "TODAY:".length();
				format = regex.substring(begin, end);
				format = format.replace('m', 'M').replace('D', 'd')
						.replace('Y', 'y');
				regex = regex.substring(0,
						begin - ("{$@DATE:TODAY:").length())
						+ formatToday(format)
						+ regex.substring(end + ("@$}").length());
			} else if (ref_format.contains("YESTERDAY")) {
				begin += ("YESTERDAY:").length();
				format = regex.substring(begin, end);
				format = format.replace('m', 'M').replace('D', 'd')
						.replace('Y', 'y');
				regex = regex.substring(0,
						begin - ("{$@DATE:YESTERDAY:").length())
						+ formatYesterday(format)
						+ regex.substring(end + ("@$}").length());
			} else if (ref_format.contains("LASTWEEKDAY")) {
				begin += ("LASTWEEKDAY:").length();
				format = regex.substring(begin, end);
				format = format.replace('m', 'M').replace('D', 'd')
						.replace('Y', 'y');
				regex = regex.substring(0, begin
						- ("{$@DATE:LASTWEEKDAY:").length())
						+ formatLastWeekday(format)
						+ regex.substring(end + ("@$}").length());
			} else {
				format = format.replace('m', 'M').replace('D', 'd')
						.replace('Y', 'y');
				regex = regex.substring(0, begin - ("{$@DATE:".length()))
						+ formatToday(format)
						+ regex.substring(end + ("@$}").length());
			}
			begin = regex.indexOf("{$@DATE:") + ("{$@DATE:").length();
			end = regex.indexOf("@$}", begin);
		}
		return regex;
	}

	public static void setObjectProperty(Object obj, String propertyName,
			String propertyValue, boolean ignoreNonExisting) {
		try {
			Method method = obj.getClass().getMethod(
					"set" + Character.toUpperCase(propertyName.charAt(0))
							+ propertyName.substring(1),
					new Class[] { String.class });
			method.invoke(obj, propertyValue);
		} catch (Exception e) {
			String msg = "failed to set property '" + propertyName
					+ "' to value '" + propertyValue + "' for object "
					+ obj;
			if (ignoreNonExisting) {
				log.info(msg);
			} else {
				log.warning(msg);
				throw new IllegalArgumentException(e);
			}
		}
	}

	public static int cellCompareTo(Cell thisCell, Cell otherCell) {
		int r = thisCell.getRowIndex() - otherCell.getRowIndex();
		if (r != 0)
			return r;

		r = thisCell.getColumnIndex() - otherCell.getColumnIndex();
		if (r != 0)
			return r;

		return 0;
	}

	public static boolean insideRange(ConfigRange child,
			ConfigRange parent) {

		if ((cellCompareTo(child.getFirstRowRef(), parent.getFirstRowRef()) >= 0)
				&& (cellCompareTo(child.getLastRowPlusRef(),
						parent.getLastRowPlusRef()) <= 0)) {
			return true;
		}
		return false;
	}

	/**
	 * return the last column of the sheet.
	 * @param sheet sheet.
	 * @return last column number (A column will return 0).
	 */
	public static int getSheetRightCol(final Sheet sheet) {

		try {
			if (sheet instanceof XSSFSheet) {
				XSSFSheet xsheet = (XSSFSheet) sheet;
				CTSheetDimension dimension = xsheet.getCTWorksheet()
						.getDimension();
				String sheetDimensions = dimension.getRef();
				if (sheetDimensions.indexOf(':')>0) {
					return CellRangeAddress.valueOf(sheetDimensions)
							.getLastColumn();
				} 
			} else if (sheet instanceof HSSFSheet) {
				HSSFSheet hsheet = (HSSFSheet) sheet;
				Field sheetField;
				sheetField = HSSFSheet.class.getDeclaredField("_sheet");
				sheetField.setAccessible(true);
				InternalSheet internalsheet = (InternalSheet) sheetField
						.get(hsheet);
				DimensionsRecord record = (DimensionsRecord) internalsheet
						.findFirstRecordBySid(DimensionsRecord.sid);
				if ((record != null) && (record.getLastCol()>0)) {
					return record.getLastCol() - 1;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	
	/**
	 * Build last cell for command Region.
	 * Last cell's row actually is the below the actually range.
	 * e.g. range is A1 : F8, then last cell is F9
	 * The reason is to hold the reference while the range expanding.
	 * i.e. one row expand to multiple rows. if last cell in the same row,
	 * then cannot hold the actual expand area with rowshift method.
	 * @param sheet
	 * @param rightCol
	 * @param lastRow
	 * @return cell lastCell 
	 */
	
	public static Cell buildLastCell(Sheet sheet, int rightCol, int lastRow) {
		
		if ((lastRow >=0) && (sheet != null) && (rightCol >= 0)) {
			Row row = sheet.getRow(lastRow + 1);
			if (row == null) {
				row = sheet.createRow(lastRow + 1);
			}
			return row.getCell(rightCol, Row.RETURN_BLANK_AS_NULL);
		}
		return null;
	}	
	
}
