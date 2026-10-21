package com.hamsoo.bpm.common.exception;

import com.hamsoo.bpm.model.dto.error.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class GeneralErrorException extends RuntimeException {

    private final List<ErrorDto> errorDtoList;

    public GeneralErrorException(List<ErrorDto> errorDtoList) {
        super();
        this.errorDtoList = errorDtoList;
    }

    public List<ErrorDto> getErrorDtoList() {
        return errorDtoList;
    }
}
