/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.poi.ss.usermodel.PictureData;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This serve as a managed bean for retrieve picture from session.
 * 
 * @author Jason Jiang
 *
 */

@ManagedBean
@SessionScoped
public class TieWebSheetPicturesService {

	/** log instance. */
	private static final Logger log = Logger.getLogger(Thread
			.currentThread().getStackTrace()[0].getClassName());

	/**
	 * Constructor.
	 */
	public TieWebSheetPicturesService() {
		log.fine("TieWebSheetPictureService Constructor");
	}

	/**
	 * Return picture to web front end.
	 * 
	 * @return empty (phase is render_response) or real picture ( browser
	 *         request).
	 * @throws IOException exception.
	 */
	public final StreamedContent getPicture() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			log.fine(" return empty picture");
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
			log.fine(" return real picture and remove session");
			return new DefaultStreamedContent(new ByteArrayInputStream(
					picData.getData()));
		}
	}

}
