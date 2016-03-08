package com.tiefaces.components.websheet.dataobjects;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.tiefaces.components.websheet.TieWebSheetBean;
import com.tiefaces.components.websheet.utility.TieWebSheetUtility;

public class CachedCells {
	
	private Map<String, String> cachedMap= new HashMap<String, String>();

	private TieWebSheetBean parent = null;

	/**
	 * 
	 */

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("DEBUG: " + msg);
		}
	}

	public CachedCells(TieWebSheetBean parent) {
		this.parent = parent;
		debug("CachedCells Constructor");
	}
	
	public  void put(Sheet sheet1, Cell cell, Integer cellType) {
		Map<String, String> map = cachedMap;
		// if cellType not null then only specified Type will be put into Cache
		// e.g. only formula cell will be cached then pass in Cell.CELL_TYPE_FORMULA
		if ((cell!=null) && ( (cellType == null) || (cell.getCellType() == cellType) )) {
			String refName = TieWebSheetUtility.getFullCellRefName(sheet1, cell);
			if (refName != null) {
				String value = parent.getCellHelper().getCellValueWithFormat(cell);
				map.put(refName, value );
				debug("put cache key= "+refName+" value = "+value);
			}
		}
	}	
	public  String get(Sheet sheet1, Cell cell) {
		Map<String, String> map = cachedMap;
		String refName = TieWebSheetUtility.getFullCellRefName(sheet1, cell);
		return map.get(refName);
	}	
	
	public void clear() {
		cachedMap.clear();
		debug("cache cleared");
	}
	
	public boolean isValueChanged(Sheet sheet1, Cell cell) {
			String refName = TieWebSheetUtility.getFullCellRefName(sheet1, cell);
			Map<String, String> map = cachedMap;
			String oldValue = map.get(refName);
			String newValue = parent.getCellHelper().getCellValueWithFormat(cell);
			if (oldValue == null) oldValue="";
			if (newValue == null) newValue="";
			if ( !oldValue.equals(newValue)) {
				return true;
			} 
			return false;
		}
}
