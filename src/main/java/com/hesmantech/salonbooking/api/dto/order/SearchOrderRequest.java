package com.hesmantech.salonbooking.api.dto.order;

import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record SearchOrderRequest(
        UUID customerId,
        List<OrderStatus> statuses,
        List<UserStatus> customerStatuses,
        LocalDate fromDate,
        LocalDate toDate,
        String technicianName,
        String customerName
) {
    public List<OrderStatus> statuses() {
        return Optional.ofNullable(statuses).orElse(List.of());
    }

    public List<UserStatus> customerStatuses() {
        return Optional.ofNullable(customerStatuses).orElse(List.of());
    }
}
