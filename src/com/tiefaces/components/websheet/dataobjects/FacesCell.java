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
package com.tiefaces.components.websheet.dataobjects;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.PictureData;
import org.primefaces.model.StreamedContent;

import com.tiefaces.components.websheet.TieWebSheetBean;

/**
 * Cell object used for JSF datatable. This object hold an reference to POI cell
 * object
 * 
 * @author Jason Jiang
 */
public class FacesCell {

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("debug: " + msg);
		}
	}

	private String style = ""; // cell web css style  
	private String columnStyle = ""; // column css style
	private int colspan = 1; // cell column span default set to 1
	private int rowspan = 1; // row span default set to 1
	private int columnIndex; // column index in the datatable.
	private boolean invalid = false; // indicate the cell hold invalid data when set to true
	private String errormsg; // hold error message when the cell is invalid
	private String inputType = ""; // data type for input cell. could be text/text area/number etc
	private boolean containPic = false; // indicate the cell hold picture when set to true
	private String pictureId; //picture Id for retrieve picture when containPic = true
	private String pictureStyle = "";

	
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getValidStyle() {
		if (invalid)
			return style + "border-color: red;";
		else
			return style;
	}

	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public int getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public boolean isInvalid() {
		return invalid;
	}
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
	
	public boolean isContainPic() {
		return containPic;
	}

	public void setContainPic(boolean containPic) {
		this.containPic = containPic;
	}

	public String getPictureStyle() {
		return pictureStyle;
	}

	public void setPictureStyle(String pictureStyle) {
		this.pictureStyle = pictureStyle;
	}


	public String getPictureId() {
		return pictureId;
	}

	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}

	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	
	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public String getColumnStyle() {
		return columnStyle;
	}

	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}


}
