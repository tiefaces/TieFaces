/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;

import org.apache.poi.ss.usermodel.PictureData;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This serve as a managed bean for retrieve picture from session.
 * 
 * @author Jason Jiang
 *
 */


@Named
@SessionScoped
public class TieWebSheetPicturesService implements Serializable  {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6230419110838095593L;
	/** log instance. */
	private static final Logger LOG = Logger
			.getLogger(TieWebSheetPicturesService.class.getName());

	/**
	 * Constructor.
	 */
	public TieWebSheetPicturesService() {
		LOG.fine("TieWebSheetPictureService Constructor");
	}

	/**
	 * Return picture to web front end.
	 * 
	 * @return empty (phase is render_response) or real picture ( browser
	 *         request).
	 * @throws IOException
	 *             exception.
	 */
	public StreamedContent getPicture() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			LOG.fine(" return empty picture");
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			String pictureId = context.getExternalContext()
					.getRequestParameterMap().get("pictureViewId");

			PictureData picData = (PictureData) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get(pictureId);
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().remove(pictureId);
			LOG.fine(" return real picture and remove session");
			return new DefaultStreamedContent(new ByteArrayInputStream(
					picData.getData()));
		}
	}
	

}
