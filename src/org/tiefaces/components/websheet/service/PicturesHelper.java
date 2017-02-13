/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
	private static final Logger LOG = Logger.getLogger(PicturesHelper.class
			.getName());

	/**
	 * Instantiates a new pictures helper.
	 *
	 * @param pparent
	 *            the parent
	 */
	public PicturesHelper(final TieWebSheetBean pparent) {
		this.parent = pparent;
		LOG.fine("TieWebSheetBean Constructor");
	}

	/**
	 * Gets the pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @return the pictrues map
	 */
	public final void getPictruesMap(final Workbook wb, final Map<String, Picture> picMap) {
		PicturesUtility.getPictruesMap(wb, picMap);
	}

	/**
	 * Setup faces cell picture charts.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param fId
	 *            the f id
	 */
	public final void setupFacesCellPictureCharts(final Sheet sheet1,
			final FacesCell fcell, final String fId) {
		if (parent.getPicturesMap() != null) {
			setupFacesCellPicture(sheet1, fcell, fId);
		}
		if (parent.getCharsData().getChartsMap() != null) {
			setupFacesCellCharts(sheet1, fcell, fId);
		}
	}

	/**
	 * Setup faces cell charts.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param fId
	 *            the f id
	 */
	private void setupFacesCellCharts(final Sheet sheet1,
			final FacesCell fcell, final String fId) {
		try {
			String chartId = parent.getCharsData().getChartPositionMap().get(fId);
			if (chartId != null) {
				BufferedImage img =
						 parent.getCharsData().getChartsMap().get(
								chartId);
				if (img != null) {
					LOG.fine(" pic dimension width = " + img.getWidth()
							+ " height = " + img.getHeight());
					fcell.setContainChart(true);
					fcell.setChartId(chartId);
					fcell.setChartStyle(PicturesUtility
							.generateChartStyle(sheet1, chartId, parent
									.getCharsData().getChartAnchorsMap()));
				}
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "setupFacesCell Charts error = "
					+ ex.getMessage(), ex);
		}
	}

	/**
	 * Setup faces cell picture.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param fId
	 *            the f id
	 */
	private void setupFacesCellPicture(final Sheet sheet1,
			final FacesCell fcell, final String fId) {
		try {
			Picture pic =  parent.getPicturesMap().get(fId);
			if (pic != null) {
				LOG.fine(" pic dimension = " + pic.getImageDimension()
						+ " perfersize = " + pic.getPreferredSize());
				fcell.setContainPic(true);
				fcell.setPictureId(fId);
				fcell.setPictureStyle(PicturesUtility
						.generatePictureStyle(sheet1, pic));
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "setupFacesCell Picture error = "
					+ ex.getMessage(), ex);
		}
	}

}
