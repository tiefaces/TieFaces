package com.tiefaces.components.websheet.dataobjects;


/**
 * static snapshot for current map object.
 * 
 * This is mainly used for save the snapshot of context for each 
 * @author Jason Jiang
 *
 */
public class MapObject {
	
	
	private Object key = null;
	private Object value = null;
	
	public Object getKey() {
		return key;
	}
	public Object getValue() {
		return value;
	}
	
	public MapObject(Object key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	
	
	
	
}
