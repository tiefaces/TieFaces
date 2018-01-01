/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.primefaces.component.tabview.TabView;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.configuration.ConfigAdvancedContext;
import org.tiefaces.components.websheet.dataobjects.TieCommandAlias;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	private int lineNumberColumnWidth = TieConstants.DEFAULT_LINENUMBER_COLUMN_WIDTH;
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
	
	/**  active tab index. */
	private int activeTabIndex = 0;

	/**
	 * Client id for whole websheet component. This is the top level client id.
	 * There're tabs and web forms under this top level.
	 */
	private String clientId = null;
	/** Client id for web forms. */
	private String webFormClientId = null;

	/** skip configuration. show the excel form as is. */
	private boolean skipConfiguration = false;

	/**
	 * workaround for rendered attributes. True -- work sheet will be rendered.
	 * False -- work sheet not rendered.
	 */
	private boolean rendered = true;

	/** The default locale. */
	private Locale defaultLocale = null;

	/** The default date pattern. */
	private String defaultDatePattern = null;

	/** The export file name. */
	private String exportFileName = "WebSheetTemplate" + "." + TieConstants.EXCEL_2007_TYPE;

	/** The is advanced context. */
	private boolean isAdvancedContext = false;

	/** The config advanced context. */
	private ConfigAdvancedContext configAdvancedContext;
	
	/** The tie web sheet validation bean. */
	private TieWebSheetValidation tieWebSheetValidationBean;
	
	/**  If true then validationBean only work in submitMode. */ 
	private boolean onlyValidateInSubmitMode = false;
	
	/** The tie command alias list. */
	private List<TieCommandAlias> tieCommandAliasList;

	/**
	 * empty constructor.
	 */
	public TieWebSheetView() {
		super();
	}

	/**
	 * assign web form client id.
	 * 
	 * @param pWebFormClientId
	 *            String client id name.
	 */
	public void setWebFormClientId(final String pWebFormClientId) {
		this.webFormClientId = pWebFormClientId;
	}

	/**
	 * Gets the web form client id.
	 *
	 * @return web form client Id.
	 */
	public String getWebFormClientId() {
		return webFormClientId;
	}

	/**
	 * Gets the client id.
	 *
	 * @return client id.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets the client id.
	 *
	 * @param pClientId
	 *            client Id.
	 */
	public void setClientId(final String pClientId) {
		this.clientId = pClientId;
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
		public TabModel(final String pid, final String ptitle, final String ptype) {
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
		 * @param pid
		 *            id.
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
	public void setLineNumberColumnWidth(final int plineNumberColumnWidth) {
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
	public void setLineNumberColumnWidthStyle(final String lineNumberCoumnWidthStyle) {
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
	public void setHideSingleSheetTabTitle(final boolean phideSingleSheetTabTitle) {
		this.hideSingleSheetTabTitle = phideSingleSheetTabTitle;
	}

	/**
	 * Checks if is show tab view.
	 *
	 * @return true, if is show tab view
	 */
	public boolean isShowTabView() {
		if (this.getTabs() == null) {
			return false;
		}
		return !this.isHideSingleSheetTabTitle() || (this.getTabs().size() > 1);

	}

	/**
	 * Checks if is skip configuration.
	 *
	 * @return the skipConfiguration
	 */
	public boolean isSkipConfiguration() {
		return skipConfiguration;
	}

	/**
	 * Sets the skip configuration.
	 *
	 * @param pskipConfiguration
	 *            the skipConfiguration to set
	 */
	public void setSkipConfiguration(final boolean pskipConfiguration) {
		this.skipConfiguration = pskipConfiguration;
	}

	/**
	 * Gets the active tab index.
	 *
	 * @return the active tab index
	 */
	public int getActiveTabIndex() {
		return activeTabIndex;
	}

	/**
	 * Sets the active tab index.
	 *
	 * @param activeTabIndex the new active tab index
	 */
	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}

	/**
	 * Checks if is rendered.
	 *
	 * @return the rendered
	 */
	public boolean isRendered() {
		return rendered;
	}

	/**
	 * Sets the rendered.
	 *
	 * @param rendered            the rendered to set
	 */
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	/**
	 * Gets the default locale.
	 *
	 * @return the default locale
	 */
	public Locale getDefaultLocale() {
		if (defaultLocale == null) {
			defaultLocale = Locale.getDefault();
		}
		return defaultLocale;
	}

	/**
	 * Sets the default locale.
	 *
	 * @param defaultLocale the new default locale
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Gets the default date pattern.
	 *
	 * @return the default date pattern
	 */
	public String getDefaultDatePattern() {
		if (defaultDatePattern == null) {
			DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
			defaultDatePattern = ((SimpleDateFormat) formatter).toLocalizedPattern();
		}
		return defaultDatePattern;
	}

	/**
	 * Sets the default date pattern.
	 *
	 * @param defaultDatePattern the new default date pattern
	 */
	public void setDefaultDatePattern(String defaultDatePattern) {
		this.defaultDatePattern = defaultDatePattern;
	}

	/**
	 * Gets the decimal separator by default locale.
	 *
	 * @return the decimal separator by default locale
	 */
	public String getDecimalSeparatorByDefaultLocale() {
		final DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(getDefaultLocale());
		return "" + nf.getDecimalFormatSymbols().getDecimalSeparator();
	}

	/**
	 * Gets the thousand separator by default locale.
	 *
	 * @return the thousand separator by default locale
	 */
	public String getThousandSeparatorByDefaultLocale() {
		final DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(getDefaultLocale());
		return "" + nf.getDecimalFormatSymbols().getGroupingSeparator();
	}

	/**
	 * Gets the export file name.
	 *
	 * @return the export file name
	 */
	public String getExportFileName() {
		return this.exportFileName;
	}

	/**
	 * Sets the export file name.
	 *
	 * @param pExportFileName the new export file name
	 */
	public void setExportFileName(String pExportFileName) {
		this.exportFileName = pExportFileName;
	}

	/**
	 * Checks if is advanced context.
	 *
	 * @return true, if is advanced context
	 */
	public boolean isAdvancedContext() {
		return isAdvancedContext;
	}

	/**
	 * Sets the advanced context.
	 *
	 * @param isAdvancedContext the new advanced context
	 */
	public void setAdvancedContext(boolean isAdvancedContext) {
		this.isAdvancedContext = isAdvancedContext;
	}

	/**
	 * Gets the config advanced context.
	 *
	 * @return the config advanced context
	 */
	public ConfigAdvancedContext getConfigAdvancedContext() {
		return configAdvancedContext;
	}

	/**
	 * Sets the config advanced context.
	 *
	 * @param configAdvancedContext the new config advanced context
	 */
	public void setConfigAdvancedContext(ConfigAdvancedContext configAdvancedContext) {
		this.configAdvancedContext = configAdvancedContext;
	}

	/**
	 * Gets the tie web sheet validation bean.
	 *
	 * @return the tie web sheet validation bean
	 */
	public TieWebSheetValidation getTieWebSheetValidationBean() {
		return tieWebSheetValidationBean;
	}

	/**
	 * Sets the tie web sheet validation bean.
	 *
	 * @param tieWebSheetValidationBean the new tie web sheet validation bean
	 * @param onlyValidationInSubmitMode the only validation in submit mode
	 */
	public void setTieWebSheetValidationBean(TieWebSheetValidation tieWebSheetValidationBean, boolean onlyValidationInSubmitMode) {
		this.tieWebSheetValidationBean = tieWebSheetValidationBean;
		this.onlyValidateInSubmitMode = onlyValidationInSubmitMode;
	}
		

	/**
	 * Checks if is only validate in submit mode.
	 *
	 * @return true, if is only validate in submit mode
	 */
	public boolean isOnlyValidateInSubmitMode() {
		return onlyValidateInSubmitMode;
	}

	/**
	 * Gets the tie command alias list.
	 *
	 * @return the tie command alias list
	 */
	public List<TieCommandAlias> getTieCommandAliasList() {
		return tieCommandAliasList;
	}
	
	

	/**
	 * Sets the tie command alias list.
	 *
	 * @param tieCommandAliasList the new tie command alias list
	 */
	public void setTieCommandAliasList(List<TieCommandAlias> tieCommandAliasList) {
		this.tieCommandAliasList = tieCommandAliasList;
	}

	
	/**
	 * Sets the tie command alias list.
	 *
	 * @param aliasListJson the new tie command alias list
	 */
	public void setTieCommandAliasList(String aliasListJson) {
		
		Gson gson = new Gson();

		Type aliasListType = new TypeToken<ArrayList<TieCommandAlias>>(){}.getType();

		this.tieCommandAliasList = gson.fromJson(aliasListJson, aliasListType); 		
	}
	
	
}
