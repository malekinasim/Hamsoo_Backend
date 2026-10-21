package com.hamsoo.bpm.service.impl.processcategory;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryModelElementDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryModelElementService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryService;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.processcategory.ProcessCategoryModelElementRepository;
import org.apache.http.util.Asserts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ProcessCategoryModelElementServiceImpl implements ProcessCategoryModelElementService {

    public final ProcessCategoryModelElementRepository processCategoryModelElementRepository;
    public final ModelVersionService modelVersionService;
    public final ProcessCategoryService processCategoryService;

    public ProcessCategoryModelElementServiceImpl(ProcessCategoryModelElementRepository processCategoryModelElementRepository,
                                                  ModelVersionService modelVersionService,
                                                  ProcessCategoryService processCategoryService) {
        this.processCategoryModelElementRepository = processCategoryModelElementRepository;
        this.modelVersionService = modelVersionService;
        this.processCategoryService = processCategoryService;
    }

    @Override
    public ProcessCategoryModelElementRepository getRepository() {
        return processCategoryModelElementRepository;
    }

    @Override
    public ProcessCategoryModelElement findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.processCategoryModelElement.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.processCategoryModelElement.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ProcessCategoryModelElement create(ProcessCategoryModelElementDto processCategoryModelElementDto) {
        Assert.notNull(processCategoryModelElementDto, "processCategoryModelElementDto is null");
        modelVersionService.checkModelVersionStatus(processCategoryModelElementDto.getModelVersionId());
        findByModelVersionIdAndElementId(processCategoryModelElementDto.getModelVersionId(),
                processCategoryModelElementDto.getElementId()).ifPresent(P -> {
            throw new BusinessException("error.processCategoryModelElement.elementIdIsDuplicate");
        });
        findByModelVersionIdAndProcessCategoryId(processCategoryModelElementDto.getModelVersionId(),
                processCategoryModelElementDto.getProcessCategoryId()).ifPresent(p -> {
            throw new BusinessException("error.processCategoryModelElement.processCategoryIsDuplicate");
        });
        ProcessCategoryModelElement processCategoryModelElement = new ProcessCategoryModelElement();
        processCategoryModelElement.setElementId(processCategoryModelElementDto.getElementId());
        processCategoryModelElement.setModelVersion(modelVersionService.findByIdOrElseThrow(processCategoryModelElementDto.getModelVersionId()));
        processCategoryModelElement.setProcessCategory(processCategoryService.findByIdOrElseThrow(processCategoryModelElementDto.getProcessCategoryId()));
        return save(processCategoryModelElement);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ProcessCategoryModelElement edit(ProcessCategoryModelElementDto processCategoryModelElementDto) {
        Assert.notNull(processCategoryModelElementDto, "processCategoryModelElementDto is null");
        ProcessCategoryModelElement processCategoryModelElement = findByIdOrElseThrow(processCategoryModelElementDto.getId());
        if (processCategoryModelElement.getProcessCategory().getId().equals(processCategoryModelElementDto.getProcessCategoryId())) {
            return processCategoryModelElement;
        }
        modelVersionService.checkModelVersionStatus(processCategoryModelElement.getModelVersion().getId());

        findByModelVersionIdAndProcessCategoryId(processCategoryModelElement.getModelVersion().getId(),
                processCategoryModelElementDto.getProcessCategoryId()).ifPresent(p -> {
            throw new BusinessException("error.processCategoryModelElement.processCategoryIsDuplicate");
        });
        processCategoryModelElement.setProcessCategory(processCategoryService.findByIdOrElseThrow(processCategoryModelElementDto.getProcessCategoryId()));
        return save(processCategoryModelElement);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ProcessCategoryModelElement createOrEditByModelVersionIdAndElementId(ProcessCategoryModelElementDto processCategoryModelElementDto) {
        Assert.notNull(processCategoryModelElementDto, "processCategoryModelElementDto is null");
        Optional<ProcessCategoryModelElement> processCategoryModelElementOptional =
                findByModelVersionIdAndElementId(processCategoryModelElementDto.getModelVersionId(), processCategoryModelElementDto.getElementId());
        ProcessCategoryModelElement result;
        if (processCategoryModelElementOptional.isPresent()) {
            ProcessCategoryModelElement processCategoryModelElement = processCategoryModelElementOptional.get();
            ProcessCategoryModelElementDto editInput = new ProcessCategoryModelElementDto();
            editInput.setId(processCategoryModelElement.getId());
            editInput.setProcessCategoryId(processCategoryModelElementDto.getProcessCategoryId());
            result = edit(editInput);
        } else {
            result = create(processCategoryModelElementDto);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        ProcessCategoryModelElement processCategoryModelElement = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(processCategoryModelElement.getModelVersion().getId());
        delete(processCategoryModelElement);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long modelVersionId, String elementId) {
        modelVersionService.checkModelVersionStatus(modelVersionId);
        Optional<ProcessCategoryModelElement> processCategoryModelElementOptional = findByModelVersionIdAndElementId(modelVersionId, elementId);
        if (processCategoryModelElementOptional.isEmpty()) {
            throw new BusinessException("error.processCategoryModelElement.notfoundAnyRecordForThisModelVersionAndElement");
        }
        delete(processCategoryModelElementOptional.get().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessCategoryModelElementDto mapToDto(ProcessCategoryModelElement processCategoryModelElement) {
        Assert.notNull(processCategoryModelElement, "processCategoryModelElement is null");
        processCategoryModelElement = findByIdOrElseThrow(processCategoryModelElement.getId());
        ProcessCategoryModelElementDto processCategoryModelElementDto = new ProcessCategoryModelElementDto();
        processCategoryModelElementDto.setId(processCategoryModelElement.getId());
        processCategoryModelElementDto.setElementId(processCategoryModelElement.getElementId());
        processCategoryModelElementDto.setModelVersionId(processCategoryModelElement.getModelVersion().getId());
        processCategoryModelElementDto.setProcessCategoryDto(processCategoryService.mapToDto(processCategoryModelElement.getProcessCategory()));
        return processCategoryModelElementDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ProcessCategoryModelElementDto processCategoryModelElementDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<ProcessCategoryModelElement, ProcessCategoryModelElementDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ProcessCategoryModelElement>> optionalSpecification = createSpecification(processCategoryModelElementDto);
        Page<ProcessCategoryModelElement> processCategoryModelElementPage = findAll(optionalSpecification.get(), pageable);
        List<ProcessCategoryModelElementDto> content = processCategoryModelElementPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(processCategoryModelElementPage.getTotalElements())
                .totalPages(processCategoryModelElementPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ProcessCategoryModelElement>> createSpecification(ProcessCategoryModelElementDto processCategoryModelElementDto) {
        List<Specification<ProcessCategoryModelElement>> specificationList = new ArrayList<>();
        Specification<ProcessCategoryModelElement> organEq = (root, query, criteriaBuilder) -> {
            Join<ProcessCategoryModelElement, ModelVersion> modelVersionJoin = root.join(ProcessCategoryModelElement_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Predicate[] predicates = new Predicate[3];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), processCategoryModelElementDto.getModelVersionId());
            predicates[2] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessCategoryModelElement> findByModelVersionIdAndProcessCategoryId(Long modelVersionId, Long processCategoryId) {
        return getRepository().findByModelVersionIdAndProcessCategoryId(modelVersionId,
                processCategoryId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessCategoryModelElement> findByModelVersionIdAndElementId(Long modelVersionId, String elementId) {
        return getRepository().findByModelVersionIdAndElementId(modelVersionId,
                elementId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByProcessCategoryId(Long processCategoryId) {
        return getRepository().countByProcessCategoryId(processCategoryId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessCategoryModelElement> findByModelVersionId(Long modelVersionId) {
        return getRepository().findByModelVersionId(modelVersionId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessCategoryModelElement clone(ProcessCategoryModelElement processCategoryModelElement) {
        Asserts.notNull(processCategoryModelElement, "processCategoryModelElement is null");
        processCategoryModelElement = findByIdOrElseThrow(processCategoryModelElement.getId());
        ProcessCategoryModelElement clone = new ProcessCategoryModelElement();
        clone.setElementId(processCategoryModelElement.getElementId());
        clone.setModelVersion(processCategoryModelElement.getModelVersion());
        clone.setProcessCategory(processCategoryModelElement.getProcessCategory());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public void copyProcessCategoryModelElementsIfExist(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<ProcessCategoryModelElement> processCategoryModelElementList = findByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(processCategoryModelElementList)) {
            return;
        }
        ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        processCategoryModelElementList.forEach(processCategoryModelElement -> {
            ProcessCategoryModelElement cloned = clone(processCategoryModelElement);
            cloned.setModelVersion(modelVersionTo);
            save(cloned);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessCategoryModelElement> findByModelId(Long modelId) {
        return getRepository().findByModelId(SecurityUtils.getCurrentOrganIdOrElseThrow(), modelId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByModelId(Long modelId) {

        List<ProcessCategoryModelElement> processCategoryModelElementList = findByModelId(modelId);
        if (!CollectionUtils.isEmpty(processCategoryModelElementList)) {
            deleteAll(processCategoryModelElementList);
        }
    }
}
