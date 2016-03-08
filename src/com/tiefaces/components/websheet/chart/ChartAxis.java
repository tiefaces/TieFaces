package com.tiefaces.components.websheet.chart;

import java.util.logging.Logger;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;

/**
 * Get chart Axis infomation from Excel xml.
 * 
 * @author Jason Jiang
 *
 */
public class ChartAxis {

	/** log instance. */
	private static final Logger log = Logger.getLogger(Thread.currentThread()
			.getStackTrace()[0].getClassName());
	/** position. */
	private String position;
	/** orientation. */
	private String orientation;
	/** title. */
	private String title;

	public final String getPosition() {
		return position;
	}

	public final void setPosition(final String position) {
		this.position = position;
	}

	public final String getOrientation() {
		return orientation;
	}

	public final void setOrientation(final String orientation) {
		this.orientation = orientation;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(final String title) {
		this.title = title;
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
			log.fine("cannot get axpos from CtCatAx");
		}
		try {
			this.orientation = ctCatAx.getScaling().getOrientation().getVal()
					.toString();
		} catch (Exception ex) {
			log.fine("cannot get scaling.orientation from CtCatAx");
		}
		try {
			this.title = ctCatAx.getTitle().getTx().getRich().getPList().get(0)
					.getRList().get(0).getT();
		} catch (Exception ex) {
			log.fine("cannot get title from CtCatAx");
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
			log.fine("cannot get AxPos from CtValAx");
		}
		try {
			this.orientation = ctValAx.getScaling().getOrientation().getVal()
					.toString();
		} catch (Exception ex) {
			log.fine("cannot get scaling.orientation from CtValAx");
		}
		try {
			this.title = ctValAx.getTitle().getTx().getRich().getPList().get(0)
					.getRList().get(0).getT();
		} catch (Exception ex) {
			log.fine("cannot get title from CtValAx");
		}
	}

}
