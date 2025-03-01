package com.hesmantech.salonbooking.api.dto.sort.giftcard;

import lombok.Getter;

@Getter
public enum GiftCardSortProperty {
    DATE_ISSUED("dateIssued", "createdDate"),
    CODE("code", "code"),
    STATUS("status", "status"),
    INITIAL_VALUE("initialValue", "initialValue");

    private final String property;
    private final String dbColumn;

    GiftCardSortProperty(String property, String dbColumn) {
        this.property = property;
        this.dbColumn = dbColumn;
    }

    public static GiftCardSortProperty fromDbColumn(String dbColumn) {
        for (GiftCardSortProperty property : GiftCardSortProperty.values()) {
            if (property.getDbColumn().equals(dbColumn)) {
                return property;
            }
        }
        return null;
    }
}
