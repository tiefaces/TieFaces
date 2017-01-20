package org.tiefaces.components.websheet;

import java.io.Serializable;

import org.tiefaces.components.websheet.chart.ChartHelper;
import org.tiefaces.components.websheet.service.CellHelper;
import org.tiefaces.components.websheet.service.PicturesHelper;
import org.tiefaces.components.websheet.service.ValidationHandler;
import org.tiefaces.components.websheet.service.WebSheetLoader;

/**
 * Bean Helper collections.
 * 
 * @author Jason Jiang
 *
 */
public class TieWebSheetBeanHelper {
	/** parent. */
	private TieWebSheetBean parent;
	/** hold instance for loader class. */
	private WebSheetLoader webSheetLoader;
	/** hold instance for cell helper class. */
	private CellHelper cellHelper;
	/** hold instance for picture helper class. */
	private PicturesHelper picHelper;
	/** hold instance for validation handler class. */
	private ValidationHandler validationHandler;
	/** hold instance for chart helper class. */
	private ChartHelper chartHelper;

	/**
	 * assign bean object into helper.
	 * 
	 * @param pparent
	 *            parent bean.
	 */
	public TieWebSheetBeanHelper(final TieWebSheetBean pparent) {
		this.parent = pparent;
	}

	/**
	 * @return the webSheetLoader
	 */
	public final WebSheetLoader getWebSheetLoader() {
		this.webSheetLoader = new WebSheetLoader(parent);

		return webSheetLoader;
	}

	/**
	 * @param pwebSheetLoader
	 *            the webSheetLoader to set
	 */
	public final void
			setWebSheetLoader(final WebSheetLoader pwebSheetLoader) {
		this.webSheetLoader = pwebSheetLoader;
	}

	/**
	 * @return the cellHelper
	 */
	public final CellHelper getCellHelper() {
		if ((this.cellHelper == null) && (this.parent != null)) {
			this.cellHelper = new CellHelper(parent);
		}
		return cellHelper;
	}

	/**
	 * @param pcellHelper
	 *            the cellHelper to set
	 */
	public final void setCellHelper(final CellHelper pcellHelper) {
		this.cellHelper = pcellHelper;
	}

	/**
	 * @return the picHelper
	 */
	public final PicturesHelper getPicHelper() {
		if ((this.picHelper == null) && (this.parent != null)) {
			this.picHelper = new PicturesHelper(parent);
		}
		return picHelper;
	}

	/**
	 * @param ppicHelper
	 *            the picHelper to set
	 */
	public final void setPicHelper(final PicturesHelper ppicHelper) {
		this.picHelper = ppicHelper;
	}

	/**
	 * @return the validationHandler
	 */
	public final ValidationHandler getValidationHandler() {
		if ((this.validationHandler == null) && (this.parent != null)) {
			this.validationHandler = new ValidationHandler(parent);
		}
		return validationHandler;
	}

	/**
	 * @param pvalidationHandler
	 *            the validationHandler to set
	 */
	public final void setValidationHandler(
			final ValidationHandler pvalidationHandler) {
		this.validationHandler = pvalidationHandler;
	}

	/**
	 * @return the chartHelper
	 */
	public final ChartHelper getChartHelper() {
		if ((this.chartHelper == null) && (this.parent != null)) {
			this.chartHelper = new ChartHelper(parent);
		}
		return chartHelper;
	}

	/**
	 * @param pchartHelper
	 *            the chartHelper to set
	 */
	public final void setChartHelper(final ChartHelper pchartHelper) {
		this.chartHelper = pchartHelper;
	}

}