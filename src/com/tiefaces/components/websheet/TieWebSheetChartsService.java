/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.PictureData;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class TieWebSheetChartsService {

	private static boolean debug = true;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("TieWebSheetChartsService: " + msg);
		}
	}

	public TieWebSheetChartsService() {
		debug("TieWebSheetBean Constructor");
	}

	public StreamedContent getChart() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			debug(" return empty chart picture");
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			String chartId = context.getExternalContext()
					.getRequestParameterMap().get("chartViewId");

			BufferedImage bufferedImg = (BufferedImage) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get(chartId);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImg, "png", os);
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().remove(chartId);
			debug(" return real chart picture and remove session");
			return new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");
		}
	}

}
