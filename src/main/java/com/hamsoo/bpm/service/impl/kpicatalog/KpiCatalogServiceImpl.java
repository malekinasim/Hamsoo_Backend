package com.hamsoo.bpm.service.impl.kpicatalog;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.kpicatalog.KpiCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog;
import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog_;
import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.kpicatalog.KpiCatalogRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityKpiService;
import com.hamsoo.bpm.service.api.kpicatalog.KpiCatalogService;
import com.hamsoo.bpm.service.api.kpiunit.KpiUnitService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KpiCatalogServiceImpl implements KpiCatalogService {

    private final KpiCatalogRepository kpiCatalogRepository;
    private final KpiUnitService kpiUnitService;
    private final OrganService organService;
    private final CodeCategoryService codeCategoryService;
    private final IdentityKpiService identityKpiService;

    public KpiCatalogServiceImpl(KpiCatalogRepository kpiCatalogRepository,
                                 KpiUnitService kpiUnitService,
                                 OrganService organService,
                                 CodeCategoryService codeCategoryService,
                                 @Lazy IdentityKpiService identityKpiService) {
        this.kpiCatalogRepository = kpiCatalogRepository;
        this.kpiUnitService = kpiUnitService;
        this.organService = organService;
        this.codeCategoryService = codeCategoryService;
        this.identityKpiService = identityKpiService;
    }

    @Override
    public KpiCatalogRepository getRepository() {
        return kpiCatalogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public KpiCatalog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.kpiCatalog.idIsNotValid");
        }
        return getRepository().findByIdAndStatusAndOrganId(id, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.kpiCatalog.idIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public KpiCatalogDto mapToDto(KpiCatalog kpiCatalog) {
        Assert.notNull(kpiCatalog, "kpiCatalog is null");
        kpiCatalog = findByIdOrElseThrow(kpiCatalog.getId());
        KpiCatalogDto kpiCatalogDto = new KpiCatalogDto();
        kpiCatalogDto.setId(kpiCatalog.getId());
        kpiCatalogDto.setName(kpiCatalog.getName());
        if (kpiCatalog.getKpiUnit() != null) {
            kpiCatalogDto.setKpiUnitDto(kpiUnitService.mapToDto(kpiCatalog.getKpiUnit()));
        }
        kpiCatalogDto.setOrganId(kpiCatalog.getOrgan().getId());
        return kpiCatalogDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public KpiCatalog create(KpiCatalogDto kpiCatalogDto) {
        Assert.notNull(kpiCatalogDto, "kpiCatalogDto is null");
        KpiCatalog kpiCatalog = new KpiCatalog();
        kpiCatalog.setName(kpiCatalogDto.getName());
        if (kpiCatalogDto.getKpiUnitId() != null) {
            kpiCatalog.setKpiUnit(kpiUnitService.findByIdOrElseThrow(kpiCatalogDto.getKpiUnitId()));
        }
        kpiCatalog.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        kpiCatalog.setStatus(Status.ACTIVE.name());
        return save(kpiCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public KpiCatalog edit(KpiCatalogDto kpiCatalogDto) {
        Assert.notNull(kpiCatalogDto, "kpiCatalogDto is null");
        KpiCatalog kpiCatalog = findByIdOrElseThrow(kpiCatalogDto.getId());
        kpiCatalog.setName(kpiCatalogDto.getName());
        KpiUnit kpiUnit = kpiCatalogDto.getKpiUnitId() == null ? null : kpiUnitService.findByIdOrElseThrow(kpiCatalogDto.getKpiUnitId());
        kpiCatalog.setKpiUnit(kpiUnit);
        return save(kpiCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        Long count = identityKpiService.countByKpiCatalogId(id);
        if (count > 0) {
            throw new BusinessException("error.kpiCatalog.kpiCatalogUsedInModelVersionKpiSchema");
        }
        KpiCatalog kpiCatalog = findByIdOrElseThrow(id);
        kpiCatalog.setStatus(Status.PASSIVE.name());
        save(kpiCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(KpiCatalogDto kpiCatalogDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<KpiCatalog>> optionalSpecification = createSpecification(kpiCatalogDto);
        Page<KpiCatalog> modelPage = findAll(optionalSpecification.get(), pageable);
        List<KpiCatalogDto> content = modelPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<KpiCatalog>> createSpecification(KpiCatalogDto kpiCatalogDto) {
        List<Specification<KpiCatalog>> specificationList = new ArrayList<>();

        Specification<KpiCatalog> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(KpiCatalog_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        Specification<KpiCatalog> organEq = (root, query, criteriaBuilder) -> {
            Join<KpiCatalog, Organ> organJoin = root.join(KpiCatalog_.ORGAN);
            return criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
        };
        specificationList.add(organEq);

        if (kpiCatalogDto.getName() != null) {
            Specification<KpiCatalog> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(KpiCatalog_.NAME), "%" + kpiCatalogDto.getName() + "%");
            specificationList.add(nameLike);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiCatalog> findActiveByKpiUnitId(Long kpiUnitId) {
        return getRepository().findActiveByKpiUnitId(kpiUnitId);
    }

}
