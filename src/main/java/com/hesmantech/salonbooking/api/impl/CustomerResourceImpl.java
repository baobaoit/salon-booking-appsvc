package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.CustomerResource;
import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckInRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckOutRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerRegistrationRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerResponse;
import com.hesmantech.salonbooking.api.dto.customer.checkout.PaymentMethod;
import com.hesmantech.salonbooking.api.dto.order.OrderResponse;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.exception.user.InvalidPhoneNumber;
import com.hesmantech.salonbooking.mapper.CustomerMapper;
import com.hesmantech.salonbooking.mapper.NailServiceMapper;
import com.hesmantech.salonbooking.mapper.OrderMapper;
import com.hesmantech.salonbooking.mapper.UserMapper;
import com.hesmantech.salonbooking.service.CreditService;
import com.hesmantech.salonbooking.service.CustomerService;
import com.hesmantech.salonbooking.service.GiftCardBalanceService;
import com.hesmantech.salonbooking.service.ReportService;
import com.hesmantech.salonbooking.utils.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

import static com.hesmantech.salonbooking.constants.ReportConstants.CUSTOMER_REPORT_FILE_NAME;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerResourceImpl implements CustomerResource {
    private static final CustomerMapper customerMapper = CustomerMapper.INSTANCE;
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private final CustomerService customerService;
    private final OrderMapper orderMapper;
    private final NailServiceMapper nailServiceMapper;
    private final CreditService creditService;
    private final GiftCardBalanceService giftCardBalanceService;
    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public CustomerResponse create(CustomerRegistrationRequest customerRegisterRequest, Principal principal) {
        try {
            if (!PhoneNumberUtils.isValid(customerRegisterRequest.phoneNumber())) {
                throw new InvalidPhoneNumber();
            }

            CustomerResponse customerResponse = customerMapper.toCustomerResponse(customerService.create(customerRegisterRequest));

            log.info("Register a new customer successfully from {}", principal.getName());

            return customerResponse;
        } catch (Exception e) {
            log.error("Failed to register a new customer: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{id}/check-in")
    @Override
    public OrderResponse checkIn(@PathVariable UUID id, CustomerCheckInRequest customerCheckInRequest, Principal principal) {
        try {
            OrderEntity orderEntity = customerService.checkIn(id, customerCheckInRequest);
            OrderResponse orderResponse = orderMapper.toOrderResponse(orderEntity)
                    .withNailTechnician(userMapper.toNailTechnicianResponse(orderEntity))
                    .withServices(nailServiceMapper.toNailServiceResponseList(orderEntity));

            log.info("Check in successfully from {}", principal.getName());

            return orderResponse;
        } catch (Exception e) {
            log.error("Failed to check in: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{id}/check-out")
    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    @Override
    public OrderResponse checkOut(@PathVariable UUID id, CustomerCheckOutRequest customerCheckOutRequest, Principal principal) {
        try {
            OrderEntity orderEntity = customerService.checkOut(id, customerCheckOutRequest);

            if (PaymentMethod.GIFT_CARD_BALANCE.equals(customerCheckOutRequest.paymentMethod())) {
                giftCardBalanceService.updateWhenCheckOutOrder(orderEntity);
            }

            creditService.updateBasedOn(orderEntity);
            OrderResponse orderResponse = orderMapper.toOrderResponse(orderEntity)
                    .withNailTechnician(userMapper.toNailTechnicianResponse(orderEntity))
                    .withServices(nailServiceMapper.toNailServiceResponseList(orderEntity));

            log.info("Check out successfully from {}", principal.getName());

            return orderResponse;
        } catch (Exception e) {
            log.error("Failed to check out {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public ResponseEntity<byte[]> report(Principal principal) {
        try {
            var data = customerService.prepareReportData();
            var report = reportService.generateUserReport(CUSTOMER_REPORT_FILE_NAME, data);

            log.info("Report customer successfully from {}", principal.getName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.fileName())
                    .body(report.content());
        } catch (Exception e) {
            log.error("Failed to report customer: {}", e.getMessage(), e);
            throw e;
        }
    }
}
