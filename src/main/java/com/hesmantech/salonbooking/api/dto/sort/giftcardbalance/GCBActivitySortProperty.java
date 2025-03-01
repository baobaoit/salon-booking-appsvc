package com.hesmantech.salonbooking.api.dto.sort.giftcardbalance;

import lombok.Getter;

@Getter
public enum GCBActivitySortProperty {
    CREATED_DATE("createdDate"),
    AMOUNT("amount"),
    CLOSING_BALANCE("closingBalance");

    private final String property;

    GCBActivitySortProperty(String property) {
        this.property = property;
    }
}
