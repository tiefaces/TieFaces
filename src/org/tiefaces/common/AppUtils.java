/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.common;

import java.util.List;

/**
 * Application Utility class. Some static command method collection.
 * 
 * @author Jason Jiang
 *
 */
public final class AppUtils {

    /**
     * hide constructor.
     */
    private AppUtils() {
        //not called
     }
    /**
     * Check empty string.
     * 
     * @param str
     *            input string.
     * @return true if string is empty or null, otherwise false.
     */
    public static boolean isEmpty(final String str) {
        return (str == null) || (str.isEmpty());
    }

    /**
     * check input list is empty.
     * 
     * @param list
     *            input list.
     * @return true if list is empty or null, otherwise false.
     */
    @SuppressWarnings("rawtypes")
    public static boolean emptyList(final List list) {
        return (list == null) || (list.size() == 0);
    }

    /**
     * check if the string only contain digital number e.g. int number.
     * 
     * @param str
     *            input string.
     * @return true if only contain digit,otherwise false.
     */
    public static boolean isNumeric(final String str) {
        if (str == null) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
    /**
     * return current build version of tiefaces.
     * @return tiefaces build version.
     */
    public static String getBuildVersion() {
        return AppUtils.class.getPackage().getImplementationVersion();
    }
}
