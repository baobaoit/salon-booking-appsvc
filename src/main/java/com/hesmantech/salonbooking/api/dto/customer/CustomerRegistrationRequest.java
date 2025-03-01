package com.hesmantech.salonbooking.api.dto.customer;

import com.hesmantech.salonbooking.domain.model.user.UserGender;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CustomerRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        LocalDate dob,
        String email,
        UserGender gender
) {
}
