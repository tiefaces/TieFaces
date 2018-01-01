package org.tiefaces.components.websheet.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.dataobjects.CellAttributesMap;
import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;
import org.tiefaces.components.websheet.utility.ParserUtility;

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
		assertFalse(ParserUtility.isCommandString("hello"));
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
		assertEquals("calendar", type);

		List<CellFormAttributes> attrs = cellAttributesMap
				.getCellInputAttributes().get(key);

		assertEquals(3, attrs.size());
		assertEquals("showOn", attrs.get(0).getType());
		assertEquals("button", attrs.get(0).getValue());
		assertEquals("pattern", attrs.get(1).getType());
		assertEquals("yyyy/MM/dd", attrs.get(1).getValue());
		assertEquals("readonlyInput", attrs.get(2).getType());
		assertEquals("true", attrs.get(2).getValue());

		String newComment2 = "$widget.inputnumber{symbol=\" years\" symbolPosition=\"s\" minValue=\"0\" maxValue=\"999\" decimalPlaces=\"2\"}";
		ParserUtility.parseWidgetAttributes(cell2, newComment2,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		String key2 = "sheet1!$1$0";
		String type2 = (String) cellAttributesMap.getCellInputType().get(
				key2);
		assertEquals("inputnumber", type2);

		List<CellFormAttributes> attrs2 = cellAttributesMap
				.getCellInputAttributes().get(key2);

		assertEquals(5, attrs2.size());
		assertEquals("symbol", attrs2.get(0).getType());
		assertEquals(" years", attrs2.get(0).getValue());
		assertEquals("symbolPosition", attrs2.get(1).getType());
		assertEquals("s", attrs2.get(1).getValue());
		assertEquals("minValue", attrs2.get(2).getType());
		assertEquals("0", attrs2.get(2).getValue());
		
		wb.close();

	}

	@Test
	public final void testParseCommandAttributes() throws Exception {
		// String command =
		// "tie:each(items=\"departments\", var=\"department\", length=\"8\" allowAdd=\"true\")";
		String commandAttr = "items=\"departments\", var=\"department\", length=\"8\" allowAdd=\"true\"";
		Map<String, String> attrs = ParserUtility
				.parseCommandAttributes(commandAttr);
		assertEquals(4, attrs.size());
		assertEquals("departments", attrs.get("items"));
		assertEquals("department", attrs.get("var"));
		assertEquals("8", attrs.get("length"));
		assertEquals("true", attrs.get("allowAdd"));

	}

	@Test
	public final void testParseInputAttributes() throws Exception {

		// comment = $widget.dropdown{itemLabels="Male;Female" itemValues="M;F"
		// }

		List<CellFormAttributes> attrs = new ArrayList<CellFormAttributes>();
		String values = "itemLabels=\"Male;Female\" itemValues=\"M;F\"";

		ParserUtility.parseInputAttributes(attrs, values);
		assertEquals(2, attrs.size());
		assertEquals("itemLabels", attrs.get(0).getType());
		assertEquals("Male;Female", attrs.get(0).getValue());
		assertEquals("itemValues", attrs.get(1).getType());
		assertEquals("M;F", attrs.get(1).getValue());
		
		
	    String controlAttrs = " symbol=\" years\" symbolPosition=\"s\" minValue=\"0\" maxValue=\"999\" decimalPlaces=\"0\"  ";
	    List<CellFormAttributes> clist = new ArrayList<CellFormAttributes>();
	    
	    ParserUtility.parseInputAttributes(clist, controlAttrs);        
	    CellFormAttributes cattr = clist.get(0);
	    assertEquals("symbol", cattr.getType());
	    assertEquals(" years", cattr.getValue());
	    		

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
		assertEquals("dropdown", type);

		Map<String, String> selectmap = cellAttributesMap
				.getCellSelectItemsAttributes().get(key);

		assertEquals("M", selectmap.get("Male"));
		assertEquals("F", selectmap.get("Female"));

		String newComment2 = "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}";
		ParserUtility.parseWidgetAttributes(cell2, newComment2,
				cellAttributesMap);
		// key = sheetName!$columnIndex$rowIndex
		String key2 = "sheet1!$1$0";
		String type2 = (String) cellAttributesMap.getCellInputType().get(
				key2);
		assertEquals("calendar", type2);
		List<CellFormAttributes> attrs2 = cellAttributesMap
				.getCellInputAttributes().get(key2);
		ParserUtility.parseSpecialAttributes(key2, type2, attrs2,
				cellAttributesMap);
		assertEquals("yyyy/MM/dd", cellAttributesMap.getCellDatePattern().get(key2));
		
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

		assertEquals(3, sheetCommentMap.size());
		assertEquals("this is normal comment.", 
				sheetCommentMap.get(TieConstants.NORMAL_COMMENT_KEY_IN_MAP)
						.get(cellKey));
		assertEquals("$init{emloyee.total}", sheetCommentMap.get("$init").get(cellKey));
		assertEquals("${emloyee.bonus}", sheetCommentMap.get("$").get(cellKey));
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

		assertEquals(1, attrs.size());
		assertEquals("$value>=100", attrs.get(0).getValue());
		assertEquals("payment must be greater than or equal to 100", attrs.get(0).getMessage());

		String newComment2 = "$validate{rule=\"$value<=employee.total\" error=\"payment must be less than total\"}";
		ParserUtility.parseValidateAttributes(cell1, newComment2,
				cellAttributesMap);
		attrs = cellAttributesMap
				.getCellValidateAttributes().get(key);
		assertEquals(2, attrs.size());
		assertEquals("$value<=employee.total", attrs.get(1).getValue());
		assertEquals("payment must be less than total", attrs.get(1).getMessage());
		
		String newComment3 = "$validate{rule=\"#{validationBean.checkRule1($value)}\" error=\"Value must be greater than zero (0).\"}";
		
		assertTrue(ParserUtility.isMethodString(newComment3));
		assertTrue(ParserUtility.isValidateMethodString(newComment3));
		ParserUtility.parseValidateAttributes(cell1, newComment3,
				cellAttributesMap);
		attrs = cellAttributesMap
				.getCellValidateAttributes().get(key);
		assertEquals(3, attrs.size());
		assertEquals("#{validationBean.checkRule1($value)}", attrs.get(2).getValue());
		assertEquals("Value must be greater than zero (0).", attrs.get(2).getMessage());

		
		
		wb.close();

	
	}

	@Test
	public void testFindFirstNonCellNamePosition() throws Exception {
		assertEquals(2, ParserUtility.findFirstNonCellNamePosition("A1 ", 0));
		assertEquals(3, ParserUtility.findFirstNonCellNamePosition("'$A'", 1));	
	}

	@Test
	public void testWildcardToRegex() throws Exception {
		assertTrue("${cost_date}".matches(ParserUtility.wildcardToRegex("${*date*}")));
		assertTrue("${cost_date}".matches(ParserUtility.wildcardToRegex("${?????date*}")));
		//assertFalse("object${cost_date}".matches(ParserUtility.wildcardToRegex("${*date*}")));
		String regx = ParserUtility.wildcardToRegex("${*date*}");
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher("object${cost_date}");				
		assertTrue(matcher.find());
	}

	@Test
	public void testRemoveCharsFromString() throws Exception {
		assertEquals("${cost}", ParserUtility.removeCharsFromString("${cost_date}", 6, 11));
		assertEquals("${cost_date}", ParserUtility.removeCharsFromString("${cost_date}${dropdown}", 12, 23));
		assertEquals("${cost_date} ", ParserUtility.removeCharsFromString("${cost_date} ${dropdown}", 13, 24));
	}

	@Test
	public void testWildcardToRegexStringBoolean() throws Exception {
		assertFalse("object${cost_date}".matches(ParserUtility.wildcardToRegex("${*date*}",true)));
		assertTrue("object${cost_date}".matches(ParserUtility.wildcardToRegex("*${*date*}",true)));
	}


	
	
	
	

}
