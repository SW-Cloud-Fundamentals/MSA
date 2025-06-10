package com.example.userservice.exception;

public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException(String message) {
        super(message);
    }
}
