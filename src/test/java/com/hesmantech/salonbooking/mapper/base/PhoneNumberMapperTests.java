package com.hesmantech.salonbooking.mapper.base;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberMapperTests {
    private static final String PHONE_NUMBER_STANDARD = "2122323122";
    private static final String PHONE_NUMBER_US_FORMAT = "212-232-3122";

    @Test
    @DisplayName("Phone number to US format success")
    void testPhoneNumberToUSFormat() {
        // when
        var result = PhoneNumberMapper.phoneNumberToUSFormat(PHONE_NUMBER_STANDARD);

        // then
        Assertions.assertThat(result).isEqualTo(PHONE_NUMBER_US_FORMAT);
    }

    @Test
    @DisplayName("Phone number standardlized")
    void testPhoneNumberStandardized() {
        // when
        var result = PhoneNumberMapper.asStandardized(PHONE_NUMBER_US_FORMAT);

        // then
        Assertions.assertThat(result).isEqualTo(PHONE_NUMBER_STANDARD);
    }
}
