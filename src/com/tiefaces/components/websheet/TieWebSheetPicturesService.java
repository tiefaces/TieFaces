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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.poi.ss.usermodel.PictureData;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@ManagedBean
@SessionScoped
public class TieWebSheetPicturesService {

	
	private static boolean debug = true;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("TieWebSheetPicturesService: " + msg);
		}
	}

	public TieWebSheetPicturesService() {
		debug("TieWebSheetBean Constructor");
	}


    public StreamedContent getPicture() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
        	debug(" return empty picture");
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String pictureId = context.getExternalContext().getRequestParameterMap().get("pictureViewId");
            
            PictureData picData = (PictureData) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(pictureId);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(pictureId);
        	debug(" return real picture and remove session");
            return new DefaultStreamedContent(new ByteArrayInputStream(picData.getData())); 
        }
    }	

}
