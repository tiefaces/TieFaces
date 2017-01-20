/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;
import java.util.List;


/**
 * Row object used for JSF datatable.
 * 
 * @author Jason Jiang
 */
public class FacesRow implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1576241602026457797L;
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
	
	/** whether the row allow add/delete. */
	private boolean allowAdd;
	
	/** The origin row index. */
	private int originRowIndex = -1;
	/**
	 * constructor.
	 * @param pRowIndex the row index.
	 */
	public FacesRow(final int pRowIndex) {
		super();
		this.rowIndex = pRowIndex;
	}
	
	/**
	 * Gets the row index.
	 *
	 * @return the row index
	 */
	public final int getRowIndex() {
		return rowIndex;
	}
	
	/**
	 * Sets the row index.
	 *
	 * @param pRowIndex
	 *            the new row index
	 */
	public final void setRowIndex(final int pRowIndex) {
		this.rowIndex = pRowIndex;
	}
	
	/**
	 * Checks if is rendered.
	 *
	 * @return true, if is rendered
	 */
	public final boolean isRendered() {
		return rendered;
	}
	
	/**
	 * Sets the rendered.
	 *
	 * @param pRendered
	 *            the new rendered
	 */
	public final void setRendered(final boolean pRendered) {
		this.rendered = pRendered;
	}
	
	/**
	 * Gets the rowheight.
	 *
	 * @return the rowheight
	 */
	public final float getRowheight() {
		return rowheight;
	}
	
	/**
	 * Sets the rowheight.
	 *
	 * @param pRowheight
	 *            the new rowheight
	 */
	public final void setRowheight(final float pRowheight) {
		this.rowheight = pRowheight;
	}
	
	/**
	 * Checks if is repeat zone.
	 *
	 * @return true, if is repeat zone
	 */
	public final boolean isRepeatZone() {
		return repeatZone;
	}
	
	/**
	 * Sets the repeat zone.
	 *
	 * @param pRepeatZone
	 *            the new repeat zone
	 */
	public final void setRepeatZone(final boolean pRepeatZone) {
		this.repeatZone = pRepeatZone;
	}
	
	/**
	 * Checks if is allow add.
	 *
	 * @return true, if is allow add
	 */
	public final boolean isAllowAdd() {
		return allowAdd;
	}

	/**
	 * Sets the allow add.
	 *
	 * @param pallowAdd
	 *            the new allow add
	 */
	public final void setAllowAdd(final boolean pallowAdd) {
		this.allowAdd = pallowAdd;
	}

	/**
	 * Gets the cells.
	 *
	 * @return the cells
	 */
	public final List<FacesCell> getCells() {
		return cells;
	}

	/**
	 * Sets the cells.
	 *
	 * @param pCells
	 *            the new cells
	 */
	public final void setCells(final List<FacesCell> pCells) {
		this.cells = pCells;
	}

	/**
	 * Gets the origin row index.
	 *
	 * @return the origin row index
	 */
	public final int getOriginRowIndex() {
		return originRowIndex;
	}

	/**
	 * Sets the origin row index.
	 *
	 * @param poriginRowIndex
	 *            the new origin row index
	 */
	public final void setOriginRowIndex(final int poriginRowIndex) {
		this.originRowIndex = poriginRowIndex;
	}
	
}
