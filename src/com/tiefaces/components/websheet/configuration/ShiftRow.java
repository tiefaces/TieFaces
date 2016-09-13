package com.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

public class ShiftRow {

	Integer  originRow;
	List<Row> rowList;
	public Integer getOriginRow() {
		return originRow;
	}
	public void setOriginRow(Integer originRow) {
		this.originRow = originRow;
	}
	public List<Row> getRowList() {
		return rowList;
	}
	public void setRowList(List<Row> rowList) {
		this.rowList = rowList;
	}
	public ShiftRow(Integer originRow, List<Row> rowList) {
		super();
		this.originRow = originRow;
		this.rowList = rowList;
	}
	
	
	
}
