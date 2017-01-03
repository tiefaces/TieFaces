/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;


/**
 * static snapshot for current map object.
 * 
 * This is mainly used for save the snapshot of context for each 
 * @author Jason Jiang
 *
 */
public class MapObject {
	
	
	/** The key. */
	private Object key = null;
	
	/** The value. */
	private Object value = null;
	
	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Instantiates a new map object.
	 *
	 * @param pkey
	 *            the key
	 * @param pvalue
	 *            the value
	 */
	public MapObject(final Object pkey, final Object pvalue) {
		super();
		this.key = pkey;
		this.value = pvalue;
	}

	
	
	
	
}
