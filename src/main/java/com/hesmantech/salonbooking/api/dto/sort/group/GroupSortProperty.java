package com.hesmantech.salonbooking.api.dto.sort.group;

import lombok.Getter;

@Getter
public enum GroupSortProperty {
    CREATED_DATE("createdDate"),
    ID("id"),
    NAME("name");

    private final String property;

    GroupSortProperty(String property) {
        this.property = property;
    }
}
