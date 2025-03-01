package com.hesmantech.salonbooking.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

import static com.hesmantech.salonbooking.constants.Constants.US_PHONE_NUMBER_FORMAT;

public final class PhoneNumberUtils {
    private static final Pattern US_PHONE_NUMBER_PATTERN = Pattern.compile(US_PHONE_NUMBER_FORMAT);

    private PhoneNumberUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValid(String phoneNumber) {
        return StringUtils.isNotBlank(phoneNumber) && US_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}
