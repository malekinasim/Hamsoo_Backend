package com.hamsoo.bpm.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ValidationException extends RuntimeException {

    private Errors errors;

    public ValidationException(Errors errors) {
        super();
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
