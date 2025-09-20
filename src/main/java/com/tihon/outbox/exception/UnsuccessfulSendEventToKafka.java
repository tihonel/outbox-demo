package com.tihon.outbox.exception;

public class UnsuccessfulSendEventToKafka extends RuntimeException {
    public UnsuccessfulSendEventToKafka(String message, Exception e) {
        super(message, e);
    }
}
