package com.tiefaces.components.websheet.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

import com.tiefaces.components.websheet.dataobjects.CachedCells;
import com.tiefaces.components.websheet.service.CellHelper;

public class ConfigBuildRef {

	XSSFEvaluationWorkbook wbWrapper;
	Sheet sheet;
	List<Integer> watchList;
	ExpressionEngine engine;
	CellHelper cellHelper;
	Map<String, Map<String, String>> templateCommentMap;
	Map<Cell, String> finalCommentMap;
	boolean bodyAllowAdd = false;
	boolean addMode = false;
	int insertPosition = -1;

	/**
	 * Saved configRange attributes for each full name. String : full name.
	 * ConfigRangeAttrs : include range (top, bottom) and rows mapping.
	 */
	Map<String, ConfigRangeAttrs> shiftMap;

	/**
	 * Saved formula cells. include original formula and rows mapping for this
	 * cell.
	 */
	Map<Cell, String> cachedCells;

	/**
	 * used for cache origin config range tree.
	 */
	ConfigRange originConfigRange;

	/**
	 * used for originConfigRange to finding command through full name. key
	 * (String) - command name. i.e. F.deparments or E.department value
	 * (Command) - configuration command.
	 */
	Map<String, Command> commandIndexMap = new HashMap<String, Command>();
	/**
	 * used for save the object for create new one. key (String) - var name.
	 * value (String) - object class name.
	 */
	Map<String, String> collectionObjNameMap = new HashMap<String, String>();

	public ConfigBuildRef(XSSFEvaluationWorkbook pWbWrapper,
			Sheet pSheet, ExpressionEngine pEngine,
			CellHelper pCellHelper, Map<Cell, String> pCachedCells,
			Map<String, Map<String, String>> pTemplateCommentMap,
			Map<Cell, String> pFinalCommentMap			
			) {
		super();
		this.wbWrapper = pWbWrapper;
		this.sheet = pSheet;
		this.engine = pEngine;
		this.cellHelper = pCellHelper;
		this.cachedCells = pCachedCells;
		this.templateCommentMap = pTemplateCommentMap;
		this.finalCommentMap = pFinalCommentMap;
		this.shiftMap = new HashMap<String, ConfigRangeAttrs>();
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

	public Map<Cell, String> getCachedCells() {
		return cachedCells;
	}

	public void putShiftAttrs(String fullName, ConfigRangeAttrs attrs,
			RowsMapping unitRowsMapping) {
		attrs.unitRowsMapping = unitRowsMapping;
		this.shiftMap.put(fullName, attrs);
	}

	public Map<String, ConfigRangeAttrs> getShiftMap() {
		return shiftMap;
	}

	public void setShiftMap(Map<String, ConfigRangeAttrs> shiftMap) {
		this.shiftMap = shiftMap;
	}

	public boolean isBodyAllowAdd() {
		return bodyAllowAdd;
	}

	public void setBodyAllowAdd(boolean bodyAllowAdd) {
		this.bodyAllowAdd = bodyAllowAdd;
	}

	public ConfigRange getOriginConfigRange() {
		return originConfigRange;
	}

	public void setOriginConfigRange(ConfigRange originConfigRange) {
		this.originConfigRange = originConfigRange;
	}

	public Map<String, Command> getCommandIndexMap() {
		return commandIndexMap;
	}

	public void setCommandIndexMap(Map<String, Command> commandIndexMap) {
		this.commandIndexMap = commandIndexMap;
	}

	public boolean isAddMode() {
		return addMode;
	}

	public void setAddMode(boolean addMode) {
		this.addMode = addMode;
	}

	public Map<String, String> getCollectionObjNameMap() {
		return collectionObjNameMap;
	}

	public void setCollectionObjNameMap(
			Map<String, String> collectionObjNameMap) {
		this.collectionObjNameMap = collectionObjNameMap;
	}

	public int getInsertPosition() {
		return insertPosition;
	}

	public void setInsertPosition(int insertPosition) {
		this.insertPosition = insertPosition;
	}


	
	public Map<String, Map<String, String>> getTemplateCommentMap() {
		return templateCommentMap;
	}

	public Map<Cell, String> getFinalCommentMap() {
		return finalCommentMap;
	}

	
	
}
