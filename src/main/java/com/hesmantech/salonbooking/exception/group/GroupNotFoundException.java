package com.hesmantech.salonbooking.exception.group;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class GroupNotFoundException extends AbstractNotFoundException {
    public GroupNotFoundException(UUID id) {
        super("Group with id " + id);
    }
}
