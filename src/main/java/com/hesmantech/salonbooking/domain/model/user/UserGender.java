package com.hesmantech.salonbooking.domain.model.user;

import lombok.Getter;

@Getter
public enum UserGender {
    OTHER("Other"),
    MALE("Male"),
    FEMALE("Female");

    private final String value;

    UserGender(String value) {
        this.value = value;
    }
}
