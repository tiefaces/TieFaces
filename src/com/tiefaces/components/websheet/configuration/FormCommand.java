package com.tiefaces.components.websheet.configuration;

import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.tiefaces.components.websheet.service.CellHelper;

/**
 * Form command. i.e. tie:form(name="departments" length="9" header="0"
 * footer="0")
 * 
 * @author Jason Jiang
 *
 */

public class FormCommand extends ConfigCommand {

	/** name holder. */
	private String name;
	/** header holder. */
	private String header;
	/** footer holder. */
	private String footer;
	/** hidden holder. */
	private String hidden;

	public final String getName() {
		return name;
	}

	public final void setName(final String pName) {
		this.name = pName;
	}

	public final String getHeader() {
		return header;
	}

	public final void setHeader(final String pHeader) {
		this.header = pHeader;
	}

	public final String getFooter() {
		return footer;
	}

	public final void setFooter(final String pFooter) {
		this.footer = pFooter;
	}

	public final String getHidden() {
		return hidden;
	}

	public final void setHidden(final String pHidden) {
		this.hidden = pHidden;
	}

	/**
	 * calc header length.
	 * 
	 * @return int header length.
	 */
	public final int calcHeaderLength() {
		return calcLength(this.getHeader());
	}

	/**
	 * calc body length.
	 * 
	 * @return int body length.
	 */
	public final int calcBodyLength() {
		return calcLength(this.getLength()) - calcHeaderLength()
				- calcFooterLength();
	}

	/**
	 * calc footer length.
	 * 
	 * @return int footer length.
	 */
	public final int calcFooterLength() {
		return calcLength(this.getFooter());
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("commandName = " + this.getCommandTypeName());
		sb.append(",");
		sb.append("form Name = " + this.getName());
		sb.append(",");
		sb.append("length = " + this.getLength());
		sb.append(",");
		sb.append("header = " + this.getHeader());
		sb.append(",");
		sb.append("footer = " + this.getFooter());
		sb.append("}");
		return sb.toString();

	}

	@Override
	public int buildAt(Sheet sheet, int startRow,
			Map<String, Object> context, ExpressionEngine engine,
			CellHelper cellHelper) {
		// TODO Auto-generated method stub
		return 0;
	}

}
