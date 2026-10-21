package com.hamsoo.bpm.model.dto.modelrole;

import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;

import java.util.List;

public class ModelRoleDto {

    private Long id;
    private String code;
    private String name;
    private List<GrantedResourceDto> grantedResourceDtos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GrantedResourceDto> getGrantedResourceDtos() {
        return grantedResourceDtos;
    }

    public void setGrantedResourceDtos(List<GrantedResourceDto> grantedResourceDtos) {
        this.grantedResourceDtos = grantedResourceDtos;
    }
}
