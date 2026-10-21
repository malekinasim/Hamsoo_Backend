package com.hamsoo.bpm.model.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceSchema {

    private String id;
    private Long resourceCatalogId;
    private Long postId;
    private ResourceType resourceType;

    //audit property
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public ResourceSchema() {
    }

    public ResourceSchema(String id) {
        this.id = id;
    }

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

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
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

    public enum ResourceType {
        POST,
        RESOURCE_CATALOG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResourceSchema e = (ResourceSchema) o;
        return (this.getId().equals(e.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
