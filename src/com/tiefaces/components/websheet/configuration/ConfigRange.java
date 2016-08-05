package com.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

import com.tiefaces.components.websheet.service.CellHelper;
import com.tiefaces.components.websheet.service.ShiftFormula;

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
public class ConfigRange {

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
	 * @param alsoCreateAddr
	 *            whether need create cell address.
	 */
	public final void setFirstRowRef(final Cell pFirstRowRef,
			final boolean alsoCreateAddr) {
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
	 * @param alsoSetAddr
	 *            whether need create cell address.
	 */
	public final void setLastRowPlusRef(final Sheet sheet,
			final int rightCol, final int lastRow,
			final boolean alsoSetAddr) {

		if ((lastRow >= 0) && (sheet != null) && (rightCol >= 0)) {
			Row row = sheet.getRow(lastRow + 1);
			if (row == null) {
				row = sheet.createRow(lastRow + 1);
			}
			Cell cell = row.getCell(rightCol);
			if (cell == null) {
				cell = row.getCell(rightCol, Row.CREATE_NULL_AS_BLANK);
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

	public final boolean isLastCellCreated() {
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
	public final int buildAt(final XSSFEvaluationWorkbook wbWrapper,
			final Sheet sheet, final int atRow,
			final Map<String, Object> context,
			final List<Integer> watchList,
			final List<RowsMapping> currentRowsMappingList,
			final List<RowsMapping> allRowsMappingList,
			final ExpressionEngine engine, final CellHelper cellHelper) {
		log.fine("build xls sheet at row : " + atRow);

		// List<Row> staticRows = setUpBuildRows(sheet,
		// this.getFirstRowRef().getRowIndex(),
		// this.getLastRowPlusRef().getRowIndex(), this.getCommandList());

		for (int i = 0; i < commandList.size(); i++) {
			// cellRange.resetChangeMatrix();
			Command command = commandList.get(i);
			command.setFinalLength(0);
			int populatedLength = command.buildAt(wbWrapper, sheet,
					command.getConfigRange().getFirstRowRef()
							.getRowIndex(), context, watchList,
					currentRowsMappingList, allRowsMappingList, engine,
					cellHelper);
			currentRowsMappingList.clear();
			currentRowsMappingList.addAll(allRowsMappingList);
			command.setFinalLength(populatedLength);
		}

		buildCells(sheet, atRow, context, wbWrapper, watchList,
				currentRowsMappingList, engine, cellHelper);

		int finalLength = this.getLastRowPlusRef().getRowIndex()
				- this.getFirstRowRef().getRowIndex() - 1;

		return finalLength;

	}

	/**
	 * Whether the row is static.
	 * @param row the row for check.
	 * @return true is static false is not.
	 */
	public boolean isStaticRow(Row row) {
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

	/**
	 * Build all the static cells in the range (exclude command areas).
	 * @param sheet sheet.
	 * @param atRow start row.
	 * @param context context.
	 * @param wbWrapper workbook wrapper.
	 * @param watchList watch list.
	 * @param currentRowsMappingList current rows mapping.
	 * @param engine engine.
	 * @param cellHelper cell helper.
	 */
	private void buildCells(final Sheet sheet, final int atRow,
			final Map<String, Object> context,
			final XSSFEvaluationWorkbook wbWrapper, final List<Integer> watchList,
			final List<RowsMapping> currentRowsMappingList,
			final ExpressionEngine engine, final CellHelper cellHelper) {
		
		if ((context==null) || (context.size()==0)) {
			// no need to evaluate as there's no data object.
			return ;
		}
		int lastRowPlus = this.getLastRowPlusRef().getRowIndex();
		for (int i = atRow; i < lastRowPlus; i++) {
			Row row = sheet.getRow(i);
			if ((row != null) && isStaticRow(row)) {
				for (Cell cell : row) {
					ExpressionHelper.evaluate(context, cell, engine,
							cellHelper);
					if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
						// rebuild formula if necessary for dynamic row
						buildCellFormulaForShiftedRows(sheet,
								wbWrapper, watchList,
								currentRowsMappingList, cell);

					}
				}
			}
		}
	}

	private void buildCellFormulaForShiftedRows(final Sheet sheet,
			final XSSFEvaluationWorkbook wbWrapper,
			final List<Integer> watchList,
			final List<RowsMapping> currentRowsMappingList, Cell cell) {
		// only shift when there's watchlist exist.
		if ((watchList!=null)&&(watchList.size()>0)) {
			Ptg[] ptgs = FormulaParser.parse(cell
					.getCellFormula(), wbWrapper,
					FormulaType.CELL, sheet.getWorkbook()
							.getSheetIndex(sheet));
			Boolean formulaChanged = false;
			Ptg[] convertedFormulaPtg = ShiftFormula
					.convertSharedFormulas(ptgs, watchList,
							currentRowsMappingList, 
							formulaChanged);
			if (formulaChanged) {
				// only change formula when indicator is true
				cell.setCellFormula(FormulaRenderer
						.toFormulaString(wbWrapper,
								convertedFormulaPtg));
			}
		}	
	}


}
