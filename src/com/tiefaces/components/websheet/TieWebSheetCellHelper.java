/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tiefaces.components.websheet;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.tiefaces.common.FacesUtility;
import com.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import com.tiefaces.components.websheet.dataobjects.FacesCell;
import com.tiefaces.components.websheet.dataobjects.FacesRow;
import com.tiefaces.components.websheet.dataobjects.SheetConfiguration;

public class TieWebSheetCellHelper {

	private TieWebSheetBean parent = null;

	private static boolean debug =false;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("TieWebSheetCellHelper: " + msg);
		}
	}

	public TieWebSheetCellHelper() {
		super();
	}

	public TieWebSheetCellHelper(TieWebSheetBean parent) {
		super();
		this.parent = parent;
	}

	public String getCellValueWithFormat(Cell poiCell) {

		if (poiCell == null)
			return null;

		String result;
		try{
			int cellType = poiCell.getCellType();
		    if (cellType == Cell.CELL_TYPE_FORMULA) 
	    	cellType = parent.getFormulaEvaluator().evaluate(poiCell).getCellType();			
			if (cellType== Cell.CELL_TYPE_ERROR)  result ="";
			else
				result = parent.getDataFormatter().formatCellValue(poiCell, parent.getFormulaEvaluator());
		}
		catch(Exception e){
			debug("Web Form WebFormHelper getCellValue Error row = "+ poiCell.getRowIndex()+" col = "+ poiCell.getColumnIndex()+" error = "+e.getLocalizedMessage()+"; Change return result to blank");
			result = "";
		}
		debug("getCellValueWithFormat result = "+ result+" row = "+poiCell.getRowIndex()+" col = "+poiCell.getColumnIndex());
		return result;
	}		

	
	// get input cell value. non input return blank
	public String getCellValueWithoutFormat(Cell poiCell){
		
		if(poiCell==null)
			return null;

		if (poiCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return getCellStringValueWithType(poiCell, poiCell.getCachedFormulaResultType());
		} else {
			return getCellStringValueWithType(poiCell, poiCell.getCellType());
		}	
	}		

	private String getCellStringValueWithType(Cell poiCell, int cellType) {
		
		switch (cellType){
		case Cell.CELL_TYPE_BOOLEAN:
			if (poiCell.getBooleanCellValue())
				return "Y";
			else
				return "N";
		case Cell.CELL_TYPE_NUMERIC:
			String result;
		    if (DateUtil.isCellDateFormatted(poiCell)) {
		        result = poiCell.getDateCellValue().toString();
		    } else {
		        result = BigDecimal.valueOf(poiCell.getNumericCellValue()).toPlainString();
		        // remove .0 from end for int
		        if (result.endsWith(".0")) result = result.substring(0, result.length() -2);
		    }				
			return result;
		case Cell.CELL_TYPE_STRING:
			return poiCell.getStringCellValue();
		}//switch
		
		// others all return blank
		return "";
	}	
	


	public Cell setCellValue(Cell c, String value) {

		try {
			if (value.length() == 0) {
				c.setCellType(Cell.CELL_TYPE_BLANK);
			} else if (TieWebSheetUtility.isNumeric(value)) {
				double val = Double.parseDouble(value.replace(""+',', ""));
				c.setCellType(Cell.CELL_TYPE_NUMERIC);
				c.setCellValue(val);
			} else if (TieWebSheetUtility.isDate(value)) {
				String date = TieWebSheetUtility.parseDate(value);
				c.setCellType(Cell.CELL_TYPE_STRING);
				c.setCellValue(date);
			} else {
				if (c.getCellType()==Cell.CELL_TYPE_BOOLEAN) {
		        	if (value.equalsIgnoreCase("Y"))
		        		c.setCellValue(true);
		        	else
		        		c.setCellValue(false);
				} else {
					c.setCellType(Cell.CELL_TYPE_STRING);
					c.setCellValue(value);
				}
			}
		} catch (Exception e) {
			c.setCellType(Cell.CELL_TYPE_STRING);
			c.setCellValue(value);
		}
		debug(" set cell value row = " + c.getRowIndex() + " col = "
				+ c.getColumnIndex() + " value = " + value
				+ " cellType = " + c.getCellType());
		return c;
	}	

	public void reCalc() {

		parent.getFormulaEvaluator().clearAllCachedResultValues();
		try {
			parent.getFormulaEvaluator().evaluateAll();
		} catch (Exception ex) {
			// skip the formula exception when recalc but log it
			debug(" recalc formula error : " + ex.getLocalizedMessage());
		}

	}

	public boolean evalBoolExpression(String script) {
		Object result = null;
		script = "( " + script + " )";
		script = script.toUpperCase().replace("AND", "&&");
		script = script.toUpperCase().replace("OR", "||");
		try {
			result = parent.getEngine().eval(script);
		} catch (Exception e) {
			e.printStackTrace();
			debug("WebForm WebFormHelper evalBoolExpression script = " + script
					+ "; error = " + e.getLocalizedMessage());
		}
		if (result != null)
			return ((Boolean) result).booleanValue();
		else
			return false;
	}



	// / <summary>
	// / Row Copy Command
	// /
	// / Description: Inserts a existing row into a new row, will automatically
	// push down
	// / any existing rows. Copy is done cell by cell and supports, and the
	// / command tries to copy all properties available (style, merged cells,
	// values, etc...)
	// / </summary>
	// / <param name="workbook">Workbook containing the worksheet that will be
	// changed</param>
	// / <param name="worksheet">WorkSheet containing rows to be copied</param>
	// / <param name="sourceRowNum">Source Row Number</param>
	// / <param name="destinationRowNum">Destination Row Number</param>
	public void copyRow(Workbook wb, Sheet worksheet, int sourceRowNum, int destinationRowNum) {
		// Get the source / new row
		Row newRow = worksheet.getRow(destinationRowNum);
		Row sourceRow = worksheet.getRow(sourceRowNum);

		// If the row exist in destination, push down all rows by 1 else create
		// a new row
		if (newRow != null) {
			worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(),
					1, true, false);
		} else {
			newRow = worksheet.createRow(destinationRowNum);
		}
		newRow.setHeight(sourceRow.getHeight());
		// Loop through source columns to add to new row
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			// Grab a copy of the old/new cell
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = newRow.createCell(i);

			// If the old cell is null jump to next cell
			if (oldCell == null) {
				newCell = null;
				continue;
			}

			// Copy style from old cell and apply to new cell
			CellStyle newCellStyle = wb.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			;
			newCell.setCellStyle(newCellStyle);

			// If there is a cell comment, copy
			if (oldCell.getCellComment() != null) {
				newCell.setCellComment(oldCell.getCellComment());
			}

			// If there is a cell hyperlink, copy
			if (oldCell.getHyperlink() != null) {
				newCell.setHyperlink(oldCell.getHyperlink());
			}

			// Set the cell data type
			newCell.setCellType(oldCell.getCellType());

			// Set the cell data value
			switch (oldCell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				if(newCellStyle.getLocked()) 
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				if(newCellStyle.getLocked()) 
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:
				if(newCellStyle.getLocked()) 
				newCell.setCellErrorValue(oldCell.getErrorCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				String newformula = oldCell.getCellFormula().replace(
						"$" + (sourceRow.getRowNum() + 1),
						"$" + (newRow.getRowNum() + 1));
				newCell.setCellFormula(newformula);
				// formulaEvaluator.notifySetFormula(newCell);
				// formulaEvaluator.evaluate(newCell);

				break;
			case Cell.CELL_TYPE_NUMERIC:
				if(newCellStyle.getLocked()) 
				newCell.setCellValue(oldCell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING:
				if(newCellStyle.getLocked()) 
				newCell.setCellValue(oldCell.getRichStringCellValue());
				break;
			}
			// formulaEvaluator.notifyUpdateCell(newCell);
		}

		// If there are are any merged regions in the source row, copy to new
		// row
		for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
			CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
			if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
				CellRangeAddress newCellRangeAddress = new CellRangeAddress(
						newRow.getRowNum(),
						(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress
								.getFirstRow())), cellRangeAddress
								.getFirstColumn(), cellRangeAddress
								.getLastColumn());
				worksheet.addMergedRegion(newCellRangeAddress);
			}
		}
	}

	
	/* Refactor row formulas */
	// properly refactor an excel formulat on a row change
	public String formulaRowRefactor(String formula, int sourceRow, int copyRow) {
		String buf = "";
		String new_formula = "";
		int i;
		char c;
		boolean skipNext = false, inParen = false;
		for (i = 0; i < formula.length(); i++) {
			c = formula.charAt(i);
			if (c == '\'') {
				if (buf.length() > 0 && buf.length() < 4 && i-buf.length()-1 >= 0 && TieWebSheetUtility.isUpperAlpha(formula.charAt(i-buf.length()-1))) {
					if (!skipNext) {
						new_formula += carefulRowFormulaRefactorString(buf,sourceRow,copyRow);
						buf = "";
					} else {
						new_formula += buf;
						skipNext = false;
						buf = "";
					}
				} else {
					new_formula += buf;
					buf = "";
				}
				inParen = (inParen ? false : true);
				new_formula += c;
			} else if (!inParen) {
				if (c == '$') {
					if (buf.length() > 0 && buf.length() < 4 && i-buf.length()-1 >= 0 && TieWebSheetUtility.isUpperAlpha(formula.charAt(i-buf.length()-1))) {
						if (!skipNext) {
							new_formula += carefulColFormulaRefactorString(buf,sourceRow,copyRow);
							buf = "";
						} else {
							new_formula += buf;
							skipNext = false;
							buf = "";
						}
					} else {
						new_formula += buf;
						buf = "";
					}
					skipNext = true;
					new_formula += c;
				} else if (skipNext) {
					if (!TieWebSheetUtility.isNumeric(c)) {
						skipNext = false;
					}
					new_formula += c;
				} else {
					if (TieWebSheetUtility.isNumeric(c)) {
						buf += c;
					} else {
						if (buf.length() > 0 && i-buf.length()-1 >= 0 && TieWebSheetUtility.isUpperAlpha(formula.charAt(i-buf.length()-1))) {
							new_formula += carefulRowFormulaRefactorString(buf,sourceRow,copyRow);
							buf = "";
						} else {
							new_formula += buf;
							buf = "";
						}
						new_formula += c;
					}
				}
			} else {
				new_formula += c;
			}
		}
		if (!skipNext && !inParen && buf.length() > 0 && i-buf.length()-1 >= 0 && TieWebSheetUtility.isUpperAlpha(formula.charAt(i-buf.length()-1))) {
			new_formula += carefulRowFormulaRefactorString(buf,sourceRow,copyRow);
			buf = "";
		} else {
			new_formula += buf;
			buf = "";
		}
		return new_formula;
	}	
	
	public int carefulRowFormulaRefactorString(String formula, int sourceRow, int copyRow) {
		return copyRow + (Integer.parseInt(formula) - sourceRow);
	}	
	
	public String carefulColFormulaRefactorString(String formula, int sourceCol, int copyCol) {
		return TieWebSheetUtility.GetExcelColumnName((copyCol + (TieWebSheetUtility.convertColToInt(formula) - sourceCol)));
	}	
	
	public boolean containsCell(CellRangeAddress cr, int rowIx, int colIx) {
		if (cr.getFirstRow() <= rowIx && cr.getLastRow() >= rowIx
				&& cr.getFirstColumn() <= colIx && cr.getLastColumn() >= colIx) {
			return true;
		}
		return false;
	}

	public List<CellFormAttributes> findCellAttributesWithOffset(
			SheetConfiguration sheetConfig, Cell cell, int initRows,
			int bodyTopRow, boolean repeatZone) {
		Map<String, List<CellFormAttributes>> map = sheetConfig
				.getCellFormAttributes();

		String key = findCellAddressWithOffset(cell, initRows, bodyTopRow,
				repeatZone);
		List<CellFormAttributes> result = map.get(key);
		if ((result == null) && repeatZone) {
			key = "$" + TieWebSheetUtility.GetExcelColumnName(cell.getColumnIndex());
			result = map.get(key);
		}
		return result;

	}

	public String findCellAddressWithOffset(Cell cell, int initRows,
			int bodyTopRow, boolean repeatZone) {

		String key;
		String columnLetter = TieWebSheetUtility.GetExcelColumnName(cell.getColumnIndex());

		if (repeatZone)
			key = "$" + columnLetter + "$" + (bodyTopRow + 1);
		else
			key = "$" + columnLetter + "$"
					+ (cell.getRowIndex() - initRows + 1 + 1);
		return key;
	}

	public String findCellAddressAfterBodyPopulated(String oldCellAddr,
			SheetConfiguration sheetConfig) {

		if (!sheetConfig.isBodyPopulated())
			return null; // not valid
		String[] rowcol = getRowColFromExcelReferenceName(oldCellAddr);
		if (rowcol[0].isEmpty()) {
			// not valid
			return null;
		}
		int row = Integer.parseInt(rowcol[0]);
		int initialRows = sheetConfig.getBodyInitialRows();
		if ((sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat"))
				&& (row > (sheetConfig.getBodyCellRange().getTopRow() + 1))) {
			return "$" + rowcol[1] + "$" + (row + initialRows - 1);
		}
		// no change
		return oldCellAddr;
	}

//	public String findCellRangeAddressWithOffset(int row, int col,
//			int initRows, int bodyTopRow, boolean repeatZone,
//			boolean bodyPopulated) {
//		String key;
//		if (bodyPopulated)
//			key = "$" + col + "$" + row;
//		else {
//
//			if (repeatZone)
//				key = "$" + col + "$" + bodyTopRow;
//			else
//				key = "$" + col + "$" + (row - initRows + 1);
//		}
//		return key;
//	}

	public List<CellFormAttributes> findCellAttributes(
			SheetConfiguration sheetConfig, Cell cell, int row, int bodyTopRow) {

		boolean repeatZone = false;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			int initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1)
				initRows = 1;
			if ((row >= bodyTopRow) && (row < (bodyTopRow + initRows)))
				repeatZone = true;
			return findCellAttributesWithOffset(sheetConfig, cell, initRows,
					bodyTopRow, repeatZone);
		}
		return findCellAttributesWithOffset(sheetConfig, cell, 1, bodyTopRow,
				false);

	}




	// This method mainly doing 2 things
	// 1. covert $A to $A$rowIndex
	// 2. Get value of $A$rowIndex and replace it in the string
	// i.e. $value >= $E
	// first $value has been taken cared before to actual value like 100
	// Here change $E to $E$8, Then get $E$8 value. Replace it in string like
	// 100 >= 80
	public String replaceExpressionWithCellValue(String attrValue,
			int rowIndex, Sheet sheet) {

		int ibegin = 0;
		int ifind = 0;
		int iblank = 0;
		String temp_str;
		String find_str;
		String replace_str;
		while ((ifind = attrValue.indexOf('$', ibegin)) > 0) {
			iblank = attrValue.indexOf(' ', ifind);
			if (iblank > 0) {
				find_str = attrValue.substring(ifind, iblank);
			} else {
				find_str = attrValue.substring(ifind);
			}
			if (find_str.indexOf('$', 1) < 0) // only $A
			{
				temp_str = find_str + "$" + (rowIndex + 1);
			} else
				temp_str = find_str;
			replace_str = getCellValueWithoutFormat(TieWebSheetUtility.getCellByReference(temp_str, sheet));
			if (replace_str == null)
				replace_str = "";
			attrValue = attrValue.replace(find_str, replace_str);

			ibegin = ifind + 1;

		}
		return attrValue;
	}

	public Map<String, CellRangeAddress> indexMergedRegion(Sheet sheet1) {

		int numRegions = sheet1.getNumMergedRegions();
		Map<String, CellRangeAddress> cellRangeMap = new HashMap<String, CellRangeAddress>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				cellRangeMap.put("$" + caddress.getFirstColumn() + "$"
						+ caddress.getFirstRow(), caddress);
			}
		}
		return cellRangeMap;
	}

	public List<String> skippedRegionCells(Sheet sheet1) {
		int numRegions = sheet1.getNumMergedRegions();
		List<String> skipCellList = new ArrayList<String>();
		for (int i = 0; i < numRegions; i++) {

			CellRangeAddress caddress = sheet1.getMergedRegion(i);
			if (caddress != null) {
				for (int col = caddress.getFirstColumn(); col <= caddress
						.getLastColumn(); col++) {
					for (int row = caddress.getFirstRow(); row <= caddress
							.getLastRow(); row++) {
						if ((col == caddress.getFirstColumn())
								&& (row == caddress.getFirstRow()))
							continue;
						skipCellList.add("$" + col + "$" + row);
					}
				}
			}
		}
		debug("skipCellList = "+skipCellList);
		return skipCellList;
	}

    public void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }    	
	// set up facesCell's attribute from poiCell and others.
	public void convertCell(SheetConfiguration sheetConfig, FacesCell fcell,Cell poiCell,
			int rowindex, int initRows, int bodyTopRow, boolean repeatZone,
			Map<String, CellRangeAddress> cellRangeMap) {
		boolean bodyPopulated = sheetConfig.isBodyPopulated();
		List<CellFormAttributes> cellAttributes = findCellAttributesWithOffset(
				sheetConfig, poiCell, initRows, bodyTopRow,
				repeatZone);
		if (cellAttributes != null) {
			for (CellFormAttributes attr : cellAttributes) {
				String attrType = attr.getType().trim();
				if (attrType.equalsIgnoreCase("load") && (!bodyPopulated)) {
					String attrValue = attr.getValue();
					attrValue = attrValue.replace("$rowIndex", rowindex + "");
					if (attrValue.contains("#{")) {
						attrValue = FacesUtility.evaluateExpression(attrValue,
								String.class);
						setCellValue(poiCell, attrValue);
					}
				} else if (attrType.equalsIgnoreCase("input")) {
					String attrValue = attr.getValue().toLowerCase();
					fcell.setInputType(attrValue);
					if ((attrValue != null) && (!attrValue.isEmpty())
							&& (!attrValue.equalsIgnoreCase("textarea"))) {
						fcell.setStyle("text-align: right;");
					}
				}
			}
		}
		CellRangeAddress caddress = null;
		String key = "$" + poiCell.getColumnIndex() + "$" + poiCell.getRowIndex(); 
		caddress = cellRangeMap.get(key);
		if (caddress != null) {
			// has col or row span
			fcell.setColspan((caddress.getLastColumn()
					- caddress.getFirstColumn() + 1)
					);
			fcell.setRowspan((caddress.getLastRow()
					- caddress.getFirstRow() + 1)
					);
		}
	}

	
	public String getRowStyle(Workbook wb, Cell poiCell, String inputType, float rowHeight ) {

		CellStyle cellStyle = poiCell.getCellStyle();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			float maxHeight = rowHeight;
			if (!inputType.isEmpty()) {
				maxHeight = Math.min( font.getFontHeightInPoints() + 6, rowHeight);
			}	
			return "height:" +  TieWebSheetUtility.pointsToPixels(maxHeight)+"px;";
		}
		return "";
	}
	public String getCellFontStyle(Workbook wb, Cell poiCell,String inputType, float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			short fontIdx = cellStyle.getFontIndex();
			Font font = wb.getFontAt(fontIdx);
			if (font.getItalic())
				webStyle.append("font-style: italic;");
			webStyle.append("font-size: " + font.getFontHeightInPoints()
					+ "pt;");
			webStyle.append("font-weight:" + font.getBoldweight() + ";");
			
			String decoration="";
			if (font.getUnderline()!= 0) decoration += " underline";
			if (font.getStrikeout()) decoration += " line-through";
			if (decoration.length()>0)
				webStyle.append("text-decoration:" + decoration + ";");
		    short[] rgbfix={256,256,256};
			if (font instanceof HSSFFont)
			{
			   HSSFColor color = ((HSSFFont) font).getHSSFColor((HSSFWorkbook)wb);
			   if (color != null)  rgbfix = color.getTriplet();
			}
			else if (font instanceof XSSFFont)
			{
			   XSSFColor color = ((XSSFFont) font).getXSSFColor();
			   if (color != null) {
				   rgbfix = TieWebSheetUtility.getTripletFromXSSFColor(color);
			   }
			}
			if (rgbfix[0]!=256) 
			webStyle.append("color:rgb("+ FacesUtility.strJoin(rgbfix,",") +");");
			
		}
		return webStyle.toString();

	}

	public String getCellStyle(Workbook wb, Cell poiCell, String inputType) {

		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			if (!inputType.isEmpty()) {
				webStyle.append(getAlignmentFromCell(poiCell,cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(poiCell,cellStyle));
			};	
			webStyle.append(getBgColorFromCell(wb, poiCell,cellStyle));
		} else {
//			webStyle.append(getAlignmentFromCellType(poiCell));
		}
		return webStyle.toString();

	}

	public String getColumnStyle(Workbook wb, FacesCell fcell, Cell poiCell, float rowHeight) {

		String inputType = fcell.getInputType();
		CellStyle cellStyle = poiCell.getCellStyle();
		StringBuffer webStyle = new StringBuffer();
		if (cellStyle != null) {
			if (fcell.isContainPic()) {
				webStyle.append("vertical-align: top;");
			} else {	
				webStyle.append(getAlignmentFromCell(poiCell,cellStyle));
				webStyle.append(getVerticalAlignmentFromCell(poiCell,cellStyle));
			}	
			webStyle.append(getBgColorFromCell(wb, poiCell,cellStyle));
			webStyle.append(getRowStyle(wb, poiCell, inputType, rowHeight));			
		} else {
			webStyle.append(getAlignmentFromCellType(poiCell));
		}
		return webStyle.toString();

	}


	private String getAlignmentFromCell(Cell poiCell, CellStyle cellStyle) {
		
		String style="";
		switch (cellStyle.getAlignment()) {
		case CellStyle.ALIGN_LEFT: {
			style = "text-align: left;";
			break;
		}
		case CellStyle.ALIGN_RIGHT: {
			style = "text-align: right;";
			break;
		}
		case CellStyle.ALIGN_CENTER: {
			style = "text-align: center;";
			break;
		}
		case CellStyle.ALIGN_GENERAL: {
			style = getAlignmentFromCellType(poiCell);
			break;
		}
		}		
		return style;
	}
	private String getVerticalAlignmentFromCell(Cell poiCell, CellStyle cellStyle) {
		
		String style="";
		switch (cellStyle.getVerticalAlignment()) {
		case CellStyle.VERTICAL_TOP: {
			style ="vertical-align: top;";
			break;
		}
		case CellStyle.VERTICAL_CENTER: {
			style = "vertical-align: middle;";
			break;
		}
		case CellStyle.VERTICAL_BOTTOM: {
			style = "vertical-align: bottom;";
			break;
		}
		}
		return style;
	}	
	
	private String getBgColorFromCell(Workbook wb, Cell poiCell, CellStyle cellStyle) {
		
		String style="";
		if (poiCell instanceof HSSFCell) {
			int bkColorIndex = cellStyle.getFillForegroundColor();
			HSSFColor color = HSSFColor.getIndexHash().get(bkColorIndex);
			if (color != null ) {
				// correct color for customPalette
			    HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
			    HSSFColor color2 = palette.getColor(bkColorIndex);			
				if (!color.getHexString().equalsIgnoreCase(color2.getHexString()))
					color = color2;
//				String hexStr = color.getHexString(); 
//				if (poiCell.getRowIndex() == 3 && poiCell.getColumnIndex() == 0) {
//					System.out.println(" hex str  = "+hexStr);					
//				}
//				if (!hexStr.equalsIgnoreCase("0:0:0")) {
//					if (hexStr.equalsIgnoreCase("FFFF:FFFF:FFFF")) { 
//						System.out.println(" poiCell row = "+poiCell.getRowIndex() + " col = "+ poiCell.getColumnIndex());					
//						style ="background-color:rgb(0,0,0);";
//					}
//					else
						style ="background-color:rgb("
								+ FacesUtility.strJoin(color.getTriplet(), ",")
								+ ");";
//				}
			}	
		} else if (poiCell instanceof XSSFCell) {
			XSSFColor color = ((XSSFCell) poiCell).getCellStyle()
					.getFillForegroundColorColor();
			if (color != null)
				style="background-color:rgb("
						+ FacesUtility.strJoin(TieWebSheetUtility
								.getTripletFromXSSFColor(color), ",")
						+ ");";
		}
		return style;
	}
	
	//additionalWidth is to calculate extra width outside spreadsheet for layout purpose
	//e.g. lineNumberColumnWidth and addRowColumnWidth
	public int calcTotalWidth(Sheet sheet1, int firstCol, int lastCol, int additionalWidth) {

		int totalWidth = additionalWidth;
		for (int i = firstCol; i <= lastCol; i++) {

			totalWidth += sheet1.getColumnWidth(i);
		}
		return totalWidth;
	}

	public void setupCellStyle(Workbook wb, Sheet sheet1, FacesCell fcell, Cell poiCell,
			float rowHeight) {

		CellStyle cellStyle = poiCell.getCellStyle();
		if ((cellStyle != null) && (!cellStyle.getLocked())) {
			// not locked
			if (fcell.getInputType().isEmpty())
				fcell.setInputType(getInputTypeFromCellType(poiCell));
		}
		String webStyle = getCellStyle(wb, poiCell, fcell.getInputType()) 
				+ getCellFontStyle(wb, poiCell, fcell.getInputType(), rowHeight) 
				+ getRowStyle(wb, poiCell, fcell.getInputType(), rowHeight);
		fcell.setStyle(webStyle);
		fcell.setColumnStyle(getColumnStyle(wb, fcell, poiCell, rowHeight));
	}

	private String getAlignmentFromCellType(Cell poiCell) {

		switch (poiCell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			return "text-align: right;";
		case Cell.CELL_TYPE_NUMERIC:
			return "text-align: right;";
		}
		return "";

	}

	private String getInputTypeFromCellType(Cell poiCell) {

		String inputType = "Text";
		switch (poiCell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			inputType = "Double";
			break;
		}
		return inputType;

	}

	public int[] getRowColFromComponentAttributes(UIComponent target) {

		int rowIndex = (Integer) target.getAttributes().get("data-row");
		int colIndex = (Integer) target.getAttributes().get("data-column");
	debug("getRowColFromComponentAttributes rowindex = "+rowIndex+" colindex = "+colIndex);		
		int[] list = { rowIndex, colIndex };
		return list;
	}
	
	
