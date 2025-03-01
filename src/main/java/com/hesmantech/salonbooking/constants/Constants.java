package com.hesmantech.salonbooking.constants;

public final class Constants {
    public static final String US_PHONE_NUMBER_FORMAT = "\\d{3}-\\d{3}-\\d{4}";
    public static final String US_PHONE_NUMBER_SPLIT_FORMAT = "(\\d{3})(\\d{3})(\\d{4})";
    public static final String US_PHONE_NUMBER_REPLACE_FORMAT = "$1-$2-$3";
    public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
    public static final String NA = "N/A";
    public static final int DOUBLE_COMPARE_EQUAL = 0;
    public static final String GIFT_CARD_DOESNT_EXPIRE = "Doesn't expire";
    public static final String DEFAULT_SYSTEM_USER = "system";
    public static final double DOUBLE_THRESHOLD = 0.01;

    private Constants() {
        throw new IllegalStateException("Constant class");
    }
}
