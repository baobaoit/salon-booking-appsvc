package com.hesmantech.salonbooking.api.dto.user;

import com.hesmantech.salonbooking.domain.model.user.UserGender;

import java.time.LocalDate;

public record UpdateUserDetailsRequest(
        String firstName,
        String lastName,
        String userId,
        String phoneNumber,
        LocalDate dob,
        String email,
        UserGender gender
) {
}
