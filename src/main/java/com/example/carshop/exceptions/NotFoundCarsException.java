package com.example.carshop.exceptions;

public class NotFoundCarsException extends RuntimeException{
    public NotFoundCarsException(String message) {
        super(message);
    }
}
