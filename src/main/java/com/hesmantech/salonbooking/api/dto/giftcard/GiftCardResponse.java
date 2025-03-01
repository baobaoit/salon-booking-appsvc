package com.hesmantech.salonbooking.api.dto.giftcard;

import com.hesmantech.salonbooking.api.dto.user.CustomerGiftCardResponse;
import lombok.With;

import java.util.Set;

public record GiftCardResponse(
        String id,
        String giftCode,
        boolean hasExpirationDate,
        String expirationDate,
        double initialBalance,
        String createdBy,
        String notes,
        String status,
        @With Set<CustomerGiftCardResponse> customers
) {
}
