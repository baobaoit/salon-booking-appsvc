package com.hesmantech.salonbooking.exception.credit;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class CreditNotFoundException extends AbstractNotFoundException {
    public CreditNotFoundException(UUID customerId) {
        super("Credit of customer with id " + customerId);
    }
}
