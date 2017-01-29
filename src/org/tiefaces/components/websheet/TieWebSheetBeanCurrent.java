/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet;

import java.io.Serializable;

/**
 * Current collections for websheet bean.
 * 
 * @author Jason Jiang
 *
 */
public class TieWebSheetBeanCurrent implements Serializable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4330466518786654088L;
	/** current data context name. */
	private String currentDataContextName;
	/** current tab name of display sheet. */
	private String currentTabName;
	/** current top row of display sheet. */
	private int currentTopRow;
	/** current left column of display sheet. */
	private int currentLeftColumn;

	/**
	 * Gets the current data context name.
	 *
	 * @return the currentDataContextName
	 */
	public final String getCurrentDataContextName() {
		return currentDataContextName;
	}

	/**
	 * Sets the current data context name.
	 *
	 * @param pcurrentDataContextName
	 *            the currentDataContextName to set
	 */
	public final void setCurrentDataContextName(
			final String pcurrentDataContextName) {
		this.currentDataContextName = pcurrentDataContextName;
	}

	/**
	 * Gets the current tab name.
	 *
	 * @return the currentTabName
	 */
	public final String getCurrentTabName() {
		return currentTabName;
	}

	/**
	 * Sets the current tab name.
	 *
	 * @param pcurrentTabName
	 *            the currentTabName to set
	 */
	public final void setCurrentTabName(final String pcurrentTabName) {
		this.currentTabName = pcurrentTabName;
	}

	/**
	 * Gets the current top row.
	 *
	 * @return the currentTopRow
	 */
	public final int getCurrentTopRow() {
		return currentTopRow;
	}

	/**
	 * Sets the current top row.
	 *
	 * @param pcurrentTopRow
	 *            the currentTopRow to set
	 */
	public final void setCurrentTopRow(final int pcurrentTopRow) {
		this.currentTopRow = pcurrentTopRow;
	}

	/**
	 * Gets the current left column.
	 *
	 * @return the currentLeftColumn
	 */
	public final int getCurrentLeftColumn() {
		return currentLeftColumn;
	}

	/**
	 * Sets the current left column.
	 *
	 * @param pcurrentLeftColumn
	 *            the currentLeftColumn to set
	 */
	public final void setCurrentLeftColumn(final int pcurrentLeftColumn) {
		this.currentLeftColumn = pcurrentLeftColumn;
	}

}