package org.tiefaces.components.websheet;

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
	 * @param parent
	 *            parent bean.
	 */
	public TieWebSheetBeanHelper(final TieWebSheetBean parent) {
		this.webSheetLoader = new WebSheetLoader(parent);
		this.cellHelper = new CellHelper(parent);
		this.validationHandler = new ValidationHandler(parent);
		this.picHelper = new PicturesHelper(parent);
		this.chartHelper = new ChartHelper(parent);
	}

	/**
	 * @return the webSheetLoader
	 */
	public final WebSheetLoader getWebSheetLoader() {
		return webSheetLoader;
	}

	/**
	 * @param pwebSheetLoader
	 *            the webSheetLoader to set
	 */
	public final void setWebSheetLoader(final WebSheetLoader pwebSheetLoader) {
		this.webSheetLoader = pwebSheetLoader;
	}

	/**
	 * @return the cellHelper
	 */
	public final CellHelper getCellHelper() {
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