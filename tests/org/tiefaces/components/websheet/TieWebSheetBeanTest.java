/**
 * 
 */
package org.tiefaces.components.websheet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.chart.ChartData;
import org.tiefaces.components.websheet.chart.ChartType;
import org.tiefaces.components.websheet.utility.CellUtility;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;
import org.tiefaces.datademo.Department;
import org.tiefaces.datademo.WebSheetDataDemo;
import org.tiefaces.common.Item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jason Jiang
 *
 */
public class TieWebSheetBeanTest {

	/**
	 * @throws Exception
	 *             java.lang.Exception.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws Exception
	 *             java.lang.Exception.
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
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/datacommentdemo.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		Workbook wb = bean.getWb();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			String sheetName = wb.getSheetName(i);
			if (sheetName.startsWith(TieConstants.COPY_SHEET_PREFIX)) {
				assertTrue(wb.isSheetVeryHidden(i));
			}
		}

	}

    /**
     * Test method for
     * {@link org.tiefaces.components.websheet.TieWebSheetBean#loadWebSheet(java.io.InputStream)}
     * .
     */
    @Test
    public final void testLoadWebSheetWithData() throws Exception {
        TieWebSheetBean bean = new TieWebSheetBean();
        bean.init();
        InputStream stream =
                this.getClass().getClassLoader().getResourceAsStream(
                        "resources/sheet/datacommentdemo.xlsx");
        Map<String, Object> context = new HashMap<String, Object>();
        List<Department> departments = WebSheetDataDemo.createDepartments();
        context.put("departments", departments);
        
        assertEquals(bean.loadWebSheet(stream, context), 1);
        Workbook wb = bean.getWb();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sheetName = wb.getSheetName(i);
            if (sheetName.startsWith(TieConstants.COPY_SHEET_PREFIX)) {
                assertTrue(wb.isSheetVeryHidden(i));
            } else {
                Sheet sheet = wb.getSheetAt(i);
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(8).getCell(6)), "2875");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(12).getCell(6)), "12415");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(21).getCell(6)), "2070");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(23).getCell(6)), "8245");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(33).getCell(6)), "2687.5");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(34).getCell(6)), "10957.5");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(35).getCell(6)), "31617.5");
                assertEquals(ConfigurationUtility.getFullNameFromRow(sheet.getRow(7)), "F.departments:E.department.0:E.employee.0");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(12).getCell(7)), "10800.75");
                assertEquals(CellUtility.getCellValueWithoutFormat(sheet.getRow(22).getCell(7)), "1900.15");

                Comment comment1 = sheet.getRow(0).getCell(0).getCellComment();
                assertNull(comment1);
                comment1 = sheet.getRow(1).getCell(1).getCellComment();
                String commentStr1 = comment1.getString().toString();
                
                Comment comment2 = sheet.getRow(13).getCell(1).getCellComment();
                String commentStr2 = comment2.getString().toString();
                assertEquals(commentStr1, commentStr2);
                assertEquals(comment1.getAuthor(), comment2.getAuthor());
                
                int size = departments.size();
                bean.addRepeatRow(2);
                assertEquals(departments.size(), (size + 1));
                
            }
        }

    }
	
	/**
	 * Test charts line. .
	 */
	@Test
	public final void testLoadWebSheetChartLine() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/linecharts1.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);

		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {
			String type = entry.getValue().getType().toString();
			assertTrue(type.toLowerCase().startsWith("line"));
		}

	}

	/**
	 * Test charts area. .
	 */
	@Test
	public final void testLoadWebSheetChartArea() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartareas.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);
		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {
			String type = entry.getValue().getType().toString();
			assertTrue(type.toLowerCase().startsWith("area"));
		}
	}

	/**
	 * Test charts bar 2d. .
	 */
	@Test
	public final void testLoadWebSheetChartBar2d() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartbars2d.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);
		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {
			String type = entry.getValue().getType().toString();
			assertTrue(type.toLowerCase().startsWith("bar"));
		}
	}

	/**
	 * Test charts bar 3d. .
	 */
	@Test
	public final void testLoadWebSheetChartBar3d() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartbars3d.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);
		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {
			String type = entry.getValue().getType().toString();
			assertTrue(type.toLowerCase().startsWith("bar3d"));
		}
	}

	/**
	 * Test charts column 2d. .
	 */
	@Test
	public final void testLoadWebSheetChartColumn2d() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartcolumns2d.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);
		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {
			String type = entry.getValue().getType().toString();
			assertTrue(type.toLowerCase().startsWith("column"));
		}
	}

	/**
	 * Test charts column 3d. .
	 */
	@Test
	public final void testLoadWebSheetChartColumn3d() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartcolumns3d.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);
		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {
			String type = entry.getValue().getType().toString();
			assertTrue(type.toLowerCase().startsWith("column3d"));
		}
	}

	/**
	 * Test charts pie 2d. .
	 */
	@Test
	public final void testLoadWebSheetChartPie2d() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartpie2d.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);
		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {

			assertEquals(entry.getValue().getType(), ChartType.PIE);
		}
	}

	/**
	 * Test charts pie 3d. .
	 */
	@Test
	public final void testLoadWebSheetChartPie3d() throws Exception {
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/chartpie3d.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		assertTrue(bean.getCharsData().getChartsMap().size() > 0);

		Map<String, ChartData> map = bean.getCharsData().getChartDataMap();
		for (Map.Entry<String, ChartData> entry : map.entrySet()) {

			assertEquals(entry.getValue().getType(), ChartType.PIE3D);
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
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/datacommentdemo.xlsx");
		bean.loadWebSheet(stream);
		assertEquals(bean.loadWorkSheetByTabName("departments"), 1);

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
		bean.addRepeatRow(4);
		assertEquals(itemList.size(), 2);
		bean.addRepeatRow(5);
		assertEquals(itemList.size(), 3);
		bean.deleteRepeatRow(5);
        assertEquals(itemList.size(), 2);

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
