package com.example.carshop.exceptions;

public class NotPremiumUserException extends RuntimeException {
    public NotPremiumUserException(String message) {
        super(message);
    }
}
