/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Range build reference.
 * 
 * @author JASON JIANG
 *
 */
public class RangeBuildRef {

	/** left. */
	private int left;
	/** right. */
	private int right;
	/** total width. */
	private int totalWidth;
	/** sheet. */
	private Sheet sheet;

	/**
	 * Instantiates a new range build ref.
	 *
	 * @param pleft
	 *            the left
	 * @param pright
	 *            the right
	 * @param ptotalWidth
	 *            the total width
	 * @param psheet
	 *            the sheet
	 */
	public RangeBuildRef(final int pleft, final int pright,
			final int ptotalWidth, final Sheet psheet) {
		super();
		this.left = pleft;
		this.right = pright;
		this.totalWidth = ptotalWidth;
		this.sheet = psheet;
	}

	/**
	 * Gets the left.
	 *
	 * @return the left
	 */
	public final int getLeft() {
		return left;
	}

	/**
	 * Sets the left.
	 *
	 * @param pleft
	 *            the left to set
	 */
	public final void setLeft(final int pleft) {
		this.left = pleft;
	}

	/**
	 * Gets the right.
	 *
	 * @return the right
	 */
	public final int getRight() {
		return right;
	}

	/**
	 * Sets the right.
	 *
	 * @param pright
	 *            the right to set
	 */
	public final void setRight(final int pright) {
		this.right = pright;
	}

	/**
	 * Gets the total width.
	 *
	 * @return the totalWidth
	 */
	public final int getTotalWidth() {
		return totalWidth;
	}

	/**
	 * Sets the total width.
	 *
	 * @param ptotalWidth
	 *            the totalWidth to set
	 */
	public final void setTotalWidth(final int ptotalWidth) {
		this.totalWidth = ptotalWidth;
	}

	/**
	 * Gets the sheet.
	 *
	 * @return the sheet1
	 */
	public final Sheet getSheet() {
		return sheet;
	}

	/**
	 * Sets the sheet.
	 *
	 * @param psheet1
	 *            the sheet1 to set
	 */
	public final void setSheet(final Sheet psheet1) {
		this.sheet = psheet1;
	}

}
