/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * use show the version of tiefaces.
 * @author Jason Jiang
 *
 */
public class PostConstructApplicationEventListener
		implements SystemEventListener {

	/** logger. */
	private static final Logger LOGGER = Logger.getLogger(
			PostConstructApplicationEventListener.class.getName());

	/**
	 * is listener for source.
	 * @param source
	 *            source.
	 * @return true.
	 * 
	 */
	@Override
	public final boolean isListenerForSource(final Object source) {
		return true;
	}

	/**
	 * process event.
	 * @param event systemevent.
	 * @throws AbortProcessingException abort process exception.
	 */
	@Override
	public final void processEvent(final SystemEvent event)
			{

		LOGGER.log(Level.INFO, "Running on TieFaces {0}",
				AppUtils.getBuildVersion());
	}
	

}
