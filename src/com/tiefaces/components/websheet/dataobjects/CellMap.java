/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;

import com.tiefaces.components.websheet.TieWebSheetBean;

public class CellMap implements Serializable, java.util.Map {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static boolean debug = false;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("debug: " + msg);
		}
	}	
	
	private TieWebSheetBean parent = null;
	
		public CellMap(TieWebSheetBean parent) {
		super();
		this.parent = parent;
	}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}



		@Override
		public Object remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Set keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection values() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set entrySet() {
			// TODO Auto-generated method stub
			return null;
		}
		
		private String loadPicture(int rowIndex, int colIndex) {
			
			FacesCell facesCell = parent.getCellHelper().getFacesCellWithRowColFromCurrentPage(rowIndex, colIndex);
			if (facesCell != null && facesCell.isContainPic()) {
				FacesContext context = FacesContext.getCurrentInstance();
				String pictureId = facesCell.getPictureId();
				String pictureViewId =  context.getViewRoot().getViewId() + pictureId;
				Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
				if (sessionMap.get(pictureViewId) == null) {
					sessionMap.put(pictureViewId, parent.getPicturesMap().get(pictureId).getPictureData());
	debug("load picture put session map id = "+pictureViewId );				
				}	
				return pictureViewId;
			} else {
				return null;
			}
		}
		
		@Override
		public Object get(Object key) {
			// TODO Auto-generated method stub
			String skey = (String) key;
			if (skey != null) {
				String[] keyList = skey.split(":");
				int keylength = keyList.length;
				if (keylength>=2) {
					int rowIndex = Integer.parseInt(keyList[0]);
					int colIndex = Integer.parseInt(keyList[1]);
					boolean formatted = false;
					if ((keylength > 2) && (keyList[2].equalsIgnoreCase("format"))) formatted=true;
					if ((keylength > 2) && (keyList[2].equalsIgnoreCase("picture"))) {
						return loadPicture(rowIndex,colIndex);
					}
					Cell poiCell = parent.getCellHelper().getPoiCellWithRowColFromCurrentPage(rowIndex, colIndex);	
					if (poiCell != null) {
						String result;
						if (formatted)
							result = parent.getCellHelper().getCellValueWithFormat(poiCell);
						else
							result = parent.getCellHelper().getCellValueWithoutFormat(poiCell);
						 debug("Web Form FacesCell getCellValue row = "+rowIndex+" col = "+colIndex +" format = "+formatted+" result = " + result);
						return result;
					}
				}
			}	
			// return blank if null
			 return "";		
		}

		@Override
		public Object put(Object key, Object value) {
			// TODO Auto-generated method stub
			String skey = (String) key;
			if (skey != null) {
				String[] keyList = skey.split(":");
				int keylength = keyList.length;
				if (keylength>=2) {
					int rowIndex = Integer.parseInt(keyList[0]);
					int colIndex = Integer.parseInt(keyList[1]);
					Cell poiCell = parent.getCellHelper().getPoiCellWithRowColFromCurrentPage(rowIndex, colIndex);	
					if (poiCell != null) {			
						String oldValue = parent.getCellHelper().getCellValueWithoutFormat(poiCell);
						String newValue = (String) value;
						FacesCell facesCell = parent.getCellHelper().getFacesCellWithRowColFromCurrentPage(rowIndex, colIndex);
						if (facesCell.getInputType().equalsIgnoreCase("textarea")
								&& (newValue != null)) {
							// remove "\r" because excel issue
							newValue = newValue.replace("\r\n", "\n");
						}
						if (newValue != null && !newValue.equals(oldValue)) {
							debug("Web Form FacesCell setCellValue Old: " + oldValue
									+ ", New: " + newValue + ", row =" + rowIndex
									+ " col =" + colIndex + " inputtype = "
									+ facesCell.getInputType());
							parent.getCellHelper().setCellValue(poiCell, newValue);
							parent.getCellHelper().reCalc();
						}			
					}
				}
			}	
			return value;
		}		
		
  	 
  }