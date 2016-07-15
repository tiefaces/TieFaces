package com.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jxls.area.CommandData;
import org.jxls.common.AreaRef;
import org.jxls.common.CellRef;
import org.jxls.common.Size;
import org.jxls.common.cellshift.CellShiftStrategy;

import com.tiefaces.components.websheet.service.CellHelper;

/**
 * Simple class for area range. Also include nested command list. Note: command
 * also can include configRange.
 * 
 * firstCell, lastCell point to the start and end point of the area. The
 * lastCell will be expanded if area contain dynamic area.
 * 
 * firstCellAddr, lastCellAddr is static address for initial area and will not
 * be changed. These mainly used for calculate related address from parent
 * command to child command area.
 * 
 * @author Jason Jiang
 *
 */
public class ConfigRange implements Cloneable {

	/** logger. */
	private final Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());

	/** first cell. */
	private Cell firstRowRef;
	/** last cell. */
	private Cell lastRowPlusRef;
	/** first cell address. This used to calculate relative address. */
	private CellAddress firstRowAddr;
	/** last cell address. This used to calculate relative address. */
	private CellAddress lastRowPlusAddr;
	/** if true then the lastCell is created instead of exist cell. */
	private boolean lastCellCreated = false;

	/** command list. */
	private List<ConfigCommand> commandList;

	public final Cell getFirstRowRef() {
		return firstRowRef;
	}

	/**
	 * set first cell also set static relative address firstCellAddress.
	 * 
	 * @param pFirstRowRef
	 *            first cell.
	 */
	public final void setFirstRowRef(final Cell pFirstRowRef, final boolean alsoCreateAddr) {
		this.firstRowRef = pFirstRowRef;
		if (alsoCreateAddr) {
			this.setFirstRowAddr(new CellAddress(pFirstRowRef));
		}
	}

	public final Cell getLastRowPlusRef() {
		return lastRowPlusRef;
	}

	/**
	 * set last cell also set static relative address lastCellAddress.
	 * 
	 * @param sheet
	 *            sheet.
	 * @param rightCol
	 *            rightColumn.
	 * @param lastRow
	 *            lastRow.
	 */
	public final void setLastRowPlusRef(Sheet sheet, int rightCol,
			int lastRow, boolean alsoSetAddr) {

		if ((lastRow >= 0) && (sheet != null) && (rightCol >= 0)) {
			Row row = sheet.getRow(lastRow + 1);
			if (row == null) {
				row = sheet.createRow(lastRow + 1);
			}
			Cell cell = row.getCell(rightCol);
			if (cell == null) {
				cell = row.getCell(rightCol, Row.RETURN_BLANK_AS_NULL);
				this.lastCellCreated = true;
			} else {
				this.lastCellCreated = false;
			}
			this.lastRowPlusRef = cell;
			if (alsoSetAddr) {
				this.setLastRowPlusAddr(new CellAddress(cell));
			}
		} else {
			this.lastRowPlusRef = null;
			if (alsoSetAddr) {
				this.lastRowPlusAddr = null;
			}	
		}
	}

	public final CellAddress getFirstRowAddr() {
		return firstRowAddr;
	}

	public final void setFirstRowAddr(final CellAddress pFirstRowAddr) {
		this.firstRowAddr = pFirstRowAddr;
	}

	public final CellAddress getLastRowPlusAddr() {
		return lastRowPlusAddr;
	}

	public final void setLastRowPlusAddr(
			final CellAddress pLastRowPlusAddr) {
		this.lastRowPlusAddr = pLastRowPlusAddr;
	}

	/**
	 * command list.
	 * 
	 * @return command list.
	 */
	public final List<ConfigCommand> getCommandList() {
		if (this.commandList == null) {
			this.commandList = new ArrayList<ConfigCommand>();
		}
		return this.commandList;
	}

	public final void setCommandList(
			final List<ConfigCommand> pCommandList) {
		this.commandList = pCommandList;
	}

	public boolean isLastCellCreated() {
		return lastCellCreated;
	}

	/**
	 * add command to the list.
	 * 
	 * @param configCommand
	 *            .
	 * @return command list.
	 */
	public final List<ConfigCommand> addCommand(
			final ConfigCommand configCommand) {
		this.getCommandList().add(configCommand);
		return this.getCommandList();
	}

	/**
	 * build the config range at specified point (start row). context include
	 * the data objects for evaluation. build sequence is inside-out. e.g. first
	 * build all the command area the range included. each command will hold the
	 * final length after data populated. then buildCells build all cells in the
	 * range except those commands. and update the formulas.
	 * 
	 * @param sheet
	 *            sheet.
	 * @param startRow
	 *            start row.
	 * @param context
	 *            context map.
	 * @return final length.
	 */
	public final int buildAt(final Sheet sheet, final int atRow,
			final Map<String, Object> context, final ExpressionEngine engine, final CellHelper cellHelper) {
		log.fine("build xls sheet at row : " + atRow);
		
		//List<Row> staticRows = setUpBuildRows(sheet, this.getFirstRowRef().getRowIndex(), this.getLastRowPlusRef().getRowIndex(), this.getCommandList());

		
		for (int i = 0; i < commandList.size(); i++) {
			// cellRange.resetChangeMatrix();
			Command command = commandList.get(i);
			command.setFinalLength(0);
			int populatedLength = command.buildAt(sheet, command.getConfigRange().getFirstRowRef().getRowIndex(),
					context, engine, cellHelper);
			command.setFinalLength(populatedLength);
		}

		buildCells(sheet, atRow, context, engine, cellHelper);
		
		//updateCellDataFinalAreaForFormulaCells(newAreaRef);

		int finalLength = this.getLastRowPlusRef().getRowIndex()
				- this.getFirstRowRef().getRowIndex() - 1;

		return finalLength;

	}

	/*
	private List<Row> setUpBuildRows(final Sheet sheet, final int startRow, final int endRow, final List<ConfigCommand> commandList) {
		List<Row> staticRows = new ArrayList<Row>();
		int endStaticRow = endRow + 1;
		boolean emptyCommand = false;
		if ((commandList != null)&&(commandList.size()>0)&&(commandList.get(0).getTopRow()>=0)) {
			endStaticRow = commandList.get(0).getTopRow();
		} else {
			emptyCommand = true;
		}
		for (int i= startRow; i< endStaticRow; i++ ) {
			Row row = sheet.getRow(i);
			if (row != null) {
				staticRows.add(row);
			}
		}
		if (!emptyCommand) {
			for (int i=endStaticRow; i<= endRow; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					if (isStaticRow(row.getRowNum(),commandList)) {
						staticRows.add(row);
					}
				}
				
			}
		}
		return staticRows;
	}
		
	private boolean  isStaticRow(int rowIndex, final List<ConfigCommand> commandList) {
		for (int i = 0; i < commandList.size(); i++) {
			Command command = commandList.get(i);
			if ((rowIndex >= command.getTopRow())
					&& (rowIndex <= (command.getTopRow() + command
							.getFinalLength()))) {
				return false;
			}
		}
		return true;
	}

	*/
	private boolean rowNotProcessed(Row row) {
		for (int i = 0; i < commandList.size(); i++) {
			Command command = commandList.get(i);
			int rowIndex = row.getRowNum();
			if ((rowIndex >= command.getTopRow())
					&& (rowIndex <= (command.getTopRow() + command
							.getFinalLength()))) {
				return false;
			}
		}
		return true;
	}

	private void buildCells(Sheet sheet, int startRow,
			Map<String, Object> context, final ExpressionEngine engine, final CellHelper cellHelper) {
		int lastRowPlus = this.getLastRowPlusRef().getRowIndex();
		for (int i = startRow; i < lastRowPlus; i++) {
			Row row = sheet.getRow(i);
			if ((row != null) && rowNotProcessed(row)) {
				for (Cell cell : row) {
	                ExpressionHelper.evaluate(context, cell, engine, cellHelper);
	            }				
			}
		}
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
	
}
