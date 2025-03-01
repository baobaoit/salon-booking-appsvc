package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.CustomerCreditResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.customercredit.CustomerCreditDetails;
import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditRequest;
import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditResponse;
import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import com.hesmantech.salonbooking.mapper.CreditMapper;
import com.hesmantech.salonbooking.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer-credits")
@RequiredArgsConstructor
@Slf4j
public class CustomerCreditResourceImpl implements CustomerCreditResource {
    private static final CreditMapper creditMapper = CreditMapper.INSTANCE;
    private final CreditService creditService;

    @PostMapping("/search")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<SearchCustomerCreditResponse> search(int page, int size, Sort.Direction direction,
                                                             CustomerCreditSortProperty property,
                                                             SearchCustomerCreditRequest request, Principal principal) {
        try {
            Page<SearchCustomerCreditResponse> pageCustomerCreditResponse = creditService.search(page, size, direction, property, request)
                    .map(creditMapper::toSearchCustomerCreditResponse);

            log.info("Search customer credit successfully from {}", principal.getName());

            return PageResponse.<SearchCustomerCreditResponse>builder()
                    .content(pageCustomerCreditResponse.getContent())
                    .page(pageCustomerCreditResponse.getPageable().getPageNumber())
                    .size(pageCustomerCreditResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageCustomerCreditResponse.getTotalPages())
                    .totalElements(pageCustomerCreditResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search customer credit: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public CustomerCreditDetails getDetails(@PathVariable UUID customerId, Principal principal) {
        try {
            CustomerCreditDetails creditDetails = creditMapper.toCustomerCreditDetails(
                    creditService.getDetails(customerId));

            log.info("Get customer credit details successfully from {}", principal.getName());

            return creditDetails;
        } catch (Exception e) {
            log.error("Failed to get customer credit details: {}", e.getMessage(), e);
            throw e;
        }
    }
}
