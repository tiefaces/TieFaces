/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.common;

import java.util.List;


public final class AppUtils {

	public static boolean isEmpty(String str) {
		if ((str==null) || (str.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public static boolean emptyList(List list) {
		if ((list == null) || (list.size() == 0)) {
			return true;
		} else {
			return false;
		}
	}
	/*
	 * check if the string only contain digital number e.g. int number
	 */
	public static boolean isNumeric(String str)
	{
		if (str==null) {
			return false;
		}
		
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    
	    return true;
	}
}
