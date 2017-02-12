/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.dataobjects;

import java.util.Collection;

import org.tiefaces.components.websheet.configuration.EachCommand;


/**
 * static snapshot for current map object.
 * 
 * This is mainly used for save the snapshot of context for each 
 * @author Jason Jiang
 *
 */
public class CollectionObject  {
	
	
	@SuppressWarnings("rawtypes")
	private Collection lastCollection;
	
	private int lastCollectionIndex = -1;
	
	private EachCommand eachCommand;

	/**
	 * @return the lastCollection
	 */
	@SuppressWarnings("rawtypes")
	public final Collection getLastCollection() {
		return lastCollection;
	}

	/**
	 * @param lastCollection the lastCollection to set
	 */
	@SuppressWarnings("rawtypes")
	public final void setLastCollection(Collection lastCollection) {
		this.lastCollection = lastCollection;
	}

	/**
	 * @return the lastCollectionIndex
	 */
	public final int getLastCollectionIndex() {
		return lastCollectionIndex;
	}

	/**
	 * @param lastCollectionIndex the lastCollectionIndex to set
	 */
	public final void setLastCollectionIndex(int lastCollectionIndex) {
		this.lastCollectionIndex = lastCollectionIndex;
	}

	/**
	 * @return the eachCommand
	 */
	public final EachCommand getEachCommand() {
		return eachCommand;
	}

	/**
	 * @param eachCommand the eachCommand to set
	 */
	public final void setEachCommand(EachCommand eachCommand) {
		this.eachCommand = eachCommand;
	}	
	

	
	
	
	
}
