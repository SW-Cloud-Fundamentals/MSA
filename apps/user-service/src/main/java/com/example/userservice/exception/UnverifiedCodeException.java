package com.example.userservice.exception;

public class UnverifiedCodeException extends RuntimeException{
    public UnverifiedCodeException(String message) {
        super(message);
    }
}
