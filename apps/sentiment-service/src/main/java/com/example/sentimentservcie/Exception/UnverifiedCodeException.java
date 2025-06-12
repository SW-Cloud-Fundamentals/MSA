package com.example.sentimentservcie.Exception;

public class UnverifiedCodeException extends RuntimeException{
    public UnverifiedCodeException(String message) {
        super(message);
    }
}
