package com.tiefaces.components.websheet.dataobjects;

import java.util.List;

import com.tiefaces.components.websheet.configuration.ShiftRow;

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
	 * rows mapping for original formula.
	 */
	List<ShiftRow> rowMappingList;
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
	public List<ShiftRow> getRowMappingList() {
		return rowMappingList;
	}
	public void setRowMappingList(List<ShiftRow> rowMappingList) {
		this.rowMappingList = rowMappingList;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
