package com.hamsoo.bpm.service.impl.kpiunit;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.model.dto.kpiunit.KpiUnitDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog;
import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit;
import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.kpiunit.KpiUnitRepository;
import com.hamsoo.bpm.service.api.kpicatalog.KpiCatalogService;
import com.hamsoo.bpm.service.api.kpiunit.KpiUnitService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KpiUnitServiceImpl implements KpiUnitService {

    private final KpiUnitRepository kpiUnitRepository;
    private final KpiCatalogService kpiCatalogService;

    public KpiUnitServiceImpl(KpiUnitRepository kpiUnitRepository,
                              @Lazy KpiCatalogService kpiCatalogService) {
        this.kpiUnitRepository = kpiUnitRepository;
        this.kpiCatalogService = kpiCatalogService;
    }

    @Override
    public KpiUnitRepository getRepository() {
        return kpiUnitRepository;
    }

    @Override
    public KpiUnit findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.kpiUnit.idIsNotValid");
        }
        return getRepository().findByIdAndStatus(id, Status.ACTIVE.name())
                .orElseThrow(() -> new BusinessException("error.kpiUnit.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public KpiUnit create(KpiUnitDto kpiUnitDto) {
        Assert.notNull(kpiUnitDto, "kpiUnitDto is null");
        Optional<KpiUnit> kpiUnitOptional = getRepository().findByUnitCodeAndStatus(kpiUnitDto.getUnitCode(), Status.ACTIVE.name());
        if (kpiUnitOptional.isPresent()) {
            throw new BusinessException("error.kpiUnit.unitCodeIsDuplicate");
        }
        KpiUnit kpiUnit = new KpiUnit();
        kpiUnit.setUnitCode(kpiUnitDto.getUnitCode());
        kpiUnit.setUnitName(kpiUnitDto.getUnitName());
        kpiUnit.setStatus(Status.ACTIVE.name());
        return save(kpiUnit);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public KpiUnit edit(KpiUnitDto kpiUnitDto) {
        Assert.notNull(kpiUnitDto, "kpiUnitDto is null");
        KpiUnit kpiUnit = findByIdOrElseThrow(kpiUnitDto.getId());
        Optional<KpiUnit> kpiUnitOptional = getRepository().findByUnitCodeAndStatus(kpiUnitDto.getUnitCode(), Status.ACTIVE.name());
        if (kpiUnitOptional.isPresent() && !kpiUnit.getId().equals(kpiUnitOptional.get().getId())) {
            throw new BusinessException("error.kpiUnit.unitCodeIsDuplicate");
        }
        kpiUnit.setUnitCode(kpiUnitDto.getUnitCode());
        kpiUnit.setUnitName(kpiUnitDto.getUnitName());
        return save(kpiUnit);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        KpiUnit kpiUnit = findByIdOrElseThrow(id);
        List<KpiCatalog> kpiCatalogList = kpiCatalogService.findActiveByKpiUnitId(id);
        if (!CollectionUtils.isEmpty(kpiCatalogList)) {
            throw new BusinessException("error.kpiUnit.kpiUnitUsedInKpiCatalog");
        }
        kpiUnit.setStatus(Status.PASSIVE.name());
        save(kpiUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiUnitDto mapToDto(KpiUnit kpiUnit) {
        Assert.notNull(kpiUnit, "kpiUnit is null");
        kpiUnit = findByIdOrElseThrow(kpiUnit.getId());
        KpiUnitDto kpiUnitDto = new KpiUnitDto();
        kpiUnitDto.setId(kpiUnit.getId());
        kpiUnitDto.setUnitCode(kpiUnit.getUnitCode());
        kpiUnitDto.setUnitName(kpiUnit.getUnitName());
        return kpiUnitDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiUnitDto> mapToDto(List<KpiUnit> kpiUnitList) {
        if (CollectionUtils.isEmpty(kpiUnitList)) {
            return new ArrayList<>();
        }
        return kpiUnitList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(KpiUnitDto kpiUnitDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<KpiUnit>> optionalSpecification = createSpecification(kpiUnitDto);
        Page<KpiUnit> kpiUnitPage = findAll(optionalSpecification.get(), pageable);
        List<KpiUnitDto> content = kpiUnitPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(kpiUnitPage.getTotalElements())
                .totalPages(kpiUnitPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<KpiUnit>> createSpecification(KpiUnitDto kpiUnitDto) {
        List<Specification<KpiUnit>> specificationList = new ArrayList<>();

        Specification<KpiUnit> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(KpiUnit_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        if (StringUtils.hasText(kpiUnitDto.getUnitName())) {
            Specification<KpiUnit> unitNameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(KpiUnit_.UNIT_NAME), "%" + kpiUnitDto.getUnitName() + "%");
            specificationList.add(unitNameLike);
        }

        if (StringUtils.hasText(kpiUnitDto.getUnitCode())) {
            Specification<KpiUnit> unitCodeLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(KpiUnit_.UNIT_CODE), "%" + kpiUnitDto.getUnitCode() + "%");
            specificationList.add(unitCodeLike);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

}
