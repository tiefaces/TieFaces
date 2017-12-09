/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.io.Serializable;

/**
 * Object used to hold objects from context. The map key is the poi skey, so they are easily related to poi cells.
 */
public class TieCell implements Serializable {

    private static final long serialVersionUID = -6811957210518221929L;

    private String skey;

    private String objectStr;

    private String methodStr;

    private Object contextObject;

    public String getSkey() {
	return skey;
    }

    public void setSkey(String skey) {
	this.skey = skey;
    }

    public String getObjectStr() {
	return objectStr;
    }

    public void setObjectStr(String objectStr) {
	this.objectStr = objectStr;
    }

    public String getMethodStr() {
	return methodStr;
    }

    public void setMethodStr(String methodStr) {
	this.methodStr = methodStr;
    }

    public Object getContextObject() {
	return contextObject;
    }

    public void setContextObject(Object contextObject) {
	this.contextObject = contextObject;
    }

}
