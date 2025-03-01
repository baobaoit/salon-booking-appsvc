package com.hesmantech.salonbooking.exception.base;

public abstract class AbstractNotFoundException extends RuntimeException {
    protected AbstractNotFoundException(String message) {
        super(message + (message.charAt(message.length() - 1) == ' ' ? "not found" : " not found"));
    }
}
