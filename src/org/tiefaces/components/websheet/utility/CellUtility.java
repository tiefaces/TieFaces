/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.utility;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFVMLDrawing;
import org.tiefaces.common.FacesUtility;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.dataobjects.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.dataobjects.FacesRow;
import org.tiefaces.components.websheet.dataobjects.TieCell;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.vml.CTShape;

/**
 * Helper class for web sheet cells.
 * 
 * @author Jason Jiang
 *
 */
public final class CellUtility {

	/** logger. */
	static final Logger LOG = Logger.getLogger(CellUtility.class.getName());

	/**
	 * Instantiates a new cell helper.
	 */
	private CellUtility() {
		super();
	}

	/**
	 * return cell value with format.
	 * 
	 * @param poiCell
	 *            cell.
	 * @param formulaEvaluator
	 *            formula evaluator.
	 * @param dataFormatter
	 *            data formatter.
	 * @return cell string value with format.
	 */
	@SuppressWarnings("deprecation")
	public static String getCellValueWithFormat(final Cell poiCell, final FormulaEvaluator formulaEvaluator,
			final DataFormatter dataFormatter) {

		if (poiCell == null) {
			return null;
		}

		String result;
		try {
			CellType cellType = poiCell.getCellTypeEnum();
			if (cellType == CellType.FORMULA) {
				cellType = formulaEvaluator.evaluate(poiCell).getCellTypeEnum();
			}
			if (cellType == CellType.ERROR) {
				result = "";
			} else {
				result = dataFormatter.formatCellValue(poiCell, formulaEvaluator);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Web Form WebFormHelper getCellValue Error row = " + poiCell.getRowIndex() + " column = "
							+ poiCell.getColumnIndex() + " error = " + e.getLocalizedMessage()
							+ "; Change return result to blank",
					e);
			result = "";
		}

		return result;
	}

	/**
	 * get input cell value. none input return blank
	 * 
	 * @param poiCell
	 *            cell.
	 * @return String cell value.
	 */
	@SuppressWarnings("deprecation")
	public static String getCellValueWithoutFormat(final Cell poiCell) {

		if (poiCell == null) {
			return null;
		}

		if (poiCell.getCellTypeEnum() == CellType.FORMULA) {
			return getCellStringValueWithType(poiCell, poiCell.getCachedFormulaResultTypeEnum());
		} else {
			return getCellStringValueWithType(poiCell, poiCell.getCellTypeEnum());
		}
	}

	/**
	 * Get cell value as string but with giving type.
	 * 
	 * @param poiCell
	 *            cell.
	 * @param cellType
	 *            cell type.
	 * @return Sting cell value.
	 */
	private static String getCellStringValueWithType(final Cell poiCell, final CellType cellType) {

		switch (cellType) {
		case BOOLEAN:
			return getCellStringValueWithBooleanType(poiCell);
		case NUMERIC:
			return getCellStringValueWithNumberType(poiCell);
		case STRING:
			return poiCell.getStringCellValue();
		default:
			return "";
		} // switch

	}

	/**
	 * Gets the cell string value with boolean type.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @return the cell string value with boolean type
	 */
	private static String getCellStringValueWithBooleanType(final Cell poiCell) {
		if (poiCell.getBooleanCellValue()) {
			return "Y";
		} else {
			return "N";
		}
	}

	/**
	 * Gets the cell string value with number type.
	 *
	 * @param poiCell
	 *            the poi cell
	 * @return the cell string value with number type
	 */
	private static String getCellStringValueWithNumberType(final Cell poiCell) {
		String result;
		if (DateUtil.isCellDateFormatted(poiCell)) {
			result = poiCell.getDateCellValue().toString();
		} else {
			result = BigDecimal.valueOf(poiCell.getNumericCellValue()).toPlainString();
			// remove .0 from end for int
			if (result.endsWith(".0")) {
				result = result.substring(0, result.length() - 2);
			}
		}
		return result;
	}

	/**
	 * Set cell value with giving String value.
	 * 
	 * @param c
	 *            cell.
	 * @param value
	 *            giving value.
	 * @return cell.
	 */
	@SuppressWarnings("deprecation")
	public static Cell setCellValue(final Cell c, final String value) {

		try {
			if (value.length() == 0) {
				c.setCellType(CellType.BLANK);
			} else if (WebSheetUtility.isNumeric(value)) {
				setCellValueNumber(c, value);
			} else if (WebSheetUtility.isDate(value)) {
				setCellValueDate(c, value);
			} else if (c.getCellTypeEnum() == CellType.BOOLEAN) {
				setCellValueBoolean(c, value);
			} else {
				setCellValueString(c, value);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, " error in setCellValue of CellUtility = " + e.getLocalizedMessage(), e);
			setCellValueString(c, value);
		}
		return c;
	}

