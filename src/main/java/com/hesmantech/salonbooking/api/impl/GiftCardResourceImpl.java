package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.GiftCardResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.CreateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.GiftCardCode;
import com.hesmantech.salonbooking.api.dto.giftcard.GiftCardResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.UpdateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.mapper.GiftCardMapper;
import com.hesmantech.salonbooking.mapper.UserMapper;
import com.hesmantech.salonbooking.service.CreditService;
import com.hesmantech.salonbooking.service.GiftCardBalanceService;
import com.hesmantech.salonbooking.service.GiftCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gift-cards")
@RequiredArgsConstructor
@Slf4j
public class GiftCardResourceImpl implements GiftCardResource {
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private static final GiftCardMapper giftCardMapper = GiftCardMapper.INSTANCE;
    private final GiftCardService giftCardService;
    private final CreditService creditService;
    private final GiftCardBalanceService giftCardBalanceService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GiftCardResponse create(CreateGiftCardRequest createGiftCardRequest, Principal principal) {
        try {
            GiftCardEntity giftCard = giftCardService.create(createGiftCardRequest);
            GiftCardResponse giftCardResponse = giftCardMapper.toGiftCardResponse(giftCard)
                    .withCustomers(userMapper.toCustomerGiftCardResponseSet(giftCard.getCustomers()));

            log.info("Create a new gift card successfully from {}", principal.getName());

            return giftCardResponse;
        } catch (Exception e) {
            log.error("Failed to create a new gift card: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GiftCardResponse getDetails(@PathVariable UUID id, Principal principal) {
        try {
            GiftCardEntity giftCard = giftCardService.getDetails(id);
            GiftCardResponse giftCardResponse = giftCardMapper.toGiftCardResponse(giftCard)
                    .withCustomers(userMapper.toCustomerGiftCardResponseSet(giftCard.getCustomers()));

            log.info("Get gift card by id {} successfully from {}", id, principal.getName());

            return giftCardResponse;
        } catch (Exception e) {
            log.error("Failed to get gift card: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GiftCardResponse update(@PathVariable UUID id, UpdateGiftCardRequest updateGiftCardRequest, Principal principal) {
        try {
            GiftCardEntity giftCard = giftCardService.update(id, updateGiftCardRequest);
            GiftCardResponse giftCardResponse = giftCardMapper.toGiftCardResponse(giftCard)
                    .withCustomers(userMapper.toCustomerGiftCardResponseSet(giftCard.getCustomers()));

            log.info("Update gift card with id {} successfully from {}", id, principal.getName());

            return giftCardResponse;
        } catch (Exception e) {
            log.error("Failed to update gift card: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/code")
    @Override
    public GiftCardCode generateGiftCardCode(Principal principal) {
        try {
            GiftCardCode giftCardCode = new GiftCardCode(giftCardService.generateGiftCardCode());

            log.info("Generate gift card code successfully from {}", principal.getName());

            return giftCardCode;
        } catch (Exception e) {
            log.error("Failed to generate gift card code: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<SearchGiftCardResponse> search(int page, int size,
                                                       Sort.Direction direction, GiftCardSortProperty property,
                                                       SearchGiftCardRequest request, Principal principal) {
        try {
            Page<SearchGiftCardResponse> pageGiftCardResponse = giftCardService.search(page, size, direction, property, request)
                    .map(giftCardMapper::toSearchGiftCardResponse);

            log.info("Search gift card successfully from {}", principal.getName());


            return PageResponse.<SearchGiftCardResponse>builder()
                    .content(pageGiftCardResponse.getContent())
                    .page(pageGiftCardResponse.getPageable().getPageNumber())
                    .size(pageGiftCardResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageGiftCardResponse.getTotalPages())
                    .totalElements(pageGiftCardResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search gift card: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GiftCardResponse deactivate(@PathVariable UUID id, Principal principal) {
        try {
            GiftCardEntity giftCard = giftCardService.deactive(id);
            GiftCardResponse giftCardResponse = giftCardMapper.toGiftCardResponse(giftCard)
                    .withCustomers(userMapper.toCustomerGiftCardResponseSet(giftCard.getCustomers()));

            log.info("Deactivate gift card with id {} successfully from {}", id, principal.getName());

            return giftCardResponse;
        } catch (Exception e) {
            log.error("Failed to deactivate gift card: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/redeem/{customerId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public SearchGiftCardResponse redeem(@PathVariable UUID id, @PathVariable UUID customerId, Principal principal) {
        try {
            GiftCardEntity giftCard = giftCardService.redeem(id, customerId);
            giftCardBalanceService.updateWhenRedeemGiftCard(giftCard);
            creditService.updateBasedOn(giftCard);
            SearchGiftCardResponse searchGiftCardResponse = giftCardMapper.toSearchGiftCardResponse(giftCard);

            log.info("Redeem gift card with id {} successfully from {}", id, principal.getName());

            return searchGiftCardResponse;
        } catch (Exception e) {
            log.error("Failed to redeem gift card: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/unlink/{customerId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public boolean unlink(@PathVariable UUID id, @PathVariable UUID customerId, Principal principal) {
        try {
            boolean isUnlinkSuccess = giftCardService.unlink(id, customerId);

            log.info("Unlink gift card with id {} successfully from {}", id, principal.getName());

            return isUnlinkSuccess;
        } catch (Exception e) {
            log.error("Failed to unlink gift card: {}", e.getMessage(), e);
            throw e;
        }
    }
}
