package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.StatisticsResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.statistics.OrderStatisticsResponse;
import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalytics;
import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalyticsPeriod;
import com.hesmantech.salonbooking.api.dto.statistics.TotalRevenue;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsResourceImpl implements StatisticsResource {
    private final StatisticsService statisticsService;

    @GetMapping("/order")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public OrderStatisticsResponse getOrderStatistics(LocalDate from, LocalDate to, Principal principal) {
        try {
            OrderStatisticsResponse response = new OrderStatisticsResponse(
                    statisticsService.countByStatusesAndBetweenDates(List.of(OrderStatus.WAITING_SERVICE), from, to),
                    statisticsService.countByStatusesAndBetweenDates(List.of(OrderStatus.IN_SERVICE), from, to),
                    statisticsService.countByStatusesAndBetweenDates(List.of(OrderStatus.WAITING_SERVICE,
                            OrderStatus.IN_SERVICE, OrderStatus.CHECK_OUT), from, to));

            log.info("Get order statistics successfully from {}", principal.getName());

            return response;
        } catch (Exception e) {
            log.error("Failed to get order statistics: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/sale-analystics")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<SaleAnalytics> getSaleAnalytics(SaleAnalyticsPeriod period, Principal principal) {
        try {
            List<SaleAnalytics> saleAnalyticsList = statisticsService.getSaleAnalytics(period);

            log.info("Get sale analytics successfully from {}", principal.getName());

            return PageResponse.<SaleAnalytics>builder()
                    .content(saleAnalyticsList)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get sale analytics: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/total-revenue")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public TotalRevenue getTotalRevenue(Principal principal) {
        try {
            TotalRevenue totalRevenue = statisticsService.getTotalRevenue();

            log.info("Get total revenue successfully from {}", principal.getName());

            return totalRevenue;
        } catch (Exception e) {
            log.error("Failed to get total revenue: {}", e.getMessage(), e);
            throw e;
        }
    }
}
