/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.exception;

/**
 * Exception Object.
 *
 * @author Jason Jiang
 */
public class AddRowException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new evaluation exception.
	 */
	public AddRowException() {
		super();
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public AddRowException(final String message, final Throwable cause) {
		super(message, cause);
	}

    /**
     * Instantiates a new evaluation exception.
     *
     * @param message
     *            the message
     */
    public AddRowException(final String message) {
        super(message);
    }

 
}
