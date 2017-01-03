/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.util.logging.Logger;

/**
 * hold row control information for the entire row. Add as the first object of
 * the row for control object.
 * 
 * @author Jason Jiang
 */
public class RowInfo {

	/** log instance. */
	private static final Logger log = Logger.getLogger(
			Thread.currentThread().getStackTrace()[0].getClassName());

	/** The row index. */
	private int rowIndex;

	/** The rendered. */
	private boolean rendered;

	/** The rowheight. */
	private float rowheight;

	/** The repeat zone. */
	private boolean repeatZone;

	/**
	 * Instantiates a new row info.
	 *
	 * @param prowIndex
	 *            the row index
	 */
	public RowInfo(final int prowIndex) {
		super();
		this.rowIndex = prowIndex;
	}

	/**
	 * Gets the row index.
	 *
	 * @return the row index
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * Sets the row index.
	 *
	 * @param prowIndex
	 *            the new row index
	 */
	public void setRowIndex(final int prowIndex) {
		this.rowIndex = prowIndex;
	}

	/**
	 * Checks if is rendered.
	 *
	 * @return true, if is rendered
	 */
	public boolean isRendered() {
		return rendered;
	}

	/**
	 * Sets the rendered.
	 *
	 * @param prendered
	 *            the new rendered
	 */
	public void setRendered(final boolean prendered) {
		this.rendered = prendered;
	}

	/**
	 * Gets the rowheight.
	 *
	 * @return the rowheight
	 */
	public float getRowheight() {
		return rowheight;
	}

	/**
	 * Sets the rowheight.
	 *
	 * @param prowheight
	 *            the new rowheight
	 */
	public void setRowheight(final float prowheight) {
		this.rowheight = prowheight;
	}

	/**
	 * Checks if is repeat zone.
	 *
	 * @return true, if is repeat zone
	 */
	public boolean isRepeatZone() {
		return repeatZone;
	}

	/**
	 * Sets the repeat zone.
	 *
	 * @param prepeatZone
	 *            the new repeat zone
	 */
	public void setRepeatZone(final boolean prepeatZone) {
		this.repeatZone = prepeatZone;
	}

}
