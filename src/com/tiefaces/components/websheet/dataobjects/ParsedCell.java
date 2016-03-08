package com.tiefaces.components.websheet.dataobjects;

import org.apache.poi.ss.util.CellReference;

import com.tiefaces.components.websheet.utility.TieWebSheetUtility;

public class ParsedCell {

	String sheetName;
	int row;
	int col;

	public ParsedCell(String sheetName, int row, int col) {
		super();
		this.sheetName = sheetName;
		this.row = row;
		this.col = col;
	}

	public ParsedCell(String fullName) {
		super();
		try {
			this.sheetName = TieWebSheetUtility
					.getSheetNameFromFullCellRefName(fullName);
			String cellrefName = TieWebSheetUtility
					.removeSheetNameFromFullCellRefName(fullName); 
			CellReference ref = new CellReference(cellrefName);
			this.row = ref.getRow();
			this.col = ref.getCol();
		} catch (Exception ex) {
			throw new RuntimeException("Cannot parse fullname "+fullName+" to ParsedCell. Error = "+ex.getLocalizedMessage());
		}
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

}
