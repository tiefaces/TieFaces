package org.tiefaces.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class FacesUtilityTest {

	@Test
	public final void testRemovePrefixPath() throws Exception {

		assertEquals(FacesUtility.removePrefixPath("/showcase/",
				"/showcase/first/first.xhtml"), "/first/first.xhtml");
	}

	@Test
	public final void testEvalInputType() throws Exception {
		assertTrue(FacesUtility.evalInputType("String", "text"));
		assertTrue(FacesUtility.evalInputType("100", "Integer"));
		assertTrue(FacesUtility.evalInputType("100.00", "Double"));
		assertTrue(FacesUtility.evalInputType("True", "Boolean"));
		assertTrue(FacesUtility.evalInputType("1", "Byte"));
		assertFalse(FacesUtility.evalInputType("Text", "Integer"));
	}

	@Test
	public final void testStrJoin() throws Exception {
		short[] rgb = { 192, 192, 192 };
		assertEquals(FacesUtility.strJoin(rgb, ","), "192,192,192");

	}

	@Test
	public final void testRound() throws Exception {
		assertEquals(FacesUtility.round(100.123456, 2), 100.12, 0.001);
	}

}
