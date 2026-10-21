package com.hamsoo.bpm.model.dto.resourceitem;

import com.hamsoo.bpm.common.exception.ErrorDetails;
import com.hamsoo.bpm.model.dto.feature.FeatureDto;

import java.util.List;

public class ResourceItemDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private FeatureDto featureDto;
    //ALL ROLE FROM APP_BASED_ROLE ORGAN_BASED_ROLE MODEL_BASED_ROLE
    private List<String> grantedRoles;
    private String type;
    private ErrorDetails errorDetails;

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

    public FeatureDto getFeatureDto() {
        return featureDto;
    }

    public void setFeatureDto(FeatureDto featureDto) {
        this.featureDto = featureDto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(ErrorDetails errorDetails) {
        this.errorDetails = errorDetails;
    }

    public List<String> getGrantedRoles() {
        return grantedRoles;
    }

    public void setGrantedRoles(List<String> grantedRoles) {
        this.grantedRoles = grantedRoles;
    }

}
