/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

/**
 * The Class RowsMapping.
 */
public class RowsMapping implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2827255600933776496L;
	/** The rows map. */
	private Map<Integer, List<Row>> rowsMap = new HashMap<Integer, List<Row>>();

	/**
	 * Instantiates a new rows mapping.
	 */
	public RowsMapping() {
		super();
		// TODO Auto-generated constructor stub
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
	public final Map<Integer, List<Row>> getRowsMap() {
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
	public final void addRow(final Integer sourceRowNum, final Row targetRow) {
		List<Row> mapRowList = rowsMap.get(sourceRowNum);
		if (mapRowList == null) {
			mapRowList = new ArrayList<Row>();
		}
		if (!mapRowList.contains(targetRow)) {
			mapRowList.add(targetRow);
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
	public final void removeRow(final Integer sourceRowNum, final Row targetRow) {
		List<Row> mapRowList = rowsMap.get(sourceRowNum);
		if (mapRowList != null) {
			mapRowList.remove(targetRow);
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
	public final List<Row> get(final Integer sourceRowNum) {
		List<Row> mapRowList = rowsMap.get(sourceRowNum);
		return mapRowList;
	}

	/**
	 * Merge map.
	 *
	 * @param addMap
	 *            the add map
	 */
	public final void mergeMap(final RowsMapping addMap) {
		Map<Integer, List<Row>> map = addMap.getRowsMap();
		for (Map.Entry<Integer, List<Row>> entry : map.entrySet()) {
			List<Row> entryRowList = entry.getValue();
			if ((entryRowList != null) && (entryRowList.size() > 0)) {
				for (Row row : entryRowList) {
					this.addRow(entry.getKey(), row);
				}
			}
		}
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public final String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (Map.Entry<Integer, List<Row>> entry : rowsMap.entrySet()) {
			sb.append(entry.getKey() + "=[");
			for (Row row : entry.getValue()) {
				sb.append(row.getRowNum() + ",");
			}
			sb.append("], ");
		}
		sb.append("}");
		return sb.toString();
	}

}
