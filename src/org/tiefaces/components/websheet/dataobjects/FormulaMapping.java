/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;



/**
 * Used for cached map.
 * 
 * @author Jason Jiang
 *
 */
public class FormulaMapping implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6332637628608363196L;
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
	public final String getOriginFormula() {
		return originFormula;
	}

	/**
	 * Sets the origin formula.
	 *
	 * @param poriginFormula
	 *            the new origin formula
	 */
	public final void setOriginFormula(final String poriginFormula) {
		this.originFormula = poriginFormula;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param pvalue
	 *            the new value
	 */
	public final void setValue(final String pvalue) {
		this.value = pvalue;
	}

}
