package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.identity.IdentityResourceDto;
import com.hamsoo.bpm.model.entity.identity.IdentityResource;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.identity.IdentityResourceRepository;
import com.hamsoo.bpm.service.api.identity.IdentityResourceService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.resourcecatalog.ResourceCatalogService;
import com.hamsoo.bpm.service.api.resourcetype.ResourceTypeService;
import org.apache.http.util.Asserts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class IdentityResourceServiceImpl implements IdentityResourceService {

    private final ModelVersionService modelVersionService;
    private final ResourceCatalogService resourceCatalogService;
    private final IdentityResourceRepository identityResourceRepository;
    private final ResourceTypeService resourceTypeService;
    private final ModelElementReferenceService modelElementReferenceService;
    public IdentityResourceServiceImpl(ModelVersionService modelVersionService,
                                       ResourceCatalogService resourceCatalogService, IdentityResourceRepository identityResourceRepository, ResourceTypeService resourceTypeService, ModelElementReferenceService modelElementReferenceService) {
        this.modelVersionService = modelVersionService;
        this.resourceCatalogService = resourceCatalogService;
        this.identityResourceRepository = identityResourceRepository;
        this.resourceTypeService = resourceTypeService;
        this.modelElementReferenceService = modelElementReferenceService;
    }
    @Override
    public IdentityResourceRepository getRepository() {
        return identityResourceRepository;
    }

    @Override
    public IdentityResource findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.IdentityResource.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(() -> new BusinessException("error.IdentityResource.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityResource create(IdentityResourceDto IdentityResourceDto) {
        Assert.notNull(IdentityResourceDto, "IdentityResourceDto is null");
        modelVersionService.checkModelVersionStatus(IdentityResourceDto.getModelVersionId());
        IdentityResource IdentityResource = new IdentityResource();
        IdentityResource.setModelVersion(modelVersionService.findByIdOrElseThrow(IdentityResourceDto.getModelVersionId()));
        IdentityResource.setResourceCatalog(resourceCatalogService.findByIdOrElseThrow(IdentityResourceDto.getResourceCatalogId()));
        IdentityResource.setStatus(Status.ACTIVE.name());
        return save(IdentityResource);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IdentityResource identityResource = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(identityResource.getModelVersion().getId());
        identityResource.setStatus(Status.PASSIVE.name());
        save(identityResource);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityResourceDto mapToDto(IdentityResource IdentityResource) {
        Assert.notNull(IdentityResource, "IdentityResource is null");
        IdentityResource = findByIdOrElseThrow(IdentityResource.getId());
        IdentityResourceDto IdentityResourceDto = new IdentityResourceDto();
        IdentityResourceDto.setId(IdentityResource.getId());
        IdentityResourceDto.setModelVersionId(IdentityResource.getModelVersion().getId());
        IdentityResourceDto.setResourceCatalogId(IdentityResourceDto.getResourceCatalogId());
        return IdentityResourceDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<IdentityResourceDto>> list(IdentityResourceDto identityResourceDto) {

        List<ResourceType>  resourceTypes = resourceTypeService.findAll();
        Map<String , List<IdentityResourceDto>> resourcesMap = new HashMap<>();
        resourceTypes.stream().forEach(resourceType -> {
             List<IdentityResourceDto> resourceNameList =  identityResourceRepository.findActiveByModelVersionIdResourceTypeId(SecurityUtils.getCurrentOrganIdOrElseThrow(), identityResourceDto.getModelVersionId(), resourceType.getId()).stream().toList();
             resourcesMap.put(resourceType.getName(), resourceNameList);
        });


        List<IdentityResourceDto> humanResourceList = new ArrayList<>();

        String[] laneType = new String[] {"LANE_ASS_POST","LANE_ASS_JOB","LANE_ASS_UNIT","LANE_ASS_USER"};

        for (String type :laneType) {
            List<IdentityResourceDto> IdentityResourceDtoList = findByReferenceTypeCodeAndModelversionId(type, identityResourceDto.getModelVersionId());

            for (IdentityResourceDto identityResourceDto1 :  IdentityResourceDtoList) {
                identityResourceDto1.setType("hr");//hr types cant delete in resourcelist
                humanResourceList.add(identityResourceDto1);
            }
        }


        resourcesMap.put(ResourceBundleUtils.getMessage("identity.humanResource"), humanResourceList);

        return resourcesMap;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityResource clone(IdentityResource IdentityResource) {
        Asserts.notNull(IdentityResource, "identityResource is null");
        IdentityResource = findByIdOrElseThrow(IdentityResource.getId());
        IdentityResource clone = new IdentityResource();
        clone.setModelVersion(IdentityResource.getModelVersion());
        clone.setResourceCatalog(IdentityResource.getResourceCatalog());
        clone.setStatus(IdentityResource.getStatus());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityResource> findActiveByModelVersionId(Long modelVersionId) {
        return getRepository().findActiveByModelVersionId(SecurityUtils.getCurrentOrganIdOrElseThrow(), modelVersionId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyIdentityResource(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<IdentityResource> identityResourceList = findActiveByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(identityResourceList)) {
            return;
        }
        ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        identityResourceList.forEach(identityResource -> {
            IdentityResource clone = clone(identityResource);
            clone.setModelVersion(modelVersionTo);
            save(clone);
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityResource edit(Long id, IdentityResourceDto newIdentityResourceDto) {

        IdentityResource IdentityResource = findByIdOrElseThrow(id);
        IdentityResource.setModelVersion(modelVersionService.findByIdOrElseThrow(newIdentityResourceDto.getModelVersionId()));
        IdentityResource.setResourceCatalog(resourceCatalogService.findByIdOrElseThrow(newIdentityResourceDto.getResourceCatalogId()));

        IdentityResource = save(IdentityResource);
        return IdentityResource;
    }

    public List<IdentityResourceDto> findByReferenceTypeCodeAndModelversionId(String referenceTypeCode, Long modelversionId) {

        //String type = ModelElementRefrence.ReferenceType.valueOf(referenceTypeCode);
        switch (referenceTypeCode)
        {
            case "LANE_ASS_POST" :
                return getRepository().findPostRefByReferenceType(SecurityUtils.getCurrentOrganIdOrElseThrow(),modelversionId, referenceTypeCode);
            case "LANE_ASS_JOB" :
                return getRepository().findJobRefByReferenceType(SecurityUtils.getCurrentOrganIdOrElseThrow(),modelversionId, referenceTypeCode);
            case "LANE_ASS_UNIT" :
                return getRepository().findUnitRefByReferenceType(SecurityUtils.getCurrentOrganIdOrElseThrow(),modelversionId, referenceTypeCode);
            case "LANE_ASS_USER" :
                return getRepository().findUserRefByReferenceType(SecurityUtils.getCurrentOrganIdOrElseThrow(),modelversionId, referenceTypeCode);
        }
        return null;
    }
    
//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public ResourceDto addResourceSchema(Long modelVersionId, ResourceDto resourceDto) {
//        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
//        modelVersionService.checkModelVersionStatus(modelVersionId);
//        List<ResourceSchema> resourceSchemaList = modelVersion.getResourceSchemaList();
//        if (resourceSchemaList == null) {
//            resourceSchemaList = new ArrayList<>();
//        }
//        ResourceSchema resourceSchema = new ResourceSchema();
//        resourceSchema.setId(UUID.randomUUID().toString());
//        resourceSchema.setResourceType(resourceDto.getResourceType());
//        if (resourceDto.getResourceType().equals(ResourceSchema.ResourceType.RESOURCE_CATALOG)) {
//            resourceSchema.setResourceCatalogId(resourceDto.getResourceCatalogId());
//        } else if (resourceDto.getResourceType().equals(ResourceSchema.ResourceType.POST)) {
//            resourceSchema.setPostId(resourceDto.getPostId());
//        }
//        resourceSchema.setCreatedDate(LocalDateTime.now());
//        resourceSchema.setModifiedDate(LocalDateTime.now());
//        resourceSchemaList.add(resourceSchema);
//        modelVersion.setResourceSchemaList(resourceSchemaList);
//        modelVersionService.save(modelVersion);
//        return mapToResourceDto(resourceSchema);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public void deleteResourceSchema(Long modelVersionId, String resourceSchemaId) {
//        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
//        modelVersionService.checkModelVersionStatus(modelVersionId);
//        if (CollectionUtils.isEmpty(modelVersion.getResourceSchemaList())) {
//            throw new BusinessException("error.identity.resourceSchemaListIsEmpty");
//        }
//        ResourceSchema resourceSchema = new ResourceSchema(resourceSchemaId);
//        if (!modelVersion.getResourceSchemaList().contains(resourceSchema)) {
//            throw new BusinessException("error.identity.resourceSchemaIdNotValid");
//        }
//        List<ResourceSchema> resourceSchemaList = modelVersion.getResourceSchemaList();
//        resourceSchemaList.remove(resourceSchema);
//        modelVersion.setResourceSchemaList(resourceSchemaList);
//        modelVersionService.save(modelVersion);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ResourceDto> getResourceSchemaList(Long modelVersionId) {
//        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
//        return mapToResourceDto(modelVersion.getResourceSchemaList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ResourceDto> getResourceDetailsList(Long modelVersionId) {
//        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
//        return mapToResourceDto(modelVersion.getResourceSchemaList());
//    }
//
//    private ResourceDto mapToResourceDto(ResourceSchema resourceSchema) {
//        ResourceDto resourceDto = new ResourceDto();
//        resourceDto.setId(resourceSchema.getId());
//        if (resourceSchema.getResourceCatalogId() != null) {
//            ResourceCatalog resourceCatalog = resourceCatalogService.findByIdOrElseThrow(resourceSchema.getResourceCatalogId());
//            resourceDto.setResourceCatalogDto(resourceCatalogService.mapToDto(resourceCatalog));
//        }
//        resourceDto.setPostId(resourceSchema.getPostId());
//        resourceDto.setResourceType(resourceSchema.getResourceType());
//        resourceDto.setCreatedDate(resourceSchema.getCreatedDate());
//        resourceDto.setModifiedDate(resourceSchema.getModifiedDate());
//        return resourceDto;
//    }
//
//    private List<ResourceDto> mapToResourceDto(List<ResourceSchema> resourceSchemaList) {
//        if (CollectionUtils.isEmpty(resourceSchemaList)) {
//            return new ArrayList<>();
//        }
//        return resourceSchemaList.stream()
//                .sorted(Comparator.comparing(ResourceSchema::getModifiedDate).reversed())
//                .map(this::mapToResourceDto)
//                .toList();
//    }
}
