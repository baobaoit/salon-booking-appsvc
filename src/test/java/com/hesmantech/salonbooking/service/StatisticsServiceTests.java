package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.statistics.SaleAnalyticsPeriod;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.service.impl.StatisticsServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTests {
    @Mock
    private OrderRepository orderRepository;

    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        this.statisticsService = new StatisticsServiceImpl(orderRepository);
    }

    @Test
    void testCountByStatusesAndBetweenDatesSuccess() {
        // given
        var from = LocalDate.now();
        var to = from.plusDays(1);
        var emptyList = List.<OrderStatus>of();
        var expected = 1L;

        Mockito.when(orderRepository.countByStatusInAndCustomer_StatusInAndCreatedDateBetween(Mockito.anyCollection(),
                        Mockito.anyCollection(), Mockito.any(), Mockito.any()))
                .thenReturn(1L);

        // when
        var resultFromNullAndToNull = statisticsService.countByStatusesAndBetweenDates(emptyList, null, null);
        var resultFromNull = statisticsService.countByStatusesAndBetweenDates(emptyList, null, to);
        var resultToNull = statisticsService.countByStatusesAndBetweenDates(emptyList, from, null);
        var resultFromAndTo = statisticsService.countByStatusesAndBetweenDates(emptyList, from, to);
        var resultFromAfterTo = statisticsService.countByStatusesAndBetweenDates(emptyList, to, from);

        // then
        Assertions.assertThat(resultFromNullAndToNull).isEqualTo(expected);
        Assertions.assertThat(resultFromNull).isEqualTo(expected);
        Assertions.assertThat(resultToNull).isEqualTo(expected);
        Assertions.assertThat(resultFromAndTo).isEqualTo(expected);
        Assertions.assertThat(resultFromAfterTo).isEqualTo(expected);
    }

    @Test
    void testGetSaleAnalyticsSuccess() {
        // given
        Mockito.when(orderRepository.countByStatusInAndCustomer_StatusInAndCreatedDateBetween(Mockito.anyCollection(),
                        Mockito.anyCollection(), Mockito.any(), Mockito.any()))
                .thenReturn(1L);

        Mockito.when(orderRepository.totalPriceByCustomerStatusInAndCreatedDateBetween(Mockito.anyCollection(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(1d);

        // when
        var resultToday = statisticsService.getSaleAnalytics(SaleAnalyticsPeriod.TODAY);
        var resultWeekly = statisticsService.getSaleAnalytics(SaleAnalyticsPeriod.WEEKLY);
        var resultMonthly = statisticsService.getSaleAnalytics(SaleAnalyticsPeriod.MONTHLY);

        // then
        Assertions.assertThat(resultToday).isNotEmpty();
        Assertions.assertThat(resultWeekly).isNotEmpty();
        Assertions.assertThat(resultMonthly).isNotEmpty();
    }

    @Test
    void testGetTotalRevenueSuccess() {
        // given
        var expected = BigDecimal.valueOf(1.00d).setScale(2, RoundingMode.HALF_UP);
        Mockito.when(orderRepository.totalPriceByCustomerStatusInAndCreatedDateBetween(Mockito.anyCollection(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(1d);

        // when
        var result = statisticsService.getTotalRevenue();

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.revenue()).isEqualTo(expected);
        Assertions.assertThat(result.totalToday()).isEqualTo(expected);
    }

    @Test
    void testGetTotalRevenueWhileLoopSuccess() {
        // given
        var recentDateRevenue = LocalDate.now().minusDays(1);
        var expected = BigDecimal.valueOf(1.00d).setScale(2, RoundingMode.HALF_UP);
        Mockito.when(orderRepository.totalPriceByCustomerStatusInAndCreatedDateBetween(Mockito.anyCollection(),
                        Mockito.any(), Mockito.any()))
                .thenAnswer((Answer<Double>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();
                    Instant from = (Instant) args[1];

                    if (recentDateRevenue.atStartOfDay().toInstant(ZoneOffset.UTC).equals(from)) {
                        return 0d;
                    }

                    return 1d;
                });

        // when
        var result = statisticsService.getTotalRevenue();

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.revenue()).isEqualTo(expected);
        Assertions.assertThat(result.totalToday()).isEqualTo(expected);
    }
}
