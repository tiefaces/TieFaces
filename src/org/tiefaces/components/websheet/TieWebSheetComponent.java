/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.tiefaces.common.TieConstants;

/**
 * component class.
 * 
 * @author Jason Jiang
 *
 */
@FacesComponent("tieWebSheetComponent")
public class TieWebSheetComponent extends UINamingContainer {

    /** parent. */
    private TieWebSheetBean webSheetBean = null;

    /** logger. */
    private static final Logger LOG = Logger
            .getLogger(TieWebSheetComponent.class.getName());

    /** constructor. */
    public TieWebSheetComponent() {
        LOG.fine("TieWebSheetComponent Constructor");
    }

    /**
     * Return the bean.
     * 
     * @return bean.
     */
    public final TieWebSheetBean getWebSheetBean() {
        return webSheetBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context
     * .FacesContext)
     */
    @Override
    public final void encodeBegin(final FacesContext context)
            throws IOException {
        webSheetBean = (TieWebSheetBean) this.getAttributes()
                .get(TieConstants.ATTRS_WEBSHEETBEAN);
        if ((webSheetBean != null)
                && (webSheetBean.getWebFormClientId() == null)) {
            LOG.info("WebSheet component parameter setup");
            webSheetBean.setClientId(this.getClientId());
            webSheetBean.setWebFormClientId(
                    this.getClientId() + ":" + TieConstants.COMPONENT_ID);

            String maxrows = (String) this.getAttributes()
                    .get("maxRowsPerPage");
            if ((maxrows != null) && (!maxrows.isEmpty())) {
                webSheetBean.setMaxRowsPerPage(Integer.valueOf(maxrows));
            }

            Boolean hideSingleSheetTabTitle = (Boolean) this.getAttributes()
                    .get("hideSingleSheetTabTitle");
            if (hideSingleSheetTabTitle != null) {
                webSheetBean.setHideSingleSheetTabTitle(
                        hideSingleSheetTabTitle);
            }
        }
        super.encodeBegin(context);
    }

}
