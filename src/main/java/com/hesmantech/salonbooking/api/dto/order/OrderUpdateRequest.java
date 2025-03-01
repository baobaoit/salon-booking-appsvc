package com.hesmantech.salonbooking.api.dto.order;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record OrderUpdateRequest(
        String clientNotes,
        UUID technicianId,
        Set<UUID> services
) {
    public Set<UUID> services() {
        return Objects.requireNonNullElseGet(services, Set::of);
    }
}
