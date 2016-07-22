package com.tiefaces.components.websheet.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
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
	private String header;
	/** footer holder. */
	private String footer;
	/** hidden holder. */
	private String hidden;

	public final String getName() {
		return name;
	}

	public final void setName(final String pName) {
		this.name = pName;
	}

	public final String getHeader() {
		return header;
	}

	public final void setHeader(final String pHeader) {
		this.header = pHeader;
	}

	public final String getFooter() {
		return footer;
	}

	public final void setFooter(final String pFooter) {
		this.footer = pFooter;
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
		return calcLength(this.getHeader());
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
		return calcLength(this.getFooter());
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
		sb.append("header = " + this.getHeader());
		sb.append(",");
		sb.append("footer = " + this.getFooter());
		sb.append("}");
		return sb.toString();

	}


	// Form is top level, Form cannot include another Form
	private List<Integer> buildFormWatchList(XSSFEvaluationWorkbook wbWrapper,
			Sheet sheet) {
		
		ConfigRange cRange = this.getConfigRange();
		List<ConfigCommand> commandList = cRange.getCommandList();
		if (commandList.size()<=0) {
			return null;
		}
		int lastStaticRow = commandList.get(0).getTopRow() - 1;
		if (lastStaticRow < 0) {
			lastStaticRow = this.getTopRow();
		}
		
		Workbook wb = sheet.getWorkbook();
		
		List<Integer> watchList = new ArrayList<Integer>();
		
		
		for (int i = this.getTopRow(); i<= this.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			for (Cell cell : row) {
				if (cell.getCellType() ==  Cell.CELL_TYPE_FORMULA) {
					
					Ptg[] ptgs = FormulaParser.parse(
							cell.getCellFormula(), wbWrapper,
							FormulaType.CELL, wb.getSheetIndex(sheet));
					
					for (int k = 0; k < ptgs.length; k++) {
						Object ptg = ptgs[k];
						int[] areaInt = ShiftFormula.getRowNumFromPtg(ptg);
						if (areaInt[0] >= 0) {
							addToWatchList(sheet,areaInt[0],lastStaticRow,watchList);
							if (areaInt[1] >= 0) {
								addToWatchList(sheet,areaInt[1],lastStaticRow,watchList);
							}
						}
					}	
					
				}
			}
		}
		
		return watchList;
		
	}
	
	private void addToWatchList(Sheet sheet, int addRow, int lastStaticRow, List<Integer> watchList) {
		if (addRow > lastStaticRow) {
			watchList.add(addRow);
		}	
	}
	
	

	@Override
	public int buildAt(
			XSSFEvaluationWorkbook wbWrapper, 
			Sheet sheet,
			int atRow, 
			Map<String, Object> context,
			List<Integer> watchList,
			List<RowsMapping> currentRowsMappingList,
			List<RowsMapping> allRowsMappingList,
			List<Cell> processedFormula,
			ExpressionEngine engine, 
			CellHelper cellHelper) {
		// TODO Auto-generated method stub
		
		watchList = buildFormWatchList(wbWrapper, sheet);
		
		RowsMapping currentMapping = new RowsMapping();
		for (Integer index: watchList) {
			Row row = sheet.getRow(index);
			if (this.getConfigRange().isStaticRow(row)) {
				currentMapping.addRow(index, row);
			}	
		}
		currentRowsMappingList = new ArrayList<RowsMapping>();
		allRowsMappingList = new ArrayList<RowsMapping>();
		currentRowsMappingList.add(currentMapping);
		allRowsMappingList.add(currentMapping);
		
        int length = this.getConfigRange().buildAt(wbWrapper, sheet, atRow, context, watchList, currentRowsMappingList, allRowsMappingList, processedFormula, engine, cellHelper);
        		
        this.setFinalLength(length);

		return length;
	}



}
