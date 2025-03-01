package com.hesmantech.salonbooking.mapper.base;

import org.mapstruct.Named;

import java.util.Optional;

import static com.hesmantech.salonbooking.constants.Constants.US_PHONE_NUMBER_REPLACE_FORMAT;
import static com.hesmantech.salonbooking.constants.Constants.US_PHONE_NUMBER_SPLIT_FORMAT;

public interface PhoneNumberMapper {
    @Named("phoneNumberToUSFormat")
    static String phoneNumberToUSFormat(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        return phoneNumber.replaceAll(US_PHONE_NUMBER_SPLIT_FORMAT, US_PHONE_NUMBER_REPLACE_FORMAT);
    }

    @Named("asStandardized")
    static String asStandardized(String phoneNumber) {
        return Optional.ofNullable(phoneNumber)
                .map(number -> number.replace("-", ""))
                .orElse(null);
    }
}
