/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.tiefaces.common.FacesUtility;
import com.tiefaces.common.TIEConstants;
import com.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import com.tiefaces.components.websheet.dataobjects.CellRange;
import com.tiefaces.components.websheet.dataobjects.SheetConfiguration;

public class TieWebSheetConfigurationHandler {

	private TieWebSheetBean parent = null;

	private static boolean debug = true;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("debug: " + msg);
		}
	}

	// Basically configuration are dived into two parts: 1. form level 2.
	// attributes level.
	// attrCol is the first column which indicate the attributes level starting
	// setting attrCol into variable just for easy extend the form level range

	// attribute column starting index
	// private int attrCol = 9;

	public TieWebSheetConfigurationHandler(TieWebSheetBean parent) {
		super();
		this.parent = parent;
	}

	@SuppressWarnings("serial")
	private Map<String, Integer> buildSchemaMap(int version) {

		Map<String, Integer> schemaMap = new HashMap<String, Integer>() {
			{
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TAB_NAME,
						0);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_SHEET_NAME,
						1);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_HEADER_RANGE,
						2);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_RANGE,
						3);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_FOOTER_RANGE,
						4);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_TYPE,
						5);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_ALLOW_ADD_ROW,
						6);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_INIT_ROWS,
						7);
				put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_PAGE_TYPE,
						8);
			}
		};

		int attributeStartColumn = 9;

		if (version >= 1) {
			schemaMap
					.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_WIDTH,
							9);
			schemaMap
					.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_MAX_ROWS_PER_PAGE,
							10);
			attributeStartColumn = 11;
		}
		if (version >= 2) {
			schemaMap
					.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_BEFORE,
							11);
			schemaMap
					.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_AFTER,
							12);
			attributeStartColumn = 13;
		}
		schemaMap
				.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL,
						attributeStartColumn);
		schemaMap
				.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE,
						attributeStartColumn + 1);
		schemaMap
				.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE,
						attributeStartColumn + 2);
		schemaMap
				.put(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG,
						attributeStartColumn + 3);
		schemaMap.put(
				TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_VERSION,
				version);

		return schemaMap;
	}

	public Map<String, SheetConfiguration> buildConfiguration() {

		Map<String, SheetConfiguration> sheetConfigMap = new LinkedHashMap<String, SheetConfiguration>();
		debug("parent configuration tab = "
				+ parent.getConfigurationTab());
		Sheet sheet1 = parent.getWb().getSheet(parent.getConfigurationTab());

		if (sheet1 == null) // no configuration tab
			return buildConfigurationWithoutTab(sheetConfigMap);
		else
			return buildConfigurationWithTab(sheet1, sheetConfigMap);
	}

	private Map<String, SheetConfiguration> buildConfigurationWithTab(
			Sheet sheet1, Map<String, SheetConfiguration> sheetConfigMap) {

		// Iterate through each rows from configuration sheet
		Iterator<Row> rowIterator = sheet1.iterator();
		String newTabName = null;
		String oldTabName = null;
		int version = 0;
		SheetConfiguration sheetConfig = null;
		Map<String, Integer> schemaMap = null;

		int startRow = 1;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				if (rowCell(row, 0)
						.equalsIgnoreCase(
								TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_VERSION)) {
					version = Integer.parseInt(rowCell(row, 1));
					startRow = 2; // start from row 2 if has version control.
				}
				schemaMap = buildSchemaMap(version);
				debug("startrow = " + startRow + " schemaMap = " + schemaMap);
			} else if (row.getRowNum() >= startRow) { // skip header rows
				// For each row, iterate through each columns
				newTabName = rowCell(
						row,
						schemaMap
								.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TAB_NAME));
				if (oldTabName == null)
					oldTabName = newTabName;
				if (newTabName.isEmpty()) {
					addAttributesToMap(sheetConfig.getCellFormAttributes(),
							row, schemaMap);
				} else {
					if (!newTabName.equalsIgnoreCase(oldTabName)) {
						sheetConfigMap.put(oldTabName, sheetConfig);
						oldTabName = newTabName;
					}
					sheetConfig = new SheetConfiguration();
					sheetConfig.setTabName(newTabName);
					sheetConfig
							.setSheetName(rowCell(
									row,
									schemaMap
											.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_SHEET_NAME)));
					String tempStr = rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_HEADER_RANGE));
					sheetConfig.setFormHeaderRange(tempStr);
					sheetConfig.setHeaderCellRange(new CellRange(tempStr));
					tempStr = rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_RANGE));
					sheetConfig.setFormBodyRange(tempStr);
					sheetConfig.setBodyCellRange(new CellRange(tempStr));
					tempStr = rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_FOOTER_RANGE));
					sheetConfig.setFormFooterRange(tempStr);
					sheetConfig.setFooterCellRange(new CellRange(tempStr));
					// only 2 type allowed: free or repeat
					if (rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_BODY_TYPE))
							.equalsIgnoreCase("Repeat"))
						sheetConfig
								.setFormBodyType(TieWebSheetConstants.TIE_WEBSHEET_FORM_TYPE_REPEAT);
					else
						sheetConfig
								.setFormBodyType(TieWebSheetConstants.TIE_WEBSHEET_FORM_TYPE_FREE);
					tempStr = rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_ALLOW_ADD_ROW));
					if (tempStr.equalsIgnoreCase("TRUE"))
						sheetConfig.setBodyAllowAddRows(true);
					else
						sheetConfig.setBodyAllowAddRows(false);

					tempStr = rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_INIT_ROWS));
					if (tempStr.startsWith("#{")) {
						tempStr = FacesUtility.evaluateExpression(tempStr,
								String.class);
					}
					if (!tempStr.isEmpty())
						sheetConfig.setBodyInitialRows(Integer
								.parseInt(tempStr));
					else
						sheetConfig.setBodyInitialRows(1);
					sheetConfig
							.setFormPageTypeId(rowCell(
									row,
									schemaMap
											.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_PAGE_TYPE)));

					sheetConfig.setMaxRowPerPage(80);
					sheetConfig.setSavedRowsBefore(0);
					sheetConfig.setSavedRowsAfter(0);

					if (version < 1) {
						// version 0
						// sheetConfig.setFormWidth("100%;");
					} else {
						sheetConfig
								.setFormWidth(rowCell(
										row,
										schemaMap
												.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_FORM_WIDTH)));
						tempStr = rowCell(
								row,
								schemaMap
										.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_MAX_ROWS_PER_PAGE))
								.trim();
						if (!tempStr.isEmpty())
							if (Integer.parseInt(tempStr) > 0)
								sheetConfig.setMaxRowPerPage(Integer
										.parseInt(tempStr));

						if (version >= 2) {
							tempStr = rowCell(
									row,
									schemaMap
											.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_BEFORE))
									.trim();
							if (!tempStr.isEmpty())
								if (Integer.parseInt(tempStr) > 0)
									sheetConfig.setSavedRowsBefore(Integer
											.parseInt(tempStr));
							tempStr = rowCell(
									row,
									schemaMap
											.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_SAVED_ROWS_AFTER))
									.trim();
							if (!tempStr.isEmpty())
								if (Integer.parseInt(tempStr) > 0)
									sheetConfig.setSavedRowsAfter(Integer
											.parseInt(tempStr));
						}
					}

					sheetConfig
							.setCellFormAttributes(new HashMap<String, List<CellFormAttributes>>());
					sheetConfig
							.getCellFormAttributes()
							.put(rowCell(
									row,
									schemaMap
											.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)),
									new ArrayList<CellFormAttributes>());
					addAttributesToMap(sheetConfig.getCellFormAttributes(),
							row, schemaMap);
				}
			}
		}
		sheetConfigMap.put(oldTabName, sheetConfig);
		debug("Web Form ConfigurationHandler after iteration sheetConfigmap = "
				+ sheetConfigMap);
		return sheetConfigMap;
	}

	private int verifyLastCell(Row row, int stoppoint) {
		int lastCol = row.getLastCellNum() - 1;
		int col;
		for (col = lastCol; col >= stoppoint; col--) {

			Cell cell = row.getCell(col);
			if ((cell != null) && (cell.getCellType() != Cell.CELL_TYPE_BLANK))
				break;
		}
		return col;
	}

	// build configuration without configuration tab
	// use max rows and max columns (256) for body
	// header/footer set to none
	private Map<String, SheetConfiguration> buildConfigurationWithoutTab(
			Map<String, SheetConfiguration> sheetConfigMap) {

		for (int i = 0; i < parent.getWb().getNumberOfSheets(); i++) {
			Sheet sheet = parent.getWb().getSheetAt(i);

			String tabName = sheet.getSheetName();
			SheetConfiguration sheetConfig = new SheetConfiguration();
			sheetConfig.setTabName(tabName);
			sheetConfig.setSheetName(tabName);
			int leftCol = sheet.getLeftCol();
			int lastRow = sheet.getLastRowNum();
			int firstRow = 0;
			int rightCol = 0;
			int maxRow = 0;
			for (Row row : sheet) {
				if (row.getRowNum() > TIEConstants.TIE_WEB_SHEET_MAX_ROWS)
					break;
				maxRow = row.getRowNum();
				int firstCellNum = row.getFirstCellNum();
				if (firstCellNum >= 0 && firstCellNum < leftCol) {
					leftCol = firstCellNum;
				}
				if ((row.getLastCellNum() - 1) > rightCol) {
					int verifiedcol = verifyLastCell(row, rightCol);
					if (verifiedcol > rightCol)
						rightCol = verifiedcol;
				}
			}
			if (maxRow < lastRow)
				lastRow = maxRow;
			debug("tabName = " + tabName + " maxRow = " + maxRow);

			// header range row set to 0 while column set to first column to max
			// column (FF) e.g. $A$0 : $FF$0
			String tempStr = "$"
					+ TieWebSheetUtility.GetExcelColumnName(leftCol) + "$0 : $"
					+ TieWebSheetUtility.GetExcelColumnName(rightCol) + "$0";
			sheetConfig.setFormHeaderRange(tempStr);
			sheetConfig.setHeaderCellRange(new CellRange(tempStr));
			// body range row set to first row to last row while column set to
			// first column to max column (FF) e.g. $A$1 : $FF$1000
			tempStr = "$" + TieWebSheetUtility.GetExcelColumnName(leftCol)
					+ "$" + (firstRow + 1) + " : $"
					+ TieWebSheetUtility.GetExcelColumnName(rightCol) + "$"
					+ (lastRow + 1);
			sheetConfig.setFormBodyRange(tempStr);
			sheetConfig.setBodyCellRange(new CellRange(tempStr));
			sheetConfig
					.setFormBodyType(TieWebSheetConstants.TIE_WEBSHEET_FORM_TYPE_FREE);
			sheetConfig
					.setCellFormAttributes(new HashMap<String, List<CellFormAttributes>>());

			sheetConfigMap.put(tabName, sheetConfig);
		}
		debug("without config tab = " + sheetConfigMap);

		return sheetConfigMap;
	}

	private String rowCell(Row row, Integer cn) {
		String value = null;
		if (cn != null)
			value = parent.getCellHelper().getCellValueWithFormat(
					row.getCell(cn));
		if (value == null)
			value = "";
		return value.trim();
	}

	private void addAttributesToMap(Map<String, List<CellFormAttributes>> map,
			Row row, Map<String, Integer> schemaMap) {
		List<CellFormAttributes> attributes = map
				.get(rowCell(
						row,
						schemaMap
								.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)));
		if (attributes == null) {
			map.put(rowCell(
					row,
					schemaMap
							.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)),
					new ArrayList<CellFormAttributes>());
			attributes = map
					.get(rowCell(
							row,
							schemaMap
									.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_TARGET_COLUMN_CELL)));
		}
		CellFormAttributes cellattribute = new CellFormAttributes();
		cellattribute
				.setType(rowCell(
						row,
						schemaMap
								.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_TYPE)));
		cellattribute
				.setValue(rowCell(
						row,
						schemaMap
								.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_ATTRIBUTE_VALUE)));
		cellattribute
				.setMessage(rowCell(
						row,
						schemaMap
								.get(TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SCHEMA_VALIDATION_ERROR_MSG)));
		attributes.add(cellattribute);
	}

}