//	public int[] getRowColFromComponentName(UIComponent component) {
//		String[] parts = component.getClientId().split(":");
//		int row = Integer.parseInt(parts[parts.length - 2]);
//		int col = Integer.parseInt(parts[parts.length - 1].substring(6));
//		int[] list = { row, col };
//		return list;
//	}

	public String[] getRowColFromExcelReferenceName(String excelRef) {
		String[] parts = excelRef.split("\\$");
		String[] list = { "", "" };
		int i = parts.length;
		if (i > 1)
			list[1] = parts[1]; // column
		if (i > 2)
			list[0] = parts[2]; // row
		return list;
	}

	public boolean getRepeatBodyFromConfig(SheetConfiguration sheetConfig) {
		boolean repeatbody = false;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			repeatbody = true;
		}
		return repeatbody;
	}

	public int getInitRowsFromConfig(SheetConfiguration sheetConfig) {
		int initRows = 1;
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			initRows = sheetConfig.getBodyInitialRows();
			if (initRows < 1)
				initRows = 1;
		}
		return initRows;
	}

	public int getBodyBottomFromConfig(SheetConfiguration sheetConfig,
			int initRows) {

		int bottom = sheetConfig.getBodyCellRange().getBottomRow();
		if (sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat")) {
			if (initRows > 1)
				bottom = bottom + initRows - 1;
		}
		return bottom;
	}

	public String getCellValueWithConfig(String targetCell, int datarow,
			int initialRows, SheetConfiguration sheetConfig, Sheet sheet) {
		Cell cell = getCellReferenceWithConfig(targetCell, datarow,
				initialRows, sheetConfig, sheet);
		if (cell != null) {
			return getCellValueWithoutFormat(cell);
		}
		return "";
	}


	public Cell getCellReferenceWithConfig(String targetCell, int datarow,
			int initialRows, SheetConfiguration sheetConfig, Sheet sheet) {

		String[] rowcol = getRowColFromExcelReferenceName(targetCell);
		if (rowcol[0].isEmpty()) {
			if (rowcol[1].isEmpty())
				return null; // both empty meaning not valid targetcell
			targetCell = "$"
					+ rowcol[1]
					+ "$"
					+ (datarow + sheetConfig.getBodyCellRange().getTopRow() + 1);
		} else {
			int row = Integer.parseInt(rowcol[0]);
			if ((sheetConfig.getFormBodyType().equalsIgnoreCase("Repeat"))
					&& (row > (sheetConfig.getBodyCellRange().getTopRow() + 1))) {
				targetCell = "$" + rowcol[1] + "$" + (row + initialRows - 1);
			}
		}
		Cell cell = TieWebSheetUtility.getCellByReference(targetCell, sheet);
		return cell;
	}

	public FacesCell getFacesCellFromBodyRow(int bodyrow, int bodycol,
			List<FacesRow> bodyRows) {
		FacesCell cell = null;
		try {
			if (bodyRows.get(bodyrow).getCells().size() > bodycol)
			cell =  bodyRows.get(bodyrow).getCells().get(bodycol);
		} catch (Exception e) {
			debug("Web Form WebFormHelper getFacesCellFromBodyRow Error bodyrow = "
					+ bodyrow + " bodycol = " + bodycol + "; error = "
					+ e.getLocalizedMessage());
		}
		return cell;
	}

	
	
	public Cell getPoiCellWithRowColFromCurrentPage(int rowIndex, int colIndex) {
		if (parent.getWb()!=null) {
			return getPoiCellFromSheet(rowIndex, colIndex, parent.getWb().getSheetAt(parent.getWb().getActiveSheetIndex()));
		}
		return null;
	}
	
	public Cell getPoiCellWithRowColFromTab(int rowIndex, int colIndex, String tabName) {
		if (parent.getWb()!=null) {
			return getPoiCellFromSheet(rowIndex, colIndex, parent.getWb().getSheet(parent.getSheetConfigMap().get(tabName).getSheetName()));			
		}
		return null;
	}

	private Cell getPoiCellFromSheet(int rowIndex, int colIndex, Sheet sheet1) {
		if ((sheet1 != null) && (sheet1.getRow(rowIndex) != null) ) 
			return sheet1.getRow(rowIndex).getCell(colIndex);
		return null;
	}	
	
	public FacesCell getFacesCellWithRowColFromCurrentPage(int rowIndex, int colIndex) {
		if (parent.getBodyRows()!=null) {
			int top = parent.getCurrentTopRow();
			int left = parent.getCurrentLeftColumn();
			List<FacesCell> cellList = parent.getBodyRows().get(rowIndex - top).getCells();
			return parent.getBodyRows().get(rowIndex - top).getCells().get(colIndex - left);
		}
		return null;
	}		
	

}
