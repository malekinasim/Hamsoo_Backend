package com.hamsoo.bpm.model.dto.processcategory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProcessCategoryDto {

    private Long id;
    @NotBlank(message = "error.processCategory.nameIsNotValid")
    @Size(max = 100, message = "error.processCategory.nameMaxLengthIsNotValid")
    private String name;
    private String description;
    private String status;
    private Long organId;

    public ProcessCategoryDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }
}
