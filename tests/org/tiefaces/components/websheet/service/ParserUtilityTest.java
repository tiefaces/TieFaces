package org.tiefaces.components.websheet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;

/**
 * 
 * @author Jason Jiang
 *
 */
public class ParserUtilityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testIsCommandString() throws Exception {
		assertTrue(ParserUtility.isCommandString("tie:form"));
		assertFalse(ParserUtility.isCommandString(""));
		assertFalse(ParserUtility.isCommandString(null));
	}

	@Test
	public final void testIsMethodString() throws Exception {
		assertTrue(ParserUtility.isMethodString("$save{department.name}"));
		assertFalse(ParserUtility.isMethodString("${department.name}"));
		assertFalse(ParserUtility.isMethodString(null));
	}

	@Test
	public final void testIsEmptyMethodString() throws Exception {
		assertTrue(ParserUtility.isEmptyMethodString("${department.name}"));
		assertFalse(ParserUtility
				.isEmptyMethodString("$save{department.name}"));
		assertFalse(ParserUtility.isEmptyMethodString(null));
	}

	@Test
	public final void testIsWidgetMethodString() throws Exception {
		assertTrue(ParserUtility
				.isWidgetMethodString("$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}"));
		assertFalse(ParserUtility
				.isWidgetMethodString("tie:each(items=\"departments\", var=\"department\", length=\"8\" allowAdd=\"true\")"));
		assertFalse(ParserUtility.isWidgetMethodString(""));
		assertFalse(ParserUtility.isWidgetMethodString(null));
	}

	@Test
	public final void testParseWidgetAttributes() throws Exception {

		Workbook wb = new XSSFWorkbook();
		// XSSFEvaluationWorkbook wbWrapper = XSSFEvaluationWorkbook
		// .create((XSSFWorkbook) wb);
		XSSFSheet sheet = (XSSFSheet) wb.createSheet("sheet1");
		// Create a row and put some cells in it. Rows are 0 based.
		XSSFRow row1 = sheet.createRow((short) 0);
		// Create a cell and put a value in it.
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue("A1");

		Cell cell2 = row1.createCell(1);
		cell2.setCellValue("A2");

		CellAttributesMap cellAttributesMap = createCellAtrributesMap();

		String newComment1 = "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}";
		ParserUtility.parseWidgetAttributes(cell1, newComment1,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		String key = "sheet1!$0$0";
		String type = (String) cellAttributesMap.getCellInputType()
				.get(key);
		assertEquals(type, "calendar");

		List<CellFormAttributes> attrs = cellAttributesMap
				.getCellInputAttributes().get(key);

		assertEquals(attrs.size(), 3);
		assertEquals(attrs.get(0).getType(), "showOn");
		assertEquals(attrs.get(0).getValue(), "button");
		assertEquals(attrs.get(1).getType(), "pattern");
		assertEquals(attrs.get(1).getValue(), "yyyy/MM/dd");
		assertEquals(attrs.get(2).getType(), "readonlyInput");
		assertEquals(attrs.get(2).getValue(), "true");

		String newComment2 = "$widget.inputnumber{symbol=\" years\" symbolPosition=\"s\" minValue=\"0\" maxValue=\"999\" decimalPlaces=\"2\"}";
		ParserUtility.parseWidgetAttributes(cell2, newComment2,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		String key2 = "sheet1!$1$0";
		String type2 = (String) cellAttributesMap.getCellInputType().get(
				key2);
		assertEquals(type2, "inputnumber");

		List<CellFormAttributes> attrs2 = cellAttributesMap
				.getCellInputAttributes().get(key2);

		assertEquals(attrs2.size(), 5);
		assertEquals(attrs2.get(0).getType(), "symbol");
		assertEquals(attrs2.get(0).getValue(), " years");
		assertEquals(attrs2.get(1).getType(), "symbolPosition");
		assertEquals(attrs2.get(1).getValue(), "s");
		assertEquals(attrs2.get(2).getType(), "minValue");
		assertEquals(attrs2.get(2).getValue(), "0");
		
		wb.close();

	}

	@Test
	public final void testParseCommandAttributes() throws Exception {
		// String command =
		// "tie:each(items=\"departments\", var=\"department\", length=\"8\" allowAdd=\"true\")";
		String commandAttr = "items=\"departments\", var=\"department\", length=\"8\" allowAdd=\"true\"";
		Map<String, String> attrs = ParserUtility
				.parseCommandAttributes(commandAttr);
		assertEquals(attrs.size(), 4);
		assertEquals(attrs.get("items"), "departments");
		assertEquals(attrs.get("var"), "department");
		assertEquals(attrs.get("length"), "8");
		assertEquals(attrs.get("allowAdd"), "true");

	}

	@Test
	public final void testParseInputAttributes() throws Exception {

		// comment = $widget.dropdown{itemLabels="Male;Female" itemValues="M;F"
		// }

		List<CellFormAttributes> attrs = new ArrayList<CellFormAttributes>();
		String values = "itemLabels=\"Male;Female\" itemValues=\"M;F\"";

		ParserUtility.parseInputAttributes(attrs, values);
		assertEquals(attrs.size(), 2);
		assertEquals(attrs.get(0).getType(), "itemLabels");
		assertEquals(attrs.get(0).getValue(), "Male;Female");
		assertEquals(attrs.get(1).getType(), "itemValues");
		assertEquals(attrs.get(1).getValue(), "M;F");

	}
	
	private final CellAttributesMap createCellAtrributesMap() {
		return new CellAttributesMap(
				new HashMap<String, Map<String, String>>(),
				new HashMap<String, String>(),
				new HashMap<String, List<CellFormAttributes>>(),
				new HashMap<String, Map<String, String>>(),
				new HashMap<String, String>(),
				new HashMap<String, List<CellFormAttributes>>()
				);
	}

	@Test
	public final void testParseSpecialAttributes() throws Exception {

		Workbook wb = new XSSFWorkbook();
		// XSSFEvaluationWorkbook wbWrapper = XSSFEvaluationWorkbook
		// .create((XSSFWorkbook) wb);
		XSSFSheet sheet = (XSSFSheet) wb.createSheet("sheet1");
		// Create a row and put some cells in it. Rows are 0 based.
		XSSFRow row1 = sheet.createRow((short) 0);
		// Create a cell and put a value in it.
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue("A1");

		Cell cell2 = row1.createCell(1);
		cell2.setCellValue("A2");

		CellAttributesMap cellAttributesMap = createCellAtrributesMap();

		String newComment1 = "$widget.dropdown{itemLabels=\"Male;Female\" itemValues=\"M;F\" }";
		ParserUtility.parseWidgetAttributes(cell1, newComment1,
				cellAttributesMap);
		String key = "sheet1!$0$0";
		String type = (String) cellAttributesMap.getCellInputType()
				.get(key);
		List<CellFormAttributes> attrs = cellAttributesMap
				.getCellInputAttributes().get(key);
		ParserUtility.parseSpecialAttributes(key, type, attrs,
				cellAttributesMap);
		assertEquals(type, "dropdown");

		Map<String, String> selectmap = cellAttributesMap
				.getCellSelectItemsAttributes().get(key);

		assertEquals(selectmap.get("Male"), "M");
		assertEquals(selectmap.get("Female"), "F");

		String newComment2 = "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}";
		ParserUtility.parseWidgetAttributes(cell2, newComment2,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		String key2 = "sheet1!$1$0";
		String type2 = (String) cellAttributesMap.getCellInputType().get(
				key2);
		assertEquals(type2, "calendar");
		List<CellFormAttributes> attrs2 = cellAttributesMap
				.getCellInputAttributes().get(key2);
		ParserUtility.parseSpecialAttributes(key2, type2, attrs2,
				cellAttributesMap);
		assertEquals(cellAttributesMap.getCellDatePattern().get(key2),
				"yyyy/MM/dd");
		
		wb.close();

	}

	@Test
	public final void testParseCommentToMap() throws Exception {

		String cellKey = "sheet1!$0$0";
		Map<String, Map<String, String>> sheetCommentMap = new HashMap<String, Map<String, String>>();
		ParserUtility.parseCommentToMap(cellKey, "$init{emloyee.total}",
				sheetCommentMap, false);
		ParserUtility.parseCommentToMap(cellKey, "${emloyee.bonus}",
				sheetCommentMap, false);
		ParserUtility.parseCommentToMap(cellKey, "this is normal comment.",
				sheetCommentMap, true);

		assertEquals(sheetCommentMap.size(), 3);
		assertEquals(
				sheetCommentMap.get(TieConstants.NORMAL_COMMENT_KEY_IN_MAP)
						.get(cellKey), "this is normal comment.");
		assertEquals(sheetCommentMap.get("$init").get(cellKey),
				"$init{emloyee.total}");
		assertEquals(sheetCommentMap.get("$").get(cellKey),
				"${emloyee.bonus}");
	}

	@Test
	public final void testIsValidateMethodString() throws Exception {
		assertTrue(ParserUtility.isValidateMethodString("$validate{ rule=\"abc\" error=\"error\"}"));
		assertFalse(ParserUtility
				.isValidateMethodString("$save{department.name}"));
		assertFalse(ParserUtility.isValidateMethodString(null));
	}

	@Test
	public final void testParseValidateAttributesCellStringCellAttributesMap()
			throws Exception {
		Workbook wb = new XSSFWorkbook();
		XSSFSheet sheet = (XSSFSheet) wb.createSheet("sheet1");
		XSSFRow row1 = sheet.createRow((short) 0);
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue("A1");

		Cell cell2 = row1.createCell(1);
		cell2.setCellValue("A2");

		CellAttributesMap cellAttributesMap = createCellAtrributesMap();

		String newComment1 = "$validate{rule=\"$value>=100\" error=\"payment must be greater than or equal to 100\"}";
		ParserUtility.parseValidateAttributes(cell1, newComment1,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		String key = "sheet1!$0$0";
		List<CellFormAttributes> attrs = cellAttributesMap
				.getCellValidateAttributes().get(key);

		assertEquals(attrs.size(), 1);
		assertEquals(attrs.get(0).getValue(), "$value>=100");
		assertEquals(attrs.get(0).getMessage(), "payment must be greater than or equal to 100");

		String newComment2 = "$validate{rule=\"$value<=employee.total\" error=\"payment must be less than total\"}";
		ParserUtility.parseValidateAttributes(cell1, newComment2,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		attrs = cellAttributesMap
				.getCellValidateAttributes().get(key);
		assertEquals(attrs.size(), 2);
		assertEquals(attrs.get(1).getValue(), "$value<=employee.total");
		assertEquals(attrs.get(1).getMessage(), "payment must be less than total");
		
		wb.close();

	
	}

}
