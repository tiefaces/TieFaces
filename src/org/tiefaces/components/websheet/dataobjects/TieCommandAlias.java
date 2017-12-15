package org.tiefaces.components.websheet.dataobjects;

/**
 * This class is to help user to easily define the command.
 * 
 * First we can setup a list alias for commands like:
 * example 1:
 * alias = ${date} , command = $widget.calendar... $validate.... 
 * end-user only need to define ${date} in the template while 
 * widget and validate command could be added in fly
 * 
 * example 2:
 * alias = <dropdown1> command = $widget.dropdown.... remove = true
 * when set the remove to true, the alias <dropdown1> will be removed
 * after setup the command.
 * 
 */
public class TieCommandAlias {
	
/** The alias. */
private String alias;

/** The command. */
private String command;

/** The remove. */
private boolean remove = false;

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
	



}
