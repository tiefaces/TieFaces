package org.tiefaces.components.websheet;
/**
 * Current collections for websheet bean.
 * @author Jason Jiang
 *
 */
public class TieWebSheetBeanCurrent {
	/** current data context name. */
	private String currentDataContextName;
	/** current tab name of display sheet. */
	private String currentTabName;
	/** current top row of display sheet. */
	private int currentTopRow;
	/** current left column of display sheet. */
	private int currentLeftColumn;
	/**
	 * @return the currentDataContextName
	 */
	public final String getCurrentDataContextName() {
		return currentDataContextName;
	}
	/**
	 * @param pcurrentDataContextName the currentDataContextName to set
	 */
	public final void setCurrentDataContextName(String pcurrentDataContextName) {
		this.currentDataContextName = pcurrentDataContextName;
	}
	/**
	 * @return the currentTabName
	 */
	public final String getCurrentTabName() {
		return currentTabName;
	}
	/**
	 * @param pcurrentTabName the currentTabName to set
	 */
	public final void setCurrentTabName(final String pcurrentTabName) {
		this.currentTabName = pcurrentTabName;
	}
	/**
	 * @return the currentTopRow
	 */
	public final int getCurrentTopRow() {
		return currentTopRow;
	}
	/**
	 * @param pcurrentTopRow the currentTopRow to set
	 */
	public final void setCurrentTopRow(final int pcurrentTopRow) {
		this.currentTopRow = pcurrentTopRow;
	}
	/**
	 * @return the currentLeftColumn
	 */
	public final int getCurrentLeftColumn() {
		return currentLeftColumn;
	}
	/**
	 * @param pcurrentLeftColumn the currentLeftColumn to set
	 */
	public final void setCurrentLeftColumn(final int pcurrentLeftColumn) {
		this.currentLeftColumn = pcurrentLeftColumn;
	}

	
	
	
	
}