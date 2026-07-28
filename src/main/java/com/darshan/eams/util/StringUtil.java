package com.darshan.eams.util;

public final class StringUtil {

    private StringUtil() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String capitalize(String value) {
        if (!hasText(value))
            return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    public static String trim(String value) {
        return value == null ? null : value.trim();
    }
}