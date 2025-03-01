package com.hesmantech.salonbooking.api.dto.sort.order;

import lombok.Getter;

@Getter
public enum OrderSortProperty {
    CREATED_DATE("createdDate"),
    ID("id"),
    STATUS("status"),
    TOTAL_PRICE("price"),
    CHECK_OUT_TIME("checkOutTime");

    private final String property;

    OrderSortProperty(String property) {
        this.property = property;
    }
}
