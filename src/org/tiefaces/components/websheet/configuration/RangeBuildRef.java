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
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param totalWidth
	 *            the total width
	 * @param sheet
	 *            the sheet
	 */
	public RangeBuildRef(int left, int right, int totalWidth, Sheet sheet) {
		super();
		this.left = left;
		this.right = right;
		this.totalWidth = totalWidth;
		this.sheet = sheet;
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
	 * @param left
	 *            the left to set
	 */
	public final void setLeft(int left) {
		this.left = left;
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
	 * @param right
	 *            the right to set
	 */
	public final void setRight(int right) {
		this.right = right;
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
	 * @param totalWidth
	 *            the totalWidth to set
	 */
	public final void setTotalWidth(int totalWidth) {
		this.totalWidth = totalWidth;
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
	 * @param sheet1
	 *            the sheet1 to set
	 */
	public final void setSheet(Sheet sheet1) {
		this.sheet = sheet1;
	}

	
	
}
