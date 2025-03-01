package com.hesmantech.salonbooking.mapper.base;

import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface BigDecimalMapper {
    @Named("toBigDecimal")
    static BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
    }

    @Named("toBigDecimalScale2")
    static BigDecimal toBigDecimalScale2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
