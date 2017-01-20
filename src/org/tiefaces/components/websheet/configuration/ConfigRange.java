/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellAddress;
import org.tiefaces.components.websheet.dataobjects.MapSnapShot;
import org.tiefaces.components.websheet.dataobjects.SerialCellAddress;

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
	private static final Logger LOG = Logger.getLogger(
			ConfigRange.class.getName());
	
	/** The attrs. */
	private ConfigRangeAttrs attrs = new ConfigRangeAttrs(false);

	/** command list. */
	private List<ConfigCommand> commandList;

	/**
	 * Instantiates a new config range.
	 */
	public ConfigRange() {
		super();
		// TODO Auto-generated constructor stub
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
			this.setFirstRowRef(
					sheet.getRow(attrs.getFirstRowAddr().getRow() + shiftnum)
							.getCell(attrs.getFirstRowAddr().getColumn(),
									MissingCellPolicy.CREATE_NULL_AS_BLANK),
					false);
			this.setLastRowPlusRef(sheet, attrs.getLastRowPlusAddr().getColumn(),
					attrs.getLastRowPlusAddr().getRow() + shiftnum - 1, false);

			if (commandList != null) {
				for (ConfigCommand command : commandList) {
					command.shiftRowRef(sheet, shiftnum);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.severe("shiftRowRef error =" + ex.getLocalizedMessage());
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
	public final void setFirstRowAddr(final SerialCellAddress pFirstRowAddr) {
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
			this.commandList = new ArrayList<ConfigCommand>();
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

		// List<Row> staticRows = setUpBuildRows(sheet,
		// this.getFirstRowRef().getRowIndex(),
		// this.getLastRowPlusRef().getRowIndex(), this.getCommandList());
		if (commandList != null) {
			for (int i = 0; i < commandList.size(); i++) {
				// cellRange.resetChangeMatrix();
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

		int finalLength = this.getLastRowPlusRef().getRowIndex()
				- this.getFirstRowRef().getRowIndex();

		return finalLength;

	}

	/**
	 * Index command range.
	 *
	 * @param indexMap
	 *            the index map
	 */
	public final void indexCommandRange(final Map<String, Command> indexMap) {
		ConfigurationHelper.indexCommandRange(this, indexMap);
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

		if ((context == null) || (context.size() == 0)) {
			// no need to evaluate as there's no data object.
			return;
		}

		// copy snapshot of context into shiftmap.
		snapShotContext(fullName, configBuildRef, context);

		// keep rowsMappingList as current as no change
		// allRowsMappingList = child + current

		List<RowsMapping> allRowsMappingList = ConfigurationHelper
				.findChildRowsMappingFromShiftMap(fullName,
						configBuildRef.getShiftMap());
		allRowsMappingList.addAll(rowsMappingList);

		int lastRowPlus = this.getLastRowPlusRef().getRowIndex();
		ShiftFormulaRef shiftFormulaRef = new ShiftFormulaRef(
				configBuildRef.getWatchList(), allRowsMappingList);
		for (int i = atRow; i < lastRowPlus; i++) {
			Row row = configBuildRef.getSheet().getRow(i);
			if ((row != null)
					&& ConfigurationHelper.isStaticRowRef(this, row)) {
				new StringBuffer();
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
						LOG.severe("build cell ( row = "
								+ cell.getRowIndex() + " column = "
								+ cell.getColumnIndex() + " error = "
								+ ex.getLocalizedMessage());
					}
				}
				ConfigurationHelper.setFullNameInHiddenColumn(row, fullName);
			}
		}
	}

	/**
	 * snap shot the current context objects. those object will be used for save
	 * data.
	 * 
	 * @param fullName
	 *            full name.
	 * @param configBuildRef
	 *            config build reference object.
	 * @param context
	 *            context.
	 */
	private void snapShotContext(final String fullName,
			final ConfigBuildRef configBuildRef,
			final Map<String, Object> context) {
		ConfigRangeAttrs lattrs = configBuildRef.getShiftMap().get(fullName);
		if ((lattrs != null) && (lattrs.getContextSnap() == null)) {
			lattrs.setContextSnap(new MapSnapShot(context));
		}
	}

}
