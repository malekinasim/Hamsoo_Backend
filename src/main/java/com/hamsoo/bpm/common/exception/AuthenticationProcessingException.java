package com.hamsoo.bpm.common.exception;

public class AuthenticationProcessingException extends RuntimeException {
    Object[] args;

    public AuthenticationProcessingException() {
    }

    public AuthenticationProcessingException(String message) {
        super(message);
    }

    public AuthenticationProcessingException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}
