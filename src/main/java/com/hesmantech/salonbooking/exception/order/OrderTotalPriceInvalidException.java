package com.hesmantech.salonbooking.exception.order;

public class OrderTotalPriceInvalidException extends RuntimeException {
    public OrderTotalPriceInvalidException(double subtotal, double discount, double giftCardBalance) {
        super("The order total price must be %.2f".formatted(
                Double.compare(giftCardBalance, (subtotal - discount)) >= 0 ?
                        0 : subtotal - discount - giftCardBalance));
    }
}
