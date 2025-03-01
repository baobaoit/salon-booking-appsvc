package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.statistics.OrderStatisticsResponse;
import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalytics;
import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalyticsPeriod;
import com.hesmantech.salonbooking.api.dto.statistics.TotalRevenue;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;

@Tag(name = "Statistics Resource")
public interface StatisticsResource {
    OrderStatisticsResponse getOrderStatistics(@RequestParam(required = false) LocalDate from,
                                               @RequestParam(required = false) LocalDate to, Principal principal);

    PageResponse<SaleAnalytics> getSaleAnalytics(@RequestParam(defaultValue = "TODAY") SaleAnalyticsPeriod period,
                                                 Principal principal);

    TotalRevenue getTotalRevenue(Principal principal);
}
