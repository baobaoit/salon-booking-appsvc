package com.hesmantech.salonbooking.api.dto.customercredit;

import java.math.BigDecimal;

public record CustomerCreditDetails(
        String customerId,
        String customerName,
        BigDecimal totalCredit,
        BigDecimal availableCredit,
        int totalNoGiftCard,
        int availableNoGiftCard,
        int redeemNoGiftCard,
        String createdBy,
        String createdDate,
        String lastModifiedBy,
        String lastModifiedDate
) {
}
