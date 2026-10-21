package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.form.FormVersionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormCatalog;
import com.hamsoo.bpm.service.api.form.*;
import com.hamsoo.bpm.model.entity.form.FormCatalog_;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.model.entity.form.FormVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.form.FormVersionRepository;
import com.hamsoo.bpm.service.api.form.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class FormVersionServiceImpl implements FormVersionService {

    private final FormVersionRepository formVersionRepository;
    private final FormLovCatalogService formLovCatalogService;
    private final FormCatalogService formCatalogService;
    private final FormVersionRuleService formVersionRuleService;
    private final FormVersionFunctionService formVersionFunctionService;
    private final FormVersionFormEntityService formVersionFormEntityService;

    public FormVersionServiceImpl(FormVersionRepository formVersionRepository,
                                  FormLovCatalogService formLovCatalogService,
                                  FormCatalogService formCatalogService,
                                  @Lazy FormVersionRuleService formVersionRuleService,
                                  @Lazy FormVersionFunctionService formVersionFunctionService,
                                  @Lazy FormVersionFormEntityService formVersionFormEntityService) {
        this.formVersionRepository = formVersionRepository;
        this.formLovCatalogService = formLovCatalogService;
        this.formCatalogService = formCatalogService;
        this.formVersionRuleService = formVersionRuleService;
        this.formVersionFunctionService = formVersionFunctionService;
        this.formVersionFormEntityService = formVersionFormEntityService;
    }

    @Override
    public FormVersionRepository getRepository() {
        return formVersionRepository;
    }

    @Override
    public FormVersion findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.formVersion.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.formVersion.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormVersion create(FormVersionDto formVersionDto) {
        Assert.notNull(formVersionDto, "formVersionDto is null");
        FormVersion formVersion = new FormVersion();
        formVersion.setDescription(formVersionDto.getDescription());
        formVersion.setFormVersion(formVersionDto.getFormVersion());
        formVersion.setPublishStatus(formLovCatalogService.findByIdOrElseThrow(formVersionDto.getPublishStatusId()));
        formVersion.setLastVersion(formVersionDto.getLastVersion());
        formVersion.setStatus(Status.ACTIVE.name());
        formVersion.setFromDate(formVersionDto.getFromDate());
        formVersion.setToDate(formVersionDto.getToDate());
        formVersion.setFormCatalog(formCatalogService.findByIdOrElseThrow(formVersionDto.getFormCatalogId()));
        return save(formVersion);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormVersion createNewVersion(FormVersionDto formVersionDto) {
        Assert.notNull(formVersionDto, "formVersionDto is null");
        FormCatalog formCatalog = formCatalogService.findByIdOrElseThrow(formVersionDto.getFormCatalogId());
        Optional<FormVersion> formVersionOptional = findLastVersionByFormCatalogId(formCatalog.getId());
        if (formVersionOptional.isEmpty()) {
            formVersionDto.setFormVersion(1);
            formVersionDto.setLastVersion(true);
            formVersionDto.setFromDate(LocalDateTime.now());
            formVersionDto.setToDate(null);
        } else {
            FormVersion oldFormVersion = formVersionOptional.get();
            oldFormVersion.setLastVersion(false);
            LocalDateTime now = LocalDateTime.now();
            oldFormVersion.setToDate(now);
            oldFormVersion = save(oldFormVersion);
            formVersionDto.setFormVersion(oldFormVersion.getFormVersion() + 1);
            formVersionDto.setLastVersion(true);
            formVersionDto.setFromDate(now);
            formVersionDto.setToDate(null);
        }
        FormVersion formVersion = create(formVersionDto);
        if (formVersionOptional.isPresent()) {
            Long formVersionIdFrom = formVersionOptional.get().getId();
            Long formVersionIdTo = formVersion.getId();
            formVersionRuleService.copyFormVersionRulesIfExist(formVersionIdFrom, formVersionIdTo);
            formVersionFunctionService.copyFormVersionFunctionsIfExist(formVersionIdFrom, formVersionIdTo);
            formVersionFormEntityService.copyFormVersionFormEntityIfExist(formVersionIdFrom, formVersionIdTo);
        }
        return formVersion;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormVersion edit(FormVersionDto formVersionDto) {
        Assert.notNull(formVersionDto, "formVersionDto is null");
        FormVersion formVersion = findByIdOrElseThrow(formVersionDto.getId());
        if (!formVersion.getLastVersion()) {
            throw new BusinessException("error.formVersion.justLastVersionCanBeEdit");
        }
        formVersion.setDescription(formVersionDto.getDescription());
        formVersion.setPublishStatus(formLovCatalogService.findByIdOrElseThrow(formVersionDto.getPublishStatusId()));
        return save(formVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public FormVersionDto mapToDto(FormVersion formVersion) {
        Assert.notNull(formVersion, "formVersion is null");
        formVersion = findByIdOrElseThrow(formVersion.getId());
        FormVersionDto formVersionDto = new FormVersionDto();
        formVersionDto.setId(formVersion.getId());
        formVersionDto.setDescription(formVersion.getDescription());
        formVersionDto.setFormVersion(formVersion.getFormVersion());
        formVersionDto.setFormLovCatalogDto(formLovCatalogService.mapToDto(formVersion.getPublishStatus()));
        formVersionDto.setLastVersion(formVersion.getLastVersion());
        formVersionDto.setFromDate(formVersion.getFromDate());
        formVersionDto.setToDate(formVersion.getToDate());
        formVersionDto.setFormCatalogId(formVersion.getFormCatalog().getId());
        return formVersionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(FormVersionDto formVersionDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<FormVersion, FormVersionDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<FormVersion>> optionalSpecification = createSpecification(formVersionDto);
        Page<FormVersion> formVersionPage = findAll(optionalSpecification.get(), pageable);
        List<FormVersionDto> content = formVersionPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(formVersionPage.getTotalElements())
                .totalPages(formVersionPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<FormVersion>> createSpecification(FormVersionDto formVersionDto) {
        List<Specification<FormVersion>> specificationList = new ArrayList<>();
        Specification<FormVersion> organEq = (root, query, criteriaBuilder) -> {

            Join<FormVersion, FormCatalog> formCatalogJoin = root.join(FormVersion_.FORM_CATALOG);
            Join<FormCatalog, Organ> organJoin = formCatalogJoin.join(FormCatalog_.ORGAN);

            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(root.get(FormVersion_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.equal(formCatalogJoin.get(FormCatalog_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(formCatalogJoin.get(FormCatalog_.ID), formVersionDto.getFormCatalogId());

            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FormVersion> findLastVersionByFormCatalogId(Long formCatalogId) {
        return getRepository().findLastVersionByFormCatalogId(formCatalogId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByRuleId(Long ruleId) {
        return getRepository().countByRuleId(ruleId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByFunctionId(Long functionId) {
        return getRepository().countByFunctionId(functionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByFormEntityId(Long formEntityId) {
        return getRepository().countByFormEntityId(formEntityId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public void checkVersion(FormVersion formVersion) {
        Assert.notNull(formVersion, "formVersion is null");
        if (formVersion.getLastVersion() == null || !formVersion.getLastVersion()) {
            throw new BusinessException("error.formVersion.formVersionNotLastVersion");
        }
    }

}
