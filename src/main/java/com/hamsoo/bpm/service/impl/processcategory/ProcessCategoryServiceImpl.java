package com.hamsoo.bpm.service.impl.processcategory;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryModelElementService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryService;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement_;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.processcategory.ProcessCategoryRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessCategoryServiceImpl implements ProcessCategoryService {

    public final ProcessCategoryRepository processCategoryRepository;
    public final OrganService organService;
    public final ModelVersionService modelVersionService;
    public final ProcessCategoryModelElementService processCategoryModelElementService;

    public ProcessCategoryServiceImpl(ProcessCategoryRepository processCategoryRepository,
                                      OrganService organService,
                                      ModelVersionService modelVersionService,
                                      @Lazy ProcessCategoryModelElementService processCategoryModelElementService) {
        this.processCategoryRepository = processCategoryRepository;
        this.organService = organService;
        this.modelVersionService = modelVersionService;
        this.processCategoryModelElementService = processCategoryModelElementService;
    }

    @Override
    public ProcessCategoryRepository getRepository() {
        return processCategoryRepository;
    }

    @Override
    public ProcessCategory findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.processCategory.idIsNotValid");
        }
        return getRepository().findByIdAndStatusAndOrganId(id, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.processCategory.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ProcessCategory create(ProcessCategoryDto processCategoryDto) {
        Assert.notNull(processCategoryDto, "processCategoryDto is null");
        Assert.notNull(processCategoryDto.getName(), "processCategoryDto name is null");
        Optional<ProcessCategory> processCategoryOptional =
                findActiveByOrganIdAndName(SecurityUtils.getCurrentOrganIdOrElseThrow(), processCategoryDto.getName());
        processCategoryOptional.ifPresent(p -> {
            throw new BusinessException("error.processCategory.nameIsDuplicate");
        });
        ProcessCategory processCategory = new ProcessCategory();
        processCategory.setName(processCategoryDto.getName());
        processCategory.setDescription(processCategoryDto.getDescription());
        processCategory.setStatus(Status.ACTIVE.name());
        processCategory.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        return save(processCategory);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ProcessCategory edit(ProcessCategoryDto processCategoryDto) {
        Assert.notNull(processCategoryDto, "processCategoryDto is null");
        Assert.notNull(processCategoryDto.getName(), "processCategoryDto name is null");
        ProcessCategory processCategory = findByIdOrElseThrow(processCategoryDto.getId());
        if (!processCategory.getName().equals(processCategoryDto.getName())) {
            Optional<ProcessCategory> processCategoryOptional =
                    findActiveByOrganIdAndName(SecurityUtils.getCurrentOrganIdOrElseThrow(), processCategoryDto.getName());
            processCategoryOptional.ifPresent(p -> {
                throw new BusinessException("error.processCategory.nameIsDuplicate");
            });
            processCategory.setName(processCategoryDto.getName());
        }
        processCategory.setDescription(processCategoryDto.getDescription());
        return save(processCategory);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        ProcessCategory processCategory = findByIdOrElseThrow(id);
        Long usageInIdentityCount = modelVersionService.getCountByProcessCategoryId(id);
        if (usageInIdentityCount > 0) {
            throw new BusinessException("error.processCategory.processCategoryUsedInIdentity", processCategory.getName());
        }
        Long processCategoryModelElementCount = processCategoryModelElementService.countByProcessCategoryId(id);
        if (processCategoryModelElementCount > 0) {
            throw new BusinessException("error.processCategory.processCategoryUsedInProcessCategoryModelElement", processCategory.getName());
        }
        processCategory.setStatus(Status.PASSIVE.name());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessCategoryDto mapToDto(ProcessCategory processCategory) {
        Assert.notNull(processCategory, "processCategory is null");
        Assert.notNull(processCategory.getId(), "processCategory's id is null");
        processCategory = findByIdOrElseThrow(processCategory.getId());
        ProcessCategoryDto processCategoryDto = new ProcessCategoryDto();
        processCategoryDto.setId(processCategory.getId());
        processCategoryDto.setName(processCategory.getName());
        processCategoryDto.setDescription(processCategory.getDescription());
        processCategoryDto.setStatus(processCategory.getStatus());
        processCategoryDto.setOrganId(processCategory.getOrgan().getId());
        return processCategoryDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ProcessCategoryDto processCategoryDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ProcessCategory>> optionalSpecification = createSpecification(processCategoryDto, null);
        Page<ProcessCategory> processCategoryPage = findAll(optionalSpecification.get(), pageable);
        List<ProcessCategoryDto> content = processCategoryPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(processCategoryPage.getTotalElements())
                .totalPages(processCategoryPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }


    @Override
    @Transactional(readOnly = true)
    public SearchDto listHasNoEMModelElement(ProcessCategoryDto processCategoryDto, Long modelVersionId, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ProcessCategory>> optionalSpecification = createSpecification(processCategoryDto, modelVersionId);
        Page<ProcessCategory> processCategoryPage = findAll(optionalSpecification.get(), pageable);
        List<ProcessCategoryDto> content = processCategoryPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(processCategoryPage.getTotalElements())
                .totalPages(processCategoryPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ProcessCategory>> createSpecification(ProcessCategoryDto processCategoryDto, Long modelVersionId) {
        List<Specification<ProcessCategory>> specificationList = new ArrayList<>();

        Specification<ProcessCategory> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ProcessCategory_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        if (processCategoryDto.getName() != null) {
            Specification<ProcessCategory> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(ProcessCategory_.NAME), "%" + processCategoryDto.getName() + "%");
            specificationList.add(nameLike);
        }

        Specification<ProcessCategory> organEq = (root, query, criteriaBuilder) -> {
            Join<ProcessCategory, Organ> organJoin = root.join(ProcessCategory_.ORGAN);
            return criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
        };
        specificationList.add(organEq);
        if (modelVersionId != null) {
            specificationList.add(processCategoryModelElementExist(modelVersionId));
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

    private Specification<ProcessCategory> processCategoryModelElementExist(Long modelVersionId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<ProcessCategoryModelElement> subQuery = query.subquery(ProcessCategoryModelElement.class);
            Root<ProcessCategoryModelElement> subQueryRoot = subQuery.from(ProcessCategoryModelElement.class);
            Join<ProcessCategoryModelElement, ProcessCategory> processCategoryJoin = subQueryRoot.join(ProcessCategoryModelElement_.PROCESS_CATEGORY);
            Join<ProcessCategoryModelElement, ModelVersion> modelVersionJoin = subQueryRoot.join(ProcessCategoryModelElement_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[3];
            predicates[0] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(processCategoryJoin.get(ProcessCategory_.ID), root.get(ProcessCategory_.ID));
            predicates[2] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            subQuery.where(predicates);
            return criteriaBuilder.not(criteriaBuilder.exists(subQuery));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessCategory> findActiveByOrganIdAndName(Long organId, String name) {
        return getRepository().findActiveByOrganIdAndName(organId, name);
    }


}
