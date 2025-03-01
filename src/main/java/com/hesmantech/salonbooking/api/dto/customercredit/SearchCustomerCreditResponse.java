package com.hesmantech.salonbooking.api.dto.customercredit;

import java.math.BigDecimal;

public record SearchCustomerCreditResponse(
        String customerId,
        String customerName,
        BigDecimal availableCredit,
        BigDecimal totalCredit,
        int availableNoGiftCard,
        int redeemNoGiftCard
) {
}
