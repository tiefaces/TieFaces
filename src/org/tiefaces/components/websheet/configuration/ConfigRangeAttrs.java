/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;
import org.tiefaces.components.websheet.dataobjects.MapSnapShot;

/**
 * The Class ConfigRangeAttrs.
 */
public class ConfigRangeAttrs {
	/** first cell. */
	private Cell firstRowRef;
	/** last cell. */
	private Cell lastRowPlusRef;
	/** first cell address. This used to calculate relative address. */
	private CellAddress firstRowAddr;
	/** last cell address. This used to calculate relative address. */
	private CellAddress lastRowPlusAddr;
	/** if true then the lastCell is created instead of exist cell. */
	private boolean lastCellCreated;
	
	/** The final length. */
	private int finalLength = 0;
	
	/** The allow add. */
	private boolean allowAdd = false;
	
	/** The unit rows mapping. */
	private RowsMapping unitRowsMapping = null;
	
	/** The context snap. */
	private MapSnapShot contextSnap = null;

	/**
	 * Instantiates a new config range attrs.
	 *
	 * @param lastCellCreated
	 *            the last cell created
	 */
	public ConfigRangeAttrs(boolean lastCellCreated) {
		this.lastCellCreated = lastCellCreated;
	}

	/**
	 * Gets the first row ref.
	 *
	 * @return the first row ref
	 */
	public Cell getFirstRowRef() {
		return firstRowRef;
	}

	/**
	 * Sets the first row ref.
	 *
	 * @param firstRowRef
	 *            the new first row ref
	 */
	public void setFirstRowRef(Cell firstRowRef) {
		this.firstRowRef = firstRowRef;
	}

	/**
	 * Gets the last row plus ref.
	 *
	 * @return the last row plus ref
	 */
	public Cell getLastRowPlusRef() {
		return lastRowPlusRef;
	}

	/**
	 * Sets the last row plus ref.
	 *
	 * @param lastRowPlusRef
	 *            the new last row plus ref
	 */
	public void setLastRowPlusRef(Cell lastRowPlusRef) {
		this.lastRowPlusRef = lastRowPlusRef;
	}

	/**
	 * Gets the first row addr.
	 *
	 * @return the first row addr
	 */
	public CellAddress getFirstRowAddr() {
		return firstRowAddr;
	}

	/**
	 * Sets the first row addr.
	 *
	 * @param firstRowAddr
	 *            the new first row addr
	 */
	public void setFirstRowAddr(CellAddress firstRowAddr) {
		this.firstRowAddr = firstRowAddr;
	}

	/**
	 * Gets the last row plus addr.
	 *
	 * @return the last row plus addr
	 */
	public CellAddress getLastRowPlusAddr() {
		return lastRowPlusAddr;
	}

	/**
	 * Sets the last row plus addr.
	 *
	 * @param lastRowPlusAddr
	 *            the new last row plus addr
	 */
	public void setLastRowPlusAddr(CellAddress lastRowPlusAddr) {
		this.lastRowPlusAddr = lastRowPlusAddr;
	}

	/**
	 * Checks if is last cell created.
	 *
	 * @return true, if is last cell created
	 */
	public boolean isLastCellCreated() {
		return lastCellCreated;
	}

	/**
	 * Sets the last cell created.
	 *
	 * @param lastCellCreated
	 *            the new last cell created
	 */
	public void setLastCellCreated(boolean lastCellCreated) {
		this.lastCellCreated = lastCellCreated;
	}

	/**
	 * Gets the final length.
	 *
	 * @return the final length
	 */
	public int getFinalLength() {
		return finalLength;
	}

	/**
	 * Sets the final length.
	 *
	 * @param finalLength
	 *            the new final length
	 */
	public void setFinalLength(int finalLength) {
		this.finalLength = finalLength;
	}

	/**
	 * Checks if is allow add.
	 *
	 * @return true, if is allow add
	 */
	public boolean isAllowAdd() {
		return allowAdd;
	}

	/**
	 * Sets the allow add.
	 *
	 * @param allowAdd
	 *            the new allow add
	 */
	public void setAllowAdd(boolean allowAdd) {
		this.allowAdd = allowAdd;
	}

	/**
	 * Gets the unit rows mapping.
	 *
	 * @return the unit rows mapping
	 */
	public RowsMapping getUnitRowsMapping() {
		return unitRowsMapping;
	}

	/**
	 * Sets the unit rows mapping.
	 *
	 * @param unitRowsMapping
	 *            the new unit rows mapping
	 */
	public void setUnitRowsMapping(RowsMapping unitRowsMapping) {
		this.unitRowsMapping = unitRowsMapping;
	}

	/**
	 * Gets the context snap.
	 *
	 * @return the context snap
	 */
	public MapSnapShot getContextSnap() {
		return contextSnap;
	}

	/**
	 * Sets the context snap.
	 *
	 * @param contextSnap
	 *            the new context snap
	 */
	public void setContextSnap(MapSnapShot contextSnap) {
		this.contextSnap = contextSnap;
	}
	
	
}