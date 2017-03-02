/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.tiefaces.components.websheet.TieWebSheetBean;
import org.tiefaces.components.websheet.dataobjects.FacesCell;
import org.tiefaces.components.websheet.utility.PicturesUtility;

/**
 * The Class PicturesHelper.
 */
public class PicturesHelper {

	/** The parent. */
	private TieWebSheetBean parent = null;

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(PicturesHelper.class.getName());

	/**
	 * Instantiates a new pictures helper.
	 *
	 * @param pparent
	 *            the parent
	 */
	public PicturesHelper(final TieWebSheetBean pparent) {
		this.parent = pparent;
	}

	/**
	 * initial pictures map for current workbook.
	 */
	public final void loadPicturesMap() {
		PicturesUtility.getPictruesMap(parent.getWb(),
				parent.getPicturesMap());
	}

	/**
	 * Setup faces cell picture charts.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param cell
	 *            the cell
	 * @param fId
	 *            the f id
	 */
	public final void setupFacesCellPictureCharts(final Sheet sheet1,
			final FacesCell fcell, final Cell cell, final String fId) {
		if (parent.getPicturesMap() != null) {
			setupFacesCellPicture(sheet1, fcell, cell, fId);
		}
		if (parent.getCharsData().getChartsMap() != null) {
			setupFacesCellCharts(sheet1, fcell, cell, fId);
		}
	}


	/**
	 * Setup faces cell charts.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param cell
	 *            the cell
	 * @param fId
	 *            the f id
	 */
	private void setupFacesCellCharts(final Sheet sheet1,
			final FacesCell fcell, final Cell cell, final String fId) {
		try {
			String chartId = parent.getCharsData().getChartPositionMap()
					.get(fId);
			if (chartId != null) {
				BufferedImage img = parent.getCharsData().getChartsMap()
						.get(chartId);
				if (img != null) {
					fcell.setContainChart(true);
					fcell.setChartId(chartId);
					fcell.setChartStyle(PicturesUtility.generateChartStyle(
							sheet1, fcell, cell, chartId,
							parent.getCharsData().getChartAnchorsMap()));
				}
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"setupFacesCell Charts error = " + ex.getMessage(), ex);
		}
	}

	/**
	 * Setup faces cell picture.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param cell
	 *            the cell
	 * @param fId
	 *            the f id
	 */
	private void setupFacesCellPicture(final Sheet sheet1,
			final FacesCell fcell, final Cell cell, final String fId) {
		try {
			Picture pic = parent.getPicturesMap().get(fId);
			if (pic != null) {
				fcell.setContainPic(true);
				fcell.setPictureId(fId);
				fcell.setPictureStyle(PicturesUtility
						.generatePictureStyle(sheet1, fcell, cell, pic));
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE,
					"setupFacesCell Picture error = " + ex.getMessage(),
					ex);
		}
	}

}
