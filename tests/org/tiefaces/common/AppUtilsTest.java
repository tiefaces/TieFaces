package org.tiefaces.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

import org.junit.Test;

/**
 * @author ihsb_developer
 *
 */
public class AppUtilsTest {

	/**
	 * Test method for
	 * {@link org.tiefaces.common.AppUtils#isEmpty(java.lang.String)}.
	 * @throws Exception exception.
	 */
	@Test
	public final void testIsEmpty() throws Exception {
		assertTrue(AppUtils.isEmpty(null));
		assertTrue(AppUtils.isEmpty(""));
		assertFalse(AppUtils.isEmpty("String"));
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.common.AppUtils#emptyList(java.util.List)}.
	 * @throws Exception exception.
	 */
	@Test
	public final void testEmptyList() throws Exception {
		assertTrue(AppUtils.emptyList(null));
		ArrayList<String> list = new ArrayList<String>();
		assertTrue(AppUtils.emptyList(list));
		list.add("String");
		assertFalse(AppUtils.emptyList(list));
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.common.AppUtils#isNumeric(java.lang.String)}.
	 * @throws Exception exception.
	 */
	@Test
	public final void testIsNumeric() throws Exception {
		assertFalse(AppUtils.isNumeric(null));
		assertTrue(AppUtils.isNumeric("100"));
		assertFalse(AppUtils.isNumeric("1A00"));
	}


}
