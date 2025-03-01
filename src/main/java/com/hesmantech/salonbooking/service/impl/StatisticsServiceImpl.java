package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalytics;
import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalyticsPeriod;
import com.hesmantech.salonbooking.api.dto.statistics.TotalRevenue;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.hesmantech.salonbooking.constants.Constants.DOUBLE_COMPARE_EQUAL;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final OrderRepository orderRepository;

    @Override
    public long countByStatusesAndBetweenDates(List<OrderStatus> statuses, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            LocalDate today = LocalDate.now();
            return countByStatusesAndBetweenDates(statuses, today, today);
        }

        if (from == null) {
            return countByStatusesAndBetweenDates(statuses, to, to);
        }

        if (to == null) {
            return countByStatusesAndBetweenDates(statuses, from, from);
        }

        if (from.isAfter(to)) {
            LocalDate tmp = from;
            from = to;
            to = tmp;
        }

        return orderRepository.countByStatusInAndCustomer_StatusInAndCreatedDateBetween(statuses,
                List.of(UserStatus.ACTIVE), InstantMapper.from(from), InstantMapper.from(to.plusDays(1)));
    }

    @Override
    public List<SaleAnalytics> getSaleAnalytics(SaleAnalyticsPeriod period) {
        List<OrderStatus> statuses = List.of(OrderStatus.CHECK_OUT);
        List<SaleAnalytics> saleAnalyticsList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate from;

        if (SaleAnalyticsPeriod.TODAY.equals(period)) {
            from = today;
        } else if (SaleAnalyticsPeriod.WEEKLY.equals(period)) {
            from = today.minusDays(6);
        } else {
            from = today.minusMonths(1);
        }

        while (from.isBefore(today) || from.equals(today)) {
            SaleAnalytics saleAnalytics = new SaleAnalytics(getTotalPrice(from, from),
                    countByStatusesAndBetweenDates(statuses, from, from),
                    String.valueOf(InstantMapper.from(from)));

            saleAnalyticsList.add(saleAnalytics);

            from = from.plusDays(1);
        }

        return saleAnalyticsList;
    }

    @Override
    public TotalRevenue getTotalRevenue() {
        LocalDate today = LocalDate.now();
        double totalToday = getTotalPrice(today, today);

        LocalDate recentDateRevenue = today.minusDays(1);
        double recentRevenue = getTotalPrice(recentDateRevenue, recentDateRevenue);

        while (Double.compare(recentRevenue, 0) == DOUBLE_COMPARE_EQUAL) {
            recentDateRevenue = recentDateRevenue.minusDays(1);
            recentRevenue = getTotalPrice(recentDateRevenue, recentDateRevenue);
        }

        BigDecimal revenue = BigDecimal.valueOf(totalToday / recentRevenue)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalTodayRevenue = BigDecimal.valueOf(totalToday)
                .setScale(2, RoundingMode.HALF_UP);

        return new TotalRevenue(revenue, totalTodayRevenue);
    }

    private double getTotalPrice(LocalDate from, LocalDate to) {
        return orderRepository.totalPriceByCustomerStatusInAndCreatedDateBetween(List.of(UserStatus.ACTIVE),
                InstantMapper.from(from), InstantMapper.from(to.plusDays(1)));
    }
}
