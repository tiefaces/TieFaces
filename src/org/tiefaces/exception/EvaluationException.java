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
public class EvaluationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new evaluation exception.
	 */
	public EvaluationException() {
		super();
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
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public EvaluationException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param message
	 *            the message
	 */
	public EvaluationException(final String message) {
		super("Evaluation exception = " + message);
	}

	/**
	 * Instantiates a new evaluation exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public EvaluationException(final Throwable cause) {
		super("Evaluation exception = " + cause.getLocalizedMessage(),
				cause);
	}

}
