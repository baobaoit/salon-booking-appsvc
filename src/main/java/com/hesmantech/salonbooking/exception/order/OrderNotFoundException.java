package com.hesmantech.salonbooking.exception.order;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class OrderNotFoundException extends AbstractNotFoundException {
    public OrderNotFoundException(UUID id, UUID customerId) {
        super("Order with id " + id + " of customer id " + customerId);
    }

    public OrderNotFoundException(UUID id) {
        super("Order with id " + id);
    }
}
