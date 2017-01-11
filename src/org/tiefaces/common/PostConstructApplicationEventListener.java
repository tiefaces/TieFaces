
package org.tiefaces.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.tiefaces.components.websheet.configuration.CellControlsHelper;

public class PostConstructApplicationEventListener
		implements SystemEventListener {

	/** logger. */
	private static final Logger LOGGER = Logger.getLogger(
			PostConstructApplicationEventListener.class.getName());

	public boolean isListenerForSource(Object source) {
		return true;
	}

	public void processEvent(SystemEvent event)
			throws AbortProcessingException {

		LOGGER.log(Level.INFO, "Running on TieFaces {0}",
				AppUtils.getBuildVersion());
	}
	

}
