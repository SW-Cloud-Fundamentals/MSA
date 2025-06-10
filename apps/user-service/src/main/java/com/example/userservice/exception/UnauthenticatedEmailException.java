package com.example.userservice.exception;

public class UnauthenticatedEmailException extends RuntimeException{
    public UnauthenticatedEmailException(String message) {
        super(message);
    }
}
