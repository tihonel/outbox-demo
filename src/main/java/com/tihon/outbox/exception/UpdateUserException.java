package com.tihon.outbox.exception;

public class UpdateUserException extends RuntimeException {
    public UpdateUserException(String message, Exception e) {
        super(message, e);
    }
}
