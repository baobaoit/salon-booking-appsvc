package com.hesmantech.salonbooking.exception.role;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

public class RoleNotFoundException extends AbstractNotFoundException {
    public RoleNotFoundException(String roleId) {
        super("Role with id " + roleId);
    }
}
