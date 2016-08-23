package com.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

import com.tiefaces.components.websheet.service.CellHelper;
import com.tiefaces.components.websheet.service.ShiftFormula;

/**
 * Form command. i.e. tie:form(name="departments" length="9" header="0"
 * footer="0")
 * 
 * @author Jason Jiang
 *
 */

public class FormCommand extends ConfigCommand {

	/** name holder. */
	private String name;
	/** header holder. */
	private String headerLength;
	/** footer holder. */
	private String footerLength;
	/** hidden holder. */
	private String hidden;
	
	

	public FormCommand() {
		super();
	}

	public FormCommand(FormCommand sourceCommand) {
		super((ConfigCommand) sourceCommand);
		this.name = sourceCommand.name;
		this.headerLength = sourceCommand.headerLength;
		this.footerLength = sourceCommand.footerLength;
		this.hidden = sourceCommand.hidden;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String pName) {
		this.name = pName;
	}

	public String getHeaderLength() {
		return headerLength;
	}

	public void setHeaderLength(String headerLength) {
		this.headerLength = headerLength;
	}

	public String getFooterLength() {
		return footerLength;
	}

	public void setFooterLength(String footerLength) {
		this.footerLength = footerLength;
	}

	public final String getHidden() {
		return hidden;
	}

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
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public final String toString() {
		StringBuffer sb = new StringBuffer();
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

		List<Integer> watchList = new ArrayList<Integer>();

		ConfigRange cRange = this.getConfigRange();
		List<ConfigCommand> commandList = cRange.getCommandList();
		if (commandList.size() <= 0) {
			return watchList;
		}
		int lastStaticRow = commandList.get(0).getTopRow() - 1;
		if (lastStaticRow < 0) {
			lastStaticRow = this.getTopRow();
		}

		Workbook wb = sheet.getWorkbook();

		for (int i = this.getTopRow(); i <= this.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			for (Cell cell : row) {
				if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

					Ptg[] ptgs = FormulaParser.parse(
							cell.getCellFormula(), wbWrapper,
							FormulaType.CELL, wb.getSheetIndex(sheet));

					for (int k = 0; k < ptgs.length; k++) {
						Object ptg = ptgs[k];
						// For area formula, only first row is watched.
						// Reason is the lastRow must shift same rows with
						// firstRow.
						// Otherwise it's difficult to calculate.
						// In case some situation cannot fit, then should make
						// change to the formula.
						int areaInt = ShiftFormula
								.getFirstSupportedRowNumFromPtg(ptg);
						if (areaInt >= 0) {
							addToWatchList(sheet, areaInt, lastStaticRow,
									watchList);
						}
					}

				}
			}
		}

		return watchList;

	}

	/**
	 * Only rows in dynamic area will be added to watch list.
	 * 
	 * @param sheet
	 *            current sheet.
	 * @param addRow
	 *            row want to add.
	 * @param lastStaticRow
	 *            last static row.
	 * @param watchList
	 *            watch list.
	 */
	private void addToWatchList(final Sheet sheet, final int addRow,
			final int lastStaticRow, final List<Integer> watchList) {
		if ((addRow > lastStaticRow) && !(watchList.contains(addRow))) {
			watchList.add(addRow);
		}
	}

	@Override
	/**
	 * build the command area at the row.
	 */
	public final int buildAt(final XSSFEvaluationWorkbook wbWrapper,
			final Sheet sheet, final int atRow,
			final Map<String, Object> context, List<Integer> watchList,
			List<RowsMapping> currentRowsMappingList,
			List<RowsMapping> allRowsMappingList,
			final ExpressionEngine engine, final CellHelper cellHelper) {
		// TODO Auto-generated method stub

		watchList = buildFormWatchList(wbWrapper, sheet);

		RowsMapping currentMapping = new RowsMapping();
		for (Integer index : watchList) {
			Row row = sheet.getRow(index);
			if (this.getConfigRange().isStaticRow(row)) {
				currentMapping.addRow(index, row);
			}
		}
		currentRowsMappingList = new ArrayList<RowsMapping>();
		allRowsMappingList = new ArrayList<RowsMapping>();
		currentRowsMappingList.add(currentMapping);
		allRowsMappingList.add(currentMapping);

		int length = this.getConfigRange().buildAt(wbWrapper, sheet,
				atRow, context, watchList, currentRowsMappingList,
				allRowsMappingList, engine, cellHelper);

		this.setFinalLength(length);

		return length;
	}

}
