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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

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

	private ConfigRangeAttrs attrs = new ConfigRangeAttrs(false);

	/** command list. */
	private List<ConfigCommand> commandList;

	public ConfigRange() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor
	 */
	public ConfigRange(ConfigRange source) {
		this.attrs.firstRowRef = source.attrs.firstRowRef;
		this.attrs.firstRowAddr = source.attrs.firstRowAddr;
		this.attrs.lastRowPlusRef = source.attrs.lastRowPlusRef;
		this.attrs.lastRowPlusAddr = source.attrs.lastRowPlusAddr;
		this.attrs.lastCellCreated = source.attrs.lastCellCreated;

		if (source.commandList != null) {
			this.commandList = this.getCommandList();
			for (ConfigCommand sourceCommand : source.commandList) {
				Object newCommand = null;
				if (sourceCommand instanceof FormCommand) {
					newCommand = new FormCommand(
							(FormCommand) sourceCommand);
				} else {
					newCommand = new EachCommand(
							(EachCommand) sourceCommand);
				}
				this.commandList.add((ConfigCommand) newCommand);
			}
		}

	}

	public ConfigRangeAttrs getAttrs() {
		return attrs;
	}

	public void shiftRowRef(Sheet sheet, int shiftnum) {
		try {
			this.setFirstRowRef(
					sheet.getRow(attrs.firstRowAddr.getRow() + shiftnum)
							.getCell(
									attrs.firstRowAddr.getColumn(),
									MissingCellPolicy.CREATE_NULL_AS_BLANK),
					false);
			this.setLastRowPlusRef(sheet,
					attrs.lastRowPlusAddr.getColumn(),
					attrs.lastRowPlusAddr.getRow() + shiftnum - 1, false);

			if (commandList != null) {
				for (ConfigCommand command : commandList) {
					command.shiftRowRef(sheet, shiftnum);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.severe("shiftRowRef error =" + ex.getLocalizedMessage());
		}
	}

	public final Cell getFirstRowRef() {
		return attrs.firstRowRef;
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
		this.attrs.firstRowRef = pFirstRowRef;
		if (alsoCreateAddr) {
			this.setFirstRowAddr(new CellAddress(pFirstRowRef));
		}
	}

	public final Cell getLastRowPlusRef() {
		return attrs.lastRowPlusRef;
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
				cell = row.getCell(rightCol,
						MissingCellPolicy.CREATE_NULL_AS_BLANK);
				this.attrs.lastCellCreated = true;
			} else {
				this.attrs.lastCellCreated = false;
			}
			this.attrs.lastRowPlusRef = cell;
			if (alsoSetAddr) {
				this.setLastRowPlusAddr(new CellAddress(cell));
			}
		} else {
			this.attrs.lastRowPlusRef = null;
			if (alsoSetAddr) {
				this.attrs.lastRowPlusAddr = null;
			}
		}
	}

	public final CellAddress getFirstRowAddr() {
		return attrs.firstRowAddr;
	}

	public final void setFirstRowAddr(final CellAddress pFirstRowAddr) {
		this.attrs.firstRowAddr = pFirstRowAddr;
	}

	public final CellAddress getLastRowPlusAddr() {
		return attrs.lastRowPlusAddr;
	}

	public final void setLastRowPlusAddr(
			final CellAddress pLastRowPlusAddr) {
		this.attrs.lastRowPlusAddr = pLastRowPlusAddr;
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
		return attrs.lastCellCreated;
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
	public final int buildAt(String fullName,
			final ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			final List<RowsMapping> currentRowsMappingList) {
		log.fine("build xls sheet at row : " + atRow);

		// List<Row> staticRows = setUpBuildRows(sheet,
		// this.getFirstRowRef().getRowIndex(),
		// this.getLastRowPlusRef().getRowIndex(), this.getCommandList());
		if (commandList != null) {
			for (int i = 0; i < commandList.size(); i++) {
				// cellRange.resetChangeMatrix();
				Command command = commandList.get(i);
				command.setFinalLength(0);
				int populatedLength = command.buildAt(fullName,
						configBuildRef, command.getConfigRange()
								.getFirstRowRef().getRowIndex(), context,
						currentRowsMappingList);
				command.setFinalLength(populatedLength);
			}
		}

		buildCells(fullName, configBuildRef, atRow, context,
				currentRowsMappingList);

		int finalLength = this.getLastRowPlusRef().getRowIndex()
				- this.getFirstRowRef().getRowIndex();

		return finalLength;

	}

	public void indexCommandRange(Map<String, Command> indexMap) {
		ConfigurationHelper.indexCommandRange(this, indexMap);
	}

	/**
	 * Build all the static cells in the range (exclude command areas).
	 * 
	 * @param sheet
	 *            sheet.
	 * @param atRow
	 *            start row.
	 * @param context
	 *            context.
	 * @param wbWrapper
	 *            workbook wrapper.
	 * @param watchList
	 *            watch list.
	 * @param currentRowsMappingList
	 *            current rows mapping.
	 * @param engine
	 *            engine.
	 * @param cellHelper
	 *            cell helper.
	 */
	private void buildCells(String fullName,
			ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			final List<RowsMapping> currentRowsMappingList) {

		if ((context == null) || (context.size() == 0)) {
			// no need to evaluate as there's no data object.
			return;
		}
		int lastRowPlus = this.getLastRowPlusRef().getRowIndex();
		ShiftFormulaRef shiftFormulaRef = new ShiftFormulaRef(
				configBuildRef.getWatchList(), currentRowsMappingList);
		for (int i = atRow; i < lastRowPlus; i++) {
			Row row = configBuildRef.getSheet().getRow(i);
			if ((row != null)
					&& ConfigurationHelper.isStaticRowRef(this, row)) {
				for (Cell cell : row) {
					try {
						ConfigurationHelper.evaluate(context, cell,
								configBuildRef.getEngine(),
								configBuildRef.getCellHelper());
						if (cell.getCellTypeEnum() == CellType.FORMULA) {
							// rebuild formula if necessary for dynamic row
							String originFormula = cell.getCellFormula();
							shiftFormulaRef.setFormulaChanged(0);
							ConfigurationHelper
									.buildCellFormulaForShiftedRows(
											configBuildRef.getSheet(),
											configBuildRef.getWbWrapper(),
											shiftFormulaRef, cell,
											cell.getCellFormula());
							if (shiftFormulaRef.getFormulaChanged() > 0) {
								configBuildRef.getCachedCells().put(cell,
										originFormula);
							}
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						log.severe("build cell ( row = "
								+ cell.getRowIndex() + " column = "
								+ cell.getColumnIndex() + " error = "
								+ ex.getLocalizedMessage());
					}
				}
				ConfigurationHelper.setFullNameInHiddenColumn(row,
						fullName, false);
			}
		}
	}

}
