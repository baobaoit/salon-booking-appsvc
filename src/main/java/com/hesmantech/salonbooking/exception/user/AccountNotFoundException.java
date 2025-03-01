package com.hesmantech.salonbooking.exception.user;

import com.hesmantech.salonbooking.exception.base.AbstractNotFoundException;

import java.util.UUID;

public class AccountNotFoundException extends AbstractNotFoundException {
    public AccountNotFoundException(UUID id) {
        super("Account with id " + id);
    }

    public AccountNotFoundException(String username) {
        super("Account with username " + username);
    }
}
