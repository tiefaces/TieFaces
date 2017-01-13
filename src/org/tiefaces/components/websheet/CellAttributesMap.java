/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet;

import java.util.List;
import java.util.Map;

import org.tiefaces.components.websheet.dataobjects.CellFormAttributes;

/**
 * Cell Attributes Map. Contain several related maps for cell.
 * 
 * @author Jason Jiang
 *
 */
public class CellAttributesMap {
	/**
	 * hold comments for the current sheet config form. key is $$ - for normal
	 * comment $ - empty method comment $xxx -- method comment while xxx is
	 * method name. (now only one method exist: init). i.e. comment could be
	 * $init{department.name} , so here init is the method name. value is the
	 * comments without tie commands.
	 */
	private Map<String, Map<String, String>> templateCommentMap;

	/**
	 * hold input type for input control widget. key is
	 * sheetName!$rowindex$columnindex value is the control type. e.g. dropdown
	 * radiobox calendar
	 */
	private Map<String, String> cellInputType;

	/**
	 * hold attributes for input control widget. key is
	 * sheetName!$rowindex$columnindex value is the list of attributes.
	 */
	private Map<String, List<CellFormAttributes>> cellInputAttributes;

	/**
	 * hold attributes for selectItems of dropdown list or radio box. key is
	 * sheetName!$rowindex$columnindex value is the map of attributes.
	 */
	private Map<String, Map<String, String>> cellSelectItemsAttributes;

	/**
	 * hold date pattern for calendar control widget. key is
	 * sheetName!$rowindex$columnindex value is the date pattern. date pattern
	 * is necessary for covert date to string
	 */

	private Map<String, String> cellDatePattern;

	/**
	 * hold attributes for validate process. key is
	 * sheetName!$rowindex$columnindex value is the list of attributes.
	 * 
	 * which .value = rule .message = error message.
	 */
	private Map<String, List<CellFormAttributes>> cellValidateAttributes;

	/**
	 * constructor.
	 * 
	 * @param pTemplateCommentMap
	 *            tempdate comment map.
	 * @param pCellInputType
	 *            cell input type.
	 * @param pCellInputAttributes
	 *            cell input attributes.
	 * @param pCellSelectItemsAttributes
	 *            cell select items attributes.
	 * @param pCellDatePattern
	 *            cell date pattern.
	 * @param pCellValidateAttributes
	 *            cell validate attributes.
	 */
	public CellAttributesMap(
			final Map<String, Map<String, String>> pTemplateCommentMap,
			final Map<String, String> pCellInputType,
			final Map<String, List<CellFormAttributes>> pCellInputAttributes,
			final Map<String, Map<String, String>> pCellSelectItemsAttributes,
			final Map<String, String> pCellDatePattern,
			final Map<String, List<CellFormAttributes>> pCellValidateAttributes) {
		this.templateCommentMap = pTemplateCommentMap;
		this.cellInputType = pCellInputType;
		this.cellInputAttributes = pCellInputAttributes;
		this.cellSelectItemsAttributes = pCellSelectItemsAttributes;
		this.cellDatePattern = pCellDatePattern;
		this.cellValidateAttributes = pCellValidateAttributes;
	}

	/**
	 * clear all the related maps.
	 */
	public final void clear() {
		if (this.templateCommentMap != null) {
			this.templateCommentMap.clear();
		}
		if (this.cellDatePattern != null) {
			this.cellDatePattern.clear();
		}
		if (this.cellInputAttributes != null) {
			this.cellInputAttributes.clear();
		}
		if (this.cellInputType != null) {
			this.cellInputType.clear();
		}
		if (this.cellSelectItemsAttributes != null) {
			this.cellSelectItemsAttributes.clear();
		}

	}

	/**
	 * return templatecommentmap.
	 * 
	 * @return templatecommentmap.
	 */
	public final Map<String, Map<String, String>> getTemplateCommentMap() {
		return templateCommentMap;
	}

	/**
	 * return cellinputtype.
	 * 
	 * @return cellinputtype.
	 */
	public final Map<String, String> getCellInputType() {
		return cellInputType;
	}

	/**
	 * return cellinputattributes.
	 * 
	 * @return cellinputattributes.
	 */
	public final Map<String, List<CellFormAttributes>> getCellInputAttributes() {
		return cellInputAttributes;
	}

	/**
	 * return cellselectitemattributes.
	 * 
	 * @return cellselectitemattributes.
	 */
	public final Map<String, Map<String, String>> getCellSelectItemsAttributes() {
		return cellSelectItemsAttributes;
	}

	/**
	 * return celldatepattern.
	 * 
	 * @return celldatepattern.
	 */
	public final Map<String, String> getCellDatePattern() {
		return cellDatePattern;
	}

	/**
	 * @return the cellValidateAttributes
	 */
	public final Map<String, List<CellFormAttributes>> getCellValidateAttributes() {
		return cellValidateAttributes;
	}

	/**
	 * @param pcellValidateAttributes
	 *            the cellValidateAttributes to set
	 */
	public final void setCellValidateAttributes(
			Map<String, List<CellFormAttributes>> pcellValidateAttributes) {
		this.cellValidateAttributes = pcellValidateAttributes;
	}

}