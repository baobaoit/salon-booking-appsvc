package com.hesmantech.salonbooking.api.dto.sort.user;

import lombok.Getter;

@Getter
public enum UserSortProperty {
    CREATED_DATE("createdDate"),
    ID("id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    USER_ID("userId"),
    PHONE_NUMBER("phoneNumber"),
    ROLE_ID("roleId");

    private final String property;

    UserSortProperty(String property) {
        this.property = property;
    }
}
