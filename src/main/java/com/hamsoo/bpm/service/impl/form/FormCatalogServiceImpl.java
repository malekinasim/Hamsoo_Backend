package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.form.FormCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormCatalog;
import com.hamsoo.bpm.model.entity.form.FormCatalog_;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.form.FormCatalogRepository;
import com.hamsoo.bpm.service.api.form.FormCatalogService;
import com.hamsoo.bpm.service.api.form.FormVersionService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class FormCatalogServiceImpl implements FormCatalogService {

    private final FormCatalogRepository formCatalogRepository;
    private final FormVersionService formVersionService;
    private final OrganService organService;

    public FormCatalogServiceImpl(FormCatalogRepository formCatalogRepository,
                                  @Lazy  FormVersionService formVersionService, OrganService organService) {
        this.formCatalogRepository = formCatalogRepository;
        this.formVersionService = formVersionService;
        this.organService = organService;
    }

    @Override
    public FormCatalogRepository getRepository() {
        return formCatalogRepository;
    }

    @Override
    public FormCatalog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.formCatalog.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.formCatalog.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormCatalog create(FormCatalogDto formCatalogDto) {
        Assert.notNull(formCatalogDto, "formCatalogDto is null");
        checkDuplicationOfFromKey(formCatalogDto.getFormKey());
        FormCatalog formCatalog = new FormCatalog();
        formCatalog.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        formCatalog.setStatus(Status.ACTIVE.name());
        formCatalog.setFaName(formCatalogDto.getFaName());
        formCatalog.setEnName(formCatalogDto.getEnName());
        formCatalog.setFormKey(formCatalogDto.getFormKey());
        return save(formCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormCatalog edit(FormCatalogDto formCatalogDto) {
        Assert.notNull(formCatalogDto, "formCatalogDto is null");
        FormCatalog formCatalog = findByIdOrElseThrow(formCatalogDto.getId());
        if (!formCatalog.getFormKey().equals(formCatalogDto.getFormKey())) {
            checkDuplicationOfFromKey(formCatalogDto.getFormKey());
        }
        formCatalog.setFaName(formCatalogDto.getFaName());
        formCatalog.setEnName(formCatalogDto.getEnName());
        formCatalog.setFormKey(formCatalogDto.getFormKey());
        return save(formCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public FormCatalogDto mapToDto(FormCatalog formCatalog) {
        Assert.notNull(formCatalog, "formCatalog is null");
        formCatalog = findByIdOrElseThrow(formCatalog.getId());
        FormCatalogDto formCatalogDto = new FormCatalogDto();
        formCatalogDto.setId(formCatalog.getId());
        formCatalogDto.setOrganId(formCatalog.getOrgan().getId());
        formCatalogDto.setFaName(formCatalog.getFaName());
        formCatalogDto.setEnName(formCatalog.getEnName());
        formCatalogDto.setFormKey(formCatalog.getFormKey());
        return formCatalogDto;
    }

    @Override
    public FormCatalogDto mapToDtoByLastVersionInfo(FormCatalog formCatalog) {
        FormCatalogDto formCatalogDto = this.mapToDto(formCatalog);
       Optional<FormVersion> lastFormVersion= formVersionService.findLastVersionByFormCatalogId(formCatalog.getId());
        if(lastFormVersion.isPresent())
            formCatalogDto.setLastFormVersion(formVersionService.mapToDto(lastFormVersion.get()));
      return  formCatalogDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(FormCatalogDto formCatalogDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<FormCatalog, FormCatalogDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<FormCatalog>> optionalSpecification = createSpecification(formCatalogDto);
        Page<FormCatalog> formCatalogPage = findAll(optionalSpecification.get(), pageable);
        List<FormCatalogDto> content = formCatalogPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(formCatalogPage.getTotalElements())
                .totalPages(formCatalogPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<FormCatalog>> createSpecification(FormCatalogDto formCatalogDto) {
        List<Specification<FormCatalog>> specificationList = new ArrayList<>();
        Specification<FormCatalog> organEq = (root, query, criteriaBuilder) -> {
            Join<FormCatalog, Organ> organJoin = root.join(FormCatalog_.ORGAN);
            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(root.get(FormCatalog_.STATUS), Status.ACTIVE.name());
            if (StringUtils.hasText(formCatalogDto.getSearchText())) {
                String pattern = "%" + formCatalogDto.getSearchText() + "%";
                Predicate faNameLike = criteriaBuilder.like(root.get(FormCatalog_.FA_NAME), pattern);
                Predicate formKeyLike = criteriaBuilder.like(root.get(FormCatalog_.FORM_KEY), pattern);
                Predicate searchTextOr = criteriaBuilder.or(faNameLike, formKeyLike);
                predicates = ArrayUtils.addAll(predicates, searchTextOr);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FormCatalog> findByFormKey(String formKey) {
        return getRepository().findActiveByFormKeyAndOrganId(formKey, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    private void checkDuplicationOfFromKey(String formKey) {
        Assert.notNull(formKey, "formKey is null");
        Optional<FormCatalog> formCatalogOptional = findByFormKey(formKey);
        if (formCatalogOptional.isPresent()) {
            throw new BusinessException("error.formCatalog.formKeyIsDuplicate");
        }
    }

}
