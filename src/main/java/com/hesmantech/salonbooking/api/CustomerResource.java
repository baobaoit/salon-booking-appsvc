package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckInRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckOutRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerRegistrationRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerResponse;
import com.hesmantech.salonbooking.api.dto.order.OrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Customer Resource")
@Validated
public interface CustomerResource {
    CustomerResponse create(@Valid @RequestBody CustomerRegistrationRequest customerRegisterRequest, Principal principal);

    OrderResponse checkIn(UUID id, @RequestBody CustomerCheckInRequest customerCheckInRequest, Principal principal);

    OrderResponse checkOut(UUID id, @Valid @RequestBody CustomerCheckOutRequest customerCheckOutRequest, Principal principal);

    ResponseEntity<byte[]> report(Principal principal);
}
