package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.GiftCardBalanceResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.LatestGCBResponse;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.RedeemByCodeRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityResponse;
import com.hesmantech.salonbooking.api.dto.sort.giftcardbalance.GCBActivitySortProperty;
import com.hesmantech.salonbooking.mapper.GiftCardBalanceMapper;
import com.hesmantech.salonbooking.service.CreditService;
import com.hesmantech.salonbooking.service.GiftCardBalanceService;
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
@RequestMapping("/api/v1/gift-card-balances")
@RequiredArgsConstructor
@Slf4j
public class GiftCardBalanceResourceImpl implements GiftCardBalanceResource {
    private static final GiftCardBalanceMapper giftCardBalanceMapper = GiftCardBalanceMapper.INSTANCE;
    private final GiftCardBalanceService giftCardBalanceService;
    private final CreditService creditService;

    @GetMapping("/{customerId}/latest")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public LatestGCBResponse retrieveGiftCardBalance(@PathVariable UUID customerId, Principal principal) {
        try {
            var latestBalance = giftCardBalanceMapper.toLatestGiftCardBalanceResponse(
                    giftCardBalanceService.getLatestGiftCardBalance(customerId));

            log.info("Retrieve latest gift card balance for customer {} successfully from {}", customerId, principal.getName());

            return latestBalance;
        } catch (Exception e) {
            log.error("Failed to retrieve gift card balance of customer {}: {}", customerId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{customerId}/redeem-gift-code")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public LatestGCBResponse redeemGiftCardCode(@PathVariable UUID customerId, RedeemByCodeRequest request, Principal principal) {
        try {
            var giftCardBalance = giftCardBalanceService.redeemGiftCardCode(customerId, request);
            creditService.updateBasedOn(giftCardBalance);
            var latestBalance = giftCardBalanceMapper.toLatestGiftCardBalanceResponse(giftCardBalance);

            log.info("Redeem gift card code of customer {} successfully from {}", customerId, principal.getName());

            return latestBalance;
        } catch (Exception e) {
            log.error("Failed to redeem gift card code of customer {}: {}", customerId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<SearchGCBActivityResponse> search(int page, int size,
                                                          Sort.Direction direction, GCBActivitySortProperty property,
                                                          SearchGCBActivityRequest request, Principal principal) {
        try {
            Page<SearchGCBActivityResponse> pageActivityResponse = giftCardBalanceService
                    .search(page, size, direction, property, request)
                    .map(giftCardBalanceMapper::toGCBActivityResponse);

            log.info("Search gift card activity successfully from {}", principal.getName());

            return PageResponse.<SearchGCBActivityResponse>builder()
                    .content(pageActivityResponse.getContent())
                    .page(pageActivityResponse.getPageable().getPageNumber())
                    .size(pageActivityResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageActivityResponse.getTotalPages())
                    .totalElements(pageActivityResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search gift card activity: {}", e.getMessage(), e);
            throw e;
        }
    }
}
