/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.
	 * FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(final FacesContext context, final UIComponent component,
			final String value) {
		Double doubleValue = 0.0;
		String symbol = "";
		String strValue = (String) value;
		try {
			symbol = (String) component.getAttributes()
					.get(TieConstants.TIE_WEBSHEET_CELL_DATA_SYMBOL);

			if ((symbol != null) && (symbol
					.equals(TieConstants.cellFormatPercentageSymbol)
					&& strValue != null)) {
				strValue = strValue.trim();
				if (strValue.endsWith(
						TieConstants.cellFormatPercentageSymbol)) {
					doubleValue = Double.valueOf(
							strValue.substring(0, strValue.length() - 1))
							/ TieConstants.cellFormatPercentageValue;
					strValue = doubleValue.toString();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
	public String getAsString(final FacesContext context,
			final UIComponent component, final Object value) {

		String strValue = null;
		String symbol = "";
		try {

			strValue = (String) value;
			symbol = (String) component.getAttributes()
					.get(TieConstants.TIE_WEBSHEET_CELL_DATA_SYMBOL);

			if ((symbol != null)
					&& (symbol
							.equals(TieConstants.cellFormatPercentageSymbol))
					&& (value != null) && !((String) value).isEmpty()) {

				Double doubleValue = Double.valueOf((String) value)
						* TieConstants.cellFormatPercentageValue;
				strValue = fmtNumber(doubleValue)
						+ TieConstants.cellFormatPercentageSymbol;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strValue;
	}

}