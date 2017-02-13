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
public class IllegalChartException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new evaluation exception.
	 */
	public IllegalChartException() {
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
	public IllegalChartException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 */
	public IllegalChartException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public IllegalChartException(final Throwable cause) {
		super(cause);
	}

 
}
