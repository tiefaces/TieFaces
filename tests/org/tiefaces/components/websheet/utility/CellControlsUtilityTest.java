/**
 * 
 */
package org.tiefaces.components.websheet.utility;

import org.junit.Test;
import org.tiefaces.components.websheet.utility.CellControlsUtility;
import org.tiefaces.components.websheet.utility.CellControlsUtility.AttributesType;

import static org.junit.Assert.assertEquals;

/**
 * @author JASON
 *
 */
public class CellControlsUtilityTest {

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.utility.CellControlsUtility#convertToObject(java.lang.Class, java.lang.String)}.
	 * private static Class[] paraMatchArray = { String.class, boolean.class,
	 * Boolean.class, int.class, Integer.class, long.class, Long.class,
	 * float.class, Float.class, double.class, Double.class, byte.class,
	 * Byte.class, short.class, Short.class };
	 *
	 */
	@Test
	public void testConvertToObject() throws Exception {
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.STRING, "String"), "String");
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.BOOLEAN, "True"), Boolean.valueOf("True"));
		assertEquals(CellControlsUtility
				.convertToObject(AttributesType.BOOLEANTYPE, "True"), true);
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.INTEGER, "100"), Integer.valueOf("100"));
		assertEquals(CellControlsUtility
				.convertToObject(AttributesType.INTEGERTYPE, "100"), 100);
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.LONG, "100"), Long.valueOf("100"));
		assertEquals(CellControlsUtility
				.convertToObject(AttributesType.LONGTYPE, "100"), 100L);
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.FLOAT, "100"), Float.valueOf("100"));
		assertEquals(CellControlsUtility
				.convertToObject(AttributesType.FLOATTYPE, "100"), 100f);
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.DOUBLE, "100"), Double.valueOf("100"));
		assertEquals(CellControlsUtility
				.convertToObject(AttributesType.DOUBLETYPE, "100"), 100d);
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.BYTE, "0"), Byte.valueOf("0"));
		assertEquals(CellControlsUtility
				.convertToObject(AttributesType.BYTETYPE, "0"), (byte) 0);
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.SHORT, "100"), Short.valueOf("100"));
		assertEquals(CellControlsUtility.convertToObject(
				AttributesType.SHORTTYPE, "100"), (short) 100);
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.utility.CellControlsUtility#setObjectProperty(java.lang.Object, java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testSetObjectProperty() throws Exception {

	}

	/**
		 * Test method for {@link org.tiefaces.components.websheet.utility.CellControlsUtility#findCellValidateAttributes(java.util.Map, org.apache.poi.ss.usermodel.Cell)}.
		 */
		@Test
		public void testFindCellValidateAttributes() throws Exception {
			
		}

}
