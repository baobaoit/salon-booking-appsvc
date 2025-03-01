package com.hesmantech.salonbooking.exception.giftcard;

import java.util.UUID;

public class GiftCardIsNotRedeemableException extends RuntimeException {
    public GiftCardIsNotRedeemableException(UUID id) {
        super("Gift card with id " + id + " is not redeemable");
    }

    public GiftCardIsNotRedeemableException(String code) {
        super("Gift card with code " + code + " is not redeemable");
    }
}
