/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.tiefaces.common.TieConstants;

/**
 * The Class TieSheetNumberConverter.
 */
@FacesConverter("tieSheetNumberConverter")
public class TieSheetNumberConverter implements Converter {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(TieSheetNumberConverter.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.
	 * FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public final Object getAsObject(final FacesContext context,
			final UIComponent component, final String value) {
		Double doubleValue = 0.0;
		String symbol = "";
		String strValue =  value;
		try {
			symbol =
					(String) component.getAttributes().get(
							TieConstants.CELL_DATA_SYMBOL);

			if ((symbol != null)
					&& (symbol
							.equals(TieConstants.CELL_FORMAT_PERCENTAGE_SYMBOL) && strValue != null)) {
				strValue = strValue.trim();
				if (strValue
						.endsWith(TieConstants.CELL_FORMAT_PERCENTAGE_SYMBOL)) {
					doubleValue =
							Double.valueOf(strValue.substring(0, strValue
									.length() - 1))
									/ TieConstants.CELL_FORMAT_PERCENTAGE_VALUE;
					strValue = doubleValue.toString();
				}
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"error in getAsObject of TieSheetNumberConverter : "
							+ ex.getLocalizedMessage(), ex);
		}
		return strValue;

	}

	/**
	 * Fmt number.
	 *
	 * @param d
	 *            the d
	 * @return the string
	 */
	private String fmtNumber(final double d) {
		if (d == (long) d) {
			return String.format("%d", (long) d);
		} else {
			return String.format("%.2f", d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.
	 * FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public final String getAsString(final FacesContext context,
			final UIComponent component, final Object value) {

		String strValue = null;
		String symbol = "";
		try {

			strValue = (String) value;
			symbol =
					(String) component.getAttributes().get(
							TieConstants.CELL_DATA_SYMBOL);

			if ((symbol != null)
					&& (symbol
							.equals(TieConstants.CELL_FORMAT_PERCENTAGE_SYMBOL))
					&& (value != null) && !((String) value).isEmpty()) {

				Double doubleValue =
						Double.valueOf((String) value)
								* TieConstants.CELL_FORMAT_PERCENTAGE_VALUE;
				strValue =
						fmtNumber(doubleValue)
								+ TieConstants.CELL_FORMAT_PERCENTAGE_SYMBOL;
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"error in getAsString of TieSheetNumberConverter : "
							+ ex.getLocalizedMessage(), ex);
		}
		return strValue;
	}

}