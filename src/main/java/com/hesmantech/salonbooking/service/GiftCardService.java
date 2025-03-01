package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.giftcard.CreateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.UpdateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface GiftCardService {
    GiftCardEntity create(CreateGiftCardRequest request);

    GiftCardEntity getDetails(UUID id);

    GiftCardEntity update(UUID id, UpdateGiftCardRequest request);

    String generateGiftCardCode();

    Page<GiftCardEntity> search(int page, int size, Sort.Direction direction,
                                GiftCardSortProperty property, SearchGiftCardRequest request);

    GiftCardEntity deactive(UUID id);

    GiftCardEntity redeem(UUID id, UUID customerId);

    boolean unlink(UUID id, UUID customerId);
}
