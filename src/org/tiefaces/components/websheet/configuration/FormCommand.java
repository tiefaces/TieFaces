/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.tiefaces.common.TieConstants;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;
import org.tiefaces.components.websheet.utility.ShiftFormulaUtility;

/**
 * Form command. i.e. tie:form(name="departments" length="9" header="0"
 * footer="0")
 * 
 * @author Jason Jiang
 *
 */

public class FormCommand extends ConfigCommand implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6612626038249384415L;
	/** name holder. */
	private String name;
	/** header holder. */
	private String headerLength;
	/** footer holder. */
	private String footerLength;
	/** hidden holder. */
	private String hidden;
	/** readonly holder. */
	private String readOnly;
	/** fixedWidthStyle holder. */
	private String fixedWidthStyle;

	/**
	 * Instantiates a new form command.
	 */
	public FormCommand() {
		super();
	}

	/**
	 * Instantiates a new form command.
	 *
	 * @param sourceCommand
	 *            the source command
	 */
	public FormCommand(final FormCommand sourceCommand) {
		super((ConfigCommand) sourceCommand);
		this.name = sourceCommand.name;
		this.headerLength = sourceCommand.headerLength;
		this.footerLength = sourceCommand.footerLength;
		this.hidden = sourceCommand.hidden;
		this.readOnly = sourceCommand.readOnly;
		this.fixedWidthStyle = sourceCommand.fixedWidthStyle;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param pName
	 *            the new name
	 */
	public final void setName(final String pName) {
		this.name = pName;
	}

	/**
	 * Gets the header length.
	 *
	 * @return the header length
	 */
	public final String getHeaderLength() {
		return headerLength;
	}

	/**
	 * Sets the header length.
	 *
	 * @param pheaderLength
	 *            the new header length
	 */
	public final void setHeaderLength(final String pheaderLength) {
		this.headerLength = pheaderLength;
	}

	/**
	 * Gets the footer length.
	 *
	 * @return the footer length
	 */
	public final String getFooterLength() {
		return footerLength;
	}

	/**
	 * Sets the footer length.
	 *
	 * @param pfooterLength
	 *            the new footer length
	 */
	public final void setFooterLength(final String pfooterLength) {
		this.footerLength = pfooterLength;
	}

	/**
	 * Gets the hidden.
	 *
	 * @return the hidden
	 */
	public final String getHidden() {
		return hidden;
	}

	/**
	 * Sets the hidden.
	 *
	 * @param pHidden
	 *            the new hidden
	 */
	public final void setHidden(final String pHidden) {
		this.hidden = pHidden;
	}

	/**
	 * calc header length.
	 * 
	 * @return int header length.
	 */
	public final int calcHeaderLength() {
		return calcLength(this.getHeaderLength());
	}

	/**
	 * calc body length.
	 * 
	 * @return int body length.
	 */
	public final int calcBodyLength() {
		return calcLength(this.getLength()) - calcHeaderLength()
				- calcFooterLength();
	}

	/**
	 * calc footer length.
	 * 
	 * @return int footer length.
	 */
	public final int calcFooterLength() {
		return calcLength(this.getFooterLength());
	}

	/**
	 * @return the readOnly
	 */
	public final String getReadOnly() {
		return readOnly;
	}

	/**
	 * @param preadOnly
	 *            the readOnly to set
	 */
	public final void setReadOnly(final String preadOnly) {
		this.readOnly = preadOnly;
	}

	/**
	 * @return the fixedWidthStyle
	 */
	public final String getFixedWidthStyle() {
		return fixedWidthStyle;
	}

	/**
	 * @param pfixedWidthStyle
	 *            the fixedWidthStyle to set
	 */
	public final void setFixedWidthStyle(final String pfixedWidthStyle) {
		this.fixedWidthStyle = pfixedWidthStyle;
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
		sb.append("commandName = " + this.getCommandTypeName());
		sb.append(",");
		sb.append("form Name = " + this.getName());
		sb.append(",");
		sb.append("length = " + this.getLength());
		sb.append(",");
		sb.append("header length = " + this.getHeaderLength());
		sb.append(",");
		sb.append("footer length = " + this.getFooterLength());
		sb.append(",");
		sb.append("readOnly = " + this.getReadOnly());
		sb.append(",");
		sb.append("fixedWidthStyle = " + this.getFixedWidthStyle());
		sb.append(",");
		sb.append("ConfigRange = " + this.getConfigRange());
		sb.append("}");
		return sb.toString();

	}

	// Form is top level, Form cannot include another Form
	/**
	 * Watch list serve for formula changes. Basically all the rows appeared in
	 * the formula in the current sheet will be watched. Note if the cell
	 * reference is from other sheet or workbooks, it will be ignored.
	 * 
	 * @param wbWrapper
	 *            XSSFEvaluationWorkbook used for formula parse.
	 * @param sheet
	 *            current sheet.
	 * @return List row number for monitoring.
	 */
	private List<Integer> buildFormWatchList(
			final XSSFEvaluationWorkbook wbWrapper, final Sheet sheet) {

		List<Integer> watchList = new ArrayList<>();

		ConfigRange cRange = this.getConfigRange();
		List<ConfigCommand> commandList = cRange.getCommandList();
		if (commandList.isEmpty()) {
			// if no command then no dynamic changes. then no need formula
			// shifts.
			return watchList;
		}
		int lastStaticRow = commandList.get(0).getTopRow() - 1;
		if (lastStaticRow < 0) {
			lastStaticRow = this.getTopRow();
		}

		int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);

		for (int i = this.getTopRow(); i <= this.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			for (Cell cell : row) {
				if (cell.getCellTypeEnum() == CellType.FORMULA) {

					buildWatchListForCell(wbWrapper, sheetIndex, cell,
							watchList, lastStaticRow);

				}
			}
		}

		return watchList;

	}

	/**
	 * Builds the watch list for cell.
	 *
	 * @param wbWrapper
	 *            the wb wrapper
	 * @param sheetIndex
	 *            the sheet index
	 * @param cell
	 *            the cell
	 * @param watchList
	 *            the watch list
	 * @param lastStaticRow
	 *            the last static row
	 */
	private void buildWatchListForCell(
			final XSSFEvaluationWorkbook wbWrapper, final int sheetIndex,
			final Cell cell, final List<Integer> watchList, final int lastStaticRow) {
		String formula = cell.getCellFormula();

		Ptg[] ptgs = FormulaParser.parse(formula, wbWrapper,
				FormulaType.CELL, sheetIndex);

		for (int k = 0; k < ptgs.length; k++) {
			Object ptg = ptgs[k];
			// For area formula, only first row is watched.
			// Reason is the lastRow must shift same rows with
			// firstRow.
			// Otherwise it's difficult to calculate.
			// In case some situation cannot fit, then should make
			// change to the formula.
			int areaInt = ShiftFormulaUtility
					.getFirstSupportedRowNumFromPtg(ptg);
			if (areaInt >= 0) {
				addToWatchList(areaInt, lastStaticRow, watchList);
			}
		}

		// when insert row, the formula may changed. so here is the
		// workaround.
		// change formula to user formula to preserve the row
		// changes.
		cell.setCellType(CellType.STRING);
		cell.setCellValue(TieConstants.USER_FORMULA_PREFIX + formula
				+ TieConstants.USER_FORMULA_SUFFIX);
	}

	/**
	 * Only rows in dynamic area will be added to watch list.
	 *
	 * @param addRow
	 *            row want to add.
	 * @param lastStaticRow
	 *            last static row.
	 * @param watchList
	 *            watch list.
	 */
	private void addToWatchList(final int addRow, final int lastStaticRow,
			final List<Integer> watchList) {
		if ((addRow > lastStaticRow) && !(watchList.contains(addRow))) {
			watchList.add(addRow);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#buildAt(java.lang.
	 * String, org.tiefaces.components.websheet.configuration.ConfigBuildRef,
	 * int, java.util.Map, java.util.List)
	 */
	@Override
	/**
	 * build the command area at the row.
	 */
	public final int buildAt(String fullName,
			final ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			List<RowsMapping> currentRowsMappingList) {

		configBuildRef.setWatchList(buildFormWatchList(
				configBuildRef.getWbWrapper(), configBuildRef.getSheet()));
		fullName = this.getCommandName();

		RowsMapping unitRowsMapping = new RowsMapping();
		for (Integer index : configBuildRef.getWatchList()) {
			if (ConfigurationUtility.isStaticRow(this.getConfigRange(),
					index)) {
				unitRowsMapping.addRow(index,
						configBuildRef.getSheet().getRow(index));
			}
		}
		currentRowsMappingList = new ArrayList<>();
		currentRowsMappingList.add(unitRowsMapping);
		this.getConfigRange().getAttrs().setAllowAdd(false);
		configBuildRef.putShiftAttrs(fullName,
				this.getConfigRange().getAttrs(),
				new RowsMapping(unitRowsMapping));
		configBuildRef.setOriginConfigRange(
				new ConfigRange(this.getConfigRange()));
		configBuildRef.getOriginConfigRange()
				.indexCommandRange(configBuildRef.getCommandIndexMap());
		int length = this.getConfigRange().buildAt(fullName, configBuildRef,
				atRow, context, currentRowsMappingList);
		this.getConfigRange().getAttrs().setFinalLength(length);
		this.setFinalLength(length);
		configBuildRef.getSheet().setColumnHidden(
				TieConstants.HIDDEN_FULL_NAME_COLUMN, true);
		configBuildRef.getSheet().setColumnHidden(
				TieConstants.HIDDEN_SAVE_OBJECTS_COLUMN, true);
		configBuildRef.getSheet().setColumnHidden(
				TieConstants.HIDDEN_ORIGIN_ROW_NUMBER_COLUMN, true);

		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#getCommandName()
	 */
	/*
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#getCommandName()
	 */
	@Override
	public final String getCommandName() {
		return this.getCommandTypeName().substring(0, 1).toUpperCase() + "."
				+ this.getName().trim();
	}

}
