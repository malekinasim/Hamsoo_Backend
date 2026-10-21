package com.hamsoo.bpm.model.dto.modelapprover;

import com.hamsoo.bpm.model.dto.error.ErrorDto;

import java.util.List;

public class StartProcessResDTO {
    private Boolean result;
    private List<ErrorDto> errorDtoList;

    public StartProcessResDTO(Boolean result, List<ErrorDto> errorDtoList) {
        this.result = result;
        this.errorDtoList = errorDtoList.stream().sorted((err1, err2) -> 1 - err1.getStatus().compareTo(err2.getStatus())).toList();
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public List<ErrorDto> getErrorDtoList() {
        return errorDtoList;
    }

    public void setErrorDtoList(List<ErrorDto> errorDtoList) {
        this.errorDtoList = errorDtoList;
    }
}
