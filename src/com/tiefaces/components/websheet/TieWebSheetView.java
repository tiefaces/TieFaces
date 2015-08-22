/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tiefaces.components.websheet;

import java.io.Serializable;
import java.util.List;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.tabview.TabView;


public class TieWebSheetView  {
	
	

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
            if (this.id!=null) this.id = this.id.replaceAll(" ","_");
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
	
    protected TabView webFormTabView=null;   
	public TabView getWebFormTabView() {
		return webFormTabView;
	}

	public void setWebFormTabView(TabView webFormTabView) {
		this.webFormTabView = webFormTabView;
	}

	protected String tabType;
	public String getTabType() {
		
    	int  sheetId = webFormTabView.getActiveIndex();
    	if ((sheetId >= 0)&&(tabs!=null))
    	{	
    		if (sheetId >= tabs.size()) sheetId = 0;
    		tabType = tabs.get(sheetId).type.toLowerCase();
    	}	
    	else
    		tabType = "none";
		return tabType;
	}
		
	protected String tabStyle;
	public String getTabStyle() {

		tabStyle = "height: 530px;";
    	int  sheetId = webFormTabView.getActiveIndex();
    	if (sheetId >= 0)
    	{	
    		if (sheetId < tabs.size()) tabStyle = "height: 30px;";
    		//if (tabs.get(sheetId).type.equalsIgnoreCase("form")) tabStyle = "height: 30px;";
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

	private String tableWidthStyle="100%;";
	public String getTableWidthStyle() {
		return tableWidthStyle;
	}

	public void setTableWidthStyle(String tableWidthStyle) {
		this.tableWidthStyle = tableWidthStyle;
	}

	private boolean bodyAllowAddRows;

	public boolean isBodyAllowAddRows() {
		return bodyAllowAddRows;
	}

	public void setBodyAllowAddRows(boolean bodyAllowAddRows) {
		this.bodyAllowAddRows = bodyAllowAddRows;
	}  


	
}
