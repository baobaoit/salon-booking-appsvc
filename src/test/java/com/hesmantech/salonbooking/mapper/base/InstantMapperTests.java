package com.hesmantech.salonbooking.mapper.base;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

class InstantMapperTests {
    private static final String TEST_DATE = "2024-06-21";
    private static final Long TEST_EPOCH_MILLI = 1_718_928_000_000L;

    @Test
    @DisplayName("When instant is null then return empty")
    void testInstantIsNull() {
        // when
        var result = InstantMapper.instantToString(null);

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("When instant is not null then return format as string")
    void testInstantIsNotNull() {
        // given
        var testDate = LocalDate.parse(TEST_DATE, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        // when
        var result = InstantMapper.instantToString(testDate);

        // then
        Assertions.assertThat(result)
                .isNotEmpty()
                .isEqualTo(TEST_DATE);
    }

    @Test
    @DisplayName("When local date is null then return null")
    void testLocalDateIsNull() {
        // when
        var result = InstantMapper.from((LocalDate) null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    @DisplayName("When local date is not null then return instant")
    void testLocalDateIsNotNull() {
        // given
        var testDate = LocalDate.parse(TEST_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        var expected = testDate.atStartOfDay().toInstant(ZoneOffset.UTC);

        // when
        var result = InstantMapper.from(testDate);

        // then
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("When epoch milli is null then return null")
    void testEpochMilliIsNull() {
        // when
        var result = InstantMapper.from((Long) null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    @DisplayName("When epoch milli is not null then return instant")
    void testEpochMilliIsNotNull() {
        // given
        var testDate = LocalDate.parse(TEST_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        var expected = testDate.atStartOfDay().toInstant(ZoneOffset.UTC);

        // when
        var result = InstantMapper.from(TEST_EPOCH_MILLI);

        // then
        Assertions.assertThat(result).isEqualTo(expected);
    }
}
