package com.hesmantech.salonbooking.exception.giftcard;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class GiftCardNotFoundException extends AbstractNotFoundException {
    public GiftCardNotFoundException(UUID id) {
        super("Gift card with id " + id);
    }

    public GiftCardNotFoundException(String code) {
        super("Gift card with code " + code);
    }
}
