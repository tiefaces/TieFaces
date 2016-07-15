package com.tiefaces.components.websheet.configuration;

import static com.tiefaces.components.websheet.TieWebSheetConstants.COPY_SHEET_PREFIX;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.tiefaces.components.websheet.service.CellHelper;

/**
 * Each command represent the repeat area which typically using a group of data.
 * i.e. tie:each(items="department.staff" var="employee" length="1"
 * allowAdd="True") department.staff is a object collection. In the cell area
 * can use ${employee.name} to define the "employee" attributes.
 * 
 * @author Jason Jiang
 *
 */
public class EachCommand extends ConfigCommand {

	/** items holder. */
	private String items;
	/** var holder. */
	private String var;
	/** allowAdd holder. */
	private String allowAdd;
	/** select holder. */
	private String select;

	public final String getItems() {
		return items;
	}

	public final void setItems(final String pItems) {
		this.items = pItems;
	}

	public final String getVar() {
		return var;
	}

	public final void setVar(final String pVar) {
		this.var = pVar;
	}

	public final String getAllowAdd() {
		return allowAdd;
	}

	public final void setAllowAdd(final String pAllowAdd) {
		this.allowAdd = pAllowAdd;
	}

	
	
	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
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
		sb.append("length = " + this.getLength());
		sb.append(",");
		sb.append("items = " + this.getItems());
		sb.append(",");
		sb.append("var = " + this.getVar());
		sb.append(",");
		sb.append("select = " + this.getSelect());
		sb.append(",");
		sb.append("Allow Add = " + this.getAllowAdd());
		sb.append("}");
		return sb.toString();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public int buildAt(Sheet sheet, int atRow,
			Map<String, Object> context, ExpressionEngine engine,
			CellHelper cellHelper) {
        Collection itemsCollection = ExpressionHelper.transformToCollectionObject(engine, items, context);
        int width = 0;
        int height = 0;
        int index = 0;
        ExpressionEngine selectEngine = null;
        if (select != null) {
            selectEngine = new ExpressionEngine(select);
        }
        
        int insertPosition = atRow;
        for (Object obj : itemsCollection) {
            context.put(var, obj);
            if (selectEngine != null && !ExpressionHelper.isConditionTrue(selectEngine, context)) {
                context.remove(var);
                continue;
            }
            ConfigRange currentRange = null;
            if (index > 0 ) {
            	insertEachTemplate(sheet, insertPosition, cellHelper);
            }
        	currentRange = buildCurrentRange(sheet, insertPosition);
            int length = currentRange.buildAt(sheet, insertPosition, context, selectEngine, cellHelper);
            insertPosition += length;
            index++;
            context.remove(var);
        }
		int finalLength = insertPosition - atRow;
        return finalLength;
     }
	
	private ConfigRange buildCurrentRange(Sheet sheet, int insertPosition) {
		ConfigRange current = (ConfigRange) this.getConfigRange().clone();
		int firstCellColumn = this.getConfigRange().getFirstRowAddr().getColumn();
		int firstCellRow = this.getConfigRange().getFirstRowAddr().getRow();
		current.setFirstRowRef(sheet.getRow(insertPosition).getCell(firstCellColumn, Row.RETURN_BLANK_AS_NULL), false);
		int lastPlusRow = this.getConfigRange().getLastRowPlusAddr().getRow() + insertPosition - firstCellRow;
		int lastPlusColumn = this.getConfigRange().getLastRowPlusAddr().getColumn();
		current.setLastRowPlusRef(sheet, lastPlusColumn, lastPlusRow -1, false);
		return current;
	}

	private void insertEachTemplate(Sheet sheet, int insertPosition, CellHelper cellHelper) {
		// TODO Auto-generated method stub
		Workbook wb = sheet.getWorkbook();
		String copyName = COPY_SHEET_PREFIX + sheet.getSheetName();
		Sheet srcSheet = wb.getSheet(copyName);
		cellHelper.copyRows(sheet.getWorkbook(), srcSheet, sheet, this.getConfigRange().getFirstRowAddr().getRow(), this.getConfigRange().getLastRowPlusAddr().getRow() - 1, insertPosition);
	}


}
