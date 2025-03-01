package com.hesmantech.salonbooking.api.dto.giftcardbalance;

import jakarta.validation.constraints.NotBlank;

public record RedeemByCodeRequest(
        @NotBlank String giftCode
) {
}
