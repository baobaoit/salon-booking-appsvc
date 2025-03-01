package com.hesmantech.salonbooking.api.dto.user;

import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;

import java.util.List;
import java.util.Optional;

public record SearchUserRequest(
        List<UserRole> userRoles,
        String name,
        String phoneNumber,
        List<UserStatus> statuses
) {
    public List<UserStatus> statuses() {
        return Optional.ofNullable(statuses).orElse(List.of());
    }
}
