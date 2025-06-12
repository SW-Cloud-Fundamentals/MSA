package com.example.sentimentservcie.Exception;

public class CodeExpiredException extends RuntimeException{
    public CodeExpiredException(String message) {
        super(message);
    }
}
