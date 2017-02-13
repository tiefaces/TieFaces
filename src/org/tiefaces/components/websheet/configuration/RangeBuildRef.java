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
	 * @param left
	 * @param right
	 * @param totalWidth
	 * @param sheet
	 */
	public RangeBuildRef(int left, int right, int totalWidth, Sheet sheet) {
		super();
		this.left = left;
		this.right = right;
		this.totalWidth = totalWidth;
		this.sheet = sheet;
	}
	
	
	/**
	 * @return the left
	 */
	public final int getLeft() {
		return left;
	}
	/**
	 * @param left the left to set
	 */
	public final void setLeft(int left) {
		this.left = left;
	}
	/**
	 * @return the right
	 */
	public final int getRight() {
		return right;
	}
	/**
	 * @param right the right to set
	 */
	public final void setRight(int right) {
		this.right = right;
	}
	/**
	 * @return the totalWidth
	 */
	public final int getTotalWidth() {
		return totalWidth;
	}
	/**
	 * @param totalWidth the totalWidth to set
	 */
	public final void setTotalWidth(int totalWidth) {
		this.totalWidth = totalWidth;
	}
	/**
	 * @return the sheet1
	 */
	public final Sheet getSheet() {
		return sheet;
	}
	/**
	 * @param sheet1 the sheet1 to set
	 */
	public final void setSheet(Sheet sheet1) {
		this.sheet = sheet1;
	}

	
	
}
