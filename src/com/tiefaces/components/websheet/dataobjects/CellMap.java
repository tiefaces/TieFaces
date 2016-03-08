/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;

import com.tiefaces.components.websheet.TieWebSheetBean;
/**
 * Cell Map is actually a virtual map which don't hold any data.
 * Instead it refer to the data in the workbook through getter/setters.
 * The purpose is to create a bridge between JSF Web and workbooks data.
 * @author Jason Jiang
 *
 */
@SuppressWarnings("rawtypes")
public class CellMap implements Serializable, java.util.Map {
	
	/** serial instance. */
	private static final long serialVersionUID = 1L;
	/** log instance. */
	private static final Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());

	/** instance to parent websheet bean. */
	private TieWebSheetBean parent = null;

	/**
	 * Construtor. Pass in websheet bean, So this helper can access related
	 * instance class.
	 * @param pParent  parent websheet bean
	 */

	public CellMap(final TieWebSheetBean pParent) {
		super();
		this.parent = pParent;
	}

	@Override
	public final int size() {
		return 0;
	}

	@Override
	public final boolean isEmpty() {
		return false;
	}

	@Override
	public final boolean containsKey(final Object key) {
		return false;
	}

	@Override
	public final boolean containsValue(final Object value) {
		return false;
	}

	@Override
	public final Object remove(final Object key) {
		return null;
	}

	@Override
	public void putAll(final Map m) {

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public final Set keySet() {
		return null;
	}

	@Override
	public final Collection values() {
		return null;
	}

	@Override
	public final Set entrySet() {
		return null;
	}
/**
 * Put picture image to session map and return the key to web. 
 * @param rowIndex the row index of the cell which contains picture.
 * @param colIndex the column index of the cell which contains picture.
 * @return the key of the picture image in the session map.
 */
	private String loadPicture(final int rowIndex, final int colIndex) {

		FacesCell facesCell = parent.getCellHelper()
				.getFacesCellWithRowColFromCurrentPage(rowIndex, colIndex);
		if (facesCell != null && facesCell.isContainPic()) {
			FacesContext context = FacesContext.getCurrentInstance();
			String pictureId = facesCell.getPictureId();
			String pictureViewId = Integer.toHexString(System
					.identityHashCode(parent.getWb())) + pictureId;
			Map<String, Object> sessionMap = context.getExternalContext()
					.getSessionMap();
			if (sessionMap.get(pictureViewId) == null) {
				sessionMap
						.put(pictureViewId,
								parent.getPicturesMap().get(pictureId)
										.getPictureData());
				log.info("load picture put session map id = " + pictureViewId);
			}
			return pictureViewId;
		} else {
			return null;
		}
	}

	/**
	 * Put chart image to session map and return the key to web. 
	 * @param rowIndex the row index of the cell which contains picture.
	 * @param colIndex the column index of the cell which contains picture.
	 * @return the key of the picture image in the session map.
	 */
	
	private String loadChart(final int rowIndex, final int colIndex) {

		FacesCell facesCell = parent.getCellHelper()
				.getFacesCellWithRowColFromCurrentPage(rowIndex, colIndex);
		if (facesCell != null && facesCell.isContainChart()) {
			FacesContext context = FacesContext.getCurrentInstance();
			String chartId = facesCell.getChartId();
			String chartViewId = Integer.toHexString(System
					.identityHashCode(parent.getWb())) + chartId;
			Map<String, Object> sessionMap = context.getExternalContext()
					.getSessionMap();
			if (sessionMap.get(chartViewId) == null) {
				sessionMap.put(chartViewId, parent.getChartsMap().get(chartId));
				log.fine("load chart put session map id = "
						+ chartViewId);
			}
			return chartViewId;
		} else {
			return null;
		}
	}

	@Override
	public final Object get(final Object key) {
		Object result = "";
		try {
			CellMapKey mkey = new CellMapKey((String) key);
			if (mkey.isParseSuccess()) {
				Cell poiCell = parent.getCellHelper()
						.getPoiCellWithRowColFromCurrentPage(
								mkey.getRowIndex(), mkey.getColIndex());
				if (poiCell != null) {
					if (mkey.isCharted()) {
						result = loadChart(mkey.getRowIndex(),
								mkey.getColIndex());
					} else if (mkey.isPictured()) {
						result = loadPicture(mkey.getRowIndex(),
								mkey.getColIndex());
					} else if (mkey.isFormatted()) {
						result = parent.getCellHelper().getCellValueWithFormat(
								poiCell);
					} else {
						result = parent.getCellHelper()
								.getCellValueWithoutFormat(poiCell);
					}
					log.fine("Web Form CellMap getCellValue row = "
							+ mkey.getRowIndex() + " col = "
							+ mkey.getColIndex() + " format = "
							+ mkey.isFormatted() + " result = " + result);
				}
			}
		} catch (Exception ex) {
			log.severe("Web Form CellMap get value error="
					+ ex.getLocalizedMessage());
		}
		// return blank if null
		return result;
	}

	@Override
	public final Object put(final Object key, final Object value) {
		CellMapKey mkey = new CellMapKey((String) key);
		if (mkey.isParseSuccess()) {
			Cell poiCell = parent.getCellHelper()
					.getPoiCellWithRowColFromCurrentPage(mkey.getRowIndex(),
							mkey.getColIndex());
			if (poiCell != null) {
				String oldValue = parent.getCellHelper()
						.getCellValueWithoutFormat(poiCell);
				String newValue = (String) value;
				FacesCell facesCell = parent.getCellHelper()
						.getFacesCellWithRowColFromCurrentPage(
								mkey.getRowIndex(), mkey.getColIndex());
				if (facesCell.getInputType().equalsIgnoreCase("textarea")
						&& (newValue != null)) {
					// remove "\r" because excel issue
					newValue = newValue.replace("\r\n", "\n");
				}
				if (newValue != null && !newValue.equals(oldValue)) {
					log.fine("Web Form CellMap setCellValue Old: " + oldValue
							+ ", New: " + newValue + ", row ="
							+ mkey.getRowIndex() + " col ="
							+ mkey.getColIndex() + " inputtype = "
							+ facesCell.getInputType());
					parent.getCellHelper().setCellValue(poiCell, newValue);
					parent.getCellHelper().reCalc();
				}
			}
		}

		return value;
	}

}