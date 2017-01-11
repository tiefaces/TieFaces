/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.tiefaces.components.websheet.dataobjects.AnchorSize;
import org.tiefaces.components.websheet.utility.TieWebSheetUtility;

/**
 * The Class PicturesHelper.
 */
public final class PicturesUtility {

	/**
	 * hide constructor.
	 */
	private PicturesUtility() {
		// not called
	}

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(PicturesUtility.class.getName());

	/**
	 * Gets the pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @return the pictrues map
	 */
	public static Map<String, Picture> getPictruesMap(final Workbook wb) {
		try {
			if (wb instanceof HSSFWorkbook) {
				return getHSSFPictruesMap((HSSFWorkbook) wb);
			} else if (wb instanceof XSSFWorkbook) {
				return getXSSFPictruesMap((XSSFWorkbook) wb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Web Form getPictruesMap Error Exception = "
					+ e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * Gets the HSSF pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @return the HSSF pictrues map
	 */
	private static Map<String, Picture> getHSSFPictruesMap(
			final HSSFWorkbook wb) {

		Map<String, Picture> picMap = new HashMap<String, Picture>();
		List<HSSFPictureData> pictures = wb.getAllPictures();
		if (pictures.size() != 0) {
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				HSSFSheet sheet = wb.getSheetAt(i);
				for (HSSFShape shape : sheet.getDrawingPatriarch()
						.getChildren()) {
					HSSFClientAnchor anchor = (HSSFClientAnchor) shape
							.getAnchor();
					if (shape instanceof HSSFPicture) {
						HSSFPicture pic = (HSSFPicture) shape;
						String picIndex = TieWebSheetUtility
								.getFullCellRefName(sheet.getSheetName(),
										anchor.getCol1(), anchor.getRow1());
						picMap.put(picIndex, pic);
					}
				}
			}
			LOG.fine(" getHSSFPicturesMap = " + picMap);
			return picMap;
		}

		return null;

	}

	/**
	 * Gets the XSSF pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @return the XSSF pictrues map
	 */
	private static Map<String, Picture> getXSSFPictruesMap(
			final XSSFWorkbook wb) {
		Map<String, Picture> picMap = new HashMap<String, Picture>();

		List<XSSFPictureData> pictures = wb.getAllPictures();
		if (pictures.size() != 0) {
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				XSSFSheet sheet = wb.getSheetAt(i);

				for (POIXMLDocumentPart dr : sheet.getRelations()) {
					if (dr instanceof XSSFDrawing) {
						XSSFDrawing drawing = (XSSFDrawing) dr;
						List<XSSFShape> shapes = drawing.getShapes();
						for (XSSFShape shape : shapes) {
							if (shape instanceof XSSFPicture) {
								XSSFPicture pic = (XSSFPicture) shape;
								XSSFClientAnchor anchor = pic
										.getPreferredSize();
								CTMarker ctMarker = anchor.getFrom();
								String picIndex = TieWebSheetUtility
										.getFullCellRefName(
												sheet.getSheetName(),
												ctMarker.getCol(),
												ctMarker.getRow());
								picMap.put(picIndex, pic);
							}
						}
					}
				}
			}

			return picMap;
		}
		return null;

	}

	/**
	 * Generate picture style.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param pic
	 *            the pic
	 * @return the string
	 */
	public static String generatePictureStyle(final Sheet sheet1,
			final Picture pic) {

		ClientAnchor anchor = pic.getPreferredSize();
		AnchorSize anchorSize = getAnchorSize(sheet1, anchor);
		String style = "PADDING-LEFT:" + anchorSize.getLeft()
				+ "px;PADDING-TOP:" + anchorSize.getTop() + "px;width:"
				+ anchorSize.getWidth() + "px; height:"
				+ anchorSize.getHeight() + "px;";
		return style;
	}

	/**
	 * Generate chart style.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param chartId
	 *            the chart id
	 * @param anchorsMap
	 *            the anchors map
	 * @return the string
	 */
	public static String generateChartStyle(final Sheet sheet1,
			final String chartId, final Map<String, ClientAnchor> anchorsMap) {

		ClientAnchor anchor = anchorsMap.get(chartId);
		if (anchor != null) {
			AnchorSize anchorSize = getAnchorSize(sheet1, anchor);
			String style = "PADDING-LEFT:" + anchorSize.getLeft()
					+ "px;PADDING-TOP:" + anchorSize.getTop() + "px;width:"
					+ anchorSize.getWidth() + "px; height:"
					+ anchorSize.getHeight() + "px;";
			return style;
		}
		return "";
	}

	/**
	 * Gets the anchor size.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param anchor
	 *            the anchor
	 * @return the anchor size
	 */
	public static AnchorSize getAnchorSize(final Sheet sheet1,
			final ClientAnchor anchor) {

		float picWidth = 0;
		float picHeight = 0;
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;

		if (sheet1 instanceof HSSFSheet) {
			left = TieWebSheetUtility
					.widthUnits2Pixel((sheet1.getColumnWidth(anchor
							.getCol1()) * anchor.getDx1() / TieWebSheetUtility.TOTAL_COLUMN_COORDINATE_POSITIONS));
			if (sheet1.getRow(anchor.getRow1()) != null) {
				top = TieWebSheetUtility
						.pointsToPixels((sheet1.getRow(anchor.getRow1())
								.getHeightInPoints() * anchor.getDy1() / TieWebSheetUtility.TOTAL_ROW_COORDINATE_POSITIONS));
			}
			right = TieWebSheetUtility
					.widthUnits2Pixel((sheet1.getColumnWidth(anchor
							.getCol2()) * anchor.getDx2() / TieWebSheetUtility.TOTAL_COLUMN_COORDINATE_POSITIONS));
			if (sheet1.getRow(anchor.getRow2()) != null) {
				bottom = TieWebSheetUtility
						.pointsToPixels((sheet1.getRow(anchor.getRow2())
								.getHeightInPoints() * anchor.getDy2() / TieWebSheetUtility.TOTAL_ROW_COORDINATE_POSITIONS));
			}
		} else if (sheet1 instanceof XSSFSheet) {
			left = TieWebSheetUtility.widthUnits2Pixel(TieWebSheetUtility
					.millimetres2WidthUnits(anchor.getDx1()
							/ TieWebSheetUtility.EMU_PER_MM));
			top = TieWebSheetUtility
					.pointsToPixels((anchor.getDy1() / TieWebSheetUtility.EMU_PER_POINTS));
			right = TieWebSheetUtility.widthUnits2Pixel(TieWebSheetUtility
					.millimetres2WidthUnits(anchor.getDx2()
							/ TieWebSheetUtility.EMU_PER_MM));
			bottom = TieWebSheetUtility
					.pointsToPixels((anchor.getDy2() / TieWebSheetUtility.EMU_PER_POINTS));
		}

		for (short col = anchor.getCol1(); col < anchor.getCol2(); col++) {
			picWidth += sheet1.getColumnWidthInPixels(col);
		}
		for (int rowindex = anchor.getRow1(); rowindex < anchor.getRow2(); rowindex++) {
			Row row = sheet1.getRow(rowindex);
			if (row != null) {
				picHeight += TieWebSheetUtility.pointsToPixels(row
						.getHeightInPoints());
			}
		}
		picWidth += right - left;
		picHeight += bottom - top;

		return new AnchorSize(left, top, (int) picWidth, (int) picHeight);

	}

}
