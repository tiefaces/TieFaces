/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.List;

/**
 * Contain objects for shift formulas. The formula use those object for
 * shifting. watchList: contain lines which involved in rows shift.
 * currentRowsMappingList: rows mapping list of current rows. *
 * 
 * @author Jason Jiang
 *
 */
public class ShiftFormulaRef {

	/** The watch list. */
	private List<Integer> watchList;

	/** The current rows mapping list. */
	private List<RowsMapping> currentRowsMappingList;

	/** The formula changed. */
	private int formulaChanged = 0;

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
	 * @param pwatchList
	 *            the new watch list
	 */
	public void setWatchList(final List<Integer> pwatchList) {
		this.watchList = pwatchList;
	}

	/**
	 * Gets the current rows mapping list.
	 *
	 * @return the current rows mapping list
	 */
	public List<RowsMapping> getCurrentRowsMappingList() {
		return currentRowsMappingList;
	}

	/**
	 * Sets the current rows mapping list.
	 *
	 * @param pcurrentRowsMappingList
	 *            the new current rows mapping list
	 */
	public void setCurrentRowsMappingList(
			final List<RowsMapping> pcurrentRowsMappingList) {
		this.currentRowsMappingList = pcurrentRowsMappingList;
	}

	/**
	 * Gets the formula changed.
	 *
	 * @return the formula changed
	 */
	public int getFormulaChanged() {
		return formulaChanged;
	}

	/**
	 * Sets the formula changed.
	 *
	 * @param pformulaChanged
	 *            the new formula changed
	 */
	public void setFormulaChanged(final int pformulaChanged) {
		this.formulaChanged = pformulaChanged;
	}

	/**
	 * Instantiates a new shift formula ref.
	 *
	 * @param pwatchList
	 *            the watch list
	 * @param pcurrentRowsMappingList
	 *            the current rows mapping list
	 */
	public ShiftFormulaRef(final List<Integer> pwatchList,
			final List<RowsMapping> pcurrentRowsMappingList) {
		super();
		this.watchList = pwatchList;
		this.currentRowsMappingList = pcurrentRowsMappingList;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{ watchList = " + watchList);
		sb.append(",");
		sb.append("currentRowsMappingList = " + currentRowsMappingList);
		sb.append("}");
		return sb.toString();
	}
}
