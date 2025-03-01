package com.hesmantech.salonbooking.exception.user;

import java.util.UUID;

public class AccountDeletedException extends RuntimeException {
    public AccountDeletedException(UUID id) {
        super("Account with id " + id + " already deleted");
    }
}
