/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

/**
 * Class object hold anchor.
 * 
 * @author Jason Jiang
 *
 */
public class AnchorSize {
	/** anchor's left. */
	private int left;
	/** anchor's top. */
	private int top;
	/** anchor's width. */
	private int width;
	/** anchor's height. */
	private int height;

	/** The cell width. */
	private double cellWidth;

	/** The cell height. */
	private double cellHeight;

	/** The percent left. */
	private double percentLeft;

	/** The percent top. */
	private double percentTop;

	/** The percent width. */
	private double percentWidth;

	/** The percent height. */
	private double percentHeight;

	/**
	 * Instantiates a new anchor size.
	 *
	 * @param pleft
	 *            the pleft
	 * @param ptop
	 *            the ptop
	 * @param pwidth
	 *            the pwidth
	 * @param pheight
	 *            the pheight
	 * @param pcellWidth
	 *            the pcell width
	 * @param pcellHeight
	 *            the pcell height
	 */
	public AnchorSize(final int pleft, final int ptop, final int pwidth,
			final int pheight, final double pcellWidth,
			final double pcellHeight) {
		super();
		this.left = pleft;
		this.top = ptop;
		this.width = pwidth;
		this.height = pheight;
		this.cellWidth = pcellWidth;
		this.cellHeight = pcellHeight;
	}

	/**
	 * Gets the top.
	 *
	 * @return the top
	 */
	public final int getTop() {
		return top;
	}

	/**
	 * Sets the top.
	 *
	 * @param ptop
	 *            the new top
	 */
	public final void setTop(final int ptop) {
		this.top = ptop;
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
	 *            the new left
	 */
	public final void setLeft(final int pleft) {
		this.left = pleft;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 *
	 * @param pwidth
	 *            the new width
	 */
	public final void setWidth(final int pwidth) {
		this.width = pwidth;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 *
	 * @param pheight
	 *            the new height
	 */
	public final void setHeight(final int pheight) {
		this.height = pheight;
	}

	/**
	 * Gets the cell width.
	 *
	 * @return the cellWidth
	 */
	public final double getCellWidth() {
		return cellWidth;
	}

	/**
	 * Sets the cell width.
	 *
	 * @param pcellWidth
	 *            the cellWidth to set
	 */
	public final void setCellWidth(double pcellWidth) {
		this.cellWidth = pcellWidth;
	}

	/**
	 * Gets the cell height.
	 *
	 * @return the cellHeight
	 */
	public final double getCellHeight() {
		return cellHeight;
	}

	/**
	 * Sets the cell height.
	 *
	 * @param pcellHeight
	 *            the cellHeight to set
	 */
	public final void setCellHeight(double pcellHeight) {
		this.cellHeight = pcellHeight;
	}

	/**
	 * Gets the percent left.
	 *
	 * @return the percentLeft
	 */
	public final double getPercentLeft() {
		if (this.getCellWidth() > 0) {
			return 100 * this.getLeft() / this.getCellWidth();
		} else {
			return 0;
		}
	}

	/**
	 * Gets the percent top.
	 *
	 * @return the percentTop
	 */
	public final double getPercentTop() {
		if (this.getCellHeight() > 0) {
			return 100 * this.getTop() / this.getCellHeight();
		} else {
			return 0;
		}
	}

	/**
	 * Gets the percent width.
	 *
	 * @return the percentWidth
	 */
	public final double getPercentWidth() {
		if (this.getCellWidth() > 0) {
			return 100 * this.getWidth() / this.getCellWidth();
		} else {
			return 0;
		}
	}

	/**
	 * Gets the percent height.
	 *
	 * @return the percentHeight
	 */
	public final double getPercentHeight() {
		if (this.getCellHeight() > 0) {
			return 100 * this.getHeight() / this.getCellHeight();
		} else {
			return 0;
		}	
	}

	/**
	 * show human readable message.
	 *
	 * @return the string
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("left = " + left);
		sb.append(",");
		sb.append("top = " + top);
		sb.append(",");
		sb.append("width = " + width);
		sb.append(",");
		sb.append("height = " + height);
		sb.append(",");
		sb.append("left% = " + this.getPercentLeft());
		sb.append(",");
		sb.append("top% = " + this.getPercentTop());
		sb.append(",");
		sb.append("width% = " + this.getPercentWidth());
		sb.append(",");
		sb.append("height% = " + this.getPercentHeight());
		sb.append("}");
		return sb.toString();
	}

}
