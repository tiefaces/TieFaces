package com.tiefaces.components.common.lookup;

import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.context.RequestContext;

import com.tiefaces.common.ColumnModel;

public abstract class TieTableLookupBean {

		private List<ColumnModel> columns;
		private List<Object> results;
		private Object searchItem;

		public TieTableLookupBean() {
			super();
		}

		@PostConstruct
		public void postinit() {
			init();
		}
		
		public void refresh() {
			System.out.println(" called refresh ");			
			return;
		}
		protected abstract void init();

		public abstract List<Object> search();
		
		public void doSearch() {
System.out.println(" into dosearch ");			
			List<Object> result = search();
System.out.println(" result = "+result);			
			if (result!=null) {
				this.setResults(result);
				//RequestContext.getCurrentInstance().execute("PF('tieRefreshTblAfter').jq.click();");
			}	
		}

		public List<ColumnModel> getColumns() {
			return columns;
		}

		public void setColumns(List<ColumnModel> columns) {
			this.columns = columns;
		}

		public List<Object> getResults() {
			return results;
		}

		public void setResults(List<Object> results) {
			this.results = results;
		}

		public Object getSearchItem() {
			return searchItem;
		}

		public void setSearchItem(Object searchItem) {
			this.searchItem = searchItem;
		}
		
		public void selectSearchResult(Object result) {
			// Only used for dialog lookup
		}
		

	}
