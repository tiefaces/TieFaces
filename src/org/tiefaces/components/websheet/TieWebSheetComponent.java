/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 * component class.
 * @author Jason Jiang
 *
 */
@FacesComponent("tieWebSheetComponent")
public class TieWebSheetComponent extends UINamingContainer {

	/** parent. */
	private TieWebSheetBean webSheetBean = null;

	/** logger. */
	private static final Logger log = Logger.getLogger(Thread
			.currentThread().getStackTrace()[0].getClassName());

	/** constructor. */
	public TieWebSheetComponent() {
		log.fine("TieWebSheetBean Constructor");
	}

	/**
	 * Return the bean.
	 * 
	 * @return bean.
	 */
	public final TieWebSheetBean getWebSheetBean() {
		return webSheetBean;
	}

	@Override
	public final void encodeBegin(final FacesContext context)
			throws IOException {
		webSheetBean = (TieWebSheetBean) this.getAttributes().get(
				TieWebSheetConstants.TIE_WEBSHEET_ATTRS_WEBSHEETBEAN);
		if ((webSheetBean != null)
				&& (webSheetBean.getWebFormClientId() == null)) {
			webSheetBean.setClientId(this.getClientId());
			webSheetBean.setWebFormClientId(this.getClientId() + ":"
					+ TieWebSheetConstants.TIE_WEBSHEET_COMPONENT_ID);

			String maxrows = (String) this.getAttributes().get(
					"maxRowsPerPage");
			if ((maxrows != null) && (!maxrows.isEmpty())) {
				webSheetBean.setMaxRowsPerPage(Integer.valueOf(maxrows));
			}
			log.fine("websheetbean max rows = "
					+ webSheetBean.getMaxRowsPerPage());
			log.fine("webclientid = " + webSheetBean.getWebFormClientId());
		}
		super.encodeBegin(context);
	}

}
