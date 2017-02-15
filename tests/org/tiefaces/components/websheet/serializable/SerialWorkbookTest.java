/**
 * 
 */
package org.tiefaces.components.websheet.serializable;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.configuration.SheetConfiguration;
import org.tiefaces.components.websheet.serializable.SerialWorkbook;

/**
 * @author Jason Jiang
 *
 */
public class SerialWorkbookTest {

	@Test
	public final void testSerialWorkbooks() throws Exception {

		
		TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/datacommentdemo.xlsx");
		bean.loadWebSheet(stream);
		assertEquals(bean.loadWorkSheetByTabName("departments"), 1);		

		SerialWorkbook swb = bean.getSerialWb();
		
		//SerializationUtils.serialize(swb);
		SerialWorkbook copy =
				(SerialWorkbook) SerializationUtils.clone(swb);
		//copy.recover();

		assertEquals(swb.getWb().getNumberOfSheets(), copy.getWb()
				.getNumberOfSheets());
		assertEquals(swb.getWb().getSheetAt(0).getLastRowNum(), copy
				.getWb().getSheetAt(0).getLastRowNum());
		
		Map<String, SheetConfiguration>  map = swb.getSheetConfigMap();
		for (Entry<String, SheetConfiguration> entry : map.entrySet()) {

			String key = entry.getKey();			
			SheetConfiguration config1 = entry.getValue();
			SheetConfiguration config2 = copy.getSheetConfigMap().get(key);
		    System.out.println("Key = " + key );
		    System.out.println("Config1 = " + config1);
		    System.out.println("Config2 = "+config2);
		}

	}

}
