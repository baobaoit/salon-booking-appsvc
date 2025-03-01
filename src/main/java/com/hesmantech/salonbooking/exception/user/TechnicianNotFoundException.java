package com.hesmantech.salonbooking.exception.user;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class TechnicianNotFoundException extends AbstractNotFoundException {
    public TechnicianNotFoundException(UUID id) {
        super("Technician with id " + id);
    }
}
