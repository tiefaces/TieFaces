package com.tiefaces.components.websheet.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

import com.tiefaces.components.websheet.service.CellHelper;

public class ConfigBuildRef {

	XSSFEvaluationWorkbook wbWrapper;
	Sheet sheet;
    List<Integer> watchList;
	ExpressionEngine engine;
	CellHelper cellHelper;
	Map<Integer, AddRowRef> addRowMap;
	Map<String, RowsMapping> shiftMap;
	
	public ConfigBuildRef(XSSFEvaluationWorkbook wbWrapper, Sheet sheet,
			ExpressionEngine engine, CellHelper cellHelper) {
		super();
		this.wbWrapper = wbWrapper;
		this.sheet = sheet;
		this.engine = engine;
		this.cellHelper = cellHelper;
		this.addRowMap = new HashMap<Integer, AddRowRef>();
		this.shiftMap = new HashMap<String, RowsMapping>();
	}
	public List<Integer> getWatchList() {
		return watchList;
	}
	public void setWatchList(List<Integer> watchList) {
		this.watchList = watchList;
	}
	public XSSFEvaluationWorkbook getWbWrapper() {
		return wbWrapper;
	}
	public Sheet getSheet() {
		return sheet;
	}
	public ExpressionEngine getEngine() {
		return engine;
	}
	public CellHelper getCellHelper() {
		return cellHelper;
	}
	public Map<Integer, AddRowRef> getAddRowMap() {
		return addRowMap;
	}
	public void setAddRowMap(Map<Integer, AddRowRef> addRowMap) {
		this.addRowMap = addRowMap;
	}
	public Map<String, RowsMapping> getShiftMap() {
		return shiftMap;
	}
	public void setShiftMap(Map<String, RowsMapping> shiftMap) {
		this.shiftMap = shiftMap;
	}	

	
	
}
