package com.hesmantech.salonbooking.api.dto.statistics;

public record SaleAnalytics(
        double revenue,
        long totalServices,
        String dateTime
) {
}
