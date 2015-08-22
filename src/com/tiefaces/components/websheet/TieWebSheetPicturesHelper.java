/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tiefaces.components.websheet;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
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
import org.primefaces.model.DefaultStreamedContent;

import com.tiefaces.components.websheet.dataobjects.FacesCell;

public class TieWebSheetPicturesHelper {

	private TieWebSheetBean parent = null;

	private static boolean debug = true;

	private static void debug(String msg) {
		if (debug) {
			System.out.println("TieWebSheetImageHelper: " + msg);
		}
	}

	public TieWebSheetPicturesHelper(TieWebSheetBean parent) {
		this.parent = parent;
		debug("TieWebSheetBean Constructor");
	}

	public void loadPictureMap() {

		parent.setPicturesMap(this.getPictruesMap(parent.getWb()));
	}

	private Map<String, Picture> getPictruesMap(Workbook wb) {
		if (wb instanceof HSSFWorkbook) {
			return getHSSFPictruesMap((HSSFWorkbook) wb);
		} else if (wb instanceof XSSFWorkbook) {
			return getXSSFPictruesMap((XSSFWorkbook) wb);
		}
		return null;
	}

	private Map<String, Picture> getHSSFPictruesMap(HSSFWorkbook wb) {

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
						// int pictureIndex = pic.getPictureIndex() - 1;
						// HSSFPictureData picData = pictures.get(pictureIndex);
						HSSFPictureData picData = pic.getPictureData();
						String picIndex = wb.getSheetName(i) + "$"
								+ String.valueOf(anchor.getCol1()) + "$"
								+ String.valueOf(anchor.getRow1()) ;
						picMap.put(picIndex, pic);
					}
				}
			}
			debug(" getHSSFPicturesMap = " + picMap);
			return picMap;
		} else {
			return null;
		}
	}

	private Map<String, Picture> getXSSFPictruesMap(XSSFWorkbook wb) {
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
							XSSFPicture pic = (XSSFPicture) shape;
							XSSFClientAnchor anchor = pic.getPreferredSize();
							CTMarker ctMarker = anchor.getFrom();
							String picIndex = wb.getSheetName(i) + "$"
									+ ctMarker.getCol() + "$"
									+ ctMarker.getRow() ;
							picMap.put(picIndex, pic);
						}
					}
				}
			}

			return picMap;
		} else {
			return null;
		}
	}

	public void setupFacesCellPictures(Sheet sheet1, FacesCell fcell,
			String pictureId) {
		if (parent.getPicturesMap() != null) {
			Picture pic = (Picture) parent.getPicturesMap().get(pictureId);
			if (pic != null) {

				debug(" pic dimension = " + pic.getImageDimension()
						+ " perfersize = " + pic.getPreferredSize());
				fcell.setContainPic(true);
				fcell.setPictureId(pictureId);
				fcell.setPictureStyle(generatePictureStyle(sheet1, pic));
			}
		}
	}

	private String generatePictureStyle(Sheet sheet1, Picture pic) {

		ClientAnchor anchor = pic.getPreferredSize();
		float picWidth = 0;
		float picHeight = 0;
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;

		if (sheet1 instanceof HSSFSheet) {
			left = TieWebSheetUtility
					.widthUnits2Pixel((sheet1.getColumnWidth(anchor.getCol1())
							* anchor.getDx1() / TieWebSheetUtility.TOTAL_COLUMN_COORDINATE_POSITIONS));
			if (sheet1.getRow(anchor.getRow1()) != null)
				top = TieWebSheetUtility
						.pointsToPixels((sheet1.getRow(anchor.getRow1())
								.getHeightInPoints() * anchor.getDy1() / TieWebSheetUtility.TOTAL_ROW_COORDINATE_POSITIONS));
			right = TieWebSheetUtility
					.widthUnits2Pixel((sheet1.getColumnWidth(anchor.getCol2())
							* anchor.getDx2() / TieWebSheetUtility.TOTAL_COLUMN_COORDINATE_POSITIONS));
			if (sheet1.getRow(anchor.getRow2()) != null)
				bottom = TieWebSheetUtility
						.pointsToPixels((sheet1.getRow(anchor.getRow2())
								.getHeightInPoints() * anchor.getDy2() / TieWebSheetUtility.TOTAL_ROW_COORDINATE_POSITIONS));
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
		String style = "PADDING-LEFT:" + left + "px;PADDING-TOP:" + top
				+ "px;width:" + (int) picWidth + "px; height:"
				+ (int) picHeight + "px;";
		return style;
	}

}
