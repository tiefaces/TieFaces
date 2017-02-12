/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.tiefaces.components.websheet.serializable.SerialRow;

/**
 * The Class RowsMapping.
 */
public class RowsMapping implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	/** The rows map. */
	private Map<Integer, List<SerialRow>> rowsMap = new HashMap<>();

	/**
	 * Instantiates a new rows mapping.
	 */
	public RowsMapping() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param source
	 *            the source
	 */
	public RowsMapping(final RowsMapping source) {
		this.rowsMap.putAll(source.getRowsMap());
	}

	/**
	 * Gets the rows map.
	 *
	 * @return the rows map
	 */
	public final Map<Integer, List<SerialRow>> getRowsMap() {
		return rowsMap;
	}

	/**
	 * Adds the row.
	 *
	 * @param sourceRowNum
	 *            the source row num
	 * @param targetRow
	 *            the target row
	 */
	public final void addRow(final Integer sourceRowNum,
			final Row targetRow) {
		List<SerialRow> mapRowList = rowsMap.get(sourceRowNum);
		if (mapRowList == null) {
			mapRowList = new ArrayList<>();
		}
		SerialRow serialTarget = new SerialRow(targetRow, -1);
		if (!mapRowList.contains(serialTarget)) {
			mapRowList.add(serialTarget);
			rowsMap.put(sourceRowNum, mapRowList);
		}
	}

	/**
	 * Removes the row.
	 *
	 * @param sourceRowNum
	 *            the source row num
	 * @param targetRow
	 *            the target row
	 */
	public final void removeRow(final Integer sourceRowNum,
			final Row targetRow) {
		List<SerialRow> mapRowList = rowsMap.get(sourceRowNum);
		if (mapRowList != null) {
			mapRowList.remove(new SerialRow(targetRow, -1));
			rowsMap.put(sourceRowNum, mapRowList);
		}
	}

	/**
	 * Gets the.
	 *
	 * @param sourceRowNum
	 *            the source row num
	 * @return the list
	 */
	public final List<SerialRow> get(final Integer sourceRowNum) {
		return rowsMap.get(sourceRowNum);
	}

	/**
	 * Merge map.
	 *
	 * @param addMap
	 *            the add map
	 */
	public final void mergeMap(final RowsMapping addMap) {
		Map<Integer, List<SerialRow>> map = addMap.getRowsMap();
		for (Map.Entry<Integer, List<SerialRow>> entry : map.entrySet()) {
			List<SerialRow> entryRowList = entry.getValue();
			if ((entryRowList != null) && (!entryRowList.isEmpty())) {
				for (SerialRow row : entryRowList) {
					this.addRow(entry.getKey(), row.getRow());
				}
			}
		}
	}

	
	/**
	 * recover rows mapping by using it's address.
	 * 
	 * @param sheet
	 *            sheet.
	 */
	public final void recover(final Sheet sheet) {

		for (Map.Entry<Integer, List<SerialRow>> entry : this.getRowsMap()
				.entrySet()) {
			List<SerialRow> listRow = entry.getValue();
			for (SerialRow serialRow : listRow) {
				serialRow.recover(sheet);
			}
		}	

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
		for (Map.Entry<Integer, List<SerialRow>> entry : this.getRowsMap()
				.entrySet()) {
			sb.append(entry.getKey() + "=[");
			for (SerialRow row : entry.getValue()) {
				sb.append(row.getRow().getRowNum() + ",");
			}
			sb.append("], ");
		}
		sb.append("}");
		return sb.toString();
	}

}
