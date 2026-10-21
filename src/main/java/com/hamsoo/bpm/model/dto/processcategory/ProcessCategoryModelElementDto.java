package com.hamsoo.bpm.model.dto.processcategory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProcessCategoryModelElementDto {

    private Long id;
    @NotBlank(message = "error.processCategoryModelElement.elementIdIsNotValid")
    @Size(max = 100, message = "error.processCategoryModelElement.elementIdMaxLengthIsNotValid")
    private String elementId;
    @NotNull(message = "error.processCategoryModelElement.modelVersionIdIsNotValid")
    private Long modelVersionId;
    @NotNull(message = "error.processCategoryModelElement.processCategoryIdIsNotValid")
    private Long processCategoryId;

    //return property
    private ProcessCategoryDto processCategoryDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public Long getProcessCategoryId() {
        return processCategoryId;
    }

    public void setProcessCategoryId(Long processCategoryId) {
        this.processCategoryId = processCategoryId;
    }

    public ProcessCategoryDto getProcessCategoryDto() {
        return processCategoryDto;
    }

    public void setProcessCategoryDto(ProcessCategoryDto processCategoryDto) {
        this.processCategoryDto = processCategoryDto;
    }
}
