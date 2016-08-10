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
	
	private Boolean parentFound = false;

	@Override
	public final String getCommandTypeName() {
		return commandTypeName;
	}

	@Override
	public final void setCommandTypeName(final String pCommandTypeName) {
		this.commandTypeName = pCommandTypeName;
	}

	@Override
	public final ConfigRange getConfigRange() {
		if (this.configRange == null) {
			configRange = new ConfigRange();
		}
		return configRange;
	}

	@Override
	public final void setConfigRange(final ConfigRange pConfigRange) {
		this.configRange = pConfigRange;
	}

	@Override
	public	Boolean isParentFound() {
		return parentFound;
	}
	
	
	
	@Override
	public void setParentFound(Boolean parentFound) {
		this.parentFound = parentFound;
	}

	@Override
	public final String getLength() {
		return length;
	}

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


	@Override
	public final int getTopRow() {
		if (this.getConfigRange().getFirstRowAddr() != null) {
			return this.getConfigRange().getFirstRowRef().getRowIndex();
		}
		return -1;
	}

	@Override
	public final int getLastRow() {
		if ((this.getTopRow() >= 0) && (this.getLength() != null)) {
			return this.getTopRow() + calcLength(this.getLength()) - 1;
		}
		return -1;
	}

	@Override
	public final int getLeftCol() {
		if (this.getConfigRange().getFirstRowRef() != null) {
			return this.getConfigRange().getFirstRowRef().getColumnIndex();
		}
		return -1;
	}

	@Override
	public final int getRightCol() {
		if (this.getConfigRange().getLastRowPlusRef() != null) {
			return this.getConfigRange().getLastRowPlusRef().getColumnIndex();
		}
		return -1;
	}

	@Override
	public final int getFinalLength() {
		if (finalLength <= 0) {
			finalLength =  calcLength(this.getLength());
		}
		return finalLength;
	}

	@Override
	public final void setFinalLength(final int populatedLength) {
		this.finalLength = populatedLength;
	}
	
	

}
