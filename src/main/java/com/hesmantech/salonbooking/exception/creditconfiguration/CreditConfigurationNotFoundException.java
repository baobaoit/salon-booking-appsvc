package com.hesmantech.salonbooking.exception.creditconfiguration;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

public class CreditConfigurationNotFoundException extends AbstractNotFoundException {
    public CreditConfigurationNotFoundException() {
        super("Credit configuration");
    }
}
