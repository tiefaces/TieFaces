/**
 * 
 */
package org.tiefaces.components.websheet.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.tiefaces.components.websheet.dataobjects.TieCommandAlias;

/**
 * @author jason jiang
 *
 */
public class ConfigurationUtilityTest {

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#getFullNameFromRow(org.apache.poi.ss.usermodel.Row)}.
     */
    @Test
    public void testGetFullNameFromRow() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#reBuildUpperLevelFormula(org.tiefaces.components.websheet.configuration.ConfigBuildRef, java.lang.String)}.
     */
    @Test
    public void testReBuildUpperLevelFormula() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#buildCellFormulaForShiftedRows(org.apache.poi.ss.usermodel.Sheet, org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook, org.tiefaces.components.websheet.configuration.ShiftFormulaRef, org.apache.poi.ss.usermodel.Cell, java.lang.String)}.
     */
    @Test
    public void testBuildCellFormulaForShiftedRows() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#gatherRowsMappingByFullName(org.tiefaces.components.websheet.configuration.ConfigBuildRef, java.lang.String)}.
     */
    @Test
    public void testGatherRowsMappingByFullName() throws Exception {
        
    }

    /**
	     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#changeIndexNumberInShiftMap(java.util.Map, java.util.Map)}.
	     */
	    @Test
	    public void testChangeIndexNumberInShiftMap() throws Exception {
	        
	    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#increaseUpperLevelFinalLength(java.util.Map, java.lang.String, int)}.
     */
    @Test
    public void testIncreaseUpperLevelFinalLength() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#increaseIndexNumberInHiddenColumn(org.tiefaces.components.websheet.configuration.ConfigBuildRef, int, java.lang.String, java.util.Map)}.
     */
    @Test
    public void testIncreaseIndexNumberInHiddenColumn() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#setFullNameInHiddenColumn(org.apache.poi.ss.usermodel.Row, java.lang.String)}.
     */
    @Test
    public void testSetFullNameInHiddenColumn() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#getOriginalRowNumInHiddenColumn(org.apache.poi.ss.usermodel.Row)}.
     */
    @Test
    public void testGetOriginalRowNumInHiddenColumn() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#setOriginalRowNumInHiddenColumn(org.apache.poi.ss.usermodel.Row, int)}.
     */
    @Test
    public void testSetOriginalRowNumInHiddenColumn() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#findParentRowsMappingFromShiftMap(java.lang.String[], java.util.Map)}.
     */
    @Test
    public void testFindParentRowsMappingFromShiftMap() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#findChildRowsMappingFromShiftMap(java.lang.String, java.util.NavigableMap)}.
     */
    @Test
    public void testFindChildRowsMappingFromShiftMap() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#findItemInCollection(java.util.Collection, int)}.
     */
    @Test
    public void testFindItemInCollection() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#buildCurrentRange(org.tiefaces.components.websheet.configuration.ConfigRange, org.apache.poi.ss.usermodel.Sheet, int)}.
     */
    @Test
    public void testBuildCurrentRange() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#isStaticRow(org.tiefaces.components.websheet.configuration.ConfigRange, int)}.
     */
    @Test
    public void testIsStaticRow() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#isStaticRowRef(org.tiefaces.components.websheet.configuration.ConfigRange, org.apache.poi.ss.usermodel.Row)}.
     */
    @Test
    public void testIsStaticRowRef() throws Exception {
        
    }

    /**
     * Test method for {@link org.tiefaces.components.websheet.utility.ConfigurationUtility#getFullDataCollectNameFromFullName(java.lang.String)}.
     */
    @Test
    public void testGetFullDataCollectNameFromFullName() throws Exception {
        
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

	@SuppressWarnings("resource")
	@Test
	public void testBuildSheetCommentFromAlias() throws Exception {

			
			Workbook wb = new XSSFWorkbook();
			// XSSFEvaluationWorkbook wbWrapper = XSSFEvaluationWorkbook
			// .create((XSSFWorkbook) wb);
			XSSFSheet sheet = (XSSFSheet) wb.createSheet("sheet1");
			// Create a row and put some cells in it. Rows are 0 based.
			XSSFRow row1 = sheet.createRow((short) 0);
			// Create a cell and put a value in it.
			Cell cell1 = row1.createCell(0);
			cell1.setCellValue("${employee.birthDate}");
			Cell cell2 = row1.createCell(1);
			cell2.setCellValue("${employee.birthdate}");
			
			List<TieCommandAlias> tieCommandAliasList = new ArrayList<>();
			
			String comment1 = "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}";
			tieCommandAliasList.add(new TieCommandAlias("*Date*", comment1)); 
			
			ConfigurationUtility.buildSheetCommentFromAlias((Sheet) sheet, tieCommandAliasList);
			
			String outStr = row1.getCell(0).getCellComment().getString().getString();
			assertEquals(comment1, outStr);
			assertNull( row1.getCell(1).getCellComment());
		}


}
