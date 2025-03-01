package com.hesmantech.salonbooking.api.dto.giftcardbalance;

import java.math.BigDecimal;

public record LatestGCBResponse(
        BigDecimal balance
) {
}
