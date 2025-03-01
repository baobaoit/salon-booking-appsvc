package com.hesmantech.salonbooking.exception.order;

import java.util.UUID;

public class OrderIsWaitingServiceException extends RuntimeException {
    public OrderIsWaitingServiceException(UUID id) {
        super(String.format("Order ID '%s' is not yet assigned to a technician, so it cannot be checked out", id));
    }
}
