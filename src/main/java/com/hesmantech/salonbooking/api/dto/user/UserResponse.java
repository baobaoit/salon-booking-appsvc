package com.hesmantech.salonbooking.api.dto.user;

public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String userId,
        String phoneNumber,
        String role,
        String gender,
        String dob,
        String email,
        String status
) {
}
