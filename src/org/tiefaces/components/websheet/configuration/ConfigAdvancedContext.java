/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.configuration;

import java.io.Serializable;

public class ConfigAdvancedContext implements Serializable {

    public ConfigAdvancedContext() {
	super();
    }

    public ConfigAdvancedContext(String errorSuffix) {
	super();
	this.errorSuffix = errorSuffix;
    }

    private static final long serialVersionUID = -1227256063733119724L;

    private String errorSuffix;

    public String getErrorSuffix() {
	return errorSuffix;
    }

    public void setErrorSuffix(String errorSuffix) {
	this.errorSuffix = errorSuffix;
    }

}
