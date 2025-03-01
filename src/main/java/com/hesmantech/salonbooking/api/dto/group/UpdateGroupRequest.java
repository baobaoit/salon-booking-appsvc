package com.hesmantech.salonbooking.api.dto.group;

import jakarta.validation.constraints.NotBlank;

public record UpdateGroupRequest(
        @NotBlank String name
) {
}