	/**
	 * Sets the cell value string.
	 *
	 * @param c
	 *            the c
	 * @param value
	 *            the value
	 */
	private static void setCellValueString(final Cell c, final String value) {
		c.setCellType(CellType.STRING);
		c.setCellValue(value);
	}

	/**
	 * Sets the cell value boolean.
	 *
	 * @param c
	 *            the c
	 * @param value
	 *            the value
	 */
	private static void setCellValueBoolean(final Cell c, final String value) {
		if ("Y".equalsIgnoreCase(value) || "Yes".equalsIgnoreCase(value) || "True".equalsIgnoreCase(value)) {
			c.setCellValue(true);
		} else {
			c.setCellValue(false);
		}
	}

	/**
	 * Sets the cell value date.
	 *
	 * @param c
	 *            the c
	 * @param value
	 *            the value
	 */
	private static void setCellValueDate(final Cell c, final String value) {
		String date = WebSheetUtility.parseDate(value);
		setCellValueString(c, date);
	}

	/**
	 * Sets the cell value number.
	 *
	 * @param c
	 *            the c
	 * @param value
	 *            the value
	 */
	private static void setCellValueNumber(final Cell c, final String value) {
		double val = Double.parseDouble(value.replace(Character.toString(','), ""));
		c.setCellType(CellType.NUMERIC);
		c.setCellValue(val);
	}

	/**
	 * Copy rows.
	 *
	 * @param srcSheet
	 *            the src sheet
	 * @param destSheet
	 *            the dest sheet
	 * @param srcRowStart
	 *            the src row start
	 * @param srcRowEnd
	 *            the src row end
	 * @param destRow
	 *            the dest row
	 * @param checkLock
	 *            the check lock
	 * @param setHiddenColumn
	 *            the set hidden column
	 */
	public static void copyRows(final Sheet srcSheet, final Sheet destSheet, final int srcRowStart, final int srcRowEnd,
			final int destRow, final boolean checkLock, final boolean setHiddenColumn) {

		int length = srcRowEnd - srcRowStart + 1;
		if (length <= 0) {
			return;
		}
		destSheet.shiftRows(destRow, destSheet.getLastRowNum(), length, true, false);
		for (int i = 0; i < length; i++) {
			copySingleRow(srcSheet, destSheet, srcRowStart + i, destRow + i, checkLock, setHiddenColumn);
		}
		// If there are are any merged regions in the source row, copy to new
		// row
		for (int i = 0; i < srcSheet.getNumMergedRegions(); i++) {
			CellRangeAddress cellRangeAddress = srcSheet.getMergedRegion(i);
			if ((cellRangeAddress.getFirstRow() >= srcRowStart) && (cellRangeAddress.getLastRow() <= srcRowEnd)) {
				int targetRowFrom = cellRangeAddress.getFirstRow() - srcRowStart + destRow;
				int targetRowTo = cellRangeAddress.getLastRow() - srcRowStart + destRow;

				CellRangeAddress newCellRangeAddress = new CellRangeAddress(targetRowFrom, targetRowTo,
						cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
				destSheet.addMergedRegion(newCellRangeAddress);
			}
		}
	}

	/**
	 * Copy single row.
	 *
	 * @param srcSheet
	 *            the src sheet
	 * @param destSheet
	 *            the dest sheet
	 * @param sourceRowNum
	 *            the source row num
	 * @param destinationRowNum
	 *            the destination row num
	 * @param checkLock
	 *            the check lock
	 * @param setHiddenColumn
	 *            the set hidden column
	 */
	private static void copySingleRow(final Sheet srcSheet, final Sheet destSheet, final int sourceRowNum,
			final int destinationRowNum, final boolean checkLock, final boolean setHiddenColumn) {
		// Get the source / new row
		Row newRow = destSheet.getRow(destinationRowNum);
		Row sourceRow = srcSheet.getRow(sourceRowNum);

		if (newRow == null) {
			newRow = destSheet.createRow(destinationRowNum);
		}
		newRow.setHeight(sourceRow.getHeight());
		// Loop through source columns to add to new row
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			// Grab a copy of the old/new cell
			copyCell(destSheet, sourceRow, newRow, i, checkLock);
		}
		if (setHiddenColumn) {
			ConfigurationUtility.setOriginalRowNumInHiddenColumn(newRow, sourceRow.getRowNum());
		}
		return;

	}

