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
 * 
 * @author Jason Jiang
 *
 */
public class CollectionObject {

	/** The last collection. */
	@SuppressWarnings("rawtypes")
	private Collection lastCollection;

	/** The last collection index. */
	private int lastCollectionIndex = -1;

	/** The each command. */
	private EachCommand eachCommand;

	/**
	 * Gets the last collection.
	 *
	 * @return the lastCollection
	 */
	@SuppressWarnings("rawtypes")
	public final Collection getLastCollection() {
		return lastCollection;
	}

	/**
	 * Sets the last collection.
	 *
	 * @param plastCollection
	 *            the lastCollection to set
	 */
	@SuppressWarnings("rawtypes")
	public final void setLastCollection(final Collection plastCollection) {
		this.lastCollection = plastCollection;
	}

	/**
	 * Gets the last collection index.
	 *
	 * @return the lastCollectionIndex
	 */
	public final int getLastCollectionIndex() {
		return lastCollectionIndex;
	}

	/**
	 * Sets the last collection index.
	 *
	 * @param plastCollectionIndex
	 *            the lastCollectionIndex to set
	 */
	public final void setLastCollectionIndex(final int plastCollectionIndex) {
		this.lastCollectionIndex = plastCollectionIndex;
	}

	/**
	 * Gets the each command.
	 *
	 * @return the eachCommand
	 */
	public final EachCommand getEachCommand() {
		return eachCommand;
	}

	/**
	 * Sets the each command.
	 *
	 * @param peachCommand
	 *            the eachCommand to set
	 */
	public final void setEachCommand(final EachCommand peachCommand) {
		this.eachCommand = peachCommand;
	}

}
