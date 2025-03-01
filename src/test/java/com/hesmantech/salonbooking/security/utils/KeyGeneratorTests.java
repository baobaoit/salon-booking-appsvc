package com.hesmantech.salonbooking.security.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KeyGeneratorTests {
    @Test
    @DisplayName("Check private constructor")
    void testPrivateConstructor() {
        try {
            // given
            var constructor = KeyGenerator.class.getDeclaredConstructor();

            // when
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            // then
            Assertions.assertInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

    @Test
    @DisplayName("Generate key")
    void testGenerateKey() {
        // given
        var keyLength = 8;

        // when
        var key = KeyGenerator.randomString(keyLength);

        // then
        Assertions.assertEquals(keyLength, key.length());
    }
}
