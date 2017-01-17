/**
 * 
 */
package org.tiefaces.components.websheet;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tiefaces.common.TieConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jason Jiang
 *
 */
public class TieWebSheetBeanTest {

	/**
	 * @throws Exception java.lang.Exception.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws Exception java.lang.Exception.
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#init()}.
	 */
	@Test
	public final void testInit() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		assertTrue(bean.getColumns().isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#reCalcMaxColCounts()}
	 * .
	 */
	@Test
	public final void testReCalcMaxColCounts() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		bean.reCalcMaxColCounts();
		assertEquals(bean.getMaxColCounts(), 0);

	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#loadWebSheet(java.io.InputStream)}
	 * .
	 */
	@Test
	public final void testLoadWebSheetInputStream() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("resources/sheet/datacommentdemo.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		Workbook wb = bean.getWb();
		for (int i = 0; i < wb.getNumberOfSheets(); i++)
		{
			String sheetName = wb.getSheetName(i);
			if (sheetName.startsWith(TieConstants.COPY_SHEET_PREFIX)) {
				assertTrue(wb.isSheetVeryHidden(i));
			}
		}
		
		
	}


	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#onTabChange(org.primefaces.event.TabChangeEvent)}
	 * .
	 */
	@Test
	public final void testloadWorkSheetByTabNameString() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("resources/sheet/datacommentdemo.xlsx");
		bean.loadWebSheet(stream);
		assertEquals(bean.loadWorkSheetByTabName("departments"),1);
		
		
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#doExport()}.
	 */
	@Test
	public final void testDoExport() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#doSave()}.
	 */
	@Test
	public final void testDoSave() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#noteChangeEvent(javax.faces.event.AjaxBehaviorEvent)}
	 * .
	 */
	@Test
	public final void testNoteChangeEvent() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#valueChangeEvent(javax.faces.event.AjaxBehaviorEvent)}
	 * .
	 */
	@Test
	public final void testValueChangeEvent() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#finish()}.
	 */
	@Test
	public final void testFinish() throws Exception {
	}


	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#addRepeatRow(int)}
	 * .
	 */
	@Test
	public final void testAddRepeatRow() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#deleteRepeatRow(int)}
	 * .
	 */
	@Test
	public final void testDeleteRepeatRow() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.TieWebSheetBean#populateComponent(javax.faces.event.ComponentSystemEvent)}
	 * .
	 */
	@Test
	public final void testPopulateComponent() throws Exception {
	}

}
