/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.service;

import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.tiefaces.common.TieConstants;

/**
 * The Class DateTimeCustomConverter.
 */
@FacesConverter("tieCalendaConverter")
public class DateTimeCustomConverter implements Converter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public final Object getAsObject(final FacesContext context,
			final UIComponent component, final String value) {
		if (value == null) {
			return null;
		}
		String pattern = (String) component.getAttributes().get("pattern");
		SimpleDateFormat formatter = new SimpleDateFormat(pattern,
				getLocale(context, component));
		try {
			return formatter.parse(value);
		} catch (Exception e) {
			throw new ConverterException(e.getLocalizedMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	public final String getAsString(final FacesContext context,
			final UIComponent component, final Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof String) {
			return (String) value;
		}
		if (context == null || component == null) {
			throw new NullPointerException();
		}

		try {
			String pattern = (String) component.getAttributes().get(
					TieConstants.WIDGET_ATTR_PATTERN);
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern,
					getLocale(context, component));
			return dateFormat.format(value);

		} catch (Exception e) {
			throw new ConverterException(e.getLocalizedMessage());
		}
	}

	/**
	 * Gets the locale.
	 *
	 * @param context
	 *            the context
	 * @param component
	 *            the component
	 * @return the locale
	 */
	private Locale getLocale(final FacesContext context,
			final UIComponent component) {

		String localeStr = (String) component.getAttributes().get(
				TieConstants.COMPONENT_ATTR_LOCALE);

		if (localeStr == null) {
			return context.getViewRoot().getLocale();
		}

		return Locale.forLanguageTag(localeStr);

	}

}