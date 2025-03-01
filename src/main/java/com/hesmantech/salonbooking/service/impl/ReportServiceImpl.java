package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.ReportData;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.mapper.base.BigDecimalMapper;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.service.ReportService;
import com.hesmantech.salonbooking.service.impl.base.GenericReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hesmantech.salonbooking.constants.Constants.NA;
import static com.hesmantech.salonbooking.constants.ReportConstants.CHECK_IN_MANAGEMENT_REPORT_FILE_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.CHECK_IN_SHEET_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.CHECK_IN_TIME_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.CHECK_OUT_TIME_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.CUSTOMERS_SHEET_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.CUSTOMER_NAME_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.CUSTOMER_REPORT_FILE_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.DATE_OF_BIRTH_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.EMAIL_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.EMPLOYEES_SHEET_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.FIRST_NAME_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.GENDER_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.LAST_NAME_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.NAIL_TECHNICIAN_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.NO_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.PHONE_NUMBER_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.STATUS_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.SUB_TOTAL_COLUMN_HEADER;
import static com.hesmantech.salonbooking.constants.ReportConstants.TECHNICIANS_SHEET_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.TECHNICIAN_REPORT_FILE_NAME;

@Service
public class ReportServiceImpl extends GenericReportService implements ReportService {
    private static final String YYYY_MM_DD_HH_MM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String MMM_DD_YYYY_HH_MM_SS_DATE_TIME_FORMAT = "_MMMddyyyy_HHmmss";

    @Override
    public ReportData generateUserReport(String fileName, List<UserEntity> data) {
        String[] columnHeaders = {
                FIRST_NAME_COLUMN_HEADER,
                LAST_NAME_COLUMN_HEADER,
                PHONE_NUMBER_COLUMN_HEADER,
                DATE_OF_BIRTH_COLUMN_HEADER,
                EMAIL_COLUMN_HEADER,
                GENDER_COLUMN_HEADER,
                STATUS_COLUMN_HEADER
        };

        String sheetName;
        switch (fileName) {
            case TECHNICIAN_REPORT_FILE_NAME -> sheetName = TECHNICIANS_SHEET_NAME;
            case CUSTOMER_REPORT_FILE_NAME -> sheetName = CUSTOMERS_SHEET_NAME;
            default -> sheetName = EMPLOYEES_SHEET_NAME;
        }

        return generateReport(fileName, sheetName, columnHeaders, data, (dataRow, employee) -> {
            var statusValue = StringUtils.capitalize(String.valueOf(employee.getStatus()).toLowerCase());
            dataRow.createCell(0).setCellValue(employee.getFirstName());
            dataRow.createCell(1).setCellValue(employee.getLastName());
            dataRow.createCell(2).setCellValue(employee.getPhoneNumber());
            dataRow.createCell(3).setCellValue(InstantMapper.instantToString(employee.getDob()));
            dataRow.createCell(4).setCellValue(employee.getEmail());
            dataRow.createCell(5).setCellValue(employee.getGender().getValue());
            dataRow.createCell(6).setCellValue(statusValue);
        });
    }

    @Override
    public ReportData generateOrderReport(List<OrderEntity> data) {
        String[] columnHeaders = {
                NO_COLUMN_HEADER,
                CUSTOMER_NAME_COLUMN_HEADER,
                NAIL_TECHNICIAN_COLUMN_HEADER,
                STATUS_COLUMN_HEADER,
                SUB_TOTAL_COLUMN_HEADER,
                CHECK_IN_TIME_COLUMN_HEADER,
                CHECK_OUT_TIME_COLUMN_HEADER
        };

        var exportTime = DateTimeFormatter.ofPattern(MMM_DD_YYYY_HH_MM_SS_DATE_TIME_FORMAT)
                .format(ZonedDateTime.now());
        var fileName = CHECK_IN_MANAGEMENT_REPORT_FILE_NAME + exportTime;

        var rowNumber = new AtomicInteger(1);

        return generateReport(fileName, CHECK_IN_SHEET_NAME, columnHeaders, data,
                (dataRow, order) -> fillOrderReportData(dataRow, order, rowNumber));
    }

    private void fillOrderReportData(Row dataRow, OrderEntity order, AtomicInteger rowNumber) {
        var customerName = Optional.ofNullable(order.getCustomer())
                .map(this::getFullName).orElse(NA);
        var technicianName = Optional.ofNullable(order.getEmployee())
                .map(this::getFullName).orElse(NA);
        var orderStatus = order.getStatus();
        if (OrderStatus.CHECK_OUT.equals(orderStatus)) {
            technicianName = order.getOrderedDetails()
                    .stream()
                    .findFirst()
                    .map(this::getFullName).orElse(NA);
        }
        var formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_DATE_TIME_FORMAT)
                .withZone(ZoneId.systemDefault());
        dataRow.createCell(0).setCellValue(rowNumber.getAndIncrement());
        dataRow.createCell(1).setCellValue(customerName);
        dataRow.createCell(2).setCellValue(technicianName);
        dataRow.createCell(3).setCellValue(orderStatus.getValue());
        dataRow.createCell(4).setCellValue(Optional.ofNullable(order.getPrice())
                .map(orderPrice -> "$" + BigDecimalMapper.toBigDecimalScale2(orderPrice)).orElse(""));
        dataRow.createCell(5).setCellValue(formatter.format(order.getCreatedDate()));
        dataRow.createCell(6).setCellValue(Optional.ofNullable(order.getCheckOutTime())
                .map(formatter::format).orElse(""));
    }

    private <T extends AbstractAuditEntity> String getFullName(T o) {
        if (o instanceof UserEntity user) {
            return user.getFirstName() + " " + user.getLastName();
        }

        if (o instanceof OrderedDetailsEntity orderedDetails) {
            return orderedDetails.getEmployeeFirstName() + " " + orderedDetails.getEmployeeLastName();
        }

        return NA;
    }
}
