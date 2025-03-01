package com.hesmantech.salonbooking.exception.giftcard;

public class GiftCardAlreadyRedeemedException extends RuntimeException {
    public GiftCardAlreadyRedeemedException(String code) {
        super("Gift card already redeemed: " + code);
    }
}
