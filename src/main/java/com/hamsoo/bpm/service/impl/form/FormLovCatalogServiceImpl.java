package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.form.FormLovCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.form.FormLovCatalog;
import com.hamsoo.bpm.model.entity.form.FormLovCatalog_;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.form.FormLovCatalogRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.form.FormLovCatalogService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import org.apache.commons.lang3.ArrayUtils;
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
public class FormLovCatalogServiceImpl implements FormLovCatalogService {

    private final FormLovCatalogRepository formLovCatalogRepository;
    private final CodeCategoryService codeCategoryService;
    private final OrganService organService;

    public FormLovCatalogServiceImpl(FormLovCatalogRepository formLovCatalogRepository,
                                     CodeCategoryService codeCategoryService,
                                     OrganService organService) {
        this.formLovCatalogRepository = formLovCatalogRepository;
        this.codeCategoryService = codeCategoryService;
        this.organService = organService;
    }

    @Override
    public FormLovCatalogRepository getRepository() {
        return formLovCatalogRepository;
    }

    @Override
    public FormLovCatalog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.formLovCatalog.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.formLovCatalog.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormLovCatalog create(FormLovCatalogDto formLovCatalogDto) {
        Assert.notNull(formLovCatalogDto, "formLovCatalogDto is null");
        FormLovCatalog formLovCatalog = new FormLovCatalog();
        formLovCatalog.setName(formLovCatalogDto.getName());
        formLovCatalog.setType(codeCategoryService.findByCategoryAndCode(FormLovCatalog.Type.CATEGORY, formLovCatalogDto.getTypeCode()));
        formLovCatalog.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        formLovCatalog.setStatus(Status.ACTIVE.name());
        return save(formLovCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormLovCatalog edit(FormLovCatalogDto formLovCatalogDto) {
        Assert.notNull(formLovCatalogDto, "formLovCatalogDto is null");
        FormLovCatalog formLovCatalog = findByIdOrElseThrow(formLovCatalogDto.getId());
        formLovCatalog.setName(formLovCatalogDto.getName());
        return save(formLovCatalog);
    }

    @Override
    public void passive(Long id) {
        FormLovCatalog formLovCatalog = findByIdOrElseThrow(id);
        //todo check delete dependent table
        formLovCatalog.setStatus(Status.PASSIVE.name());
        save(formLovCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public FormLovCatalogDto mapToDto(FormLovCatalog formLovCatalog) {
        Assert.notNull(formLovCatalog, "formLovCatalog is null");
        formLovCatalog = findByIdOrElseThrow(formLovCatalog.getId());
        FormLovCatalogDto formLovCatalogDto = new FormLovCatalogDto();
        formLovCatalogDto.setId(formLovCatalog.getId());
        formLovCatalogDto.setName(formLovCatalog.getName());
        formLovCatalogDto.setTypeCode(formLovCatalog.getType().getCode());
        formLovCatalogDto.setTypeTitle(formLovCatalog.getType().getFaName());
        formLovCatalogDto.setOrganId(formLovCatalog.getOrgan().getId());
        return formLovCatalogDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(FormLovCatalogDto formLovCatalogDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<FormLovCatalog, FormLovCatalogDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<FormLovCatalog>> optionalSpecification = createSpecification(formLovCatalogDto);
        Page<FormLovCatalog> taskCatalogPage = findAll(optionalSpecification.get(), pageable);
        List<FormLovCatalogDto> content = taskCatalogPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(taskCatalogPage.getTotalElements())
                .totalPages(taskCatalogPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<FormLovCatalog>> createSpecification(FormLovCatalogDto formLovCatalogDto) {
        List<Specification<FormLovCatalog>> specificationList = new ArrayList<>();

        Specification<FormLovCatalog> organEq = (root, query, criteriaBuilder) -> {
            Join<FormLovCatalog, Organ> organJoin = root.join(Model_.ORGAN);
            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(root.get(FormLovCatalog_.STATUS), Status.ACTIVE.name());
            if (StringUtils.hasText(formLovCatalogDto.getName())) {
                Predicate nameLike = criteriaBuilder.like(root.get(FormLovCatalog_.NAME), "%" + formLovCatalogDto.getName() + "%");
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);

        if (StringUtils.hasText(formLovCatalogDto.getTypeCode())) {
            Specification<FormLovCatalog> typeCodeEqual = (root, query, criteriaBuilder) -> {
                Join<FormLovCatalog, CodeCategory> typeJoin = root.join(FormLovCatalog_.TYPE);
                Predicate typeCodeEqualPredicate = criteriaBuilder.equal(typeJoin.get(CodeCategory_.CODE), formLovCatalogDto.getTypeCode());
                return criteriaBuilder.and(typeCodeEqualPredicate);
            };
            specificationList.add(typeCodeEqual);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

}
