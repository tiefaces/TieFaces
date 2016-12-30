package org.tiefaces.components.websheet.dataobjects;

import java.util.List;


/**
 * Used for cached map.
 * @author Jason Jiang
 *
 */
public class FormulaMapping {

	/**
	 * original formula.
	 */
	String originFormula;
	/**
	 * cached value for the cell.
	 */
	String value;
	public String getOriginFormula() {
		return originFormula;
	}
	public void setOriginFormula(String originFormula) {
		this.originFormula = originFormula;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
