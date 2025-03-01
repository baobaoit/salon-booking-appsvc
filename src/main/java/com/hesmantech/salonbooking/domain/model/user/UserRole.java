package com.hesmantech.salonbooking.domain.model.user;

public enum UserRole {
    ROLE_MANAGER,
    ROLE_TECHNICIAN,
    ROLE_CUSTOMER;

    public String id() {
        return name();
    }
}
