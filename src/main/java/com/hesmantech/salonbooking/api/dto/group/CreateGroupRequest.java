package com.hesmantech.salonbooking.api.dto.group;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupRequest(
        @NotBlank String name
) {
}
