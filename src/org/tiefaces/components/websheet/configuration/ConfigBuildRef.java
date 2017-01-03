/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.service.CellHelper;

/**
 * Config build reference.
 * 
 * @author JASON JIANG
 *
 */
public class ConfigBuildRef {

	/** workbook wrapper. */
	private XSSFEvaluationWorkbook wbWrapper;
	/** sheet. */
	private Sheet sheet;
	/** list of row need watched. */
	private List<Integer> watchList;
	/** expression engine. */
	private ExpressionEngine engine;
	/** cell helper. */
	private CellHelper cellHelper;
	/** cell attributes map. */
	private CellAttributesMap cellAttributesMap;
	/** final comment map. */
	private Map<Cell, String> finalCommentMap;
	/** body allow add. */
	private boolean bodyAllowAdd = false;
	/** true if in addMOde. */
	private boolean addMode = false;
	/** insert position. */
	private int insertPosition = -1;

	/**
	 * Saved configRange attributes for each full name. String : full name.
	 * ConfigRangeAttrs : include range (top, bottom) and rows mapping.
	 */
	private TreeMap<String, ConfigRangeAttrs> shiftMap;

	/**
	 * Saved formula cells. include original formula and rows mapping for this
	 * cell.
	 */
	private Map<Cell, String> cachedCells;

	/**
	 * used for cache origin config range tree.
	 */
	private ConfigRange originConfigRange;

	/**
	 * used for originConfigRange to finding command through full name. key
	 * (String) - command name. i.e. F.deparments or E.department value
	 * (Command) - configuration command.
	 */
	private Map<String, Command> commandIndexMap = new HashMap<String, Command>();
	/**
	 * used for save the object for create new one. key (String) - var name.
	 * value (String) - object class name.
	 */
	private Map<String, String> collectionObjNameMap = new HashMap<String, String>();

	/**
	 * constructor.
	 * 
	 * @param pWbWrapper
	 *            workbook wrapper.
	 * @param pSheet
	 *            sheet.
	 * @param pEngine
	 *            expression engine.
	 * @param pCellHelper
	 *            cellhelper.
	 * @param pCachedCells
	 *            cached cells.
	 * @param pCellAttributesMap
	 *            cell attributes map.
	 * @param pFinalCommentMap
	 *            final comment map.
	 */
	public ConfigBuildRef(final XSSFEvaluationWorkbook pWbWrapper,
			final Sheet pSheet, final ExpressionEngine pEngine,
			final CellHelper pCellHelper,
			final Map<Cell, String> pCachedCells,
			final CellAttributesMap pCellAttributesMap,
			final Map<Cell, String> pFinalCommentMap) {
		super();
		this.wbWrapper = pWbWrapper;
		this.sheet = pSheet;
		this.engine = pEngine;
		this.cellHelper = pCellHelper;
		this.cachedCells = pCachedCells;
		this.cellAttributesMap = pCellAttributesMap;
		this.finalCommentMap = pFinalCommentMap;
		this.shiftMap = new TreeMap<String, ConfigRangeAttrs>();
	}

	/**
	 * Gets the watch list.
	 *
	 * @return the watch list
	 */
	public List<Integer> getWatchList() {
		return watchList;
	}

	/**
	 * Sets the watch list.
	 *
	 * @param watchList
	 *            the new watch list
	 */
	public void setWatchList(final List<Integer> watchList) {
		this.watchList = watchList;
	}

	/**
	 * Gets the wb wrapper.
	 *
	 * @return the wb wrapper
	 */
	public XSSFEvaluationWorkbook getWbWrapper() {
		return wbWrapper;
	}

	/**
	 * Gets the sheet.
	 *
	 * @return the sheet
	 */
	public Sheet getSheet() {
		return sheet;
	}

	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	public ExpressionEngine getEngine() {
		return engine;
	}

	/**
	 * Gets the cell helper.
	 *
	 * @return the cell helper
	 */
	public CellHelper getCellHelper() {
		return cellHelper;
	}

