package com.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

public class RowsMapping  {
//implements Cloneable
	private Map<Integer, List<Row>> rowsMap = new HashMap<Integer, List<Row>>();

	
	
   public RowsMapping() {
		super();
		// TODO Auto-generated constructor stub
	}

   /**
     * Copy constructor
     */
    public  RowsMapping( RowsMapping source) {
    	this.rowsMap.putAll(source.getRowsMap());
    }		
	
	public Map<Integer, List<Row>> getRowsMap() {
		return rowsMap;
	}

	public void addRow(Integer sourceRowNum, Row targetRow) {
		List<Row> mapRowList = rowsMap.get(sourceRowNum);
		if (mapRowList == null) {
			mapRowList = new ArrayList<Row>();
		}
		if (!mapRowList.contains(targetRow)) {
			mapRowList.add(targetRow);
			rowsMap.put(sourceRowNum, mapRowList);
		}	
	}
	
	public void removeRow(Integer sourceRowNum, Row targetRow) {
		List<Row> mapRowList = rowsMap.get(sourceRowNum);
		if (mapRowList != null) {
			mapRowList.remove(targetRow);
			rowsMap.put(sourceRowNum, mapRowList);
		}
	}
	
	public List<Row> get(Integer sourceRowNum) {
		List<Row> mapRowList = rowsMap.get(sourceRowNum);
		return mapRowList;
	}
	
	
	/*
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			return null;
		}
	}
	*/
	public void mergeMap(RowsMapping addMap) {
		Map<Integer, List<Row>>  map = addMap.getRowsMap();
		for (Map.Entry<Integer, List<Row>> entry : map.entrySet()) {
			List<Row> entryRowList = entry.getValue();
			if ((entryRowList != null) && (entryRowList.size()>0)) {
				for (Row row: entryRowList) {
					this.addRow(entry.getKey(), row);
				}
			}
		}		
	}
}
