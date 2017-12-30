package org.tiefaces.components.websheet;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface for server side validation. User can handle the validation through
 * this.
 * 
 * @author JASON JIANG
 *
 */
public interface TieWebSheetValidation extends Serializable {
	
	/**
	 * After enable this validation bean, it will be triggered during each cell
	 * update. User can use the parameters to determine the cell position 
	 * with context object and field attributes and return the proper error message.
	 * If validation passed, then return empty.
	 * 
	 * @param dataContext
	 *            data context.
	 * @param dataField
	 *            data field.
	 * @param nestedDataFullName
	 *            nested data full name. 
	 *            used for form with each command.
	 * @param sheetName
	 *            sheetName
	 * @param rowIndex
	 *            row index
	 * @param colIndex
	 *            column index
	 * @param inputValue
	 *            current input value
	 * @return return empty string if passed. otherwise return error message.
	 */
	public String validate(Map<String, Object> dataContext, String dataField, String nestedDataFullName, String sheetName, int rowIndex, int colIndex, String inputValue);

}
