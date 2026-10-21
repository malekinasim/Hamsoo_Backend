package com.hamsoo.bpm.model.dto.identity;

import javax.validation.constraints.NotNull;

public class IdentityComplicationDto {

    private Long id;
    @NotNull(message = "error.modelVersionIdNotValid")
    private Long modelVersionId;
    private String status;
    private String name;
    private String idea;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }
}
