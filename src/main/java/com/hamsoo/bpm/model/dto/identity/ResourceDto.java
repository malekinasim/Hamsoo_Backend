package com.hamsoo.bpm.model.dto.identity;

import com.hamsoo.bpm.model.dto.resourcecatalog.ResourceCatalogDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ResourceDto {

    private String id;
    private Long resourceCatalogId;
    private Long postId;
    @NotNull(message = "error.ioSchema.resourceTypeNotValid")
    private ResourceSchema.ResourceType resourceType;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    //return property
    private ResourceCatalogDto resourceCatalogDto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getResourceCatalogId() {
        return resourceCatalogId;
    }

    public void setResourceCatalogId(Long resourceCatalogId) {
        this.resourceCatalogId = resourceCatalogId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public ResourceSchema.ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceSchema.ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public ResourceCatalogDto getResourceCatalogDto() {
        return resourceCatalogDto;
    }

    public void setResourceCatalogDto(ResourceCatalogDto resourceCatalogDto) {
        this.resourceCatalogDto = resourceCatalogDto;
    }
}
