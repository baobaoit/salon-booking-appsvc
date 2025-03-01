package com.hesmantech.salonbooking.api.dto.user;

public record CustomerGiftCardResponse(
        UserResponse customer,
        boolean hasRedeemed
) {
}
