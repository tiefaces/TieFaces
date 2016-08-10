package com.tiefaces.components.websheet.configuration;

import static com.tiefaces.components.websheet.TieWebSheetConstants.COPY_SHEET_PREFIX;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

import com.google.gson.Gson;
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
	public int buildAt(
			XSSFEvaluationWorkbook wbWrapper, 
			Sheet sheet,
			int atRow, 
			Map<String, Object> context,
			List<Integer> watchList,
			List<RowsMapping> currentRowsMappingList,
			List<RowsMapping> allRowsMappingList,
			ExpressionEngine engine, 
			CellHelper cellHelper) {
		
        Collection itemsCollection = ExpressionHelper.transformToCollectionObject(engine, items, context);
        int index = 0;
        ExpressionEngine selectEngine = null;
        if (select != null) {
            selectEngine = new ExpressionEngine(select);
        }
        
        int insertPosition = atRow;
        
        Gson gson = new Gson();
        String jsonCurrentRange = gson.toJson(this.getConfigRange(), this.getConfigRange().getClass());
        

        // clone is a deep-clone of o
        for (Object obj : itemsCollection) {
        	RowsMapping unitRowsMapping = new RowsMapping();
            context.put(var, obj);
            if (selectEngine != null && !ExpressionHelper.isConditionTrue(selectEngine, context)) {
                context.remove(var);
                continue;
            }
            ConfigRange currentRange = null;
            if (index > 0 ) {
            	insertEachTemplate(wbWrapper, sheet, insertPosition, watchList, unitRowsMapping, cellHelper);
            }
        	currentRange = buildCurrentRange(sheet, insertPosition, gson, jsonCurrentRange);
        	currentRowsMappingList.add(unitRowsMapping);
        	allRowsMappingList.add(unitRowsMapping);
            int length = currentRange.buildAt(wbWrapper, sheet, insertPosition, context, watchList, currentRowsMappingList, allRowsMappingList, selectEngine, cellHelper);
            insertPosition += length;
            currentRowsMappingList.remove(unitRowsMapping);
            index++;
            context.remove(var);
        }
		int finalLength = insertPosition - atRow;
        return finalLength;
     }
	
	public <T> T deepCopy(T object, Class<T> type) {
	    try {
	        Gson gson = new Gson();
	        return gson.fromJson(gson.toJson(object, type), type);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}	
	
	private ConfigRange buildCurrentRange(Sheet sheet, int insertPosition, Gson gson, String jsonRange) {
		ConfigRange current = gson.fromJson(jsonRange, this.getConfigRange().getClass());
		int firstCellColumn = this.getConfigRange().getFirstRowAddr().getColumn();
		int firstCellRow = this.getConfigRange().getFirstRowAddr().getRow();
		current.setFirstRowRef(sheet.getRow(insertPosition).getCell(firstCellColumn, Row.CREATE_NULL_AS_BLANK), false);
		int lastPlusRow = this.getConfigRange().getLastRowPlusAddr().getRow() + insertPosition - firstCellRow;
		int lastPlusColumn = this.getConfigRange().getLastRowPlusAddr().getColumn();
		current.setLastRowPlusRef(sheet, lastPlusColumn, lastPlusRow -1, false);
		return current;
	}

	private void insertEachTemplate(XSSFEvaluationWorkbook wbWrapper, Sheet sheet, int insertPosition, List<Integer> watchList, RowsMapping unitRowsMapping, CellHelper cellHelper) {
		// TODO Auto-generated method stub
		Workbook wb = sheet.getWorkbook();
		String copyName = COPY_SHEET_PREFIX + sheet.getSheetName();
		Sheet srcSheet = wb.getSheet(copyName);
		
		int srcStartRow =  this.getConfigRange().getFirstRowAddr().getRow();
		int srcEndRow = this.getConfigRange().getLastRowPlusAddr().getRow() - 1;
		cellHelper.copyRows(sheet.getWorkbook(), wbWrapper,srcSheet, sheet, srcStartRow, srcEndRow, insertPosition);
		
		for (int rowIndex= srcStartRow; rowIndex<= srcEndRow; rowIndex++) {
			if (watchList.contains(rowIndex)) {
				unitRowsMapping.addRow(rowIndex, sheet.getRow(insertPosition + rowIndex - srcStartRow));
			}
		}
	}
	
	private void shiftFormulas(Sheet srcSheet, Sheet sheet, int top, int bottom, int insertPosition ) {
		
		int shiftRows = insertPosition - top;
		if (shiftRows == 0) {
			return;
		}
		
		
		
	}



}
