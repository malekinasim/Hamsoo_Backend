package com.hamsoo.bpm.model.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityDataSchema {

    private String mission;
    private Long processCategoryId;
    private String scope;

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public Long getProcessCategoryId() {
        return processCategoryId;
    }

    public void setProcessCategoryId(Long processCategoryId) {
        this.processCategoryId = processCategoryId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
