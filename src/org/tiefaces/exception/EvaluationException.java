/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.exception;

/**
 * Exception Object.
 *
 * @author Jason Jiang
 */
public class EvaluationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new evaluation exception.
	 */
	public EvaluationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            the enable suppression
	 * @param writableStackTrace
	 *            the writable stack trace
	 */
	public EvaluationException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public EvaluationException(final String message, final Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 */
	public EvaluationException(final String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public EvaluationException(final Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

 
}
