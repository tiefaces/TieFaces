/**
 * 
 */
package org.tiefaces.components.websheet.dataobjects;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.commons.lang.SerializationUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

/**
 * @author Jason Jiang
 *
 */
public class SerialWorkbookTest {

	@Test
	public final void testSerialWorkbooks() throws Exception {

		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/linecharts1.xlsx");

		Workbook wb = WorkbookFactory.create(stream);
		stream.close();
		SerialWorkbook swb = new SerialWorkbook();
		swb.setWb(wb);
		SerializationUtils.serialize(swb);
		SerialWorkbook copy =
				(SerialWorkbook) SerializationUtils.clone(swb);
		assertEquals(swb.getWb().getNumberOfSheets(), copy.getWb()
				.getNumberOfSheets());
		assertEquals(swb.getWb().getSheetAt(0).getLastRowNum(), copy
				.getWb().getSheetAt(0).getLastRowNum());

	}

}