	/**
	 * Gets the cached cells.
	 *
	 * @return the cached cells
	 */
	public Map<Cell, String> getCachedCells() {
		return cachedCells;
	}

	/**
	 * Put shift attrs.
	 *
	 * @param fullName
	 *            the full name
	 * @param attrs
	 *            the attrs
	 * @param unitRowsMapping
	 *            the unit rows mapping
	 */
	public void putShiftAttrs(final String fullName,
			final ConfigRangeAttrs attrs,
			final RowsMapping unitRowsMapping) {
		attrs.setUnitRowsMapping(unitRowsMapping);
		this.shiftMap.put(fullName, attrs);
	}

	/**
	 * Gets the shift map.
	 *
	 * @return the shift map
	 */
	public TreeMap<String, ConfigRangeAttrs> getShiftMap() {
		return shiftMap;
	}

	/**
	 * Sets the shift map.
	 *
	 * @param pshiftMap
	 *            the shift map
	 */
	public void setShiftMap(
			final TreeMap<String, ConfigRangeAttrs> pshiftMap) {
		this.shiftMap = pshiftMap;
	}

	/**
	 * Checks if is body allow add.
	 *
	 * @return true, if is body allow add
	 */
	public boolean isBodyAllowAdd() {
		return bodyAllowAdd;
	}

	/**
	 * Sets the body allow add.
	 *
	 * @param pbodyAllowAdd
	 *            the new body allow add
	 */
	public void setBodyAllowAdd(final boolean pbodyAllowAdd) {
		this.bodyAllowAdd = pbodyAllowAdd;
	}

	/**
	 * Gets the origin config range.
	 *
	 * @return the origin config range
	 */
	public ConfigRange getOriginConfigRange() {
		return originConfigRange;
	}

	/**
	 * Sets the origin config range.
	 *
	 * @param poriginConfigRange
	 *            the new origin config range
	 */
	public void setOriginConfigRange(final ConfigRange poriginConfigRange) {
		this.originConfigRange = poriginConfigRange;
	}

	/**
	 * Gets the command index map.
	 *
	 * @return the command index map
	 */
	public Map<String, Command> getCommandIndexMap() {
		return commandIndexMap;
	}

	/**
	 * Sets the command index map.
	 *
	 * @param pcommandIndexMap
	 *            the command index map
	 */
	public void setCommandIndexMap(
			final Map<String, Command> pcommandIndexMap) {
		this.commandIndexMap = pcommandIndexMap;
	}

	/**
	 * Checks if is adds the mode.
	 *
	 * @return true, if is adds the mode
	 */
	public boolean isAddMode() {
		return addMode;
	}

	/**
	 * Sets the adds the mode.
	 *
	 * @param paddMode
	 *            the new adds the mode
	 */
	public void setAddMode(final boolean paddMode) {
		this.addMode = paddMode;
	}

	/**
	 * Gets the collection obj name map.
	 *
	 * @return the collection obj name map
	 */
	public Map<String, String> getCollectionObjNameMap() {
		return collectionObjNameMap;
	}

	/**
	 * Sets the collection obj name map.
	 *
	 * @param pcollectionObjNameMap
	 *            the collection obj name map
	 */
	public void setCollectionObjNameMap(
			final Map<String, String> pcollectionObjNameMap) {
		this.collectionObjNameMap = pcollectionObjNameMap;
	}

	/**
	 * Gets the insert position.
	 *
	 * @return the insert position
	 */
	public int getInsertPosition() {
		return insertPosition;
	}

	/**
	 * Sets the insert position.
	 *
	 * @param pinsertPosition
	 *            the new insert position
	 */
	public void setInsertPosition(final int pinsertPosition) {
		this.insertPosition = pinsertPosition;
	}

	/**
	 * Gets the cell attributes map.
	 *
	 * @return the cell attributes map
	 */
	public CellAttributesMap getCellAttributesMap() {
		return cellAttributesMap;
	}

	/**
	 * Gets the final comment map.
	 *
	 * @return the final comment map
	 */
	public Map<Cell, String> getFinalCommentMap() {
		return finalCommentMap;
	}

}
