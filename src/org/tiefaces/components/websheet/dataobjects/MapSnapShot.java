/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * static snapshot for current map object.
 * 
 * This is mainly used for save the snapshot of context for each
 * 
 * @author Jason Jiang
 *
 */
public class MapSnapShot {

	/** The snap list. */
	private List<MapObject> snapList = new ArrayList<MapObject>();

	/**
	 * Instantiates a new map snap shot.
	 *
	 * @param context
	 *            the context
	 */
	public MapSnapShot(final Map<String, Object> context) {

		for (Map.Entry<String, Object> entry : context.entrySet()) {
			snapList.add(new MapObject(entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * Gets the snap list.
	 *
	 * @return the snap list
	 */
	public final List<MapObject> getSnapList() {
		return snapList;
	}

}
