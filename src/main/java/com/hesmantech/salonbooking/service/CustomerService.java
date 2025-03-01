package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckInRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckOutRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerRegistrationRequest;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.service.report.PrepareReportData;

import java.util.UUID;

public interface CustomerService extends PrepareReportData<UserEntity, Void> {
    UserEntity create(CustomerRegistrationRequest customerRegistrationRequest);

    OrderEntity checkIn(UUID id, CustomerCheckInRequest customerCheckInRequest);

    OrderEntity checkOut(UUID id, CustomerCheckOutRequest customerCheckOutRequest);
}
