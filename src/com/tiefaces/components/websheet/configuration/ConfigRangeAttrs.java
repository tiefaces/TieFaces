package com.tiefaces.components.websheet.configuration;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;

import com.tiefaces.components.websheet.dataobjects.MapSnapShot;

public class ConfigRangeAttrs {
	/** first cell. */
	public Cell firstRowRef;
	/** last cell. */
	public Cell lastRowPlusRef;
	/** first cell address. This used to calculate relative address. */
	public CellAddress firstRowAddr;
	/** last cell address. This used to calculate relative address. */
	public CellAddress lastRowPlusAddr;
	/** if true then the lastCell is created instead of exist cell. */
	public boolean lastCellCreated;
	
	public int finalLength = 0;
	
	public boolean allowAdd = false;
	
	public RowsMapping unitRowsMapping = null;
	
	public MapSnapShot contextSnap = null;

	public ConfigRangeAttrs(boolean lastCellCreated) {
		this.lastCellCreated = lastCellCreated;
	}
}