package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalytics;
import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalyticsPeriod;
import com.hesmantech.salonbooking.api.dto.statistics.TotalRevenue;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    long countByStatusesAndBetweenDates(List<OrderStatus> statuses, LocalDate from, LocalDate to);

    List<SaleAnalytics> getSaleAnalytics(SaleAnalyticsPeriod period);

    TotalRevenue getTotalRevenue();
}
