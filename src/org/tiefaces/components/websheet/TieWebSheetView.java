/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.io.Serializable;
import java.util.List;

import org.primefaces.component.tabview.TabView;
import org.tiefaces.common.TieConstants;

/**
 * sheet view class.
 * 
 * @author Jason Jiang.
 *
 */
public class TieWebSheetView {
	
	/**
	 * tab type.
	 */
	private String tabType;
	
	/**
	 * tabs.
	 */
	private List<TabModel> tabs;

	/**
	 * web form tab view.
	 */
	private TabView webFormTabView = null;

	/** maxrowsperpage. */
	private Integer maxRowsPerPage = TieConstants.DEFAULT_MAX_ROWS_PER_PAGE;
	
	/**
	 * tabwidth style.
	 */
	private String tableWidthStyle = TieConstants.DEFAULT_TABLE_WIDTH_STYLE;

	/** line number column width. */
	private int lineNumberColumnWidth 
	= TieConstants.DEFAULT_LINENUMBER_COLUMN_WIDTH;
	/** add row column width. */
	private int addRowColumnWidth = TieConstants.DEFAULT_ADDROW_COLUMN_WIDTH;
	/** line number column width style. */
	private String lineNumberColumnWidthStyle = "";
	/** add row column width style. */
	private String addRowColumnWidthStyle = "";	
	/** allow add row in body. */
	private boolean bodyAllowAddRows;	
	/** show line number in web gui. */
	private boolean showLineNumber = false;
	/** The hide single sheet tab title. */
	private boolean hideSingleSheetTabTitle = true;
	/** active tab index */
	private int activeTabIndex = 0;
	
	
	/**
	 * empty constructor.
	 */
	public TieWebSheetView() {
		super();
	}


	/**
	 * get tabs.
	 * 
	 * @return list of tabs.
	 */
	public List<TabModel> getTabs() {
		return tabs;
	}

	/**
	 * set tabs.
	 * 
	 * @param ptabs
	 *            list of tabs.
	 */
	public void setTabs(final List<TabModel> ptabs) {
		this.tabs = ptabs;
	}

	/**
	 * tab model class.
	 * 
	 * @author Jason Jiang
	 *
	 */
	public static class TabModel implements Serializable {

		/** serialid. */
		private static final long serialVersionUID = 1L;
		/** tab id. */
		private String id;
		/** tab title. */
		private String title;
		/** tab type. */
		private String type;

		/**
		 * constructor.
		 * 
		 * @param pid
		 *            id.
		 * @param ptitle
		 *            title.
		 * @param ptype
		 *            type.
		 */
		public TabModel(final String pid, final String ptitle,
				final String ptype) {
			this.id = pid;
			if (this.id != null) {
				this.id = this.id.replaceAll(" ", "_");
			}
			this.title = ptitle;
			this.type = ptype;
		}

		/**
		 * get id.
		 * 
		 * @return id.
		 */
		public String getId() {
			return id;
		}

		/**
		 * set id.
		 * 
		 * @param pid id.
		 */
		public void setId(final String pid) {
			this.id = pid;
		}

		/**
		 * get title.
		 * 
		 * @return title.
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * get type.
		 * 
		 * @return type.
		 */
		public String getType() {
			return type;
		}
	}


	/**
	 * get web form tab view.
	 * 
	 * @return webformtabview.
	 */
	public TabView getWebFormTabView() {
		return webFormTabView;
	}

	/**
	 * set pwebform tab view.
	 * 
	 * @param pwebFormTabView
	 *            webformtabview.
	 */
	public void setWebFormTabView(final TabView pwebFormTabView) {
		this.webFormTabView = pwebFormTabView;
	}


	/**
	 * get tab type.
	 * 
	 * @return tab type.
	 */
	public String getTabType() {

		int sheetId = webFormTabView.getActiveIndex();
		if ((sheetId >= 0) && (tabs != null)) {
			if (sheetId >= tabs.size()) {
				sheetId = 0;
			}
			tabType = tabs.get(sheetId).type.toLowerCase();
		} else {
			tabType = TieConstants.TAB_TYPE_NONE;
		}
		return tabType;
	}


	/**
	 * get tab style.
	 * 
	 * @return string.
	 */
	public String getTabStyle() {

		String tabStyle = TieConstants.TAB_STYLE_VISIBLE;
		int sheetId = webFormTabView.getActiveIndex();
		if ((sheetId >= 0) && (sheetId < tabs.size())) {
				tabStyle = TieConstants.TAB_STYLE_INVISIBLE;
			
		}

		return tabStyle;
	}

