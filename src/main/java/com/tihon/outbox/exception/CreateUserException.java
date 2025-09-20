package com.tihon.outbox.exception;

public class CreateUserException extends RuntimeException {
    public CreateUserException(String message, Exception e) {
        super(message, e);
    }
}
