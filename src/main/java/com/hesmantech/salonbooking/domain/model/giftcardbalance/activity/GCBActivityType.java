package com.hesmantech.salonbooking.domain.model.giftcardbalance.activity;

import lombok.Getter;

@Getter
public enum GCBActivityType {
    PAID_ORDER("Gift Card applied to order"),
    REDEEM_GIFT_CARD("Gift Card added");

    private final String description;

    GCBActivityType(String description) {
        this.description = description;
    }
}
