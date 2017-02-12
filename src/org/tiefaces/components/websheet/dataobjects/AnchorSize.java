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
	
	/**
	 * Constructor.
	 *
	 * @param pleft
	 *            the pleft
	 * @param ptop
	 *            the ptop
	 * @param pwidth
	 *            the pwidth
	 * @param pheight
	 *            the pheight
	 */
	public AnchorSize(final int pleft, final int ptop, final int pwidth,
			final int pheight) {
		super();
		this.left = pleft;
		this.top = ptop;
		this.width = pwidth;
		this.height = pheight;
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
		sb.append("}");
		return sb.toString();
	}

}
