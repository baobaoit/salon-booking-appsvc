package com.hesmantech.salonbooking.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.hesmantech.salonbooking.constants.Constants.US_PHONE_NUMBER_FORMAT;

public record AuthenticationRequest(
        @NotBlank String username,
        @NotBlank String password
) {
    public String username() {
        return Optional.of(username)
                .filter(u -> Pattern.matches(US_PHONE_NUMBER_FORMAT, username))
                .map(u -> u.replace("-", "")).orElse(username);
    }
}
