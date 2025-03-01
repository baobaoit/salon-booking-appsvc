package com.hesmantech.salonbooking.exception.service;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class ServiceNotFoundException extends AbstractNotFoundException {
    public ServiceNotFoundException(UUID id) {
        super("Service with id " + id);
    }
}
