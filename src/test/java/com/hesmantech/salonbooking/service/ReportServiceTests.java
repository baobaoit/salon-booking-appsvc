package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserGender;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.service.impl.ReportServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static com.hesmantech.salonbooking.constants.Constants.NA;
import static com.hesmantech.salonbooking.constants.ReportConstants.CHECK_IN_MANAGEMENT_REPORT_FILE_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.CUSTOMER_REPORT_FILE_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.EMPLOYEE_REPORT_FILE_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.TECHNICIAN_REPORT_FILE_NAME;

class ReportServiceTests {
    private static final Logger log = LoggerFactory.getLogger(ReportServiceTests.class);

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        this.reportService = new ReportServiceImpl();
    }

    @ParameterizedTest
    @CsvSource({
            TECHNICIAN_REPORT_FILE_NAME,
            EMPLOYEE_REPORT_FILE_NAME,
            CUSTOMER_REPORT_FILE_NAME
    })
    void testGenerateUserReport(String fileName) {
        // given
        var data = List.of(UserEntity.builder()
                .status(UserStatus.ACTIVE)
                .gender(UserGender.OTHER)
                .build());

        // when
        var result = reportService.generateUserReport(fileName, data);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.fileName()).contains(fileName);
    }

    @ParameterizedTest
    @CsvSource({
            "CHECK_OUT",
            "IN_SERVICE"
    })
    void testGenerateOrderReport(OrderStatus status) {
        // given
        var exampleCustomer = UserEntity.builder()
                .firstName("Customer first name")
                .lastName("Customer last name")
                .build();
        var exampleTechnician = UserEntity.builder()
                .firstName("Technician first name")
                .lastName("Technician last name")
                .build();
        var exampleOrderEntity = OrderEntity.builder()
                .status(status)
                .customer(exampleCustomer)
                .employee(exampleTechnician)
                .orderedDetails(List.of(OrderedDetailsEntity.builder()
                        .employeeFirstName(exampleTechnician.getFirstName())
                        .employeeLastName(exampleTechnician.getLastName())
                        .build()))
                .price(0d)
                .checkOutTime(Instant.now())
                .build();
        exampleOrderEntity.setCreatedDate(Instant.now());
        var data = List.of(exampleOrderEntity);

        // when
        var result = reportService.generateOrderReport(data);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.fileName()).contains(CHECK_IN_MANAGEMENT_REPORT_FILE_NAME);
    }

    @Test
    void testGetFullNameReturnNA() {
        try {
            // given
            var method = reportService.getClass().getDeclaredMethod("getFullName", AbstractAuditEntity.class);
            method.setAccessible(true);

            // when
            var result = (String) method.invoke(reportService, new OrderEntity());

            // then
            Assertions.assertThat(result)
                    .isNotEmpty()
                    .isEqualTo(NA);
        } catch (Exception e) {
            log.error("Failed to test get full name return NA: {}", e.getMessage(), e);
        }
    }
}
