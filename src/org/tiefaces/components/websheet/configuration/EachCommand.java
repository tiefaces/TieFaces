package org.tiefaces.components.websheet.configuration;

import static org.tiefaces.common.TieConstants.COPY_SHEET_PREFIX;
import static org.tiefaces.common.TieConstants.EXCEL_SHEET_NAME_LIMIT;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.tiefaces.components.websheet.service.CellHelper;

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
	/** class name holder. */
	private String className;


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
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
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
		sb.append("className = " + this.getClassName());
		sb.append(",");
		sb.append("select = " + this.getSelect());
		sb.append(",");
		sb.append("Allow Add = " + this.getAllowAdd());
		sb.append("}");
		return sb.toString();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public int buildAt(String fullName, ConfigBuildRef configBuildRef,
			int atRow, Map<String, Object> context,
			List<RowsMapping> currentRowsMappingList
			) {

		fullName = fullName + ":"+ this.getCommandName();
		Collection itemsCollection = ConfigurationHelper
				.transformToCollectionObject(configBuildRef.getEngine(), items, context);

		int index = 0;
		ExpressionEngine selectEngine = null;
		if (select != null) {
			selectEngine = new ExpressionEngine(select);
		}

		int insertPosition = atRow;
		List<RowsMapping> commandRowsMappingList = new ArrayList<RowsMapping>();
		
		String objClassName = this.getClassName(); 
		
		if (objClassName == null) {
			objClassName = configBuildRef.getCollectionObjNameMap().get(this.var);
		}		
		if (configBuildRef.isAddMode() && itemsCollection.isEmpty()) {
			// do something here to insert one empty object
			try {
				itemsCollection.add(Class.forName(objClassName).newInstance());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// loop through each object in the collection
		for (Object obj : itemsCollection) {
			// gather and cache object class name which used for add row
			if (objClassName == null) {
				objClassName = obj.getClass().getName();
				configBuildRef.getCollectionObjNameMap().put(this.var, objClassName);
			}
			RowsMapping unitRowsMapping = new RowsMapping();
			context.put(var, obj);
			if (selectEngine != null
					&& !ConfigurationHelper.isConditionTrue(selectEngine,
							context)) {
				context.remove(var);
				continue;
			}
			ConfigurationHelper.insertEachTemplate(this.getConfigRange(), configBuildRef, index, insertPosition,
					unitRowsMapping);
			ConfigRange currentRange = ConfigurationHelper.buildCurrentRange(this.getConfigRange(), configBuildRef.getSheet(), insertPosition);
			currentRowsMappingList.add(unitRowsMapping);
			commandRowsMappingList.add(unitRowsMapping);
			
			String unitFullName = fullName + "." + index;
			currentRange.getAttrs().allowAdd = false;
			if ((this.allowAdd != null)
					&& (this.allowAdd.trim().equalsIgnoreCase("true"))) {			
				currentRange.getAttrs().allowAdd = true;
				configBuildRef.setBodyAllowAdd(true);
			} 
			configBuildRef.putShiftAttrs(unitFullName, currentRange.getAttrs(), new RowsMapping(unitRowsMapping));
			
			int length = currentRange.buildAt( unitFullName, configBuildRef,
					insertPosition, context, 
					currentRowsMappingList );
			currentRange.getAttrs().finalLength = length;
			insertPosition += length;
			currentRowsMappingList.remove(unitRowsMapping);
			index++;
			context.remove(var);
		}
/* remove this as it caused issues.		
		// save the commandRowsMapping to the last one in currentRowsMapping
		// which is also the parent of the each command
		RowsMapping parentRowsMapping = new RowsMapping();
		int  parentIndex = currentRowsMappingList.size() - 1; 
		parentRowsMapping.mergeMap(currentRowsMappingList
				.get(parentIndex));
		for (RowsMapping rowsMapping : commandRowsMappingList) {
			parentRowsMapping.mergeMap(rowsMapping);
		}
		currentRowsMappingList.set(parentIndex, parentRowsMapping);
*/		
		int finalLength = insertPosition - atRow;
		return finalLength;
	}

	@Override
	public String getCommandName() {
		return this.getCommandTypeName().substring(0,1).toUpperCase()+"."+this.getVar().trim();
	}

	// public Object deepClone(Object object) {
	// try {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ObjectOutputStream oos = new ObjectOutputStream(baos);
	// oos.writeObject(object);
	// ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	// ObjectInputStream ois = new ObjectInputStream(bais);
	// return ois.readObject();
	// }
	// catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }


}
