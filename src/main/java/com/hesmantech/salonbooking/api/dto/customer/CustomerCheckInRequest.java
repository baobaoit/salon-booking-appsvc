package com.hesmantech.salonbooking.api.dto.customer;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record CustomerCheckInRequest(
        UUID technicianId,
        String clientNotes,
        Set<UUID> services
) {
    public Set<UUID> services() {
        return Objects.requireNonNullElseGet(services, Set::of);
    }

    public String clientNotes() {
        return Objects.requireNonNullElse(clientNotes, "");
    }
}
