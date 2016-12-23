package com.tiefaces.components.websheet.dataobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.tiefaces.components.websheet.TieWebSheetBean;
/**
 * Use to cache formula cells.
 * The purpose is to compare current cell with cached one.
 * If they are different, then mean need to refresh them.
 * All cached cells are in current display sheet.
 * @author Jason Jiang
 *
 */
public class CachedCells {
	
	private Map<Cell, FormulaMapping> cachedMap= new HashMap<Cell, FormulaMapping>();

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
	
	public  void put(Cell cell, Integer cellType) {
		Map<Cell, FormulaMapping> map = cachedMap;
		// if cellType not null then only specified Type will be put into Cache
		// e.g. only formula cell will be cached then pass in Cell.CELL_TYPE_FORMULA
		if ((cell!=null) && ( (cellType == null) || (cell.getCellType() == cellType) )) {
			//String refName = TieWebSheetUtility.getFullCellRefName(sheet1, cell);
			String value = parent.getCellHelper().getCellValueWithFormat(cell);
			FormulaMapping f = map.get(cell);
			if (f == null) {
				f = new FormulaMapping();
			}
			f.setValue(value);
			map.put(cell, f );
			debug("put cache key= "+cell.getAddress()+" value = "+value);
		}
	}	
	public  void put(Cell cell, String originFormula) {
		Map<Cell, FormulaMapping> map = cachedMap;
		// if cellType not null then only specified Type will be put into Cache
		// e.g. only formula cell will be cached then pass in Cell.CELL_TYPE_FORMULA
		if ((cell!=null) && ( originFormula != null)) {
			FormulaMapping f = map.get(cell);
			if (f == null) {
				f = new FormulaMapping();
			}
			f.setOriginFormula(originFormula);
			String value = parent.getCellHelper().getCellValueWithFormat(cell);
			f.setValue(value);
			map.put(cell, f );
			debug("put cache key= "+cell.getAddress()+" origin formula = "+originFormula+" value = "+value);
		}
	}	
	public  String getValue(Cell cell) {
		return cachedMap.get(cell).getValue();
	}	
	
	public void clear() {
		cachedMap.clear();
		debug("cache cleared");
	}
	
	public boolean isValueChanged(Sheet sheet1, Cell cell) {
		String newValue = parent.getCellHelper().getCellValueWithFormat(cell);
		return isValueChanged(sheet1, cell, newValue);
		}

	public boolean isValueChanged(Sheet sheet1, Cell cell, String newValue) {
		Map<Cell, FormulaMapping> map = cachedMap;
		String oldValue = map.get(cell).getValue();
		if (oldValue == null) oldValue="";
		if (newValue == null) newValue="";
		if ( !oldValue.equals(newValue)) {
			return true;
		} 
		return false;
	}	
	
	public Map<Cell, FormulaMapping> getCachedMap() {
		return cachedMap;
	}
	
	
	
}
