/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tiefaces.components.websheet;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

@FacesComponent("tieWebSheetComponent")
public class TieWebSheetComponent extends UINamingContainer {

	private TieWebSheetBean webSheetBean = null;

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("DEBUG: " + msg);
		}
	}

	public TieWebSheetComponent() {
		debug("TieWebSheetBean Constructor");
	}

	public TieWebSheetBean getWebSheetBean() {
		return webSheetBean;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		webSheetBean = (TieWebSheetBean) this.getAttributes().get(
				TieWebSheetConstants.TIE_WEBSHEET_ATTRS_WEBSHEETBEAN);
		if ((webSheetBean != null) && (webSheetBean.getWebFormClientId() == null))
		{	
			webSheetBean.setClientId(this.getClientId());
			webSheetBean.setWebFormClientId(this.getClientId() + ":"
					+ TieWebSheetConstants.TIE_WEBSHEET_COMPONENT_ID);
			
			String maxrows = (String) this.getAttributes().get("maxRowsPerPage");
			if ((maxrows != null)&&(!maxrows.isEmpty()))
			webSheetBean.setMaxRowsPerPage(Integer.valueOf(maxrows));
			debug("websheetbean max rows = "+webSheetBean.getMaxRowsPerPage());
			debug("webclientid = "+webSheetBean.getWebFormClientId());
		}
		super.encodeBegin(context);
	}

}
