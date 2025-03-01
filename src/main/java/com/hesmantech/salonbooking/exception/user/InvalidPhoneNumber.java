package com.hesmantech.salonbooking.exception.user;

public class InvalidPhoneNumber extends RuntimeException {
    public InvalidPhoneNumber() {
        super("Invalid phone number");
    }
}
