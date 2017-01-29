/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;

import org.apache.poi.ss.util.CellReference;

/**
 * Range object used to define header/body range in the web form for
 * configuration.
 * 
 * @author Jason Jiang
 * This object corresponds to header/body/footer range columns in
 *       configuration tab.
 */

public class CellRange implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2504125263900250917L;

	/** The top row. */
	private int topRow;
	
	/** The bottom row. */
	private int bottomRow;
	
	/** The left col. */
	private int leftCol;
	
	/** The right col. */
	private int rightCol;

	
	/**
	 * Instantiates a new cell range.
	 *
	 * @param range
	 *            the range
	 */
	public CellRange(final String range) {

		if ((range != null) && range.contains(":")) {

			String[] rlist = range.split(":");

			if (rlist.length == 2) {
				if (rlist[0].trim().endsWith("$0")) {
					// no row configured
					CellReference ref0 = new CellReference(rlist[0].trim()
							.replaceAll("$0", "$1"));
					CellReference ref1 = new CellReference(rlist[1].trim()
							.replaceAll("$0", "$1"));
					this.setTopRow(-1);
					this.setLeftCol(ref0.getCol());
					this.setBottomRow(-1);
					this.setRightCol(ref1.getCol());

				} else {
					CellReference ref0 = new CellReference(rlist[0].trim());
					CellReference ref1 = new CellReference(rlist[1].trim());
					this.setTopRow(ref0.getRow());
					this.setLeftCol(ref0.getCol());
					this.setBottomRow(ref1.getRow());
					this.setRightCol(ref1.getCol());
				}

			}
		}
	}
	
	/**
	 * Gets the top row.
	 *
	 * @return the top row
	 */
	public final int getTopRow() {
		return topRow;
	}

	/**
	 * Sets the top row.
	 *
	 * @param ptopRow
	 *            the new top row
	 */
	public final void setTopRow(final int ptopRow) {
		this.topRow = ptopRow;
	}

	/**
	 * Gets the bottom row.
	 *
	 * @return the bottom row
	 */
	public final int getBottomRow() {
		return bottomRow;
	}

	/**
	 * Sets the bottom row.
	 *
	 * @param pbottomRow
	 *            the new bottom row
	 */
	public final void setBottomRow(final int pbottomRow) {
		this.bottomRow = pbottomRow;
	}

	/**
	 * Gets the left col.
	 *
	 * @return the left col
	 */
	public final int getLeftCol() {
		return leftCol;
	}

	/**
	 * Sets the left col.
	 *
	 * @param pleftCol
	 *            the new left col
	 */
	public final void setLeftCol(final int pleftCol) {
		this.leftCol = pleftCol;
	}

	/**
	 * Gets the right col.
	 *
	 * @return the right col
	 */
	public final int getRightCol() {
		return rightCol;
	}

	/**
	 * Sets the right col.
	 *
	 * @param prightCol
	 *            the new right col
	 */
	public final void setRightCol(final int prightCol) {
		this.rightCol = prightCol;
	}



	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	@Override
	public final String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("topRow = " + topRow);
		sb.append(",");
		sb.append("bottomRow = " + bottomRow);
		sb.append(",");
		sb.append("leftCol = " + leftCol);
		sb.append(",");
		sb.append("rightCol = " + rightCol);
		sb.append("}");
		return sb.toString();
	}

}
