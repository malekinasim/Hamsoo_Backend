package com.hamsoo.bpm.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class BusinessException extends RuntimeException {
    private String[] args;

    public BusinessException() {
    }

    public BusinessException(String message, String... args) {
        super(message);
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
