package org.tiefaces.components.websheet;
/**
 * Interface for server side validation.
 * User can handle the validation through this. 
 * @author JASON JIANG
 *
 */
public interface TieWebSheetValidation {
	
	/**
	 * After enable this validation bean, it will be triggered during each cell update.
	 * User can use the parameters to determine the cell position and return the proper error message.
	 * If validaten passed, then return empty.  
	 * @param sheetName 
	 * 			sheetName
	 * @param rowIndex
	 * 			row index
	 * @param colIndex
	 * 			column index
	 * @param inputValue
	 * 			current input value
	 * @return  return empty string if passed. otherwise return error message.
	 */
	public String validate(String sheetName, int rowIndex, int colIndex, String inputValue);

}
