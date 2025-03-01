package com.hesmantech.salonbooking.api.dto.user;

import com.hesmantech.salonbooking.domain.model.user.UserGender;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        LocalDate dob,
        String email,
        UserGender gender,
        UserRole role
) {
}
