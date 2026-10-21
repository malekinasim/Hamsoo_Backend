package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityResourceDto;
import com.hamsoo.bpm.model.entity.identity.IdentityResource;
import com.hamsoo.bpm.repository.jpa.identity.IdentityResourceRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;
import java.util.Map;

public interface IdentityResourceService extends BaseService<IdentityResource, Long, IdentityResourceRepository> {

//    ResourceDto addResourceSchema(Long modelVersionId, ResourceDto resourceDto);
//
//    void deleteResourceSchema(Long modelVersionId, String resourceSchemaId);
//
//    List<ResourceDto> getResourceSchemaList(Long modelVersionId);
//
//    List<ResourceDto> getResourceDetailsList(Long modelVersionId);

    IdentityResource findByIdOrElseThrow(Long id);

    IdentityResource create(IdentityResourceDto IdentityResourceDto);

    void passive(Long id);

    IdentityResourceDto mapToDto(IdentityResource IdentityResource);

    Map<String, List<IdentityResourceDto>> list(IdentityResourceDto IdentityResourceDto);

//    Long countByKpiCatalogId(Long IdentityResourceId);

    IdentityResource clone(IdentityResource IdentityResource);

    List<IdentityResource> findActiveByModelVersionId(Long modelVersionId);

    void copyIdentityResource(Long modelVersionIdFrom, Long modelVersionIdTo);

    IdentityResource edit(Long id, IdentityResourceDto IdentityResourceDto);

}
