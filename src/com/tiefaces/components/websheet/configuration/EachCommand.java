package com.tiefaces.components.websheet.configuration;

import static com.tiefaces.components.websheet.TieWebSheetConstants.COPY_SHEET_PREFIX;
import static com.tiefaces.components.websheet.TieWebSheetConstants.EXCEL_SHEET_NAME_LIMIT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

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

	
	public EachCommand() {
		super();
	}

	public EachCommand(EachCommand sourceCommand) {
		super((ConfigCommand) sourceCommand);
		this.items = sourceCommand.items;
		this.var = sourceCommand.var;
		this.allowAdd = sourceCommand.allowAdd;
		this.select = sourceCommand.select;
	}

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
			ExpressionEngine engine, 
			CellHelper cellHelper) {
		
        Collection itemsCollection = ExpressionHelper.transformToCollectionObject(engine, items, context);
        int index = 0;
        ExpressionEngine selectEngine = null;
        if (select != null) {
            selectEngine = new ExpressionEngine(select);
        }
        
        int insertPosition = atRow;
        List<RowsMapping> commandRowsMappingList = new ArrayList<RowsMapping>();
        // clone is a deep-clone of o
        for (Object obj : itemsCollection) {
        	RowsMapping unitRowsMapping = new RowsMapping();
            context.put(var, obj);
            if (selectEngine != null && !ExpressionHelper.isConditionTrue(selectEngine, context)) {
                context.remove(var);
                continue;
            }
            ConfigRange currentRange = null;
           	insertEachTemplate(index, wbWrapper, sheet, insertPosition, watchList, unitRowsMapping, cellHelper);
        	currentRange = buildCurrentRange(sheet, insertPosition);
        	currentRowsMappingList.add(unitRowsMapping);
        	commandRowsMappingList.add(unitRowsMapping);
            int length = currentRange.buildAt(wbWrapper, sheet, insertPosition, context, watchList, currentRowsMappingList, engine, cellHelper);
            insertPosition += length;
            currentRowsMappingList.remove(unitRowsMapping);
            index++;
            context.remove(var);
        }
        RowsMapping parentRowsMapping = currentRowsMappingList.get(currentRowsMappingList.size() - 1);
        for (RowsMapping rowsMapping: commandRowsMappingList) {
        	parentRowsMapping.mergeMap(rowsMapping);
        }
		int finalLength = insertPosition - atRow;
        return finalLength;
     }
	
	 public Object deepClone(Object object) {
		   try {
		     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		     ObjectOutputStream oos = new ObjectOutputStream(baos);
		     oos.writeObject(object);
		     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		     ObjectInputStream ois = new ObjectInputStream(bais);
		     return ois.readObject();
		   }
		   catch (Exception e) {
		     e.printStackTrace();
		     return null;
		   }
		 }	
	private ConfigRange buildCurrentRange(Sheet sheet, int insertPosition) {
		ConfigRange current = new ConfigRange(this.getConfigRange());
		int shiftNum = insertPosition - this.getConfigRange().getFirstRowAddr().getRow();
		current.shiftRowRef(sheet, shiftNum);
		return current;
	}

	private void insertEachTemplate(int index, XSSFEvaluationWorkbook wbWrapper, Sheet sheet, int insertPosition, List<Integer> watchList, RowsMapping unitRowsMapping, CellHelper cellHelper) {
		// TODO Auto-generated method stub
		int srcStartRow =  this.getConfigRange().getFirstRowAddr().getRow();
		int srcEndRow = this.getConfigRange().getLastRowPlusAddr().getRow() - 1;

		Workbook wb = sheet.getWorkbook();
		// excel sheet name has limit 31 chars
		String copyName = (COPY_SHEET_PREFIX + sheet.getSheetName());
		if (copyName.length() >  EXCEL_SHEET_NAME_LIMIT) {
			copyName = copyName.substring(0, EXCEL_SHEET_NAME_LIMIT);
		}	
		Sheet srcSheet = wb.getSheet(copyName);
		if (index > 0) {
			cellHelper.copyRows(sheet.getWorkbook(), wbWrapper,srcSheet, sheet, srcStartRow, srcEndRow, insertPosition);
		}	
		
		for (int rowIndex= srcStartRow; rowIndex<= srcEndRow; rowIndex++) {
			if (watchList.contains(rowIndex)&&(this.getConfigRange().isStaticRow(rowIndex))) {
				unitRowsMapping.addRow(rowIndex, sheet.getRow(insertPosition + rowIndex - srcStartRow));
			}
		}
	}
	




}
