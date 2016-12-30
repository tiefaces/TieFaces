/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;


/**
 * hold row control information for the entire row. Add as the first object of the row for control object.
 * 
 * @author Jason Jiang
 */
public class RowInfo {

	private boolean debug = false;

	private void debug(String msg) {
		if (debug) {
			System.out.println("debug: " + msg);
		}
	}

	private int rowIndex;
	
	private boolean rendered;
	
	private float rowheight; 

	private boolean repeatZone;

	public RowInfo(int rowIndex) {
		super();
		this.rowIndex = rowIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public float getRowheight() {
		return rowheight;
	}

	public void setRowheight(float rowheight) {
		this.rowheight = rowheight;
	}

	public boolean isRepeatZone() {
		return repeatZone;
	}

	public void setRepeatZone(boolean repeatZone) {
		this.repeatZone = repeatZone;
	}
	
	
	
	
	
}
