package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.identity.IdentityRiskDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityRisk;
import com.hamsoo.bpm.model.entity.identity.IdentityRisk_;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.identity.IdentityRiskRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityRiskService;
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
public class IdentityRiskServiceImpl implements IdentityRiskService {

    private final IdentityRiskRepository IdentityRiskRepository;
    private final ModelVersionService modelVersionService;
    private final CodeCategoryService codeCategoryService;


    public IdentityRiskServiceImpl(com.hamsoo.bpm.repository.jpa.identity.IdentityRiskRepository IdentityRiskRepository,
                                   ModelVersionService modelVersionService,
                                   CodeCategoryService codeCategoryService) {
        this.IdentityRiskRepository = IdentityRiskRepository;
        this.modelVersionService = modelVersionService;
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public IdentityRiskRepository getRepository() {
        return IdentityRiskRepository;
    }

    @Override
    public IdentityRisk findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.IdentityRisk.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.IdentityRisk.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityRisk create(IdentityRiskDto IdentityRiskDto) {
        Assert.notNull(IdentityRiskDto, "IdentityRiskDto is null");
        modelVersionService.checkModelVersionStatus(IdentityRiskDto.getModelVersionId());
        IdentityRisk IdentityRisk = new IdentityRisk();

        IdentityRisk.setModelVersion(modelVersionService.findByIdOrElseThrow(IdentityRiskDto.getModelVersionId()));
        IdentityRisk.setStatus(Status.ACTIVE.name());
        IdentityRisk.setCause(IdentityRiskDto.getCause());
        IdentityRisk.setDescription(IdentityRiskDto.getDescription());
        IdentityRisk.setResult(IdentityRiskDto.getResult());
        return save(IdentityRisk);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IdentityRisk IdentityRisk = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(IdentityRisk.getModelVersion().getId());
        IdentityRisk.setStatus(Status.PASSIVE.name());
        save(IdentityRisk);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityRiskDto mapToDto(IdentityRisk IdentityRisk) {
        Assert.notNull(IdentityRisk, "IdentityRisk is null");
        IdentityRisk = findByIdOrElseThrow(IdentityRisk.getId());
        IdentityRiskDto IdentityRiskDto = new IdentityRiskDto();
        IdentityRiskDto.setId(IdentityRisk.getId());
        IdentityRiskDto.setModelVersionId(IdentityRisk.getModelVersion().getId());
        IdentityRiskDto.setCause(IdentityRisk.getCause());
        IdentityRiskDto.setDescription(IdentityRisk.getDescription());
        IdentityRiskDto.setResult(IdentityRisk.getResult());
        return IdentityRiskDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(IdentityRiskDto IdentityRiskDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<IdentityRisk, IdentityRiskDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<IdentityRisk>> optionalSpecification = createSpecification(IdentityRiskDto);
        Page<IdentityRisk> IdentityRiskPage = findAll(optionalSpecification.get(), pageable);
        List<IdentityRiskDto> content = IdentityRiskPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(IdentityRiskPage.getTotalElements())
                .totalPages(IdentityRiskPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<IdentityRisk>> createSpecification(IdentityRiskDto IdentityRiskDto) {
        List<Specification<IdentityRisk>> specificationList = new ArrayList<>();

        Specification<IdentityRisk> organEq = (root, query, criteriaBuilder) -> {
            Join<IdentityRisk, ModelVersion> modelVersionJoin = root.join(IdentityRisk_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);

            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), IdentityRiskDto.getModelVersionId());
            predicates[2] = criteriaBuilder.equal(root.get(IdentityRisk_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());

            if (IdentityRiskDto.getResult() != null) {
                Predicate nameLike = criteriaBuilder.like(root.get(IdentityRisk_.result), "%" + IdentityRiskDto.getResult() + "%");
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }
            if (IdentityRiskDto.getCause() != null) {
                Predicate nameLike = criteriaBuilder.like(root.get(IdentityRisk_.cause), "%" + IdentityRiskDto.getCause() + "%");
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }

            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }



    @Override
    @Transactional(readOnly = true)
    public IdentityRisk clone(IdentityRisk IdentityRisk) {
        Asserts.notNull(IdentityRisk, "IdentityRisk is null");
        IdentityRisk = findByIdOrElseThrow(IdentityRisk.getId());
        IdentityRisk clone = new IdentityRisk();
        clone.setModelVersion(IdentityRisk.getModelVersion());
        clone.setStatus(IdentityRisk.getStatus());
        clone.setCause(IdentityRisk.getCause());
        clone.setDescription(IdentityRisk.getDescription());
        clone.setResult(IdentityRisk.getResult());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityRisk> findActiveByModelVersionId(Long modelVersionId) {
        return getRepository().findActiveByModelVersionId(SecurityUtils.getCurrentOrganIdOrElseThrow(), modelVersionId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyIdentityRisk(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<IdentityRisk> IdentityRiskList = findActiveByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(IdentityRiskList)) {
            return;
        }
        ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        IdentityRiskList.forEach(IdentityRisk -> {
            IdentityRisk clone = clone(IdentityRisk);
            clone.setModelVersion(modelVersionTo);
            save(clone);
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityRisk edit(Long id, IdentityRiskDto identityRiskDto) {

        IdentityRisk identityRisk = findByIdOrElseThrow(id);
        identityRisk.setResult(identityRiskDto.getResult());
        identityRisk.setDescription(identityRiskDto.getDescription());
        identityRisk.setCause(identityRiskDto.getCause());
        identityRisk = save(identityRisk);
        return identityRisk;

    }
}
