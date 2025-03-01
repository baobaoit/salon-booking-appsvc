package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.giftcardbalance.RedeemByCodeRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcardbalance.GCBActivitySortProperty;
import com.hesmantech.salonbooking.domain.GiftCardBalanceActivityEntity;
import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface GiftCardBalanceService {
    GiftCardBalanceEntity getLatestGiftCardBalance(UUID customerId);

    GiftCardBalanceEntity redeemGiftCardCode(UUID customerId, RedeemByCodeRequest request);

    void updateWhenRedeemGiftCard(GiftCardEntity giftCard);

    void updateWhenCheckOutOrder(OrderEntity order);

    Page<GiftCardBalanceActivityEntity> search(int page, int size, Sort.Direction direction, GCBActivitySortProperty property,
                                               SearchGCBActivityRequest request);
}
