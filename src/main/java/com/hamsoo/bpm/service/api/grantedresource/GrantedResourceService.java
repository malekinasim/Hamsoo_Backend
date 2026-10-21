package com.hamsoo.bpm.service.api.grantedresource;

import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;
import com.hamsoo.bpm.model.entity.grantedresource.GrantedResource;
import com.hamsoo.bpm.repository.jpa.grantedesource.GrantedResourceRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface GrantedResourceService extends BaseService<GrantedResource, Long, GrantedResourceRepository> {

    List<GrantedResourceDto> findAllByRoleIdAndCurrentOrganSalePlan(Long roleId, Long OrganId);
    //List<GrantedResourceDto> findAllBasedOnOrganPlanByRoleId(Long roleId);

    List<GrantedResourceDto> findAllByModelRoleId(Long roleId);

    GrantedResourceDto mapToDto(GrantedResource grantedResource);

    List<GrantedResource> findAllGrantedResourceByResourceId(Long id);

}
