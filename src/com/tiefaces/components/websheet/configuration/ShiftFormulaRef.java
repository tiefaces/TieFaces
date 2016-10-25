package com.tiefaces.components.websheet.configuration;

import java.util.List;

public class ShiftFormulaRef {
	
	List<Integer> watchList;
	List<RowsMapping> currentRowsMappingList;
	int formulaChanged=0;
	public List<Integer> getWatchList() {
		return watchList;
	}
	public void setWatchList(List<Integer> watchList) {
		this.watchList = watchList;
	}
	public List<RowsMapping> getCurrentRowsMappingList() {
		return currentRowsMappingList;
	}
	public void setCurrentRowsMappingList(
			List<RowsMapping> currentRowsMappingList) {
		this.currentRowsMappingList = currentRowsMappingList;
	}
	public int getFormulaChanged() {
		return formulaChanged;
	}
	public void setFormulaChanged(int formulaChanged) {
		this.formulaChanged = formulaChanged;
	}
	public ShiftFormulaRef(List<Integer> watchList,
			List<RowsMapping> currentRowsMappingList) {
		super();
		this.watchList = watchList;
		this.currentRowsMappingList = currentRowsMappingList;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{ watchList = "+watchList);
		sb.append(",");
		sb.append("currentRowsMappingList = " + currentRowsMappingList);
		sb.append("}");
		return sb.toString();
	}	
}
