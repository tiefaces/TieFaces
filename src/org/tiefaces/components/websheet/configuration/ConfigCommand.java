/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Base class for all configuration command.
 * 
 * @author Jason Jiang
 *
 */

public abstract class ConfigCommand implements Command {
	/** logger. */
	private static final Logger LOG = Logger.getLogger(
			ConfigCommand.class.getName());
		
	/** Type Name. */
	private String commandTypeName;
	/** Range. */
	private ConfigRange configRange;
	/**
	 * The command's area's rows number. This is convenient way to set up area
	 * compare to set the last cell attribute. As in some case we only want to
	 * specify rows, not columns for area.
	 */
	private String length = null;
	/**
	 * command actual length after populate data.
	 */
	private int finalLength = 0;

	/** The parent found. */
	private Boolean parentFound = false;

	/**
	 * Instantiates a new config command.
	 */
	public ConfigCommand() {
		super();
	}

	/**
	 * Instantiates a new config command.
	 *
	 * @param source
	 *            the source
	 */
	public ConfigCommand(final ConfigCommand source) {
		this.commandTypeName = source.commandTypeName;
		this.length = source.length;
		this.finalLength = source.finalLength;
		this.configRange = new ConfigRange(source.configRange);
	}

	/**
	 * Shift row ref.
	 *
	 * @param sheet
	 *            the sheet
	 * @param shiftnum
	 *            the shiftnum
	 */
	public final void shiftRowRef(final Sheet sheet, final int shiftnum) {
		this.getConfigRange().shiftRowRef(sheet, shiftnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#getCommandTypeName
	 * ()
	 */
	@Override
	public final String getCommandTypeName() {
		return commandTypeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#setCommandTypeName
	 * (java.lang.String)
	 */
	@Override
	public final void setCommandTypeName(final String pCommandTypeName) {
		this.commandTypeName = pCommandTypeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#getConfigRange()
	 */
	@Override
	public final ConfigRange getConfigRange() {
		if (this.configRange == null) {
			configRange = new ConfigRange();
		}
		return configRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#setConfigRange
	 * (org.tiefaces.components.websheet.configuration.ConfigRange)
	 */
	@Override
	public final void setConfigRange(final ConfigRange pConfigRange) {
		this.configRange = pConfigRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#isParentFound()
	 */
	@Override
	public final Boolean isParentFound() {
		return parentFound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#setParentFound
	 * (java.lang.Boolean)
	 */
	@Override
	public final void setParentFound(final Boolean pparentFound) {
		this.parentFound = pparentFound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.configuration.Command#getLength()
	 */
	@Override
	public final String getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#setLength(java
	 * .lang.String)
	 */
	@Override
	public final void setLength(final String pLength) {
		this.length = pLength;
	}

	/**
	 * convert string to int (length).
	 * 
	 * @param lengthStr
	 *            String.
	 * @return int length.
	 */
	protected final int calcLength(final String lengthStr) {
		try {
			return Integer.parseInt(lengthStr);
		} catch (Exception ex) {
			LOG.log(Level.FINE,"canot calcLength :"+ex.getLocalizedMessage(), ex);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.configuration.Command#getTopRow()
	 */
	@Override
	public final int getTopRow() {
		if (this.getConfigRange().getFirstRowAddr() != null) {
			return this.getConfigRange().getFirstRowRef().getRowIndex();
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.configuration.Command#getLastRow()
	 */
	@Override
	public final int getLastRow() {
		if ((this.getTopRow() >= 0) && (this.getLength() != null)) {
			return this.getTopRow() + calcLength(this.getLength()) - 1;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.configuration.Command#getLeftCol()
	 */
	@Override
	public final int getLeftCol() {
		if (this.getConfigRange().getFirstRowRef() != null) {
			return this.getConfigRange().getFirstRowRef().getColumnIndex();
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tiefaces.components.websheet.configuration.Command#getRightCol()
	 */
	@Override
	public final int getRightCol() {
		if (this.getConfigRange().getLastRowPlusRef() != null) {
			return this.getConfigRange().getLastRowPlusRef()
					.getColumnIndex();
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#getFinalLength()
	 */
	@Override
	public final int getFinalLength() {
		if (finalLength <= 0) {
			finalLength = calcLength(this.getLength());
		}
		return finalLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tiefaces.components.websheet.configuration.Command#setFinalLength
	 * (int)
	 */
	@Override
	public final void setFinalLength(final int populatedLength) {
		this.finalLength = populatedLength;
	}

}
