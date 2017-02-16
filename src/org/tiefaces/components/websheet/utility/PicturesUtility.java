/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.utility;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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

/**
 * The Class PicturesHelper.
 */
public final class PicturesUtility {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(PicturesUtility.class.getName());

	/**
	 * hide constructor.
	 */
	private PicturesUtility() {
		// not called
	}

	/**
	 * Gets the pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @param picMap
	 *            the pic map
	 */
	public static void getPictruesMap(final Workbook wb,
			final Map<String, Picture> picMap) {
		if (wb instanceof HSSFWorkbook) {
			getHSSFPictruesMap((HSSFWorkbook) wb, picMap);
		} else if (wb instanceof XSSFWorkbook) {
			getXSSFPictruesMap((XSSFWorkbook) wb, picMap);
		}
		return;
	}

	/**
	 * Gets the HSSF pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @param picMap
	 *            the pic map
	 */
	private static void getHSSFPictruesMap(final HSSFWorkbook wb,
			final Map<String, Picture> picMap) {

		picMap.clear();
		List<HSSFPictureData> pictures = wb.getAllPictures();
		if (pictures.isEmpty()) {
			return;
		}
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			HSSFSheet sheet = wb.getSheetAt(i);
			for (HSSFShape shape : sheet.getDrawingPatriarch()
					.getChildren()) {
				HSSFClientAnchor anchor = (HSSFClientAnchor) shape
						.getAnchor();
				if (shape instanceof HSSFPicture) {
					HSSFPicture pic = (HSSFPicture) shape;
					String picIndex = WebSheetUtility.getFullCellRefName(
							sheet.getSheetName(), anchor.getCol1(),
							anchor.getRow1());
					picMap.put(picIndex, pic);
				}
			}
		}
		return;

	}

	/**
	 * Gets the XSSF pictrues map.
	 *
	 * @param wb
	 *            the wb
	 * @param picMap
	 *            the pic map
	 */
	private static void getXSSFPictruesMap(final XSSFWorkbook wb,
			final Map<String, Picture> picMap) {

		picMap.clear();
		List<XSSFPictureData> pictures = wb.getAllPictures();
		if (pictures.isEmpty()) {
			return;
		}
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			for (POIXMLDocumentPart dr : sheet.getRelations()) {
				try {
					indexPictureInMap(picMap, sheet, dr);
				} catch (Exception ex) {
					LOG.log(Level.SEVERE, "Load Picture error = "
							+ ex.getLocalizedMessage(), ex);
				}
			}
		}

		return;

	}

	/**
	 * save pciture in map with index.
	 * 
	 * @param picMap
	 *            pciture map.
	 * @param sheet
	 *            sheet.
	 * @param dr
	 *            documentme part.
	 */
	private static void indexPictureInMap(final Map<String, Picture> picMap,
			final XSSFSheet sheet, final POIXMLDocumentPart dr) {
		if (dr instanceof XSSFDrawing) {
			XSSFDrawing drawing = (XSSFDrawing) dr;
			List<XSSFShape> shapes = drawing.getShapes();
			for (XSSFShape shape : shapes) {
				if (shape instanceof XSSFPicture) {
					XSSFPicture pic = (XSSFPicture) shape;
					XSSFClientAnchor anchor = pic.getPreferredSize();
					CTMarker ctMarker = anchor.getFrom();
					String picIndex = WebSheetUtility.getFullCellRefName(
							sheet.getSheetName(), ctMarker.getCol(),
							ctMarker.getRow());
					picMap.put(picIndex, pic);
				}
			}
		}
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
		return "PADDING-LEFT:" + anchorSize.getLeft() + "px;PADDING-TOP:"
				+ anchorSize.getTop() + "px;width:" + anchorSize.getWidth()
				+ "px; height:" + anchorSize.getHeight() + "px;";

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
			final String chartId,
			final Map<String, ClientAnchor> anchorsMap) {

		ClientAnchor anchor = anchorsMap.get(chartId);
		if (anchor != null) {
			AnchorSize anchorSize = getAnchorSize(sheet1, anchor);
			return "PADDING-LEFT:" + anchorSize.getLeft()
					+ "px;PADDING-TOP:" + anchorSize.getTop() + "px;width:"
					+ anchorSize.getWidth() + "px; height:"
					+ anchorSize.getHeight() + "px;";

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
			left = WebSheetUtility.widthUnits2Pixel(sheet1
					.getColumnWidth(anchor.getCol1()) * anchor.getDx1()
					/ WebSheetUtility.TOTAL_COLUMN_COORDINATE_POSITIONS);
			if (sheet1.getRow(anchor.getRow1()) != null) {
				top = WebSheetUtility.pointsToPixels(sheet1
						.getRow(anchor.getRow1()).getHeightInPoints()
						* anchor.getDy1()
						/ WebSheetUtility.TOTAL_ROW_COORDINATE_POSITIONS);
			}
			right = WebSheetUtility.widthUnits2Pixel(sheet1
					.getColumnWidth(anchor.getCol2()) * anchor.getDx2()
					/ WebSheetUtility.TOTAL_COLUMN_COORDINATE_POSITIONS);
			if (sheet1.getRow(anchor.getRow2()) != null) {
				bottom = WebSheetUtility.pointsToPixels(sheet1
						.getRow(anchor.getRow2()).getHeightInPoints()
						* anchor.getDy2()
						/ WebSheetUtility.TOTAL_ROW_COORDINATE_POSITIONS);
			}
		} else if (sheet1 instanceof XSSFSheet) {
			left = WebSheetUtility.widthUnits2Pixel(WebSheetUtility
					.millimetres2WidthUnits((double) anchor.getDx1()
							/ WebSheetUtility.EMU_PER_MM));
			top = WebSheetUtility.pointsToPixels((double) anchor.getDy1()
					/ WebSheetUtility.EMU_PER_POINTS);
			right = WebSheetUtility.widthUnits2Pixel(WebSheetUtility
					.millimetres2WidthUnits((double) anchor.getDx2()
							/ WebSheetUtility.EMU_PER_MM));
			bottom = WebSheetUtility.pointsToPixels((double) anchor.getDy2()
					/ WebSheetUtility.EMU_PER_POINTS);
		}

		for (short col = anchor.getCol1(); col < anchor.getCol2(); col++) {
			picWidth += sheet1.getColumnWidthInPixels(col);
		}
		for (int rowindex = anchor.getRow1(); rowindex < anchor
				.getRow2(); rowindex++) {
			Row row = sheet1.getRow(rowindex);
			if (row != null) {
				picHeight += WebSheetUtility
						.pointsToPixels(row.getHeightInPoints());
			}
		}
		picWidth += right - left;
		picHeight += bottom - top;

		return new AnchorSize(left, top, (int) picWidth, (int) picHeight);

	}

}
