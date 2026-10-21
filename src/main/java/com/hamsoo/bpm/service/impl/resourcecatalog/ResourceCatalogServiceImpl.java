package com.hamsoo.bpm.service.impl.resourcecatalog;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.resourcecatalog.ResourceCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.resourcecatalog.ResourceCatalogService;
import com.hamsoo.bpm.service.api.resourcetype.ResourceTypeService;
import com.hamsoo.bpm.model.entity.iotype.IoType_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.resourcecatalog.ResourceCatalog;
import com.hamsoo.bpm.model.entity.resourcecatalog.ResourceCatalog_;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.resourcecatalog.ResourceCatalogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceCatalogServiceImpl implements ResourceCatalogService {

    private final ResourceCatalogRepository resourceCatalogRepository;
    private final ResourceTypeService resourceTypeService;
    private final OrganService organService;
    private final ModelVersionService modelVersionService;

    public ResourceCatalogServiceImpl(ResourceCatalogRepository resourceCatalogRepository,
                                      ResourceTypeService resourceTypeService,
                                      OrganService organService,
                                      ModelVersionService modelVersionService) {
        this.resourceCatalogRepository = resourceCatalogRepository;
        this.resourceTypeService = resourceTypeService;
        this.organService = organService;
        this.modelVersionService = modelVersionService;
    }

    @Override
    public ResourceCatalogRepository getRepository() {
        return resourceCatalogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceCatalog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.resourceCatalog.idIsNotValid");
        }
        return getRepository().findByIdAndStatusAndOrganId(id, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.resourceCatalog.idIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceCatalogDto mapToDto(ResourceCatalog resourceCatalog) {
        Assert.notNull(resourceCatalog, "resourceCatalog is null");
        resourceCatalog = findByIdOrElseThrow(resourceCatalog.getId());
        ResourceCatalogDto resourceCatalogDto = new ResourceCatalogDto();
        resourceCatalogDto.setId(resourceCatalog.getId());
        resourceCatalogDto.setName(resourceCatalog.getName());
        resourceCatalogDto.setResourceTypeDto(resourceTypeService.mapToDto(resourceCatalog.getResourceType()));
        resourceCatalogDto.setOrganId(resourceCatalog.getOrgan().getId());
        return resourceCatalogDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResourceCatalog create(ResourceCatalogDto resourceCatalogDto) {
        Assert.notNull(resourceCatalogDto, "resourceCatalogDto is null");
        ResourceCatalog resourceCatalog = new ResourceCatalog();
        resourceCatalog.setName(resourceCatalogDto.getName());
        resourceCatalog.setStatus(Status.ACTIVE.name());
        resourceCatalog.setResourceType(resourceTypeService.findByIdOrElseThrow(resourceCatalogDto.getResourceTypeId()));
        resourceCatalog.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        return save(resourceCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResourceCatalog edit(ResourceCatalogDto resourceCatalogDto) {
        Assert.notNull(resourceCatalogDto, "resourceCatalogDto is null");
        ResourceCatalog resourceCatalog = findByIdOrElseThrow(resourceCatalogDto.getId());
        resourceCatalog.setName(resourceCatalogDto.getName());
        resourceCatalog.setResourceType(resourceTypeService.findByIdOrElseThrow(resourceCatalogDto.getResourceTypeId()));
        return save(resourceCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        List<ModelVersion> modelVersionList = modelVersionService.getByResourceSchemaResourceCatalogId(id);
        if (!CollectionUtils.isEmpty(modelVersionList)) {
            throw new BusinessException("error.resourceCatalog.resourceCatalogUsedInModelVersionResourceSchema");
        }
        ResourceCatalog resourceCatalog = findByIdOrElseThrow(id);
        resourceCatalog.setStatus(Status.PASSIVE.name());
        save(resourceCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ResourceCatalogDto resourceCatalogDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ResourceCatalog>> optionalSpecification = createSpecification(resourceCatalogDto);
        Page<ResourceCatalog> modelPage = findAll(optionalSpecification.get(), pageable);
        List<ResourceCatalogDto> content = modelPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ResourceCatalog>> createSpecification(ResourceCatalogDto resourceCatalogDto) {
        List<Specification<ResourceCatalog>> specificationList = new ArrayList<>();

        Specification<ResourceCatalog> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ResourceCatalog_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        Specification<ResourceCatalog> organEq = (root, query, criteriaBuilder) -> {
            Join<ResourceCatalog, Organ> organJoin = root.join(ResourceCatalog_.ORGAN);
            return criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
        };
        specificationList.add(organEq);

        if (resourceCatalogDto.getName() != null) {
            Specification<ResourceCatalog> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(ResourceCatalog_.NAME), "%" + resourceCatalogDto.getName() + "%");
            specificationList.add(nameLike);
        }

        if (resourceCatalogDto.getResourceTypeId() != null) {
            Specification<ResourceCatalog> ioTypeEq = (root, query, criteriaBuilder) -> {
                Join<ResourceCatalog, ResourceType> ioTypeJoin = root.join(ResourceCatalog_.RESOURCE_TYPE);
                return criteriaBuilder.equal(ioTypeJoin.get(IoType_.ID), resourceCatalogDto.getResourceTypeId());
            };
            specificationList.add(ioTypeEq);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceCatalogDto> getlistByResourceTypeId(Long id) {
        List<ResourceCatalog> resourceCatalogs = getRepository().getlistByResourceTypeId(id);
        return resourceCatalogs.stream().map(this::mapToDto).collect(Collectors.toList());
    }



}
