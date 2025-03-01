package com.hesmantech.salonbooking.api.dto.giftcard;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record UpdateGiftCardRequest(
        @NotBlank String giftCode,
        @Min(0) double initialValue,
        Long expirationDate,
        String notes,
        Set<UUID> customers
) {
}
