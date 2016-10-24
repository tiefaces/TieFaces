/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.io.Serializable;
import java.util.List;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.tabview.TabView;

public class TieWebSheetView {

	public TieWebSheetView() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected List<tabModel> tabs;

	public List<tabModel> getTabs() {
		return tabs;
	}

	public void setTabs(List<tabModel> tabs) {
		this.tabs = tabs;
	}

	static public class tabModel implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 
		 */
		private String id;
		private String title;
		private String type;

		public tabModel(String id, String title, String type) {
			this.id = id;
			if (this.id != null)
				this.id = this.id.replaceAll(" ", "_");
			this.title = title;
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public String getType() {
			return type;
		}
	}

	protected TabView webFormTabView = null;

	public TabView getWebFormTabView() {
		return webFormTabView;
	}

	public void setWebFormTabView(TabView webFormTabView) {
		this.webFormTabView = webFormTabView;
	}

	protected String tabType;

	public String getTabType() {

		int sheetId = webFormTabView.getActiveIndex();
		if ((sheetId >= 0) && (tabs != null)) {
			if (sheetId >= tabs.size())
				sheetId = 0;
			tabType = tabs.get(sheetId).type.toLowerCase();
		} else
			tabType = "none";
		return tabType;
	}

	protected String tabStyle;

	public String getTabStyle() {

		tabStyle = "height: 530px;";
		int sheetId = webFormTabView.getActiveIndex();
		if (sheetId >= 0) {
			if (sheetId < tabs.size())
				tabStyle = "height: 30px;";
			// if (tabs.get(sheetId).type.equalsIgnoreCase("form")) tabStyle =
			// "height: 30px;";
		}

		return tabStyle;
	}

	public void setTabType(String tabType) {
		this.tabType = tabType;
	}

	private Integer maxRowsPerPage = 80;

	public Integer getMaxRowsPerPage() {
		return maxRowsPerPage;
	}

	public void setMaxRowsPerPage(Integer maxRowsPerPage) {
		this.maxRowsPerPage = maxRowsPerPage;
	}

	private String tableWidthStyle = "100%;";

	public String getTableWidthStyle() {
		return tableWidthStyle;
	}

	public void setTableWidthStyle(String tableWidthStyle) {
		this.tableWidthStyle = tableWidthStyle;
	}

	// width use pixel 24 = 24px;
	private int lineNumberColumnWidth = 26;
	private int addRowColumnWidth = 38;

	private String lineNumberColumnWidthStyle = "";
	private String addRowColumnWidthStyle = "";

	public int getLineNumberColumnWidth() {
		return lineNumberColumnWidth;
	}

	public void setLineNumberColumnWidth(int lineNumberColumnWidth) {
		this.lineNumberColumnWidth = lineNumberColumnWidth;
	}

	public int getAddRowColumnWidth() {
		return addRowColumnWidth;
	}

	public void setAddRowColumnWidth(int addWidth) {
		this.addRowColumnWidth = addWidth;
	}

	public String getLineNumberColumnWidthStyle() {
		return lineNumberColumnWidthStyle;
	}

	public void setLineNumberColumnWidthStyle(String lineNumberCoumnWidthStyle) {
		this.lineNumberColumnWidthStyle = lineNumberCoumnWidthStyle;
	}

	public String getAddRowColumnWidthStyle() {
		return addRowColumnWidthStyle;
	}

	public void setAddRowColumnWidthStyle(String addWidthStyle) {
		this.addRowColumnWidthStyle = addWidthStyle;
	}

	private boolean bodyAllowAddRows;

	public boolean isBodyAllowAddRows() {
		return bodyAllowAddRows;
	}

	public void setBodyAllowAddRows(boolean bodyAllowAddRows) {
		this.bodyAllowAddRows = bodyAllowAddRows;
	}

	private boolean showLineNumber = false;

	public boolean isShowLineNumber() {
		return showLineNumber;
	}

	public void setShowLineNumber(boolean showLineNumber) {
		this.showLineNumber = showLineNumber;
	}

}
