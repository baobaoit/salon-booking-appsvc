package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.ReportData;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;

import java.util.List;

public interface ReportService {
    ReportData generateUserReport(String fileName, List<UserEntity> data);

    ReportData generateOrderReport(List<OrderEntity> data);
}
