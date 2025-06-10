package com.example.articleservice.Exception;

public class CodeExpiredException extends RuntimeException{
    public CodeExpiredException(String message) {
        super(message);
    }
}
