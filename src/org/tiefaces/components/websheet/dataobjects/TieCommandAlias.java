package org.tiefaces.components.websheet.dataobjects;

import org.tiefaces.components.websheet.utility.ParserUtility;


// TODO: Auto-generated Javadoc
/**
 * This class is to help end user to easily define the control behavior of the cell.
 * In the real world, two group of people will involve in the design of template:
 * (1) developer (2) business users 
 * While business user are difficult to understand the $widget command, but they
 * are the main desinger for the template. So they need a short cut to control the 
 * 
 * First we can setup a list alias for commands like:
 * example 1: matches part of data field 
 * alias = ${*date*} , command = $widget.calendar... $validate.... 
 * end-user only need to define ${date} in the template while 
 * widget and validate command could be added in fly
 * 
 * example 2: matches special string which could be removed
 * alias = ${dropdown1} command = $widget.dropdown.... remove = true
 * when set the remove to true, the alias ${dropdown1} will be removed
 * after set the command. In this case, user can define a cell like:
 * ${data_field1} ${dropdown1} 
 * And the ${dropdown1} will be removed later.
 * 
 * The alias will support wildcard, e.g. ${*date*} match ${object.field_date_1} 
 */
public class TieCommandAlias {
	
/** The alias. */
private String alias;

/** The command. */
private String command;

/** The remove. */
private boolean remove = false;


/** The alias value. */
private String aliasRegex = null;


/**
 * Instantiates a new tie command alias.
 *
 * @param alias the alias
 * @param command the command
 */
public TieCommandAlias(String alias, String command) {
	this.alias = alias;
	this.command = command;
}


/**
 * Instantiates a new tie command alias.
 *
 * @param alias the alias
 * @param command the command
 * @param remove the remove
 */
public TieCommandAlias(String alias, String command, boolean remove) {
	this.alias = alias;
	this.command = command;
	this.remove = remove;
}

/**
 * Gets the alias.
 *
 * @return the alias
 */
public String getAlias() {
	return alias;
}

/**
 * Sets the alias.
 *
 * @param alias the new alias
 */
public void setAlias(String alias) {
	this.alias = alias;
}

/**
 * Gets the command.
 *
 * @return the command
 */
public String getCommand() {
	return command;
}

/**
 * Sets the command.
 *
 * @param command the new command
 */
public void setCommand(String command) {
	this.command = command;
}

/**
 * Checks if is removes the.
 *
 * @return true, if is removes the
 */
public boolean isRemove() {
	return remove;
}

/**
 * Sets the removes the.
 *
 * @param remove the new removes the
 */
public void setRemove(boolean remove) {
	this.remove = remove;
}

/**
 * Gets the alias value.
 *
 * @return the alias value
 */
public String getAliasRegex() {
	if (this.aliasRegex == null) {
		this.aliasRegex = ParserUtility.wildcardToRegex(alias);
	}
	return aliasRegex;
}


}
