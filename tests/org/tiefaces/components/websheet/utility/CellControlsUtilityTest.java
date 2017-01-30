/**
 * 
 */
package org.tiefaces.components.websheet.utility;

import org.junit.Test;
import org.tiefaces.components.websheet.utility.CellControlsUtility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 * @author JASON
 *
 */
public class CellControlsUtilityTest {

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellControlsUtility#convertToObject(java.lang.Class, java.lang.String)}.
	* 	private static Class[] paraMatchArray = { String.class, boolean.class,
	*		Boolean.class, int.class, Integer.class, long.class, Long.class,
	*		float.class, Float.class, double.class, Double.class,
	*		byte.class, Byte.class, short.class, Short.class };
	*
	 */
	@Test
	public void testConvertToObject() throws Exception {
		assertEquals( CellControlsUtility.convertToObject(String.class, "String"), "String");
		assertEquals( CellControlsUtility.convertToObject(Boolean.class, "True"), Boolean.valueOf("True"));
		assertEquals( CellControlsUtility.convertToObject(boolean.class, "True"), true);
		assertEquals( CellControlsUtility.convertToObject(Integer.class, "100"), Integer.valueOf("100"));
		assertEquals( CellControlsUtility.convertToObject(int.class, "100"), 100);
		assertEquals( CellControlsUtility.convertToObject(Long.class, "100"), Long.valueOf("100"));
		assertEquals( CellControlsUtility.convertToObject(long.class, "100"), 100L);
		assertEquals( CellControlsUtility.convertToObject(Float.class, "100"), Float.valueOf("100"));
		assertEquals( CellControlsUtility.convertToObject(float.class, "100"), 100f);
		assertEquals( CellControlsUtility.convertToObject(Double.class, "100"), Double.valueOf("100"));
		assertEquals( CellControlsUtility.convertToObject(double.class, "100"), 100d);
		assertEquals( CellControlsUtility.convertToObject(Byte.class, "0"), Byte.valueOf("0"));
		assertEquals( CellControlsUtility.convertToObject(byte.class, "0"), (byte) 0);
		assertEquals( CellControlsUtility.convertToObject(Short.class, "100"), Short.valueOf("100"));
		assertEquals( CellControlsUtility.convertToObject(short.class, "100"), (short) 100);
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.utility.CellControlsUtility#setObjectProperty(java.lang.Object, java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testSetObjectProperty() throws Exception {
	
	}

}