	/**
	 * set tab type.
	 * 
	 * @param ptabType
	 *            tab type.
	 */
	public void setTabType(final String ptabType) {
		this.tabType = ptabType;
	}



	/**
	 * get maxrowsperpage.
	 * 
	 * @return maxrowsperpage.
	 */
	public Integer getMaxRowsPerPage() {
		return maxRowsPerPage;
	}

	/**
	 * set maxrowsperpage.
	 * 
	 * @param pmaxRowsPerPage
	 *            maxrowsperpage.
	 */
	public void setMaxRowsPerPage(final Integer pmaxRowsPerPage) {
		this.maxRowsPerPage = pmaxRowsPerPage;
	}



	/**
	 * get table width style.
	 * 
	 * @return tablewidthstyle.
	 */
	public String getTableWidthStyle() {
		return tableWidthStyle;
	}

	/**
	 * set tablewidthstyle.
	 * 
	 * @param ptableWidthStyle
	 *            tablewidth style.
	 */
	public void setTableWidthStyle(final String ptableWidthStyle) {
		this.tableWidthStyle = ptableWidthStyle;
	}



	/**
	 * get line number column width.
	 * 
	 * @return linenumbercolumnwidth.
	 */
	public int getLineNumberColumnWidth() {
		return lineNumberColumnWidth;
	}

	/**
	 * set line number column width .
	 * 
	 * @param plineNumberColumnWidth
	 *            column width.
	 */
	public void setLineNumberColumnWidth(
			final int plineNumberColumnWidth) {
		this.lineNumberColumnWidth = plineNumberColumnWidth;
	}

	/**
	 * get add row column width.
	 * 
	 * @return add row column width.
	 */
	public int getAddRowColumnWidth() {
		return addRowColumnWidth;
	}

	/**
	 * set add row column width.
	 * 
	 * @param addWidth
	 *            add row column width.
	 */
	public void setAddRowColumnWidth(final int addWidth) {
		this.addRowColumnWidth = addWidth;
	}

	/**
	 * get line number column width style.
	 * 
	 * @return column width style.
	 */
	public String getLineNumberColumnWidthStyle() {
		return lineNumberColumnWidthStyle;
	}

	/**
	 * set line number column width style.
	 * 
	 * @param lineNumberCoumnWidthStyle
	 *            style.
	 */
	public void setLineNumberColumnWidthStyle(
			final String lineNumberCoumnWidthStyle) {
		this.lineNumberColumnWidthStyle = lineNumberCoumnWidthStyle;
	}

	/**
	 * get add row column width style.
	 * 
	 * @return style.
	 */
	public String getAddRowColumnWidthStyle() {
		return addRowColumnWidthStyle;
	}

	/**
	 * set add row column style.
	 * 
	 * @param addWidthStyle
	 *            style.
	 */
	public void setAddRowColumnWidthStyle(final String addWidthStyle) {
		this.addRowColumnWidthStyle = addWidthStyle;
	}



	/**
	 * is allow add row.
	 * 
	 * @return true if allowed.
	 */
	public boolean isBodyAllowAddRows() {
		return bodyAllowAddRows;
	}

	/**
	 * set body allow add rows.
	 * 
	 * @param pbodyAllowAddRows
	 *            true if allowed.
	 */
	public void setBodyAllowAddRows(final boolean pbodyAllowAddRows) {
		this.bodyAllowAddRows = pbodyAllowAddRows;
	}



	/**
	 * is show line number.
	 * 
	 * @return true if show.
	 */
	public boolean isShowLineNumber() {
		return showLineNumber;
	}

	/**
	 * set show line number.
	 * 
	 * @param pshowLineNumber
	 *            true if show.
	 */
	public void setShowLineNumber(final boolean pshowLineNumber) {
		this.showLineNumber = pshowLineNumber;
	}

	
	/**
	 * Checks if is hide single sheet tab title.
	 *
	 * @return the hideSingleSheetTabTitle
	 */
	public boolean isHideSingleSheetTabTitle() {
		return hideSingleSheetTabTitle;
	}

	/**
	 * Sets the hide single sheet tab title.
	 *
	 * @param phideSingleSheetTabTitle
	 *            the hideSingleSheetTabTitle to set
	 */
	public void setHideSingleSheetTabTitle(
			final boolean phideSingleSheetTabTitle) {
		this.hideSingleSheetTabTitle = phideSingleSheetTabTitle;
	}

	public int getActiveTabIndex() {
		return activeTabIndex;
	}

	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}

}
