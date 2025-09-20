package com.tihon.outbox.handler;

import com.tihon.outbox.exception.CreateUserException;
import com.tihon.outbox.exception.UpdateUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler({CreateUserException.class, UpdateUserException.class})
    public ResponseEntity<String> handleCreateUserException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
