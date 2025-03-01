package com.hesmantech.salonbooking.exception.user;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class CustomerNotFoundException extends AbstractNotFoundException {
    public CustomerNotFoundException(UUID id) {
        super("Customer with id " + id);
    }
}
