package com.hamsoo.bpm.model.dto.role;

import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;

import java.util.List;

public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String roleType;
    private List<GrantedResourceDto> resourceRoleDtoList;

    private String roleGroup;

    public String getRoleGroup() {
        return this.roleGroup;
    }

    public void setRoleGroup(String roleGroup) {
        this.roleGroup = roleGroup;
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

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public List<GrantedResourceDto> getResourceRoleDtoList() {
        return resourceRoleDtoList;
    }

    public void setResourceRoleDtoList(List<GrantedResourceDto> resourceRoleDtoList) {
        this.resourceRoleDtoList = resourceRoleDtoList;
    }
}
