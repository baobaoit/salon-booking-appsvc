package com.hesmantech.salonbooking.api.dto.giftcard;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record CreateGiftCardRequest(
        @NotBlank String giftCode,
        @Min(0) double initialValue,
        Long expirationDate,
        String notes,
        Set<UUID> customers
) {
    public Set<UUID> customers() {
        return Objects.requireNonNullElseGet(customers, Set::of);
    }
}
