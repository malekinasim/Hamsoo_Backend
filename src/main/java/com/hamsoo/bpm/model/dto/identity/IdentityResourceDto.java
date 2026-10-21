package com.hamsoo.bpm.model.dto.identity;

import javax.validation.constraints.NotNull;

public class IdentityResourceDto {

    private Long id;
    @NotNull(message = "error.modelVersionIdNotValid")
    private Long modelVersionId;
    @NotNull(message = "error.resourceCatalogId")
    private Long resourceCatalogId;
    private String status;
    private String name;
    private String type;
    private Long resourceTypeId;

    public Long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public IdentityResourceDto()
    {
    }
    public IdentityResourceDto(String name,Long id,String type,Long modelVersionId,Long resourceCatalogId,Long resourceTypeId)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.modelVersionId = modelVersionId;
        this.resourceCatalogId = resourceCatalogId;
        this.resourceTypeId = resourceTypeId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public Long getResourceCatalogId() {
        return resourceCatalogId;
    }

    public void setResourceCatalogId(Long resourceCatalogId) {
        this.resourceCatalogId = resourceCatalogId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
