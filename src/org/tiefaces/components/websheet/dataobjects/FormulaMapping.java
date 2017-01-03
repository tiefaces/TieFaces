/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;



/**
 * Used for cached map.
 * 
 * @author Jason Jiang
 *
 */
public class FormulaMapping {

	/**
	 * original formula.
	 */
	private String originFormula;
	/**
	 * cached value for the cell.
	 */
	private String value;

	/**
	 * Gets the origin formula.
	 *
	 * @return the origin formula
	 */
	public String getOriginFormula() {
		return originFormula;
	}

	/**
	 * Sets the origin formula.
	 *
	 * @param poriginFormula
	 *            the new origin formula
	 */
	public void setOriginFormula(final String poriginFormula) {
		this.originFormula = poriginFormula;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param pvalue
	 *            the new value
	 */
	public void setValue(final String pvalue) {
		this.value = pvalue;
	}

}
