package com.hamsoo.bpm.model.dto.modelelementereference;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AssigneeDto {

    private Long modelElementReferenceId;
    @NotNull(message = "error.AssigneeDto.IdIsNotValid")
    private Long id;
    private String code;
    private String name;
    @NotBlank(message = "error.AssigneeDto.typeNotValid")
    private String type;

    public Long getModelElementReferenceId() {
        return modelElementReferenceId;
    }

    public void setModelElementReferenceId(Long modelElementReferenceId) {
        this.modelElementReferenceId = modelElementReferenceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
