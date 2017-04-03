/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tiefaces.components.websheet.utility.CommandUtility;
import org.tiefaces.components.websheet.utility.ConfigurationUtility;

/**
 * Each command represent the repeat area which typically using a group of data.
 * i.e. tie:each(items="department.staff" var="employee" length="1"
 * allowAdd="True") department.staff is a object collection. In the cell area
 * can use ${employee.name} to define the "employee" attributes.
 * 
 * @author Jason Jiang
 *
 */
public class EachCommand extends ConfigCommand implements Serializable {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(EachCommand.class.getName());

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -281289717576958023L;
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

	/**
	 * Instantiates a new each command.
	 */
	public EachCommand() {
		super();
	}

	/**
	 * Instantiates a new each command.
	 *
	 * @param sourceCommand
	 *            the source command
	 */
	public EachCommand(final EachCommand sourceCommand) {
		super((ConfigCommand) sourceCommand);
		this.items = sourceCommand.items;
		this.var = sourceCommand.var;
		this.allowAdd = sourceCommand.allowAdd;
		this.select = sourceCommand.select;
	}

	/**
	 * Gets the items.
	 *
	 * @return the items
	 */
	public final String getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 *
	 * @param pItems
	 *            the new items
	 */
	public final void setItems(final String pItems) {
		this.items = pItems;
	}

	/**
	 * Gets the var.
	 *
	 * @return the var
	 */
	public final String getVar() {
		return var;
	}

	/**
	 * Sets the var.
	 *
	 * @param pVar
	 *            the new var
	 */
	public final void setVar(final String pVar) {
		this.var = pVar;
	}

	/**
	 * Gets the allow add.
	 *
	 * @return the allow add
	 */
	public final String getAllowAdd() {
		return allowAdd;
	}

	/**
	 * Sets the allow add.
	 *
	 * @param pAllowAdd
	 *            the new allow add
	 */
	public final void setAllowAdd(final String pAllowAdd) {
		this.allowAdd = pAllowAdd;
	}

	/**
	 * Gets the select.
	 *
	 * @return the select
	 */
	public final String getSelect() {
		return select;
	}

	/**
	 * Sets the select.
	 *
	 * @param pselect
	 *            the new select
	 */
	public final void setSelect(final String pselect) {
		this.select = pselect;
	}

	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 */
	public final String getClassName() {
		return className;
	}

	/**
	 * Sets the class name.
	 *
	 * @param pclassName
	 *            the new class name
	 */
	public final void setClassName(final String pclassName) {
		this.className = pclassName;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#buildAt(java.lang.
	 * String, org.tiefaces.components.websheet.configuration.ConfigBuildRef,
	 * int, java.util.Map, java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final int buildAt(String fullName,
			final ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			final List<RowsMapping> currentRowsMappingList) {

		fullName = fullName + ":" + this.getCommandName();
		Collection itemsCollection = ConfigurationUtility
				.transformToCollectionObject(configBuildRef.getEngine(),
						this.getItems(), context);

		String objClassName = this.getClassName();

		if (objClassName == null) {
			objClassName = configBuildRef.getCollectionObjNameMap()
					.get(this.getVar());
		}
		if (configBuildRef.isAddMode() && itemsCollection.isEmpty()) {
			// do something here to insert one empty object
			try {
				itemsCollection
						.add(Class.forName(objClassName).newInstance());
			} catch (Exception ex) {
				LOG.log(Level.SEVERE,
						"canot insert empty object in itemCollections error = "
								+ ex.getLocalizedMessage(),
						ex);
				return 0;
			}
		}

		int insertPosition = buildEachObjects(fullName, configBuildRef,
				atRow, context, currentRowsMappingList, itemsCollection,
				objClassName);

		return insertPosition - atRow;
	}

	/**
	 * Builds the each objects.
	 *
	 * @param fullName
	 *            the full name
	 * @param configBuildRef
	 *            the config build ref
	 * @param atRow
	 *            the at row
	 * @param context
	 *            the context
	 * @param currentRowsMappingList
	 *            the current rows mapping list
	 * @param itemsCollection
	 *            the items collection
	 * @param objClassName
	 *            the obj class name
	 * @return the int
	 */
	@SuppressWarnings("rawtypes")
	private int buildEachObjects(String fullName,
			final ConfigBuildRef configBuildRef, final int atRow,
			final Map<String, Object> context,
			final List<RowsMapping> currentRowsMappingList,
			final Collection itemsCollection, final String objClassName) {

		int index = 0;
		int insertPosition = atRow;
		String thisObjClassName = objClassName;

		// loop through each object in the collection
		for (Object obj : itemsCollection) {
			// gather and cache object class name which used for add row
			if (thisObjClassName == null) {
				thisObjClassName = obj.getClass().getName();
				configBuildRef.getCollectionObjNameMap().put(this.var,
						thisObjClassName);
			}
			RowsMapping unitRowsMapping = new RowsMapping();
			context.put(var, obj);
			CommandUtility.insertEachTemplate(this.getConfigRange(),
					configBuildRef, index, insertPosition, unitRowsMapping);
			ConfigRange currentRange = ConfigurationUtility
					.buildCurrentRange(this.getConfigRange(),
							configBuildRef.getSheet(), insertPosition);
			currentRowsMappingList.add(unitRowsMapping);

			String unitFullName = fullName + "." + index;
			currentRange.getAttrs().setAllowAdd(false);
			if ((this.allowAdd != null)
					&& ("true".equalsIgnoreCase(this.allowAdd.trim()))) {
				currentRange.getAttrs().setAllowAdd(true);
				configBuildRef.setBodyAllowAdd(true);
			}
			configBuildRef.putShiftAttrs(unitFullName,
					currentRange.getAttrs(),
					new RowsMapping(unitRowsMapping));

			int length = currentRange.buildAt(unitFullName, configBuildRef,
					insertPosition, context, currentRowsMappingList);
			currentRange.getAttrs().setFinalLength(length);
			insertPosition += length;
			currentRowsMappingList.remove(unitRowsMapping);
			index++;
			context.remove(var);
		}
		return insertPosition;
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
				+ this.getVar().trim();
	}

}
