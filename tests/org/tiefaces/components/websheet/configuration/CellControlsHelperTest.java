/**
 * 
 */
package org.tiefaces.components.websheet.configuration;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 * @author JASON
 *
 */
public class CellControlsHelperTest {

	/**
	 * Test method for {@link org.tiefaces.components.websheet.configuration.CellControlsHelper#convertToObject(java.lang.Class, java.lang.String)}.
	* 	private static Class[] paraMatchArray = { String.class, boolean.class,
	*		Boolean.class, int.class, Integer.class, long.class, Long.class,
	*		float.class, Float.class, double.class, Double.class,
	*		byte.class, Byte.class, short.class, Short.class };
	*
	 */
	@Test
	public void testConvertToObject() throws Exception {
		assertEquals( CellControlsHelper.convertToObject(String.class, "String"), "String");
		assertEquals( CellControlsHelper.convertToObject(Boolean.class, "True"), Boolean.valueOf("True"));
		assertEquals( CellControlsHelper.convertToObject(boolean.class, "True"), true);
		assertEquals( CellControlsHelper.convertToObject(Integer.class, "100"), Integer.valueOf("100"));
		assertEquals( CellControlsHelper.convertToObject(int.class, "100"), 100);
		assertEquals( CellControlsHelper.convertToObject(Long.class, "100"), Long.valueOf("100"));
		assertEquals( CellControlsHelper.convertToObject(long.class, "100"), 100L);
		assertEquals( CellControlsHelper.convertToObject(Float.class, "100"), Float.valueOf("100"));
		assertEquals( CellControlsHelper.convertToObject(float.class, "100"), 100f);
		assertEquals( CellControlsHelper.convertToObject(Double.class, "100"), Double.valueOf("100"));
		assertEquals( CellControlsHelper.convertToObject(double.class, "100"), 100d);
		assertEquals( CellControlsHelper.convertToObject(Byte.class, "0"), Byte.valueOf("0"));
		assertEquals( CellControlsHelper.convertToObject(byte.class, "0"), (byte) 0);
		assertEquals( CellControlsHelper.convertToObject(Short.class, "100"), Short.valueOf("100"));
		assertEquals( CellControlsHelper.convertToObject(short.class, "100"), (short) 100);
	}

	/**
	 * Test method for {@link org.tiefaces.components.websheet.configuration.CellControlsHelper#setObjectProperty(java.lang.Object, java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testSetObjectProperty() throws Exception {
	
	}

}
