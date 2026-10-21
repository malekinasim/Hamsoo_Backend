package com.hamsoo.bpm.model.dto.grantedresourse;

import com.hamsoo.bpm.model.dto.resourceitem.ResourceItemDto;

public class GrantedResourceDto {
    private Long id;
    private Long roleId;
    private Long modelRoleId;
    private ResourceItemDto resourceItemDto;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getModelRoleId() {
        return modelRoleId;
    }

    public void setModelRoleId(Long modelRoleId) {
        this.modelRoleId = modelRoleId;
    }


    public ResourceItemDto getResourceItemDto() {
        return resourceItemDto;
    }

    public void setResourceItemDto(ResourceItemDto resourceItemDto) {
        this.resourceItemDto = resourceItemDto;
    }

}