	/**
	 * Copy cell.
	 *
	 * @param destSheet
	 *            the dest sheet
	 * @param sourceRow
	 *            the source row
	 * @param newRow
	 *            the new row
	 * @param cellIndex
	 *            the cell index
	 * @param checkLock
	 *            the check lock
	 * @return the int
	 */
	public static Cell copyCell(final Sheet destSheet, final Row sourceRow, final Row newRow, final int cellIndex,
			final boolean checkLock) {
		// If the old cell is null jump to next cell
		Cell sourceCell = sourceRow.getCell(cellIndex);
		if (sourceCell == null) {
			return null;
		}
		// If source cell is dest cell refresh it
		if (sourceRow.equals(newRow)) {
		    sourceRow.removeCell(sourceCell);
		}
		Cell newCell = newRow.createCell(cellIndex);
		try {
			copyCellSetStyle(destSheet, sourceCell, newCell);
			copyCellSetValue(sourceCell, newCell, checkLock);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "copy cell set error = " + ex.getLocalizedMessage(), ex);
		}

		return newCell;
	}

	/**
	 * set cell value.
	 * 
	 * @param sourceCell
	 *            source cell.
	 * @param newCell
	 *            new cell.
	 * @param checkLock
	 *            check lock flag.
	 */
	@SuppressWarnings("deprecation")
	private static void copyCellSetValue(final Cell sourceCell, final Cell newCell, final boolean checkLock) {

		CellStyle newCellStyle = newCell.getCellStyle();
		String name = sourceCell.getCellTypeEnum().toString();
		CellValueType e = Enum.valueOf(CellValueType.class, name);
		e.setCellValue(newCell, sourceCell, checkLock, newCellStyle);
	}

	/**
	 * The Enum CellValueType.
	 */
	public enum CellValueType {

		/** The string. */
		STRING {
			@Override
			public void setCellValue(final Cell newCell, final Cell sourceCell, final boolean checkLock,
					final CellStyle newCellStyle) {
				if ((!checkLock) || newCellStyle.getLocked()) {
					newCell.setCellValue(sourceCell.getRichStringCellValue());
				}
			}
		},

		/** The boolean. */
		BOOLEAN {
			@Override
			public void setCellValue(final Cell newCell, final Cell sourceCell, final boolean checkLock,
					final CellStyle newCellStyle) {
				if ((!checkLock) || newCellStyle.getLocked()) {
					newCell.setCellValue(sourceCell.getBooleanCellValue());
				}
			}
		},

		/** The numeric. */
		NUMERIC {
			@Override
			public void setCellValue(final Cell newCell, final Cell sourceCell, final boolean checkLock,
					final CellStyle newCellStyle) {
				if ((!checkLock) || newCellStyle.getLocked()) {
					newCell.setCellValue(sourceCell.getNumericCellValue());
				}
			}
		},

		/** The formula. */
		FORMULA {
			@Override
			public void setCellValue(final Cell newCell, final Cell sourceCell, final boolean checkLock,
					final CellStyle newCellStyle) {
				newCell.setCellFormula(sourceCell.getCellFormula());
			}
		},

		/** The error. */
		ERROR {
			@Override
			public void setCellValue(final Cell newCell, final Cell sourceCell, final boolean checkLock,
					final CellStyle newCellStyle) {
				if ((!checkLock) || newCellStyle.getLocked()) {
					newCell.setCellErrorValue(sourceCell.getErrorCellValue());
				}
			}
		},

		/** The blank. */
		BLANK {
			@Override
			public void setCellValue(final Cell newCell, final Cell sourceCell, final boolean checkLock,
					final CellStyle newCellStyle) {
				newCell.setCellType(CellType.BLANK);
			}
		};

		/**
		 * Sets the cell value.
		 *
		 * @param newCell
		 *            the new cell
		 * @param sourceCell
		 *            the source cell
		 * @param checkLock
		 *            the check lock
		 * @param newCellStyle
		 *            the new cell style
		 */
		public abstract void setCellValue(Cell newCell, Cell sourceCell, boolean checkLock, CellStyle newCellStyle);

	}

	/**
	 * set up cell style.
	 * 
	 * @param destSheet
	 *            dest sheet.
	 * @param sourceCell
	 *            source cell.
	 * @param newCell
	 *            new cell.
	 */
	@SuppressWarnings("deprecation")
	private static void copyCellSetStyle(final Sheet destSheet, final Cell sourceCell, final Cell newCell) {
		CellStyle newCellStyle = getCellStyleFromSourceCell(destSheet, sourceCell);
		newCell.setCellStyle(newCellStyle);

		// If there is a cell comment, copy
		if (sourceCell.getCellComment() != null) {
			cloneComment(sourceCell, newCell);
		}

		// If there is a cell hyperlink, copy
		if (sourceCell.getHyperlink() != null) {
			newCell.setHyperlink(sourceCell.getHyperlink());
		}

		// Set the cell data type
		newCell.setCellType(sourceCell.getCellTypeEnum());
	}
	
	/**
	 * clone existing comments into new cell comment.
	 * @param sourceCell source cell.
	 * @param newCell target cell.
	 */

	private static void cloneComment(final Cell sourceCell, final Cell newCell) {

		XSSFSheet sheet = (XSSFSheet) newCell.getSheet();
		CreationHelper factory = sheet.getWorkbook().getCreationHelper();
		Drawing drawing = sheet.createDrawingPatriarch();
		XSSFComment sourceComment = (XSSFComment) sourceCell.getCellComment();
		// Below code are from POI busy manual.
		// When the comment box is visible, have it show in a 1x3 space
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setCol1(newCell.getColumnIndex());
		anchor.setCol2(newCell.getColumnIndex() + 1);
		anchor.setRow1(newCell.getRowIndex());
		anchor.setRow2(newCell.getRowIndex() + 3);
		// Create the comment and set the text+author
		Comment comment = drawing.createCellComment(anchor);
		RichTextString str = factory.createRichTextString(sourceComment.getString().toString());
		comment.setString(str);
		comment.setAuthor(sourceComment.getAuthor());
		// Assign the comment to the cell
		newCell.setCellComment(comment);
		comment.setColumn(newCell.getColumnIndex());
		comment.setRow(newCell.getRowIndex());
		// As POI doesn't has well support for comments,
		// So we have to use low level api to match the comments. 
		matchCommentSettings(newCell, sourceCell);
	}

	/**
	 * Use low level API to match the comments setting.
	 * @param newCell target cell.
	 * @param sourceCell source cell.
	 */
	private static void matchCommentSettings(final Cell newCell, final Cell sourceCell) {
		try {
			XSSFVMLDrawing sourceVml = getVmlDrawingFromCell(sourceCell);
			XSSFVMLDrawing targetVml = getVmlDrawingFromCell(newCell);
			CTShape sourceCtShape = getCtShapeFromVml(sourceCell, sourceVml);
			CTShape targetCtShape = getCtShapeFromVml(newCell, targetVml);
			targetCtShape.setType(sourceCtShape.getType());
			CTClientData sourceClientData = sourceCtShape.getClientDataArray(0);
			CTClientData targetClientData = targetCtShape.getClientDataArray(0);
			String[] anchorArray = sourceClientData.getAnchorList().get(0).split(",");
			int shiftRows = newCell.getRowIndex() - sourceCell.getRowIndex();
			/*
			 * AchorArray mappings: 0->col1 1->dx1 2->row1 3->dy1 4->col2
			 * 5->dx2 6-> row2 7->dy2
			 */
			anchorArray[2] = Integer.toString(Integer.parseInt(anchorArray[2].trim()) + shiftRows);
			anchorArray[6] = Integer.toString(Integer.parseInt(anchorArray[6].trim()) + shiftRows);
			targetClientData.getAnchorList().set(0, FacesUtility.strJoin(anchorArray,","));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "matchCommentSettings error = " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Find vmldrawing part according to cell.
	 * @param cell cell.
	 * @return vmldrawing.
	 */
	private static XSSFVMLDrawing getVmlDrawingFromCell(final Cell cell) {
		XSSFSheet sourceSheet = (XSSFSheet) cell.getSheet();
		for (POIXMLDocumentPart sourcePart : sourceSheet.getRelations()) {
			if ((sourcePart != null) && (sourcePart instanceof XSSFVMLDrawing)) {
				return (XSSFVMLDrawing) sourcePart;
			}
		}
		return null;
	}

	/**
	 * Find CtShape from vml object.
	 * This class use reflection to invoke the protected method in POI.
	 * @param sourceCell cell.
	 * @param sourceVml vml.
	 * @return ctShape.
	 * @throws ReflectiveOperationException
	 * @throws SecurityException
	 */
	@SuppressWarnings("rawtypes")
	private static CTShape getCtShapeFromVml(final Cell sourceCell, XSSFVMLDrawing sourceVml)
			throws ReflectiveOperationException, SecurityException {
		Method findshape;
		// int parameter
		Class[] paramInt = new Class[2];
		paramInt[0] = Integer.TYPE;
		paramInt[1] = Integer.TYPE;
		findshape = sourceVml.getClass().getDeclaredMethod("findCommentShape", paramInt);
		findshape.setAccessible(true);
		CTShape ctShape = (CTShape) findshape.invoke(sourceVml, sourceCell.getRowIndex(), sourceCell.getColumnIndex());
		return ctShape;
	}

	/**
	 * create cell style from source cell.
	 * 
	 * @param destSheet
	 *            dest sheet.
	 * @param sourceCell
	 *            source cell.
	 * @return cell style.
	 */
	private static CellStyle getCellStyleFromSourceCell(final Sheet destSheet, final Cell sourceCell) {
		Workbook wb = destSheet.getWorkbook();
		// Copy style from old cell and apply to new cell
		CellStyle newCellStyle = wb.createCellStyle();
		newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
		return newCellStyle;
	}

	/**
	 * return cell index number key. e.g. $0$0 for A1 cell.
	 * 
	 * @param cell
	 *            input cell.
	 * @return string.
	 */
	public static String getCellIndexNumberKey(final Cell cell) {
		if (cell != null) {
			return TieConstants.CELL_ADDR_PRE_FIX + cell.getColumnIndex() + TieConstants.CELL_ADDR_PRE_FIX
					+ cell.getRowIndex();
		}
		return null;
	}

	/**
	 * return cell index number key. e.g. $0$0 for A1 cell.
	 * 
	 * @param columnIndex
	 *            column index.
	 * @param rowIndex
	 *            row index.
	 * @return string.
	 */
	public static String getCellIndexNumberKey(final int columnIndex, final int rowIndex) {
		return TieConstants.CELL_ADDR_PRE_FIX + columnIndex + TieConstants.CELL_ADDR_PRE_FIX + rowIndex;
	}

	/**
	 * return cell index key with column letter and row index. e.g. $A$0 for A1
	 * cell.
	 * 
	 * @param columnLetter
	 *            column letter.
	 * @param rowIndex
	 *            row index.
	 * @return String.
	 */
	public static String getCellIndexLetterKey(final String columnLetter, final int rowIndex) {
		return TieConstants.CELL_ADDR_PRE_FIX + columnLetter + TieConstants.CELL_ADDR_PRE_FIX + rowIndex;
	}

	/**
	 * return cell index key with column and row index. e.g. $A$0 for A1 cell.
	 * 
	 * @param columnIndex
	 *            column index.
	 * @param rowIndex
	 *            row index.
	 * @return key.
	 */

	public static String getCellIndexLetterKey(final int columnIndex, final int rowIndex) {
		return TieConstants.CELL_ADDR_PRE_FIX + WebSheetUtility.getExcelColumnName(columnIndex)
				+ TieConstants.CELL_ADDR_PRE_FIX + rowIndex;
	}

	/**
	 * Convert cell.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @param fcell
	 *            the fcell
	 * @param poiCell
	 *            the poi cell
	 * @param cellRangeMap
	 *            the cell range map
	 * @param originRowIndex
	 *            the origin row index
	 * @param cellAttributesMap
	 *            the cell attributes map
	 * @param saveAttrs
	 *            the save attrs
	 */
	// set up facesCell's attribute from poiCell and others.
	public static void convertCell(final SheetConfiguration sheetConfig, final FacesCell fcell, final Cell poiCell,
			final Map<String, CellRangeAddress> cellRangeMap, final int originRowIndex,
			final CellAttributesMap cellAttributesMap, final String saveAttrs) {
		CellRangeAddress caddress;
		String key = getCellIndexNumberKey(poiCell);
		caddress = cellRangeMap.get(key);
		if (caddress != null) {
			// has col or row span
			fcell.setColspan(caddress.getLastColumn() - caddress.getFirstColumn() + 1);
			fcell.setRowspan(caddress.getLastRow() - caddress.getFirstRow() + 1);
		}

		CellControlsUtility.setupControlAttributes(originRowIndex, fcell, poiCell, sheetConfig, cellAttributesMap);
		fcell.setHasSaveAttr(SaveAttrsUtility.isHasSaveAttr(poiCell, saveAttrs));

	}

	/**
	 * Gets the row col from component attributes.
	 *
	 * @param target
	 *            the target
	 * @return the row col from component attributes
	 */
	public static int[] getRowColFromComponentAttributes(final UIComponent target) {

		int rowIndex = (Integer) target.getAttributes().get("data-row");
		int colIndex = (Integer) target.getAttributes().get("data-column");
		int[] list = new int[2];
		list[0] = rowIndex;
		list[1] = colIndex;
		return list;
	}

	/**
	 * Gets the inits the rows from config.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @return the inits the rows from config
	 */
	public static int getInitRowsFromConfig(final SheetConfiguration sheetConfig) {
		int initRows = 1;
		if ("Repeat".equalsIgnoreCase(sheetConfig.getFormBodyType())) {
			initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1) {
				initRows = 1;
			}
		}
		return initRows;
	}

	/**
	 * Gets the body bottom from config.
	 *
	 * @param sheetConfig
	 *            the sheet config
	 * @return the body bottom from config
	 */
	public static int getBodyBottomFromConfig(final SheetConfiguration sheetConfig) {

		return sheetConfig.getBodyCellRange().getBottomRow();

	}

	/**
	 * Gets the faces row from body row.
	 * @param row
	 * 		the row
	 * @param bodyRows
	 * 		the body rows
	 * @param topRow
	 * 		the top row
	 * @return
	 * 		faces row
	 */
	public static FacesRow getFacesRowFromBodyRow(final int row, final List<FacesRow> bodyRows,
			final int topRow) {
		FacesRow frow = null;

		try {
			frow = bodyRows.get(row - topRow);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "getFacesRowFromBodyRow Error row = " + row +  "top row = " + topRow
					+ " ; error = " + e.getLocalizedMessage(), e);
		}
		return frow;
	}	
	
	/**
	 * Gets the faces cell from body row.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @param bodyRows
	 *            the body rows
	 * @param topRow
	 *            the top row
	 * @param leftCol
	 *            the left col
	 * @return the faces cell from body row
	 */
	public static FacesCell getFacesCellFromBodyRow(final int row, final int col, final List<FacesRow> bodyRows,
			final int topRow, final int leftCol) {
		FacesCell cell = null;

		try {
			cell = bodyRows.get(row - topRow).getCells().get(col - leftCol);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "getFacesCellFromBodyRow Error row = " + row + " col = " + col + "top row = " + topRow
					+ " leftCol = " + leftCol + " ; error = " + e.getLocalizedMessage(), e);
		}
		return cell;
	}

	/**
	 * Gets the poi cell with row col from current page.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @param wb
	 *            workbook.
	 * @return the poi cell with row col from current page
	 */
	public static Cell getPoiCellWithRowColFromCurrentPage(final int rowIndex, final int colIndex, final Workbook wb) {
		if (wb != null) {
			return getPoiCellFromSheet(rowIndex, colIndex, wb.getSheetAt(wb.getActiveSheetIndex()));
		}
		return null;
	}

	/**
	 * Gets the poi cell from sheet.
	 *
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * @param sheet1
	 *            the sheet 1
	 * @return the poi cell from sheet
	 */
	public static Cell getPoiCellFromSheet(final int rowIndex, final int colIndex, final Sheet sheet1) {
		if ((sheet1 != null) && (sheet1.getRow(rowIndex) != null)) {
			return sheet1.getRow(rowIndex).getCell(colIndex);
		}
		return null;
	}


	public static String getSkeyFromPoiCell(final Cell poiCell) {
		String skey = poiCell.getSheet().getSheetName() + "!" + CellUtility.getCellIndexNumberKey(poiCell.getColumnIndex(), poiCell.getRowIndex());
		return skey;
	}

	public static TieCell getOrAddTieCellInMap(final Cell poiCell, HashMap<String, TieCell> tieCells) {
		String skey = CellUtility.getSkeyFromPoiCell(poiCell);
		TieCell tieCell = tieCells.get(skey);
		if (tieCell == null)  {
			tieCell = new TieCell();
			tieCell.setSkey(skey);
			tieCells.put(skey, tieCell);
		}
		return tieCell;
	}

}
