/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.tiefaces.components.websheet.serializable.SerialCell;
import org.tiefaces.components.websheet.serializable.SerialCellAddress;

/**
 * The Class ConfigRangeAttrs.
 */
public class ConfigRangeAttrs implements Serializable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6331060203032223425L;

	/** first cell. */
	private SerialCell serialFirstRowRef;
	/** last cell. */
	private SerialCell serialLastRowPlusRef;
	/** first cell address. This used to calculate relative address. */
	private SerialCellAddress firstRowAddr;
	/** last cell address. This used to calculate relative address. */
	private SerialCellAddress lastRowPlusAddr;
	/** if true then the lastCell is created instead of exist cell. */
	private boolean lastCellCreated;

	/** The final length. */
	private int finalLength = 0;

	/** The allow add. */
	private boolean allowAdd = false;

	/** The unit rows mapping. */
	private RowsMapping unitRowsMapping = null;

	/**
	 * Instantiates a new config range attrs.
	 *
	 * @param plastCellCreated
	 *            the last cell created
	 */
	public ConfigRangeAttrs(final boolean plastCellCreated) {
		this.lastCellCreated = plastCellCreated;
	}

	/**
	 * Gets the first row ref.
	 *
	 * @return the first row ref
	 */
	public final Cell getFirstRowRef() {
		return serialFirstRowRef.getCell();
	}

	/**
	 * @return the serialFirstRowRef
	 */
	public final SerialCell getSerialFirstRowRef() {
		if (this.serialFirstRowRef == null) {
			this.serialFirstRowRef = new SerialCell();
		}
		return serialFirstRowRef;
	}

	/**
	 * Sets the first row ref.
	 *
	 * @param pfirstRowRef
	 *            the new first row ref
	 */
	public final void setFirstRowRef(final Cell pfirstRowRef) {
		this.getSerialFirstRowRef().setCell(pfirstRowRef);
	}

	/**
	 * Gets the last row plus ref.
	 *
	 * @return the last row plus ref
	 */
	public final Cell getLastRowPlusRef() {
		return serialLastRowPlusRef.getCell();
	}

	/**
	 * @return the serialLastRowPlusRef
	 */
	public final SerialCell getSerialLastRowPlusRef() {
		if (this.serialLastRowPlusRef == null) {
			this.serialLastRowPlusRef = new SerialCell();
		}
		return serialLastRowPlusRef;
	}

	/**
	 * Sets the last row plus ref.
	 *
	 * @param plastRowPlusRef
	 *            the new last row plus ref
	 */
	public final void setLastRowPlusRef(final Cell plastRowPlusRef) {
		this.getSerialLastRowPlusRef().setCell(plastRowPlusRef);
	}

	/**
	 * Gets the first row addr.
	 *
	 * @return the first row addr
	 */
	public final SerialCellAddress getFirstRowAddr() {
		return firstRowAddr;
	}

	/**
	 * Sets the first row addr.
	 *
	 * @param pfirstRowAddr
	 *            the new first row addr
	 */
	public final void setFirstRowAddr(
			final SerialCellAddress pfirstRowAddr) {
		this.firstRowAddr = pfirstRowAddr;
	}

	/**
	 * Gets the last row plus addr.
	 *
	 * @return the last row plus addr
	 */
	public final SerialCellAddress getLastRowPlusAddr() {
		return lastRowPlusAddr;
	}

	/**
	 * Sets the last row plus addr.
	 *
	 * @param plastRowPlusAddr
	 *            the new last row plus addr
	 */
	public final void setLastRowPlusAddr(
			final SerialCellAddress plastRowPlusAddr) {
		this.lastRowPlusAddr = plastRowPlusAddr;
	}

	/**
	 * Checks if is last cell created.
	 *
	 * @return true, if is last cell created
	 */
	public final boolean isLastCellCreated() {
		return lastCellCreated;
	}

	/**
	 * Sets the last cell created.
	 *
	 * @param plastCellCreated
	 *            the new last cell created
	 */
	public final void setLastCellCreated(final boolean plastCellCreated) {
		this.lastCellCreated = plastCellCreated;
	}

	/**
	 * Gets the final length.
	 *
	 * @return the final length
	 */
	public final int getFinalLength() {
		return finalLength;
	}

	/**
	 * Sets the final length.
	 *
	 * @param pfinalLength
	 *            the new final length
	 */
	public final void setFinalLength(final int pfinalLength) {
		this.finalLength = pfinalLength;
	}

	/**
	 * Checks if is allow add.
	 *
	 * @return true, if is allow add
	 */
	public final boolean isAllowAdd() {
		return allowAdd;
	}

	/**
	 * Sets the allow add.
	 *
	 * @param pallowAdd
	 *            the new allow add
	 */
	public final void setAllowAdd(final boolean pallowAdd) {
		this.allowAdd = pallowAdd;
	}

	/**
	 * Gets the unit rows mapping.
	 *
	 * @return the unit rows mapping
	 */
	public final RowsMapping getUnitRowsMapping() {
		return unitRowsMapping;
	}

	/**
	 * Sets the unit rows mapping.
	 *
	 * @param punitRowsMapping
	 *            the new unit rows mapping
	 */
	public final void setUnitRowsMapping(
			final RowsMapping punitRowsMapping) {
		this.unitRowsMapping = punitRowsMapping;
	}


	/**
	 * recover by using it's address.
	 * 
	 * @param sheet
	 *            sheet.
	 */
	public final void recover(final Sheet sheet) {

		this.getSerialFirstRowRef().recover(sheet);
		this.getSerialLastRowPlusRef().recover(sheet);
		if (this.getUnitRowsMapping() != null) {
			this.getUnitRowsMapping().recover(sheet);
		}
	}

}