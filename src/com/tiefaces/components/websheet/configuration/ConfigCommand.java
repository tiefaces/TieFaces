package com.tiefaces.components.websheet.configuration;


/**
 * Base class for all configuration command.
 * 
 * @author Jason Jiang
 *
 */

public abstract class ConfigCommand implements Command {
	/** Type Name. */
	private String commandTypeName;
	/** Range. */
	private ConfigRange configRange;
	/** Parent command. */
	private Command parentCommand = null;
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

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getCommandTypeName()
	 */
	@Override
	public final String getCommandTypeName() {
		return commandTypeName;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#setCommandTypeName(java.lang.String)
	 */
	@Override
	public final void setCommandTypeName(final String pCommandTypeName) {
		this.commandTypeName = pCommandTypeName;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getConfigRange()
	 */
	@Override
	public final ConfigRange getConfigRange() {
		if (this.configRange == null) {
			configRange = new ConfigRange();
		}
		return configRange;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#setConfigRange(com.tiefaces.components.websheet.configuration.ConfigRange)
	 */
	@Override
	public final void setConfigRange(final ConfigRange pConfigRange) {
		this.configRange = pConfigRange;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getParentCommand()
	 */
	@Override
	public final Command getParentCommand() {
		return parentCommand;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#setParentCommand(com.tiefaces.components.websheet.configuration.Command)
	 */
	@Override
	public final void setParentCommand(final Command pParentCommand) {
		this.parentCommand = pParentCommand;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getLength()
	 */
	@Override
	public final String getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#setLength(java.lang.String)
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
			// do nothing
		}
		return 0;
	}


	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getTopRow()
	 */
	@Override
	public final int getTopRow() {
		if (this.getConfigRange().getFirstRowAddr() != null) {
			return this.getConfigRange().getFirstRowRef().getRowIndex();
		}
		return -1;
	}
	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getLastRow()
	 */

	@Override
	public final int getLastRow() {
		if ((this.getTopRow() >= 0) && (this.getLength() != null)) {
			return this.getTopRow() + calcLength(this.getLength()) - 1;
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getLeftCol()
	 */
	@Override
	public final int getLeftCol() {
		if (this.getConfigRange().getFirstRowRef() != null) {
			return this.getConfigRange().getFirstRowRef().getColumnIndex();
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getRightCol()
	 */
	@Override
	public final int getRightCol() {
		if (this.getConfigRange().getLastRowPlusRef() != null) {
			return this.getConfigRange().getLastRowPlusRef().getColumnIndex();
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#getFinalLength()
	 */
	@Override
	public int getFinalLength() {
		return finalLength;
	}

	/* (non-Javadoc)
	 * @see com.tiefaces.components.websheet.configuration.Command#setFinalLength(int)
	 */
	@Override
	public void setFinalLength(int populatedLength) {
		this.finalLength = populatedLength;
	}
	
	

}
