package com.hamsoo.bpm.model.dto.resourcecatalog;

import com.hamsoo.bpm.model.dto.resourcetype.ResourceTypeDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ResourceCatalogDto {

    private Long id;
    @NotBlank(message = "error.resourceCatalog.nameIsNotValid")
    private String name;
    @NotNull(message = "error.resourceCatalog.resourceTypeIdIsNotValid")
    private Long resourceTypeId;
    private Long organId;

    //return property
    private ResourceTypeDto resourceTypeDto;

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

    public Long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public ResourceTypeDto getResourceTypeDto() {
        return resourceTypeDto;
    }

    public void setResourceTypeDto(ResourceTypeDto resourceTypeDto) {
        this.resourceTypeDto = resourceTypeDto;
    }
}
