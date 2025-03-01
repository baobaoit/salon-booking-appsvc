package com.hesmantech.salonbooking.api.dto.giftcardbalance;

import com.hesmantech.salonbooking.domain.model.giftcardbalance.activity.GCBActivityType;

import java.math.BigDecimal;

public record SearchGCBActivityResponse(
        String date,
        String description,
        String orderId,
        String giftCardId,
        GCBActivityType activityType,
        BigDecimal amount,
        BigDecimal closingBalance
) {
}
