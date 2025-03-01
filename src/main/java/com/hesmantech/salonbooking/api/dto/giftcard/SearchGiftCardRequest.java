package com.hesmantech.salonbooking.api.dto.giftcard;

import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;

import java.util.Objects;
import java.util.Set;

public record SearchGiftCardRequest(
        String code,
        Set<GiftCardStatus> statuses
) {
    public Set<GiftCardStatus> statuses() {
        return Objects.requireNonNullElseGet(statuses, Set::of);
    }
}
