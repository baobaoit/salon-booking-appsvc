package com.hesmantech.salonbooking.api.dto.customer;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String userId,
        String phoneNumber,
        String dob,
        String email,
        String gender
) {
}
