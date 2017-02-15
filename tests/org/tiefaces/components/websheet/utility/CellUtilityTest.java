/**
 * 
 */
package org.tiefaces.components.websheet.utility;

import static org.junit.Assert.assertEquals;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;


import org.junit.Test;

/**
 * @author ihsb_developer
 *
 */
public class CellUtilityTest {

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getCellValueWithFormat(org.apache.poi.ss.usermodel.Cell, org.apache.poi.ss.usermodel.FormulaEvaluator, org.apache.poi.ss.usermodel.DataFormatter)}.
	 */
	@Test
	public void testGetCellValueWithFormat() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getCellValueWithoutFormat(org.apache.poi.ss.usermodel.Cell)}.
	 */
	@Test
	public void testGetCellValueWithoutFormat() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#setCellValue(org.apache.poi.ss.usermodel.Cell, java.lang.String)}.
	 */
	@Test
	public void testSetCellValue() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#copyRows(org.apache.poi.ss.usermodel.Sheet, org.apache.poi.ss.usermodel.Sheet, int, int, int, boolean, boolean)}.
	 */
	@Test
	public void testCopyRows() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#copyCell(org.apache.poi.ss.usermodel.Sheet, org.apache.poi.ss.usermodel.Row, org.apache.poi.ss.usermodel.Row, int, boolean)}.
	 */
	@Test
	public void testCopyCell() throws Exception {
		
		
		Workbook wb = new XSSFWorkbook();
		XSSFEvaluationWorkbook wbWrapper = XSSFEvaluationWorkbook
				.create((XSSFWorkbook) wb);	
		CreationHelper createHelper = wb.getCreationHelper();		
		Sheet sheet1 = wb.createSheet("sheet1");
		// Create a row and put some cells in it. Rows are 0 based.
		Row row1 = sheet1.createRow((short) 0);
		// Create a cell and put a value in it.
		row1.createCell(0).setCellValue(1);
		row1.createCell(1).setCellValue(true);
		row1.createCell(2).setCellValue(
				createHelper.createRichTextString("This is a string"));
		row1.createCell(3).setCellFormula("A1+A1");
		row1.createCell(4).setCellFormula("A1+X1");
		row1.createCell(5).setCellType(CellType.BLANK);

		
		Sheet sheet2 = wb.createSheet("sheet2");
		Row row2 = sheet2.createRow((short) 0);
		
		Cell cell1 = row1.getCell(0);
		Cell cell2 = CellUtility.copyCell(sheet2, row1, row2, 0, true);
		assertEquals(cell1.getCellTypeEnum(), cell2.getCellTypeEnum());
		assertEquals(cell1.getNumericCellValue(), cell2.getNumericCellValue(),0.001);
		
		cell1 = row1.getCell(1);
		cell2 = CellUtility.copyCell(sheet2, row1, row2, 1, true);
		assertEquals(cell1.getCellTypeEnum(), cell2.getCellTypeEnum());
		assertEquals(cell1.getBooleanCellValue(), cell2.getBooleanCellValue());
		
		cell1 = row1.getCell(2);
		cell2 = CellUtility.copyCell(sheet2, row1, row2, 2, true);
		assertEquals(cell1.getCellTypeEnum(), cell2.getCellTypeEnum());
		assertEquals(cell1.getStringCellValue(), cell2.getStringCellValue());

		cell1 = row1.getCell(3);
		cell2 = CellUtility.copyCell(sheet2, row1, row2, 3, true);
		assertEquals(cell1.getCellTypeEnum(), cell2.getCellTypeEnum());
		assertEquals(cell1.getCellFormula(), cell2.getCellFormula());

		cell1 = row1.getCell(4);
		cell2 = CellUtility.copyCell(sheet2, row1, row2, 4, true);
		assertEquals(cell1.getCellTypeEnum(), cell2.getCellTypeEnum());
		assertEquals(cell1.getCellFormula(), cell2.getCellFormula());
		
		cell1 = row1.getCell(5);
		cell2 = CellUtility.copyCell(sheet2, row1, row2, 5, true);
		assertEquals(cell1.getCellTypeEnum(), cell2.getCellTypeEnum());
		
		wb.close();
		
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getCellIndexNumberKey(org.apache.poi.ss.usermodel.Cell)}.
	 */
	@Test
	public void testGetCellIndexNumberKeyCell() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getCellIndexNumberKey(int, int)}.
	 */
	@Test
	public void testGetCellIndexNumberKeyIntInt() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getCellIndexLetterKey(java.lang.String, int)}.
	 */
	@Test
	public void testGetCellIndexLetterKeyStringInt() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getCellIndexLetterKey(int, int)}.
	 */
	@Test
	public void testGetCellIndexLetterKeyIntInt() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#replaceExpressionWithCellValue(java.lang.String, int, org.apache.poi.ss.usermodel.Sheet)}.
	 */
	@Test
	public void testReplaceExpressionWithCellValue() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#indexMergedRegion(org.apache.poi.ss.usermodel.Sheet)}.
	 */
	@Test
	public void testIndexMergedRegion() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#skippedRegionCells(org.apache.poi.ss.usermodel.Sheet)}.
	 */
	@Test
	public void testSkippedRegionCells() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#convertCell(org.tiefaces.components.websheet.configuration.SheetConfiguration, org.tiefaces.components.websheet.dataobjects.FacesCell, org.apache.poi.ss.usermodel.Cell, java.util.Map, int, org.tiefaces.components.websheet.dataobjects.CellAttributesMap, java.lang.String)}.
	 */
	@Test
	public void testConvertCell() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getRowColFromComponentAttributes(javax.faces.component.UIComponent)}.
	 */
	@Test
	public void testGetRowColFromComponentAttributes() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getInitRowsFromConfig(org.tiefaces.components.websheet.configuration.SheetConfiguration)}.
	 */
	@Test
	public void testGetInitRowsFromConfig() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getBodyBottomFromConfig(org.tiefaces.components.websheet.configuration.SheetConfiguration)}.
	 */
	@Test
	public void testGetBodyBottomFromConfig() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getFacesCellFromBodyRow(int, int, java.util.List, int, int)}.
	 */
	@Test
	public void testGetFacesCellFromBodyRow() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getPoiCellWithRowColFromCurrentPage(int, int, org.apache.poi.ss.usermodel.Workbook)}.
	 */
	@Test
	public void testGetPoiCellWithRowColFromCurrentPage() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellUtility#getPoiCellFromSheet(int, int, org.apache.poi.ss.usermodel.Sheet)}.
	 */
	@Test
	public void testGetPoiCellFromSheet() throws Exception {
		
	}

}
