package com.hesmantech.salonbooking.exception.order;

import java.util.UUID;

public class OrderAlreadyCheckedOutException extends RuntimeException {
    public OrderAlreadyCheckedOutException(UUID id) {
        super("Order with id " + id + " already checked out");
    }
}
