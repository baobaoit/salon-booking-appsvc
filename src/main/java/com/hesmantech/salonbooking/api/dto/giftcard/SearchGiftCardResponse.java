package com.hesmantech.salonbooking.api.dto.giftcard;

public record SearchGiftCardResponse(
        String id,
        String giftCode,
        String status,
        String customerId,
        String customerName,
        boolean isRedeemed,
        String dateIssued,
        double initialValue,
        String createdBy
) {
}
