/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.utility.CellUtility;

/**
 * Cell Map is actually a virtual map which don't hold any data. Instead it
 * refer to the data in the workbook through getter/setters. The purpose is to
 * create a bridge between JSF Web and workbooks data.
 * 
 * @author Jason Jiang
 *
 */
@SuppressWarnings("rawtypes")
public class CellMap implements Serializable, java.util.Map {

	/** serial instance. */
	private static final long serialVersionUID = 1L;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(CellMap.class.getName());

	/** The Constant emptySet. */
	private static final Set emptySet = new HashSet();

	/** instance to parent websheet bean. */
	private TieWebSheetBean parent = null;

	/**
	 * Construtor. Pass in websheet bean, So this helper can access related
	 * instance class.
	 * 
	 * @param pParent
	 *            parent websheet bean
	 */

	public CellMap(final TieWebSheetBean pParent) {
		super();
		this.parent = pParent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	@Override
	public final int size() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public final boolean isEmpty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public final boolean containsKey(final Object key) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public final boolean containsValue(final Object value) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public final Object remove(final Object key) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(final Map m) {
		emptySet.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		emptySet.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#keySet()
	 */
	@Override
	public final Set keySet() {
		return emptySet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#values()
	 */
	@Override
	public final Collection values() {
		return emptySet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public final Set entrySet() {
		return emptySet;
	}

	/**
	 * Put picture image to session map and return the key to web.
	 * 
	 * @param rowIndex
	 *            the row index of the cell which contains picture.
	 * @param colIndex
	 *            the column index of the cell which contains picture.
	 * @return the key of the picture image in the session map.
	 */
	private String loadPicture(final int rowIndex, final int colIndex) {

		FacesCell facesCell = parent.getCellHelper()
				.getFacesCellWithRowColFromCurrentPage(rowIndex, colIndex);
		if (facesCell != null && facesCell.isContainPic()) {
			FacesContext context = FacesContext.getCurrentInstance();
			String pictureId = facesCell.getPictureId();
			String pictureViewId = Integer.toHexString(
					System.identityHashCode(parent.getWb())) + pictureId;
			Map<String, Object> sessionMap = context.getExternalContext()
					.getSessionMap();
			if (sessionMap.get(pictureViewId) == null) {
				sessionMap.put(pictureViewId, parent.getPicturesMap()
						.get(pictureId).getPictureData());
			}
			return pictureViewId;
		} else {
			return null;
		}
	}

	/**
	 * Put chart image to session map and return the key to web.
	 * 
	 * @param rowIndex
	 *            the row index of the cell which contains picture.
	 * @param colIndex
	 *            the column index of the cell which contains picture.
	 * @return the key of the picture image in the session map.
	 */

	private String loadChart(final int rowIndex, final int colIndex) {

		FacesCell facesCell = parent.getCellHelper()
				.getFacesCellWithRowColFromCurrentPage(rowIndex, colIndex);
		if (facesCell != null && facesCell.isContainChart()) {
			FacesContext context = FacesContext.getCurrentInstance();
			String chartId = facesCell.getChartId();
			String chartViewId = Integer.toHexString(
					System.identityHashCode(parent.getWb())) + chartId;
			if (context != null) {
				Map<String, Object> sessionMap = context
						.getExternalContext().getSessionMap();
				if (sessionMap.get(chartViewId) == null) {
					sessionMap.put(chartViewId, parent.getCharsData()
							.getChartsMap().get(chartId));
				}
			}
			return chartViewId;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public final Object get(final Object key) {
		Object result = "";
		try {
			CellMapKey mkey = new CellMapKey((String) key);
			if (!mkey.isParseSuccess()) {
				return result;
			}
			Cell poiCell = parent.getCellHelper()
					.getPoiCellWithRowColFromCurrentPage(mkey.getRowIndex(),
							mkey.getColIndex());
			if (poiCell == null) {
				return result;
			}
			if (mkey.isCharted()) {
				result = loadChart(mkey.getRowIndex(), mkey.getColIndex());
			} else if (mkey.isPictured()) {
				result = loadPicture(mkey.getRowIndex(),
						mkey.getColIndex());
			} else if (mkey.isFormatted()) {
				result = CellUtility.getCellValueWithFormat(poiCell,
						parent.getFormulaEvaluator(),
						parent.getDataFormatter());
			} else {
				result = CellUtility.getCellValueWithoutFormat(poiCell);
			}

		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Web Form CellMap get value error="
					+ ex.getLocalizedMessage(), ex);
		}
		// return blank if null
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final Object put(final Object key, final Object value) {
		try {
			CellMapKey mkey = new CellMapKey((String) key);
			if (!mkey.isParseSuccess()) {
				return null;
			}
			Cell poiCell = parent.getCellHelper()
					.getPoiCellWithRowColFromCurrentPage(mkey.getRowIndex(),
							mkey.getColIndex());
			if (poiCell == null) {
				return null;
			}
			String oldValue = CellUtility
					.getCellValueWithoutFormat(poiCell);
			FacesCell facesCell = parent.getCellHelper()
					.getFacesCellWithRowColFromCurrentPage(
							mkey.getRowIndex(), mkey.getColIndex());
			String newValue = assembleNewValue(value, facesCell);
			if (newValue != null && !newValue.equals(oldValue)) {
				CellUtility.setCellValue(poiCell, newValue);
				if (facesCell.isHasSaveAttr()) {
					parent.getCellHelper().saveDataInContext(poiCell,
							newValue);
				}
				// patch to avoid not updated downloaded file
				CellUtility.copyCell(poiCell.getSheet(), poiCell.getRow(), poiCell.getRow(), poiCell.getColumnIndex(),false);
				parent.getCellHelper().reCalc();
			}

			return value;
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"Save cell data error : " + ex.getLocalizedMessage(),
					ex);
		}
		return null;
	}

	/**
	 * Assemble new value.
	 *
	 * @param value
	 *            the value
	 * @param facesCell
	 *            the faces cell
	 * @return the string
	 */
	private String assembleNewValue(final Object value,
			final FacesCell facesCell) {
		String newValue;
		if (value instanceof java.util.Date) {
			String datePattern = facesCell.getDatePattern();
			if (datePattern == null || datePattern.isEmpty()) {
			    datePattern = parent.getDefaultDatePattern();
			}
			Format formatter = new SimpleDateFormat(datePattern);
			newValue = formatter.format(value);
		} else {
			newValue = (String) value;
		}
		if ("textarea".equalsIgnoreCase(facesCell.getInputType())
				&& (newValue != null)) {
			// remove "\r" because excel issue
			newValue = newValue.replace("\r\n", "\n");
		}
		return newValue;
	}
}