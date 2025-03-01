package com.hesmantech.salonbooking.exception.giftcardbalance;

public class GiftCardBalanceIsNotEnoughToPayException extends RuntimeException {
    public GiftCardBalanceIsNotEnoughToPayException() {
        super("Your gift card balance is not enough to pay");
    }
}
