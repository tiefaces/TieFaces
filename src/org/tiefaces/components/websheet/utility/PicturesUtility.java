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
import org.apache.poi.ss.usermodel.Cell;
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
import org.tiefaces.components.websheet.dataobjects.FacesCell;

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
	 * 
	 */
	public static void getPictruesMap(final Workbook wb,
			final Map<String, Picture> picMap) {
		if (wb instanceof XSSFWorkbook) {
			getXSSFPictruesMap((XSSFWorkbook) wb, picMap);
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
							sheet.getSheetName(), ctMarker.getRow(),
							ctMarker.getCol());
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
	 * @param fcell
	 *            the fcell
	 * @param cell
	 *            the cell
	 * @param pic
	 *            the pic
	 * @return the string
	 */
	public static String generatePictureStyle(final Sheet sheet1,
			final FacesCell fcell, final Cell cell, final Picture pic) {

		ClientAnchor anchor = pic.getClientAnchor();
		if (anchor != null) {
			AnchorSize anchorSize = getAnchorSize(sheet1, fcell, cell,
					anchor);
			if (anchorSize != null) {
				return "MARGIN-LEFT:"
						+ String.format("%.2f", anchorSize.getPercentLeft())
						+ "%;MARGIN-TOP:"
						+ String.format("%.2f", anchorSize.getPercentTop())
						+ "%;width:" + String.format("%.2f",
								anchorSize.getPercentWidth())
						+ "%;";
			}
		}
		return "";

	}

	/**
	 * Generate chart style.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param cell
	 *            the cell
	 * @param chartId
	 *            the chart id
	 * @param anchorsMap
	 *            the anchors map
	 * @return the string
	 */
	public static String generateChartStyle(final Sheet sheet1,
			final FacesCell fcell, final Cell cell, final String chartId,
			final Map<String, ClientAnchor> anchorsMap) {

		ClientAnchor anchor = anchorsMap.get(chartId);
		if (anchor != null) {
			AnchorSize anchorSize = getAnchorSize(sheet1, fcell, cell,
					anchor);
			if (anchorSize != null) {
				return "MARGIN-LEFT:"
						+ String.format("%.2f", anchorSize.getPercentLeft())
						+ "%;MARGIN-TOP:"
						+ String.format("%.2f", anchorSize.getPercentTop())
						+ "%;width:"
						+ String.format("%.2f",
								anchorSize.getPercentWidth())
						+ "%;height:135%;";
			}
		}
		return "";
	}

	/**
	 * Gets the anchor size.
	 *
	 * @param sheet1
	 *            the sheet 1
	 * @param fcell
	 *            the fcell
	 * @param cell
	 *            the cell
	 * @param anchor
	 *            the anchor
	 * @return the anchor size
	 */
	public static AnchorSize getAnchorSize(final Sheet sheet1,
			final FacesCell fcell, final Cell cell,
			final ClientAnchor anchor) {

		if (!(sheet1 instanceof XSSFSheet)) {
			return null;
		}
		double picWidth = 0.0;
		double picHeight = 0.0;
		int left = anchor.getDx1()
				/ org.apache.poi.util.Units.EMU_PER_PIXEL;
		int top = (int) (anchor.getDy1()
				/ org.apache.poi.util.Units.EMU_PER_PIXEL
				/ WebSheetUtility.PICTURE_HEIGHT_ADJUST);
		int right = anchor.getDx2()
				/ org.apache.poi.util.Units.EMU_PER_PIXEL;
		int bottom = (int) (anchor.getDy2()
				/ org.apache.poi.util.Units.EMU_PER_PIXEL
				/ WebSheetUtility.PICTURE_HEIGHT_ADJUST);

		double cellWidth = 0.0;
		double cellHeight = 0.0;

		if ((cell != null) && (fcell != null)) {
			for (int col = cell.getColumnIndex(); col < cell
					.getColumnIndex() + fcell.getColspan(); col++) {
				cellWidth += sheet1.getColumnWidthInPixels(col);
			}
			double lastCellWidth = sheet1.getColumnWidthInPixels(
					cell.getColumnIndex() + fcell.getColspan() - 1);

			for (int rowIndex = cell.getRowIndex(); rowIndex < cell
					.getRowIndex() + fcell.getRowspan(); rowIndex++) {
				cellHeight += WebSheetUtility.pointsToPixels(
						sheet1.getRow(rowIndex).getHeightInPoints());
			}
			double lastCellHeight = WebSheetUtility.pointsToPixels(sheet1
					.getRow(cell.getRowIndex() + fcell.getRowspan() - 1)
					.getHeightInPoints());

			picWidth = cellWidth - lastCellWidth + right - left;
			picHeight = cellHeight - lastCellHeight + bottom - top;
		} else {
			for (short col = anchor.getCol1(); col < anchor
					.getCol2(); col++) {
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
		}

		return new AnchorSize(left, top, (int) picWidth, (int) picHeight,
				cellWidth, cellHeight);

	}

}
