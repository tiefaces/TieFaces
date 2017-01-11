/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.chart;

import java.util.logging.Logger;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.tiefaces.components.websheet.configuration.CellControlsHelper;

/**
 * Get chart Axis infomation from Excel xml.
 * 
 * @author Jason Jiang
 *
 */
public class ChartAxis {

	/** logger. */
	private static final Logger LOG = Logger.getLogger(
			CellControlsHelper.class.getName());
	
	/** position. */
	private String position;
	/** orientation. */
	private String orientation;
	/** title. */
	private String title;

	/**
	 * get position.
	 * @return position.
	 */
	public final String getPosition() {
		return position;
	}

	/**
	 * set position.
	 * @param pPosition position.
	 */
	public final void setPosition(final String pPosition) {
		this.position = pPosition;
	}

	/**
	 * get orientation.
	 * @return orientation.
	 */
	public final String getOrientation() {
		return orientation;
	}

	/**
	 * set orientation.
	 * @param pOrientation orientation.
	 */
	public final void setOrientation(final String pOrientation) {
		this.orientation = pOrientation;
	}

	/**
	 * get title.
	 * @return title.
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * set title.
	 * @param pTitle title.
	 */
	public final void setTitle(final String pTitle) {
		this.title = pTitle;
	}

	/**
	 * Constructor from CTCatAx.
	 * 
	 * @param ctCatAx
	 *            the CTCatAx object from xml.
	 */
	public ChartAxis(final CTCatAx ctCatAx) {
		super();
		try {
			this.position = ctCatAx.getAxPos().getVal().toString();
		} catch (Exception ex) {
			LOG.fine("cannot get axpos from CtCatAx");
		}
		try {
			this.orientation = ctCatAx.getScaling().getOrientation().getVal()
					.toString();
		} catch (Exception ex) {
			LOG.fine("cannot get scaling.orientation from CtCatAx");
		}
		try {
			this.title = ctCatAx.getTitle().getTx().getRich().getPList().get(0)
					.getRList().get(0).getT();
		} catch (Exception ex) {
			LOG.fine("cannot get title from CtCatAx");
		}
	}
/**
 * Constructor from CTValAx.
 * @param ctValAx CTValAx object from xml.
 */
	public ChartAxis(final CTValAx ctValAx) {
		super();
		try {
			this.position = ctValAx.getAxPos().getVal().toString();
		} catch (Exception ex) {
			LOG.fine("cannot get AxPos from CtValAx");
		}
		try {
			this.orientation = ctValAx.getScaling().getOrientation().getVal()
					.toString();
		} catch (Exception ex) {
			LOG.fine("cannot get scaling.orientation from CtValAx");
		}
		try {
			this.title = ctValAx.getTitle().getTx().getRich().getPList().get(0)
					.getRList().get(0).getT();
		} catch (Exception ex) {
			LOG.fine("cannot get title from CtValAx");
		}
	}

}
