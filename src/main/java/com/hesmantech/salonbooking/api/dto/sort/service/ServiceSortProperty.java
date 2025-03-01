package com.hesmantech.salonbooking.api.dto.sort.service;

import lombok.Getter;

@Getter
public enum ServiceSortProperty {
    CREATED_DATE("createdDate"),
    ID("id"),
    NAME("name"),
    GROUP_NAME("group.name");

    private final String property;

    ServiceSortProperty(String property) {
        this.property = property;
    }
}
