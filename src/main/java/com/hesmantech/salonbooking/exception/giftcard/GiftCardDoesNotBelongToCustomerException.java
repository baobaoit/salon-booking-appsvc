package com.hesmantech.salonbooking.exception.giftcard;

public class GiftCardDoesNotBelongToCustomerException extends RuntimeException {
    public GiftCardDoesNotBelongToCustomerException(String code, String customerName) {
        super("Gift card " + code + " does not belong to customer " + customerName);
    }
}
