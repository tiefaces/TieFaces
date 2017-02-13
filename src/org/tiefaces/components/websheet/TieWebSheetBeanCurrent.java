/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private transient String currentDataContextName;
	/** current data context name list. */
	private transient List<String> currentDataContextNameList;
	/** current tab name of display sheet. */
	private String currentTabName;
	/** current top row of display sheet. */
	private int currentTopRow;
	/** current left column of display sheet. */
	private int currentLeftColumn;

	/**
	 * Gets the current data context name list.
	 *
	 * @return the currentDataContextNameList
	 */
	public final List<String> getCurrentDataContextNameList() {
		if (currentDataContextNameList==null) {
			currentDataContextNameList = new ArrayList<>();
		}
		return currentDataContextNameList;
	}

	/**
	 * Sets the current data context name list.
	 *
	 * @param currentDataContextNameList
	 *            the currentDataContextNameList to set
	 */
	public final void setCurrentDataContextNameList(
			List<String> currentDataContextNameList) {
		this.currentDataContextNameList = currentDataContextNameList;
	}




	/**
	 * Gets the current data context name.
	 *
	 * @return the currentDataContextName
	 */
	public final String getCurrentDataContextName() {
		if (currentDataContextName==null) {
			StringBuilder sb = new StringBuilder();
			List<String> list = this.getCurrentDataContextNameList();
			for (int i=0; i< list.size(); i++ ) {
				if (i>0) {
					sb.append(":"+list.get(i));
				} else {
					sb.append(list.get(i));
				}
			}
			this.setCurrentDataContextName(sb.toString());
		}
		return currentDataContextName;
	}
	
	
	

	/**
	 * Sets the current data context name.
	 *
	 * @param currentDataContextName
	 *            the currentDataContextName to set
	 */
	public final void setCurrentDataContextName(String currentDataContextName) {
		this.currentDataContextName = currentDataContextName;
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