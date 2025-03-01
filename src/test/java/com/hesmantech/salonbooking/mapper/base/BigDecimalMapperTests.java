package com.hesmantech.salonbooking.mapper.base;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class BigDecimalMapperTests {
    @Test
    @DisplayName("Convert double to BigDecimal with scale 0 and half up")
    void testToBigDecimalSuccess() {
        // given
        var value = 12.34;

        // when
        var result = BigDecimalMapper.toBigDecimal(value);

        // then
        Assertions.assertThat(result)
                .isNotNull()
                .isEqualTo(BigDecimal.valueOf(12));
    }

    @Test
    @DisplayName("Convert double to BigDecimal with scale 2 and half up")
    void testToBigDecimalScale2Success() {
        // given
        var value = 12.34;

        // when
        var result = BigDecimalMapper.toBigDecimalScale2(value);

        // then
        Assertions.assertThat(result)
                .isNotNull()
                .isEqualTo(BigDecimal.valueOf(value));
    }
}
