package com.hesmantech.salonbooking.api.dto.customer;

import com.hesmantech.salonbooking.api.dto.customer.checkout.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record CustomerCheckOutRequest(
        @NotNull UUID orderId,
        @NotNull UUID technicianId,
        @NotNull @Min(0) Double subtotalPrice,
        @NotNull @Min(0) Double totalPrice,
        String checkOutNotes,
        @NotNull @Min(0) Double discount,
        @NotEmpty Set<UUID> services,
        PaymentMethod paymentMethod
) {
    public PaymentMethod paymentMethod() {
        return Objects.requireNonNullElse(paymentMethod, PaymentMethod.OTHER);
    }
}
