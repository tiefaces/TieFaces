package com.tiefaces.components.websheet.configuration;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;

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
	
	public boolean allowAdd = false;
	
	public RowsMapping unitRowsMapping = new RowsMapping();

	public ConfigRangeAttrs(boolean lastCellCreated) {
		this.lastCellCreated = lastCellCreated;
	}
}