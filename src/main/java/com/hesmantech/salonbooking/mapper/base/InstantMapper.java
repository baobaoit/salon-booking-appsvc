package com.hesmantech.salonbooking.mapper.base;

import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.hesmantech.salonbooking.constants.Constants.YYYY_MM_DD_FORMAT;

public interface InstantMapper {
    @Named("instantToString")
    static String instantToString(Instant instant) {
        return Optional.ofNullable(instant)
                .map(d -> DateTimeFormatter.ofPattern(YYYY_MM_DD_FORMAT)
                        .withZone(ZoneId.systemDefault())
                        .format(d))
                .orElse("");
    }

    static Instant from(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(d -> d.atStartOfDay().toInstant(ZoneOffset.UTC))
                .orElse(null);
    }

    static Instant from(Long epochMilli) {
        return Optional.ofNullable(epochMilli)
                .map(Instant::ofEpochMilli)
                .orElse(null);
    }
}
