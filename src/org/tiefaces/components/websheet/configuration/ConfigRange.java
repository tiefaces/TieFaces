/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.tiefaces.components.websheet.serializable.SerialCellAddress;
import org.tiefaces.components.websheet.utility.CommandUtility;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;

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
public class ConfigRange implements Serializable {

	/**
	 * serialid.
	 */
	private static final long serialVersionUID = 1L;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(ConfigRange.class.getName());

	/** The attrs. */
	private ConfigRangeAttrs attrs = new ConfigRangeAttrs(false);

	/** command list. */
	private List<ConfigCommand> commandList;

	/**
	 * Instantiates a new config range.
	 */
	public ConfigRange() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param source
	 *            the source
	 */
	public ConfigRange(final ConfigRange source) {
		this.attrs.setFirstRowRef(source.attrs.getFirstRowRef());
		this.attrs.setFirstRowAddr(source.attrs.getFirstRowAddr());
		this.attrs.setLastRowPlusRef(source.attrs.getLastRowPlusRef());
		this.attrs.setLastRowPlusAddr(source.attrs.getLastRowPlusAddr());
		this.attrs.setLastCellCreated(source.attrs.isLastCellCreated());

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

	/**
	 * Gets the attrs.
	 *
	 * @return the attrs
	 */
	public final ConfigRangeAttrs getAttrs() {
		return attrs;
	}

	/**
	 * Shift row ref.
	 *
	 * @param sheet
	 *            the sheet
	 * @param shiftnum
	 *            the shiftnum
	 */
	public final void shiftRowRef(final Sheet sheet, final int shiftnum) {
		try {
			this.setFirstRowRef(sheet
					.getRow(attrs.getFirstRowAddr().getRow() + shiftnum)
					.getCell(attrs.getFirstRowAddr().getColumn(),
							MissingCellPolicy.CREATE_NULL_AS_BLANK),
					false);
			this.setLastRowPlusRef(sheet,
					attrs.getLastRowPlusAddr().getColumn(),
					attrs.getLastRowPlusAddr().getRow() + shiftnum - 1,
					false);

			if (commandList != null) {
				for (ConfigCommand command : commandList) {
					command.shiftRowRef(sheet, shiftnum);
				}
			}

		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"shiftRowRef error =" + ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Gets the first row ref.
	 *
	 * @return the first row ref
	 */
	public final Cell getFirstRowRef() {
		return attrs.getFirstRowRef();
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
		this.attrs.setFirstRowRef(pFirstRowRef);
		if (alsoCreateAddr) {
			this.setFirstRowAddr(new SerialCellAddress(pFirstRowRef));
		}
	}

	/**
	 * Gets the last row plus ref.
	 *
	 * @return the last row plus ref
	 */
	public final Cell getLastRowPlusRef() {
		return attrs.getLastRowPlusRef();
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
				this.attrs.setLastCellCreated(true);
			} else {
				this.attrs.setLastCellCreated(false);
			}
			this.attrs.setLastRowPlusRef(cell);
			if (alsoSetAddr) {
				this.setLastRowPlusAddr(new SerialCellAddress(cell));
			}
		} else {
			this.attrs.setLastRowPlusRef(null);
			if (alsoSetAddr) {
				this.attrs.setLastRowPlusAddr(null);
			}
		}
	}

	/**
	 * Gets the first row addr.
	 *
	 * @return the first row addr
	 */
	public final SerialCellAddress getFirstRowAddr() {
		return attrs.getFirstRowAddr();
	}

	/**
	 * Sets the first row addr.
	 *
	 * @param pFirstRowAddr
	 *            the new first row addr
	 */
	public final void setFirstRowAddr(
			final SerialCellAddress pFirstRowAddr) {
		this.attrs.setFirstRowAddr(pFirstRowAddr);
	}

	/**
	 * Gets the last row plus addr.
	 *
	 * @return the last row plus addr
	 */
	public final SerialCellAddress getLastRowPlusAddr() {
		return attrs.getLastRowPlusAddr();
	}

	/**
	 * Sets the last row plus addr.
	 *
	 * @param pLastRowPlusAddr
	 *            the new last row plus addr
	 */
	public final void setLastRowPlusAddr(
			final SerialCellAddress pLastRowPlusAddr) {
		this.attrs.setLastRowPlusAddr(pLastRowPlusAddr);
	}

	/**
	 * command list.
	 * 
	 * @return command list.
	 */
	public final List<ConfigCommand> getCommandList() {
		if (this.commandList == null) {
			this.commandList = new ArrayList<>();
		}
		return this.commandList;
	}

	/**
	 * Sets the command list.
	 *
	 * @param pCommandList
	 *            the new command list
	 */
	public final void setCommandList(
			final List<ConfigCommand> pCommandList) {
		this.commandList = pCommandList;
	}

	/**
	 * Checks if is last cell created.
	 *
	 * @return true, if is last cell created
	 */
	public final boolean isLastCellCreated() {
		return attrs.isLastCellCreated();
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
	 * @param fullName
	 *            the full name
	 * @param configBuildRef
	 *            the config build ref
	 * @param atRow
	 *            the at row
	 * @param context
	 *            context map.
	 * @param currentRowsMappingList
	 *            the current rows mapping list
	 * @return final length.
	 */
	public final int buildAt(final String fullName,
			final ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			final List<RowsMapping> currentRowsMappingList) {
		LOG.fine("build xls sheet at row : " + atRow);

		if (commandList != null) {
			for (int i = 0; i < commandList.size(); i++) {
				Command command = commandList.get(i);
				command.setFinalLength(0);
				int populatedLength = command.buildAt(fullName,
						configBuildRef,
						command.getConfigRange().getFirstRowRef()
								.getRowIndex(),
						context, currentRowsMappingList);
				command.setFinalLength(populatedLength);
			}
		}

		buildCells(fullName, configBuildRef, atRow, context,
				currentRowsMappingList);

		return this.getLastRowPlusRef().getRowIndex()
				- this.getFirstRowRef().getRowIndex();

	}

	/**
	 * Index command range.
	 *
	 * @param indexMap
	 *            the index map
	 */
	public final void indexCommandRange(
			final Map<String, Command> indexMap) {
		CommandUtility.indexCommandRange(this, indexMap);
	}

	/**
	 * Build all the static cells in the range (exclude command areas).
	 *
	 * @param fullName
	 *            the full name
	 * @param configBuildRef
	 *            the config build ref
	 * @param atRow
	 *            start row.
	 * @param context
	 *            context.
	 * @param rowsMappingList
	 *            the rows mapping list
	 */
	private void buildCells(final String fullName,
			final ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			final List<RowsMapping> rowsMappingList) {

		if ((context == null) || context.isEmpty()) {
			// no need to evaluate as there's no data object.
			return;
		}

		// keep rowsMappingList as current as no change
		// allRowsMappingList = child + current

		List<RowsMapping> allRowsMappingList = ConfigurationUtility
				.findChildRowsMappingFromShiftMap(fullName,
						configBuildRef.getShiftMap());
		allRowsMappingList.addAll(rowsMappingList);

		int lastRowPlus = this.getLastRowPlusRef().getRowIndex();
		ShiftFormulaRef shiftFormulaRef = new ShiftFormulaRef(
				configBuildRef.getWatchList(), allRowsMappingList);
		for (int i = atRow; i < lastRowPlus; i++) {
			buildCellsForRow(configBuildRef.getSheet().getRow(i), fullName, context, configBuildRef,
					shiftFormulaRef);
		}
	}

	/**
	 * @param row
	 * @param fullName
	 * @param context
	 * @param configBuildRef
	 * @param shiftFormulaRef
	 */
	private void buildCellsForRow(Row row, final String fullName,
			final Map<String, Object> context,
			final ConfigBuildRef configBuildRef,
			ShiftFormulaRef shiftFormulaRef) {
		if ((row == null) || !ConfigurationUtility.isStaticRowRef(this, row)) {
			return;
		}	
		for (Cell cell : row) {
			buildSingleCell(cell, context, configBuildRef, shiftFormulaRef);
		}
		ConfigurationUtility.setFullNameInHiddenColumn(row,
				fullName);
	}

	/**
	 * @param cell
	 * @param context
	 * @param configBuildRef
	 * @param shiftFormulaRef
	 */
	private void buildSingleCell(Cell cell,
			final Map<String, Object> context,
			final ConfigBuildRef configBuildRef,
			ShiftFormulaRef shiftFormulaRef) {
		try {
			CommandUtility.evaluate(context, cell,
					configBuildRef.getEngine());
			if (cell.getCellTypeEnum() == CellType.FORMULA) {
				// rebuild formula if necessary for dynamic row
				String originFormula = cell.getCellFormula();
				shiftFormulaRef.setFormulaChanged(0);
				ConfigurationUtility
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
			LOG.log(Level.SEVERE, "build cell ( row = "
					+ cell.getRowIndex() + " column = "
					+ cell.getColumnIndex() + " error = "
					+ ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * recover by using it's address.
	 * 
	 * @param sheet
	 *            sheet.
	 */
	public final void recover(final Sheet sheet) {

		this.getAttrs().recover(sheet);
		if (this.commandList != null) {
			for (ConfigCommand command : this.commandList) {
				command.recover(sheet);
			}
		}

	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("FirstRowRef = " + this.getFirstRowRef());
		sb.append(",");
		sb.append("FirstRowAddr = " + this.getFirstRowAddr());
		sb.append(",");
		sb.append("LastRowPlusRef = " + this.getLastRowPlusRef());
		sb.append(",");
		sb.append("LastRowPlusAddr = " + this.getLastRowPlusAddr());
		sb.append(",");
		sb.append("LastCellCreated = " + this.isLastCellCreated());
		if (this.commandList != null) {
			sb.append("[");
			for (ConfigCommand command : this.commandList) {
				sb.append("command = " + command);
				sb.append(",");
			}
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}

}
