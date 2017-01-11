/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.utility;

import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.configuration.ConfigRange;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class TieWebSheetUtility.
 */
public final class TieWebSheetUtility {

	/**
	 * hide constructor.
	 */
	private TieWebSheetUtility() {
		// not called
	}

	/** logger. */
	private static final Logger LOG = Logger.getLogger(
			TieWebSheetUtility.class.getName());
	
	/**
	 * Gets the excel column name.
	 *
	 * @param pnumber
	 *            the number
	 * @return the string
	 */
	public static String getExcelColumnName(final int pnumber) {
		String converted = "";
		// Repeatedly divide the number by 26 and convert the
		// remainder into the appropriate letter.
		int number = pnumber;
		while (number >= 0) {
			int remainder = number % TieConstants.EXCEL_LETTER_NUMBERS;
			converted = (char) (remainder + 'A') + converted;
			number = (number / TieConstants.EXCEL_LETTER_NUMBERS) - 1;
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

	public static String getFullCellRefName(final Sheet sheet1,
			final Cell cell) {
		if ((sheet1 != null) && (cell != null)) {
			return sheet1.getSheetName() + "!$"
					+ getExcelColumnName(cell.getColumnIndex()) + "$"
					+ (cell.getRowIndex() + 1);
		}
		return null;
	}

	/**
	 * return full name for cell with sheet name and $ format e.g. Sheet1$A$1
	 *
	 * @param sheetName
	 *            the sheet name
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @return String full cell reference name
	 */

	public static String getFullCellRefName(final String sheetName,
			final int rowIndex, final int colIndex) {
		if (sheetName != null) {
			return sheetName + "!$" + getExcelColumnName(colIndex) + "$"
					+ (rowIndex + 1);
		}
		return null;
	}

	/**
	 * return sheet name from cell full name e.g. return Sheet1 from Sheet1$A$1
	 *
	 * @param fullName
	 *            the full name
	 * @return String Sheet Name
	 */

	public static String getSheetNameFromFullCellRefName(
			final String fullName) {
		if ((fullName != null) && (fullName.contains("!"))) {
			return fullName.substring(0, fullName.indexOf("!"));
		}
		return null;
	}

	/**
	 * remove sheet name from cell full name e.g. return $A$1 from Sheet1$A$1
	 *
	 * @param fullName
	 *            the full name
	 * @return remove Sheet Name from full name
	 */

	public static String removeSheetNameFromFullCellRefName(
			final String fullName) {
		if ((fullName != null) && (fullName.contains("!"))) {
			return fullName.substring(fullName.indexOf("!") + 1);
		}
		return fullName;
	}

	/**
	 * Convert col to int.
	 *
	 * @param col
	 *            the col
	 * @return the int
	 */
	public static int convertColToInt(final String col) {
		String name = col.toUpperCase();
		int number = 0;
		int pow = 1;
		for (int i = name.length() - 1; i >= 0; i--) {
			number += (name.charAt(i) - 'A' + 1) * pow;
			pow *= TieConstants.EXCEL_LETTER_NUMBERS;
		}

		return number - 1;
	}

	/**
	 * Gets the cell by reference.
	 *
	 * @param cellRef
	 *            the cell ref
	 * @param sheet
	 *            the sheet
	 * @return the cell by reference
	 */
	public static Cell getCellByReference(final String cellRef,
			final Sheet sheet) {

		// Sheet sheet =
		// wb.getSheet(sheetConfigMap.get(currentTabName).getSheetName());
		Cell c = null;
		try {
			CellReference ref = new CellReference(cellRef);
			Row r = sheet.getRow(ref.getRow());
			if (r != null) {
				c = r.getCell(ref.getCol(),
						MissingCellPolicy.CREATE_NULL_AS_BLANK);
			}
		} catch (Exception ex) {
			// use log.debug because mostly it's expected
			LOG.severe("WebForm WebFormHelper getCellByReference cellRef = "
					+ cellRef + "; error = " + ex.getLocalizedMessage());
		}
		return c;
	}

	// Each cell conatins a fixed number of co-ordinate points; this number
	// does not vary with row height or column width or with font. These two
	/** The Constant TOTAL_COLUMN_COORDINATE_POSITIONS. */
	// constants are defined below.
	public static final int TOTAL_COLUMN_COORDINATE_POSITIONS = 1023; // MB

	/** The Constant TOTAL_ROW_COORDINATE_POSITIONS. */
	public static final int TOTAL_ROW_COORDINATE_POSITIONS = 255; // MB
	// The resoultion of an image can be expressed as a specific number
	// of pixels per inch. Displays and printers differ but 96 pixels per
	/** The Constant PIXELS_PER_INCH. */
	// inch is an acceptable standard to beging with.
	public static final int PIXELS_PER_INCH = 96; // MB
	/** The Constant MILLIMETERS_PER_INCH. */
	public static final double MILLIMETERS_PER_INCH = 25.4;
	/** The Constant POINTS_PER_INCH. */
	public static final double POINTS_PER_INCH = 72D;
	// Cnstants that defines how many pixels and points there are in a
	/** The Constant PIXELS_PER_MILLIMETRES. */
	// millimetre. These values are required for the conversion algorithm.
	public static final double PIXELS_PER_MILLIMETRES = 3.78; // MB

	/** The Constant POINTS_PER_MILLIMETRE. */
	public static final double POINTS_PER_MILLIMETRE = 2.83; // MB
	// The column width returned by HSSF and the width of a picture when
	// positioned to exactly cover one cell are different by almost exactly
	// 2mm - give or take rounding errors. This constant allows that
	// additional amount to be accounted for when calculating how many
	/** The Constant CELL_BORDER_WIDTH_MILLIMETRES. */
	// celles the image ought to overlie.
	public static final double CELL_BORDER_WIDTH_MILLIMETRES = 2.0D; // MB

	/** The Constant EXCEL_COLUMN_WIDTH_FACTOR. */
	public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;

	/** The Constant UNIT_OFFSET_LENGTH. */
	public static final int UNIT_OFFSET_LENGTH = 7;

	/** The Constant UNIT_OFFSET_MAP. */
	public static final int[] UNIT_OFFSET_MAP = new int[] { 0, 36, 73, 109,
			146, 182, 219 };

	/** The Constant EXCEL_ROW_HEIGHT_FACTOR. */
	public static final short EXCEL_ROW_HEIGHT_FACTOR = 20;

	/** The Constant EMU_PER_MM. */
	public static final int EMU_PER_MM = 36000;

	/** The Constant EMU_PER_POINTS. */
	public static final int EMU_PER_POINTS = 12700;

	/**
	 * pixel units to excel width units(units of 1/256th of a character width).
	 *
	 * @param pxs
	 *            the pxs
	 * @return the short
	 */
	public static short pixel2WidthUnits(final int pxs) {
		short widthUnits = (short) (EXCEL_COLUMN_WIDTH_FACTOR
				* (pxs / UNIT_OFFSET_LENGTH));
		widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)];
		return widthUnits;
	}

