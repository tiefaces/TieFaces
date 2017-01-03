/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.tiefaces.components.websheet.service.CellHelper;

/**
 * Interface for all the command.
 * 
 * @author Jason Jiang
 *
 */
public interface Command {

	/**
	 * Return command type.
	 * 
	 * @return command type name.
	 */
	String getCommandTypeName();

	/**
	 * Set command type.
	 * 
	 * @param pCommandTypeName
	 *            command type name.
	 */
	void setCommandTypeName(String pCommandTypeName);

	/**
	 * Constructor configRange if it's null. Always return an object for
	 * configRange.
	 * 
	 * @return ConfigRange object.
	 */
	ConfigRange getConfigRange();

	/**
	 * Set config range.
	 * 
	 * @param pConfigRange
	 *            config range.
	 */
	void setConfigRange(ConfigRange pConfigRange);

	/**
	 * Return parent command.
	 * 
	 * @return parent command.
	 */
	Boolean isParentFound();

	/**
	 * set parent found.
	 * 
	 * @param parentFound
	 *            true if parent found.
	 */
	void setParentFound(Boolean parentFound);

	/**
	 * command area length.
	 * 
	 * @return length.
	 */
	String getLength();

	/**
	 * Set command area length.
	 * 
	 * @param pLength
	 *            length.
	 */
	void setLength(String pLength);

	/**
	 * Get top row of command range.
	 * 
	 * @return int top row index.
	 */
	int getTopRow();

	/**
	 * Get last row of command range.
	 * 
	 * @return int last row index.
	 */

	int getLastRow();

	/**
	 * get left column index of the command range.
	 * 
	 * @return int left column index.
	 */
	int getLeftCol();

	/**
	 * get right column index of the command range.
	 * 
	 * @return int right column index.
	 */
	int getRightCol();

	/**
	 * the final length of command area.
	 * 
	 * @return final length.
	 */
	int getFinalLength();

	/**
	 * Set the final length.
	 * 
	 * @param populatedLength
	 *            final length.
	 */
	void setFinalLength(int populatedLength);

	/**
	 * Builds the at.
	 *
	 * @param fullName
	 *            full name.
	 * @param configBuildRef
	 *            configbuild reference.
	 * @param atRow
	 *            populate at the row.
	 * @param context
	 *            context map.
	 * @param currentRowsMappingList
	 *            rowsMapping for formula.
	 * @return build area length.
	 */
	int buildAt(String fullName, ConfigBuildRef configBuildRef, int atRow,
			Map<String, Object> context,
			List<RowsMapping> currentRowsMappingList);
	/**
	 * get command name.
	 * @return command name.
	 */
	String getCommandName();

}