package com.hamsoo.bpm.model.dto.externalprocess;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ExternalProcessDto {

    private Long id;
    @NotBlank(message = "error.externalProcess.nameIsNotValid")
    @Size(max = 100, message = "error.externalProcess.nameMaxLengthIsNotValid")
    private String name;

    //search property
    private Boolean filterHasReference = false;

    //return property
    private Long organId;

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

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Boolean getFilterHasReference() {
        return filterHasReference;
    }

    public void setFilterHasReference(Boolean filterHasReference) {
        this.filterHasReference = filterHasReference;
    }
}
