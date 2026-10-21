package com.hamsoo.bpm.service.impl.resourcetype;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.model.dto.resourcetype.ResourceTypeDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;
import com.hamsoo.bpm.service.api.resourcetype.ResourceTypeService;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.resourcetype.ResourceTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceTypeServiceImpl implements ResourceTypeService {

    private final ResourceTypeRepository resourceTypeRepository;

    public ResourceTypeServiceImpl(ResourceTypeRepository resourceTypeRepository) {
        this.resourceTypeRepository = resourceTypeRepository;
    }

    @Override
    public ResourceTypeRepository getRepository() {
        return resourceTypeRepository;
    }

    @Override
    public ResourceType findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.resourceType.idIsNotValid");
        }
        return getRepository().findByIdAndStatus(id, Status.ACTIVE.name())
                .orElseThrow(() -> new BusinessException("error.resourceType.idIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceTypeDto mapToDto(ResourceType resourceType) {
        Assert.notNull(resourceType, "resourceType is null");
        resourceType = findByIdOrElseThrow(resourceType.getId());
        ResourceTypeDto resourceTypeDto = new ResourceTypeDto();
        resourceTypeDto.setId(resourceType.getId());
        resourceTypeDto.setName(resourceType.getName());
        resourceTypeDto.setDescription(resourceType.getDescription());
//        CodeCategory category = resourceType.getCategory();
//        resourceTypeDto.setCategoryCode(category.getCode());
//        resourceTypeDto.setCategoryTitle(category.getFaName());
        return resourceTypeDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceTypeDto> mapToDto(List<ResourceType> resourceTypeList) {
        if (CollectionUtils.isEmpty(resourceTypeList)) {
            return new ArrayList<>();
        }
        return resourceTypeList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceTypeDto> getList() {
        List<ResourceType> resourceTypeList = getRepository().findByStatus(Status.ACTIVE.name(), PageUtils.getInstance());
        return mapToDto(resourceTypeList);
    }

    @Override
    public ResourceType create(ResourceTypeDto resourceTypeDto) {
        Assert.notNull(resourceTypeDto, "resourceTypeDto is null");

        ResourceType resourceType = new ResourceType();
        resourceType.setDescription(resourceTypeDto.getDescription());
        resourceType.setName(resourceTypeDto.getName());
        resourceType.setStatus(Status.ACTIVE.name());
        return save(resourceType);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResourceType edit(ResourceTypeDto resourceTypeDto) {
        Assert.notNull(resourceTypeDto, "resourceTypeDto is null");
        ResourceType resourceType = findByIdOrElseThrow(resourceTypeDto.getId());
        resourceType.setDescription(resourceTypeDto.getDescription());
        resourceType.setName(resourceTypeDto.getName());
        return save(resourceType);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ResourceTypeDto resourceTypeDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ResourceType>> optionalSpecification = createSpecification(resourceTypeDto);
        Page<ResourceType> modelPage = findAll(optionalSpecification.get(), pageable);
        List<ResourceTypeDto> content = modelPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ResourceType>> createSpecification(ResourceTypeDto resourceTypeDto) {
        List<Specification<ResourceType>> specificationList = new ArrayList<>();

        Specification<ResourceType> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ResourceType_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);


        if (resourceTypeDto.getName() != null) {
            Specification<ResourceType> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(ResourceType_.NAME), "%" + resourceTypeDto.getName() + "%");
            specificationList.add(nameLike);
        }

        return SpecificationUtils.andSpecifications(specificationList);
    }
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
       ResourceType resourceType = findByIdOrElseThrow(id);
       resourceType.setStatus(Status.PASSIVE.name());
       save(resourceType);
    }


}