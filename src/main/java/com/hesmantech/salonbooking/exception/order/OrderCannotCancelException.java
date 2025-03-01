package com.hesmantech.salonbooking.exception.order;

import java.util.UUID;

public class OrderCannotCancelException extends RuntimeException {
    public OrderCannotCancelException(UUID id) {
        super("Order with id " + id + " already cancelled or checked out");
    }
}
