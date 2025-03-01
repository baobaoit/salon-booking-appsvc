package com.hesmantech.salonbooking.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberUtilsTests {
    @Test
    @DisplayName("Valid US phone number")
    void testValidUSPhoneNumber() {
        // given
        var usPhoneNumber = "706-542-0074";

        // when
        var result = PhoneNumberUtils.isValid(usPhoneNumber);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Invalid US phone number")
    void testInvalidUSPhoneNumber() {
        // given
        var phoneNumberBlank = " ";
        var phoneNumberInvalidFormat = "0987654321";

        // when
        var resultPhoneNumberBlank = PhoneNumberUtils.isValid(phoneNumberBlank);
        var resultPhoneNumberInvalidFormat = PhoneNumberUtils.isValid(phoneNumberInvalidFormat);

        // then
        Assertions.assertFalse(resultPhoneNumberBlank);
        Assertions.assertFalse(resultPhoneNumberInvalidFormat);
    }

    @Test
    @DisplayName("Check private constructor")
    void testPrivateConstructor() {
        try {
            // given
            var constructor = PhoneNumberUtils.class.getDeclaredConstructor();

            // when
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            // then
            Assertions.assertInstanceOf(IllegalStateException.class, e.getCause());
        }
    }
}
