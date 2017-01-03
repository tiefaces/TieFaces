
package org.tiefaces.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class PostConstructApplicationEventListener
		implements SystemEventListener {

	private static final Logger log = Logger.getLogger(
			Thread.currentThread().getStackTrace()[0].getClassName());

	public boolean isListenerForSource(Object source) {
		return true;
	}

	public void processEvent(SystemEvent event)
			throws AbortProcessingException {

		log.log(Level.INFO, "Running on TieFaces {0}",
				getBuildVersion());
	}
	
	private String getBuildVersion() {
		//return "1.0.8";
		return getClass().getPackage().getImplementationVersion();
	}
}
