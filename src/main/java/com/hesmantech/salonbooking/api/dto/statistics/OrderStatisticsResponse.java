package com.hesmantech.salonbooking.api.dto.statistics;

public record OrderStatisticsResponse(
        long waitingServices,
        long inServices,
        long totalServices
) {
}
