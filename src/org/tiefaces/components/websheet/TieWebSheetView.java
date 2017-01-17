/*
 * Copyright 2015 TieFaces.
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
	 * empty constructor.
	 */
	public TieWebSheetView() {
	}

	/**
	 * tabs.
	 */
	private List<TabModel> tabs;

	/**
	 * get tabs.
	 * 
	 * @return list of tabs.
	 */
	public final List<TabModel> getTabs() {
		return tabs;
	}

	/**
	 * set tabs.
	 * 
	 * @param ptabs
	 *            list of tabs.
	 */
	public final void setTabs(final List<TabModel> ptabs) {
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
		public final String getId() {
			return id;
		}

		/**
		 * set id.
		 * 
		 * @param pid id.
		 */
		public final void setId(final String pid) {
			this.id = pid;
		}

		/**
		 * get title.
		 * 
		 * @return title.
		 */
		public final String getTitle() {
			return title;
		}

		/**
		 * get type.
		 * 
		 * @return type.
		 */
		public final String getType() {
			return type;
		}
	}

	/**
	 * web form tab view.
	 */
	private TabView webFormTabView = null;

	/**
	 * get web form tab view.
	 * 
	 * @return webformtabview.
	 */
	public final TabView getWebFormTabView() {
		return webFormTabView;
	}

	/**
	 * set pwebform tab view.
	 * 
	 * @param pwebFormTabView
	 *            webformtabview.
	 */
	public final void setWebFormTabView(final TabView pwebFormTabView) {
		this.webFormTabView = pwebFormTabView;
	}

	/**
	 * tab type.
	 */
	private String tabType;

	/**
	 * get tab type.
	 * 
	 * @return tab type.
	 */
	public final String getTabType() {

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
	 * tab style.
	 */
	private String tabStyle;

	/**
	 * get tab style.
	 * 
	 * @return string.
	 */
	public final String getTabStyle() {

		tabStyle = TieConstants.TAB_STYLE_VISIBLE;
		int sheetId = webFormTabView.getActiveIndex();
		if (sheetId >= 0) {
			if (sheetId < tabs.size()) {
				tabStyle = TieConstants.TAB_STYLE_INVISIBLE;
			}
		}

		return tabStyle;
	}

	/**
	 * set tab type.
	 * 
	 * @param ptabType
	 *            tab type.
	 */
	public final void setTabType(final String ptabType) {
		this.tabType = ptabType;
	}

	/** maxrowsperpage. */
	private Integer maxRowsPerPage = TieConstants.DEFAULT_MAX_ROWS_PER_PAGE;

	/**
	 * get maxrowsperpage.
	 * 
	 * @return maxrowsperpage.
	 */
	public final Integer getMaxRowsPerPage() {
		return maxRowsPerPage;
	}

	/**
	 * set maxrowsperpage.
	 * 
	 * @param pmaxRowsPerPage
	 *            maxrowsperpage.
	 */
	public final void setMaxRowsPerPage(final Integer pmaxRowsPerPage) {
		this.maxRowsPerPage = pmaxRowsPerPage;
	}

	/**
	 * tabwidth style.
	 */
	private String tableWidthStyle = TieConstants.DEFAULT_TABLE_WIDTH_STYLE;

	/**
	 * get table width style.
	 * 
	 * @return tablewidthstyle.
	 */
	public final String getTableWidthStyle() {
		return tableWidthStyle;
	}

	/**
	 * set tablewidthstyle.
	 * 
	 * @param ptableWidthStyle
	 *            tablewidth style.
	 */
	public final void setTableWidthStyle(final String ptableWidthStyle) {
		this.tableWidthStyle = ptableWidthStyle;
	}

	/** line number column width. */
	private int lineNumberColumnWidth 
	= TieConstants.DEFAULT_LINENUMBER_COLUMN_WIDTH;
	/** add row column width. */
	private int addRowColumnWidth = TieConstants.DEFAULT_ADDROW_COLUMN_WIDTH;
	/** line number column width style. */
	private String lineNumberColumnWidthStyle = "";
	/** add row column width style. */
	private String addRowColumnWidthStyle = "";

	/**
	 * get line number column width.
	 * 
	 * @return linenumbercolumnwidth.
	 */
	public final int getLineNumberColumnWidth() {
		return lineNumberColumnWidth;
	}

	/**
	 * set line number column width .
	 * 
	 * @param plineNumberColumnWidth
	 *            column width.
	 */
	public final void setLineNumberColumnWidth(
			final int plineNumberColumnWidth) {
		this.lineNumberColumnWidth = plineNumberColumnWidth;
	}

	/**
	 * get add row column width.
	 * 
	 * @return add row column width.
	 */
	public final int getAddRowColumnWidth() {
		return addRowColumnWidth;
	}

	/**
	 * set add row column width.
	 * 
	 * @param addWidth
	 *            add row column width.
	 */
	public final void setAddRowColumnWidth(final int addWidth) {
		this.addRowColumnWidth = addWidth;
	}

	/**
	 * get line number column width style.
	 * 
	 * @return column width style.
	 */
	public final String getLineNumberColumnWidthStyle() {
		return lineNumberColumnWidthStyle;
	}

	/**
	 * set line number column width style.
	 * 
	 * @param lineNumberCoumnWidthStyle
	 *            style.
	 */
	public final void setLineNumberColumnWidthStyle(
			final String lineNumberCoumnWidthStyle) {
		this.lineNumberColumnWidthStyle = lineNumberCoumnWidthStyle;
	}

	/**
	 * get add row column width style.
	 * 
	 * @return style.
	 */
	public final String getAddRowColumnWidthStyle() {
		return addRowColumnWidthStyle;
	}

	/**
	 * set add row column style.
	 * 
	 * @param addWidthStyle
	 *            style.
	 */
	public final void setAddRowColumnWidthStyle(final String addWidthStyle) {
		this.addRowColumnWidthStyle = addWidthStyle;
	}

	/** allow add row in body. */
	private boolean bodyAllowAddRows;

	/**
	 * is allow add row.
	 * 
	 * @return true if allowed.
	 */
	public final boolean isBodyAllowAddRows() {
		return bodyAllowAddRows;
	}

	/**
	 * set body allow add rows.
	 * 
	 * @param pbodyAllowAddRows
	 *            true if allowed.
	 */
	public final void setBodyAllowAddRows(final boolean pbodyAllowAddRows) {
		this.bodyAllowAddRows = pbodyAllowAddRows;
	}

	/** show line number in web gui. */
	private boolean showLineNumber = false;

	/**
	 * is show line number.
	 * 
	 * @return true if show.
	 */
	public final boolean isShowLineNumber() {
		return showLineNumber;
	}

	/**
	 * set show line number.
	 * 
	 * @param pshowLineNumber
	 *            true if show.
	 */
	public final void setShowLineNumber(final boolean pshowLineNumber) {
		this.showLineNumber = pshowLineNumber;
	}

}
