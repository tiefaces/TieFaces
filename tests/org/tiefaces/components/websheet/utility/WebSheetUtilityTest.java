/**
 * 
 */
package org.tiefaces.components.websheet.utility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author JASON
 *
 */
public class WebSheetUtilityTest {

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#getExcelColumnName(int)}.
	 */
	@Test
	public void testGetExcelColumnName() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#getFullCellRefName(org.apache.poi.ss.usermodel.Sheet, org.apache.poi.ss.usermodel.Cell)}.
	 */
	@Test
	public void testGetFullCellRefNameSheetCell() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#getFullCellRefName(java.lang.String, int, int)}.
	 */
	@Test
	public void testGetFullCellRefNameStringIntInt() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#getSheetNameFromFullCellRefName(java.lang.String)}.
	 */
	@Test
	public void testGetSheetNameFromFullCellRefName() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#removeSheetNameFromFullCellRefName(java.lang.String)}.
	 */
	@Test
	public void testRemoveSheetNameFromFullCellRefName() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#convertColToInt(java.lang.String)}.
	 */
	@Test
	public void testConvertColToInt() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#getCellByReference(java.lang.String, org.apache.poi.ss.usermodel.Sheet)}.
	 */
	@Test
	public void testGetCellByReference() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#pixel2WidthUnits(int)}.
	 */
	@Test
	public void testPixel2WidthUnits() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#widthUnits2Pixel(int)}.
	 */
	@Test
	public void testWidthUnits2Pixel() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#heightUnits2Pixel(short)}.
	 */
	@Test
	public void testHeightUnits2Pixel() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#widthUnits2Millimetres(short)}.
	 */
	@Test
	public void testWidthUnits2Millimetres() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#millimetres2WidthUnits(double)}.
	 */
	@Test
	public void testMillimetres2WidthUnits() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#pointsToPixels(double)}.
	 */
	@Test
	public void testPointsToPixels() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#pointsToMillimeters(double)}.
	 */
	@Test
	public void testPointsToMillimeters() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#isDate(java.lang.String)}.
	 */
	@Test
	public void testIsDate() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#parseDate(java.lang.String)}.
	 */
	@Test
	public void testParseDate() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#isNumeric(java.lang.String)}.
	 */
	@Test
	public void testIsNumeric() throws Exception {
		
		assertTrue(WebSheetUtility.isNumeric("-123.0"));
		assertTrue(WebSheetUtility.isNumeric("-123."));
		assertTrue(WebSheetUtility.isNumeric("100,100,123.00"));
		assertFalse(WebSheetUtility.isNumeric("$100,123.00"));
		assertFalse(WebSheetUtility.isNumeric("1,00,123.00"));
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#setObjectProperty(java.lang.Object, java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testSetObjectProperty() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#cellCompareTo(org.apache.poi.ss.usermodel.Cell, org.apache.poi.ss.usermodel.Cell)}.
	 */
	@Test
	public void testCellCompareTo() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#insideRange(org.tiefaces.components.websheet.configuration.ConfigRange, org.tiefaces.components.websheet.configuration.ConfigRange)}.
	 */
	@Test
	public void testInsideRange() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#getSheetRightCol(org.apache.poi.ss.usermodel.Sheet)}.
	 */
	@Test
	public void testGetSheetRightCol() throws Exception {
		
	}

	/**
		 * Test method for {@link org.tiefaces.components.websheet.utility.WebSheetUtility#removeRow(org.apache.poi.ss.usermodel.Sheet, int)}.
		 */
		@Test
		public void testRemoveRow() throws Exception {
			
		}

}
