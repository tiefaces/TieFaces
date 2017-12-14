/**
 * 
 */
package org.tiefaces.components.websheet.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Jason Jiang
 *
 */
public class SaveAttrsUtilityTest {

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#parseSaveAttr(org.apache.poi.ss.usermodel.Cell)}.
	 */
	@Test
	public void testParseSaveAttr() throws Exception {
		assertEquals("employee.name", SaveAttrsUtility.parseSaveAttrString("${employee.name}"));
		assertEquals("employee.name", SaveAttrsUtility.parseSaveAttrString("${employee.name} "));
		assertEquals("", SaveAttrsUtility.parseSaveAttrString("${employee.name} ${employee.birthday}"));
		assertEquals("", SaveAttrsUtility.parseSaveAttrString("${employee.name"));
		
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#saveDataToObjectInContext(java.util.Map, java.lang.String, java.lang.String, org.tiefaces.components.websheet.configuration.ExpressionEngine)}.
	 */
	@Test
	public void testSaveDataToObjectInContext() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#parseSaveAttrString(java.lang.String)}.
	 */
	@Test
	public void testParseSaveAttrString() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#getSaveAttrListFromRow(org.apache.poi.ss.usermodel.Row)}.
	 */
	@Test
	public void testGetSaveAttrListFromRow() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#getSaveAttrFromList(int, java.lang.String)}.
	 */
	@Test
	public void testGetSaveAttrFromList() throws Exception {

		String attrs = "$0=employee.name,$1=employee.birthDate,$2=employee.age,$3=employee.payment,$4=employee.bonus,";
		assertEquals("employee.name", SaveAttrsUtility.getSaveAttrFromList(0, attrs));
		assertEquals("employee.birthDate", SaveAttrsUtility.getSaveAttrFromList(1, attrs));
		assertEquals("employee.age", SaveAttrsUtility.getSaveAttrFromList(2, attrs));
		assertEquals("employee.payment", SaveAttrsUtility.getSaveAttrFromList(3, attrs));
		assertEquals("employee.bonus", SaveAttrsUtility.getSaveAttrFromList(4, attrs));		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#isHasSaveAttr(org.apache.poi.ss.usermodel.Cell)}.
	 */
	@Test
	public void testIsHasSaveAttrCell() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#isHasSaveAttr(int, java.lang.String)}.
	 */
	@Test
	public void testIsHasSaveAttrIntString() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#setSaveObjectsInHiddenColumn(org.apache.poi.ss.usermodel.Row, java.lang.String)}.
	 */
	@Test
	public void testSetSaveObjectsInHiddenColumn() throws Exception {
		
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.SaveAttrsUtility#setSaveAttrsForSheet(org.apache.poi.ss.usermodel.Sheet, int, int)}.
	 */
	@Test
	public void testSetSaveAttrsForSheet() throws Exception {
		
	}

}