	/**
	 * excel width units(units of 1/256th of a character width) to pixel units.
	 *
	 * @param widthUnits
	 *            the width units
	 * @return the int
	 */
	public static int widthUnits2Pixel(final int widthUnits) {
		int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR)
				* UNIT_OFFSET_LENGTH;
		int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
		pixels += Math.round(offsetWidthUnits
				/ ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));
		return pixels;
	}

	/**
	 * PIXEL_HEIGHT_ASPC_ADJUST.
	 */
	private static final double PIXEL_HEIGHT_ASPC_ADJUST = 14;
	/**
	 * Height units 2 pixel.
	 *
	 * @param heightUnits
	 *            the height units
	 * @return the int
	 */
	public static int heightUnits2Pixel(final short heightUnits) {
		int pixels = (heightUnits / EXCEL_ROW_HEIGHT_FACTOR);
		int offsetHeightUnits = heightUnits % EXCEL_ROW_HEIGHT_FACTOR;
		pixels += Math.round((float) offsetHeightUnits
				/ ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH
						/ 2));
		pixels += (Math.floor(pixels / PIXEL_HEIGHT_ASPC_ADJUST) + 1) * 4;

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
	public static double widthUnits2Millimetres(final short widthUnits) {
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
	public static int millimetres2WidthUnits(final double millimetres) {
		return (pixel2WidthUnits(
				(int) (millimetres * PIXELS_PER_MILLIMETRES)));
	}

	/**
	 * Points to pixels.
	 *
	 * @param points
	 *            the points
	 * @return the int
	 */
	public static int pointsToPixels(final double points) {
		return (int) Math.round(points / POINTS_PER_INCH * PIXELS_PER_INCH);
	}

	/**
	 * Points to millimeters.
	 *
	 * @param points
	 *            the points
	 * @return the double
	 */
	public static double pointsToMillimeters(final double points) {
		return points / POINTS_PER_INCH * MILLIMETERS_PER_INCH;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * class Helper * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * Helper methods and miscellaneous tools
	 * for Extension of Apache POI * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * To do: [ ] work on date handling [
	 * ] work on CSV parsing to further generalize [ ] String < - > Date * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */

	/** The Constant DATE_REGEX_4_DIGIT_YEAR. */
	private static final String DATE_REGEX_4_DIGIT_YEAR = "("
			+ "(19|20)[0-9]{2}" + "([-/.\\\\]{1})"
			+ "[0?[1-9]|[1-9]|1[012]]{1,2}" + "\\3"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + ")" + "|" + "("
			+ "[0?[1-9]|[1-9]|1[012]]{1,2}" + "([-/.\\\\]{1})"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + "\\6"
			+ "(19|20)[0-9]{2}" + ")";

	/** The Constant DATE_REGEX_2_DIGIT_YEAR. */
	private static final String DATE_REGEX_2_DIGIT_YEAR = "(" + "[0-9]{2}"
			+ "([-/.\\\\]{1})" + "[0?[1-9]|[1-9]|1[012]]{1,2}" + "\\3"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + ")" + "|" + "("
			+ "[0?[1-9]|[1-9]|1[012]]{1,2}" + "([-/.\\\\]{1})"
			+ "([0?[1-9]|[1-9]|1[0-9]|2[0-9]|3[01]]{1,2})" + "\\6"
			+ "[0-9]{2}" + ")";

	/**
	 * Checks if is weekend.
	 *
	 * @param date
	 *            the date
	 * @return true, if is weekend
	 */
	private static boolean isWeekend(final Calendar date) {
		return date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	/**
	 * Gets the yesterday.
	 *
	 * @return the yesterday
	 */
	private static Calendar getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -1);
		return date;
	}

	/**
	 * Gets the last weekday.
	 *
	 * @return the last weekday
	 */
	private static Calendar getLastWeekday() {
		return getLastWeekday(getYesterday());
	}

	/**
	 * Gets the last weekday.
	 *
	 * @param date
	 *            the date
	 * @return the last weekday
	 */
	private static Calendar getLastWeekday(final Calendar date) {
		Calendar lDate = Calendar.getInstance();
		lDate.setTime(date.getTime());
		while (isWeekend(lDate)) {
			lDate.add(Calendar.DATE, -1);
		}
		return lDate;
	}

	/**
	 * Checks if is date.
	 *
	 * @param s
	 *            the s
	 * @return true, if is date
	 */
	public static boolean isDate(final String s) {
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

	/**
	 * Parses the date.
	 *
	 * @param entry
	 *            the entry
	 * @return the string
	 */
	public static String parseDate(final String entry) {
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

	/**
	 * Checks if is numeric.
	 *
	 * @param str
	 *            the str
	 * @return true, if is numeric
	 */
	public static boolean isNumeric(final String str) {
		String s = str;
		if (s.startsWith("-")) {
			s = s.substring(1);
		}
		char c;
		int i, sLen = s.length(), sinceLastComma = 0;
		boolean decimalHit = false, commaHit = false;
		for (i = 0; i < sLen; i++) {
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
				if (commaHit) {
					sinceLastComma++;
				}
			}
		}
		return true;
	}

	/**
	 * Sets the object property.
	 *
	 * @param obj
	 *            the obj
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the property value
	 * @param ignoreNonExisting
	 *            the ignore non existing
	 */
	public static void setObjectProperty(final Object obj,
			final String propertyName, final String propertyValue,
			final boolean ignoreNonExisting) {
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
				LOG.info(msg);
			} else {
				LOG.warning(msg);
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Cell compare to.
	 *
	 * @param thisCell
	 *            the this cell
	 * @param otherCell
	 *            the other cell
	 * @return the int
	 */
	public static int cellCompareTo(final Cell thisCell,
			final Cell otherCell) {
		int r = thisCell.getRowIndex() - otherCell.getRowIndex();
		if (r != 0) {
			return r;
		}

		r = thisCell.getColumnIndex() - otherCell.getColumnIndex();
		if (r != 0) {
			return r;
		}

		return 0;
	}

	/**
	 * Inside range.
	 *
	 * @param child
	 *            the child
	 * @param parent
	 *            the parent
	 * @return true, if successful
	 */
	public static boolean insideRange(final ConfigRange child,
			final ConfigRange parent) {

		if ((cellCompareTo(child.getFirstRowRef(),
				parent.getFirstRowRef()) >= 0)
				&& (cellCompareTo(child.getLastRowPlusRef(),
						parent.getLastRowPlusRef()) <= 0)) {
			return true;
		}
		return false;
	}

	/**
	 * return the last column of the sheet.
	 * 
	 * @param sheet
	 *            sheet.
	 * @return last column number (A column will return 0).
	 */
	public static int getSheetRightCol(final Sheet sheet) {

		try {
			if (sheet instanceof XSSFSheet) {
				XSSFSheet xsheet = (XSSFSheet) sheet;
				CTSheetDimension dimension = xsheet.getCTWorksheet()
						.getDimension();
				String sheetDimensions = dimension.getRef();
				if (sheetDimensions.indexOf(':') > 0) {
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
				if ((record != null) && (record.getLastCol() > 0)) {
					return record.getLastCol() - 1;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

}
