package com.hesmantech.salonbooking.domain.model.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    WAITING_SERVICE("Waiting service"),
    IN_SERVICE("In service"),
    CANCEL("Cancel"),
    CHECK_OUT("Check out");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
