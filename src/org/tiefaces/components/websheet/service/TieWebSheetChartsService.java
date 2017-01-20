/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This serve as a managed bean for retrieve chart as picture from session.
 * 
 * @author Jason Jiang
 *
 */

@Named
@SessionScoped
public class TieWebSheetChartsService implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7490246985617724098L;
	/** log instance. */
	private static final Logger LOG = Logger
			.getLogger(TieWebSheetChartsService.class.getName());

	/**
	 * Constructor.
	 */
	public TieWebSheetChartsService() {
		LOG.fine("ChartsService Constructor");
	}

	/**
	 * Return real chart picture when browser requesting the image.
	 * 
	 * @return empty chart ( phase == render) or real chart ( browser request).
	 * @throws IOException
	 *             exception.
	 */
	public StreamedContent getChart() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			LOG.fine(" return empty chart picture");
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			String chartId = context.getExternalContext()
					.getRequestParameterMap().get("chartViewId");

			BufferedImage bufferedImg = (BufferedImage) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get(chartId);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bufferedImg, "png", os);
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().remove(chartId);
			LOG.fine(" return real chart picture and remove session chartId = "
					+ chartId);
			return new DefaultStreamedContent(new ByteArrayInputStream(
					os.toByteArray()), "image/png");
		}
	}

}
