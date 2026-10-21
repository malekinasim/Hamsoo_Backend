package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.identity.IdentityKpiDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityKpi;
import com.hamsoo.bpm.model.entity.identity.IdentityKpi_;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.identity.IdentityKpiRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityKpiService;
import com.hamsoo.bpm.service.api.kpicatalog.KpiCatalogService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
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
public class IdentityKpiServiceImpl implements IdentityKpiService {

    private final IdentityKpiRepository identityKpiRepository;
    private final ModelVersionService modelVersionService;
    private final KpiCatalogService kpiCatalogService;
    private final CodeCategoryService codeCategoryService;


    public IdentityKpiServiceImpl(IdentityKpiRepository identityKpiRepository,
                                  ModelVersionService modelVersionService,
                                  CodeCategoryService codeCategoryService,
                                  KpiCatalogService kpiCatalogService) {
        this.identityKpiRepository = identityKpiRepository;
        this.modelVersionService = modelVersionService;
        this.kpiCatalogService = kpiCatalogService;
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public IdentityKpiRepository getRepository() {
        return identityKpiRepository;
    }

    @Override
    public IdentityKpi findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.identityKpi.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.identityKpi.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityKpi create(IdentityKpiDto identityKpiDto) {
        Assert.notNull(identityKpiDto, "identityKpiDto is null");
        modelVersionService.checkModelVersionStatus(identityKpiDto.getModelVersionId());
        IdentityKpi identityKpi = new IdentityKpi();

        identityKpi.setModelVersion(modelVersionService.findByIdOrElseThrow(identityKpiDto.getModelVersionId()));
        identityKpi.setStatus(Status.ACTIVE.name());
        identityKpi.setKpiCatalog(kpiCatalogService.findByIdOrElseThrow(identityKpiDto.getKpiCatalogId()));
        identityKpi.setKpiEvlDuration(codeCategoryService.findByCategoryAndCode(IdentityKpi.EvlDurationType.CATEGORY, identityKpiDto.getKpiEvlDuration()));
        identityKpi.setKpiMonitorType(codeCategoryService.findByCategoryAndCode(IdentityKpi.MonitorType.CATEGORY, identityKpiDto.getKpiMonitorType()));
        identityKpi.setKpiOptimalTrend(codeCategoryService.findByCategoryAndCode(IdentityKpi.TrendType.CATEGORY, identityKpiDto.getKpiOptimalTrend()));
        return save(identityKpi);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IdentityKpi identityKpi = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(identityKpi.getModelVersion().getId());
        identityKpi.setStatus(Status.PASSIVE.name());
        save(identityKpi);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityKpiDto mapToDto(IdentityKpi identityKpi) {
        Assert.notNull(identityKpi, "identityKpi is null");
        identityKpi = findByIdOrElseThrow(identityKpi.getId());
        IdentityKpiDto identityKpiDto = new IdentityKpiDto();
        identityKpiDto.setId(identityKpi.getId());
        identityKpiDto.setModelVersionId(identityKpi.getModelVersion().getId());
        identityKpiDto.setKpiCatalogDto(kpiCatalogService.mapToDto(identityKpi.getKpiCatalog()));
        if(identityKpi.getKpiEvlDuration()!=null) {
            identityKpiDto.setKpiEvlDuration(identityKpi.getKpiEvlDuration().getCode());
            identityKpiDto.setKpiEvlDurationName(codeCategoryService.findByCategoryAndCode(IdentityKpi.EvlDurationType.CATEGORY, identityKpi.getKpiEvlDuration().getCode()).getFaName());
        }
        if(identityKpi.getKpiMonitorType()!=null) {
            identityKpiDto.setKpiMonitorType(identityKpi.getKpiMonitorType().getCode());
            identityKpiDto.setKpiMonitorTypeName(codeCategoryService.findByCategoryAndCode(IdentityKpi.MonitorType.CATEGORY, identityKpi.getKpiMonitorType().getCode()).getFaName());
        }
        if(identityKpi.getKpiMonitorType()!=null) {
            identityKpiDto.setKpiOptimalTrend(identityKpi.getKpiOptimalTrend().getCode());
            identityKpiDto.setKpiOptimalTrendName(codeCategoryService.findByCategoryAndCode(IdentityKpi.TrendType.CATEGORY, identityKpi.getKpiOptimalTrend().getCode()).getFaName());
        }
        return identityKpiDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(IdentityKpiDto identityKpiDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<IdentityKpi, IdentityKpiDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<IdentityKpi>> optionalSpecification = createSpecification(identityKpiDto);
        Page<IdentityKpi> identityKpiPage = findAll(optionalSpecification.get(), pageable);
        List<IdentityKpiDto> content = identityKpiPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(identityKpiPage.getTotalElements())
                .totalPages(identityKpiPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<IdentityKpi>> createSpecification(IdentityKpiDto identityKpiDto) {
        List<Specification<IdentityKpi>> specificationList = new ArrayList<>();

        Specification<IdentityKpi> organEq = (root, query, criteriaBuilder) -> {
            Join<IdentityKpi, ModelVersion> modelVersionJoin = root.join(IdentityKpi_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);

            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), identityKpiDto.getModelVersionId());
            predicates[2] = criteriaBuilder.equal(root.get(IdentityKpi_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByKpiCatalogId(Long kpiCatalogId) {
        return getRepository().countByKpiCatalogId(kpiCatalogId);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityKpi clone(IdentityKpi identityKpi) {
        Asserts.notNull(identityKpi, "identityKpi is null");
        identityKpi = findByIdOrElseThrow(identityKpi.getId());
        IdentityKpi clone = new IdentityKpi();
        clone.setModelVersion(identityKpi.getModelVersion());
        clone.setStatus(identityKpi.getStatus());
        clone.setKpiCatalog(identityKpi.getKpiCatalog());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityKpi> findActiveByModelVersionId(Long modelVersionId) {
        return getRepository().findActiveByModelVersionId(SecurityUtils.getCurrentOrganIdOrElseThrow(), modelVersionId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyIdentityKpi(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<IdentityKpi> identityKpiList = findActiveByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(identityKpiList)) {
            return;
        }
        ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        identityKpiList.forEach(identityKpi -> {
            IdentityKpi clone = clone(identityKpi);
            clone.setModelVersion(modelVersionTo);
            save(clone);
        });
    }
}
