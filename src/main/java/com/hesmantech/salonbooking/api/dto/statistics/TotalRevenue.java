package com.hesmantech.salonbooking.api.dto.statistics;

import java.math.BigDecimal;

public record TotalRevenue(
        BigDecimal revenue,
        BigDecimal totalToday
) {
}
