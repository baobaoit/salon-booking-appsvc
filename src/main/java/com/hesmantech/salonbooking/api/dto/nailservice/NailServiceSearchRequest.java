package com.hesmantech.salonbooking.api.dto.nailservice;

import com.hesmantech.salonbooking.domain.model.service.ServiceStatus;

import java.util.List;
import java.util.Optional;

public record NailServiceSearchRequest(
        String name,
        List<ServiceStatus> statuses
) {
    public List<ServiceStatus> statuses() {
        return Optional.ofNullable(statuses).orElse(List.of());
    }
}
