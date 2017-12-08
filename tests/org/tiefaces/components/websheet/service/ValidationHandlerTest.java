/**
 * 
 */
package org.tiefaces.components.websheet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tiefaces.common.Item;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.dataobjects.FacesCell;

/**
 * @author Jason Jiang
 *
 */
public class ValidationHandlerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.service.ValidationHandler#validateWithRowColInCurrentPage(int, int, boolean)}
	 * .
	 */
	@Test
	public final void testValidateWithRowColInCurrentPage()
			throws Exception {

		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/PRICELISTINPUTVALIDATION.xlsx");
		List<Item> itemList = new ArrayList<Item>();
		itemList.add(new Item());
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("items", itemList);
		assertEquals(bean.loadWebSheet(stream, context), 1);

		bean.getCellsMap().put("4:3", "-1");
		bean.getValidationHandler().validateWithRowColInCurrentPage(4,
					3, true);
		FacesCell fcell = bean.getBodyRows().get(4).getCells().get(3);
		assertTrue(fcell.isInvalid());
		assertTrue(fcell.getValidStyle().contains(TieConstants.CELL_INVALID_STYLE));
		bean.getCellsMap().put("4:3", "1");
		bean.getValidationHandler().validateWithRowColInCurrentPage(4, 3,
				true);
		assertFalse(fcell.isInvalid());
		
		bean.addRepeatRow(4);
		
		bean.getCellsMap().put("5:3", "-1");
		bean.getValidationHandler().validateWithRowColInCurrentPage(5,
					3, true);
		fcell = bean.getBodyRows().get(5).getCells().get(3);
		assertTrue(fcell.isInvalid());
		assertTrue(fcell.getValidStyle().contains(TieConstants.CELL_INVALID_STYLE));
		bean.getCellsMap().put("5:3", "1");
		bean.getValidationHandler().validateWithRowColInCurrentPage(5, 3,
				true);
		assertFalse(fcell.isInvalid());
		

	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.service.ValidationHandler#validateRowInCurrentPage(int, boolean)}
	 * .
	 */
	@Test
	public final void testValidateRowInCurrentPage() throws Exception {

		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/PRICELISTINPUTVALIDATION.xlsx");
		List<Item> itemList = new ArrayList<Item>();
		itemList.add(new Item());
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("items", itemList);
		assertEquals(bean.loadWebSheet(stream, context), 1);

		bean.getCellsMap().put("4:3", "-1");

		assertFalse(bean.getValidationHandler().validateRowInCurrentPage(4,
				true));

	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.service.ValidationHandler#findFirstInvalidSheet(boolean)}
	 * .
	 */
	@Test
	public final void testFindFirstInvalidSheet() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/PRICELISTINPUTVALIDATION.xlsx");
		List<Item> itemList = new ArrayList<Item>();
		itemList.add(new Item());
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("items", itemList);
		assertEquals(bean.loadWebSheet(stream, context), 1);
		bean.getCellsMap().put("4:3", "-1");
		assertEquals(bean.getValidationHandler()
				.findFirstInvalidSheet(true), "Sale Price Report");

	}

}
