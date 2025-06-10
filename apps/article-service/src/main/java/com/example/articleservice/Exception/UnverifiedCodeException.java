package com.example.articleservice.Exception;

public class UnverifiedCodeException extends RuntimeException{
    public UnverifiedCodeException(String message) {
        super(message);
    }
}
