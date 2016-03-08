/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This serve as a managed bean for retrieve chart as picture from session.
 * 
 * @author Jason Jiang
 *
 */

@ManagedBean
@SessionScoped
public class TieWebSheetChartsService {

	
	/** log instance. */
	private static final Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());
	
/**
 * Constructor.
 */
	public TieWebSheetChartsService() {
		log.fine("TieWebSheetBean Constructor");
	}
/**
 * Return real chart picture when browser requesting the image.
 * @return empty chart ( phase == render) or real chart ( browser request).
 * @throws IOException
 */
	public StreamedContent getChart() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			log.fine(" return empty chart picture");
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
			log.fine(" return real chart picture and remove session chartId = "+chartId);
			return new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");
		}
	}

}
