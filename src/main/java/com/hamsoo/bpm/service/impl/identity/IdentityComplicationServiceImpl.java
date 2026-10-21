package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.identity.IdentityComplicationDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityComplication;
import com.hamsoo.bpm.model.entity.identity.IdentityComplication_;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.identity.IdentityComplicationRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityComplicationService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.apache.commons.lang3.ArrayUtils;
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
public class IdentityComplicationServiceImpl implements IdentityComplicationService {
    private final IdentityComplicationRepository identityComplicationRepository;
    private final ModelVersionService modelVersionService;
    private final CodeCategoryService codeCategoryService;


    public IdentityComplicationServiceImpl(IdentityComplicationRepository identityComplicationRepository, ModelVersionService modelVersionService, CodeCategoryService codeCategoryService) {
        this.identityComplicationRepository = identityComplicationRepository;
        this.modelVersionService = modelVersionService;
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public IdentityComplicationRepository getRepository() {
        return identityComplicationRepository;
    }

    @Override
    public IdentityComplication findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.IdentityComplication.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(() -> new BusinessException("error.IdentityComplication.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityComplication create(IdentityComplicationDto identityComplicationDto) {
        Assert.notNull(identityComplicationDto, "identityComplicationDto is null");
        modelVersionService.checkModelVersionStatus(identityComplicationDto.getModelVersionId());
        IdentityComplication identityComplication = new IdentityComplication();
        identityComplication.setModelVersion(modelVersionService.findByIdOrElseThrow(identityComplicationDto.getModelVersionId()));
        identityComplication.setStatus(Status.ACTIVE.name());
        identityComplication.setIdea(identityComplicationDto.getIdea());
        identityComplication.setName(identityComplicationDto.getName());
        return save(identityComplication);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IdentityComplication identityComplication = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(identityComplication.getModelVersion().getId());
        identityComplication.setStatus(Status.PASSIVE.name());
        save(identityComplication);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityComplicationDto mapToDto(IdentityComplication identityComplication) {
        Assert.notNull(identityComplication, "IdentityComplication is null");
        identityComplication = findByIdOrElseThrow(identityComplication.getId());
        IdentityComplicationDto identityComplicationDto = new IdentityComplicationDto();
        identityComplicationDto.setId(identityComplication.getId());
        identityComplicationDto.setName(identityComplication.getName());
        identityComplicationDto.setIdea(identityComplication.getIdea());
        identityComplicationDto.setModelVersionId(identityComplication.getModelVersion().getId());

        return identityComplicationDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(IdentityComplicationDto identityComplicationDto, int page, int size, String orderBy, Sort.Direction direction, Function<IdentityComplication, IdentityComplicationDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<IdentityComplication>> optionalSpecification = createSpecification(identityComplicationDto);
        Page<IdentityComplication> identityKpiPage = findAll(optionalSpecification.get(), pageable);
        List<IdentityComplicationDto> content = identityKpiPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content).totalElements(identityKpiPage.getTotalElements()).totalPages(identityKpiPage.getTotalPages()).currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<IdentityComplication>> createSpecification(IdentityComplicationDto identityKpiDto) {
        List<Specification<IdentityComplication>> specificationList = new ArrayList<>();

        Specification<IdentityComplication> organEq = (root, query, criteriaBuilder) -> {
            Join<IdentityComplication, ModelVersion> modelVersionJoin = root.join(IdentityComplication_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);

            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), identityKpiDto.getModelVersionId());
            predicates[2] = criteriaBuilder.equal(root.get(IdentityComplication_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());

            if (identityKpiDto.getName() != null) {
                Predicate nameLike = criteriaBuilder.like(root.get(IdentityComplication_.NAME), "%" + identityKpiDto.getName() + "%");
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }

            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Long countByKpiCatalogId(Long kpiCatalogId) {
//        return getRepository().countByKpiCatalogId(kpiCatalogId);
//    }

    @Override
    @Transactional(readOnly = true)
    public IdentityComplication clone(IdentityComplication identityComplication) {
        Asserts.notNull(identityComplication, "identityKpi is null");
        identityComplication = findByIdOrElseThrow(identityComplication.getId());
        IdentityComplication clone = new IdentityComplication();
        clone.setModelVersion(identityComplication.getModelVersion());
        clone.setStatus(identityComplication.getStatus());
        clone.setName(identityComplication.getName());
        clone.setIdea(identityComplication.getIdea());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityComplication> findActiveByModelVersionId(Long modelVersionId) {
        return getRepository().findActiveByModelVersionId(SecurityUtils.getCurrentOrganIdOrElseThrow(), modelVersionId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyIdentityComplication(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<IdentityComplication> identityKpiList = findActiveByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(identityKpiList)) {
            return;
        }
        ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        identityKpiList.forEach(identityKpi -> {
            IdentityComplication clone = clone(identityKpi);
            clone.setModelVersion(modelVersionTo);
            save(clone);
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityComplication edit(Long id, IdentityComplicationDto newIdentityComplicationDto) {

        IdentityComplication identityComplication = findByIdOrElseThrow(id);
        identityComplication.setName(newIdentityComplicationDto.getName());
        identityComplication.setIdea(newIdentityComplicationDto.getIdea());

        identityComplication = save(identityComplication);
        return identityComplication;
    }

}
