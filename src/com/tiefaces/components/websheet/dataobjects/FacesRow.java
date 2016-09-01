/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet.dataobjects;

import java.util.List;


/**
 * Row object used for JSF datatable.
 * 
 * @author Jason Jiang
 */
public class FacesRow {

	/** row number. */
	private int rowIndex;
	/** whether the row is visible. */
	private boolean rendered;
	/** the row height. */
	private float rowheight;
	/** whether the row is repeat. */
	private boolean repeatZone;
	/** cells in the row. */
	private List<FacesCell> cells;
	
	/**
	 * constructor.
	 * @param pRowIndex the row index.
	 */
	public FacesRow(final int pRowIndex) {
		super();
		this.rowIndex = pRowIndex;
	}
	
	public final int getRowIndex() {
		return rowIndex;
	}
	public final void setRowIndex(final int pRowIndex) {
		this.rowIndex = pRowIndex;
	}
	public final boolean isRendered() {
		return rendered;
	}
	public final void setRendered(final boolean pRendered) {
		this.rendered = pRendered;
	}
	public final float getRowheight() {
		return rowheight;
	}
	public final void setRowheight(final float pRowheight) {
		this.rowheight = pRowheight;
	}
	public final boolean isRepeatZone() {
		return repeatZone;
	}
	public final void setRepeatZone(final boolean pRepeatZone) {
		this.repeatZone = pRepeatZone;
	}

	public final List<FacesCell> getCells() {
		return cells;
	}

	public final void setCells(final List<FacesCell> pCells) {
		this.cells = pCells;
	}
	
}
