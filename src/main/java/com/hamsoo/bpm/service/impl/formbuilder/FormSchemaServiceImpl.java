package com.hamsoo.bpm.service.impl.formbuilder;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.FormBuilder.FormSchemaCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.formbuilder.FormSchemaCatalog;
import com.hamsoo.bpm.model.entity.formbuilder.FormSchemaCatalog_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.formbuilder.FormSchemaRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.formbuilder.FormSchemaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class FormSchemaServiceImpl implements FormSchemaService {

    private final FormSchemaRepository formSchemaRepository;

    private final CodeCategoryService codeCategoryService;

    public FormSchemaServiceImpl(FormSchemaRepository formSchemaRepository, CodeCategoryService codeCategoryService) {
        this.formSchemaRepository = formSchemaRepository;
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public FormSchemaRepository getRepository() {
        return formSchemaRepository;
    }

    @Override
    public FormSchemaCatalog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.FormSchemaCatalog.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.FormSchemaCatalog.idIsNotValid"));
    }


    public FormSchemaCatalog findActiveByCodeAndOrganId(String code) {
        if (code == null) {
            throw new BusinessException("error.FormSchemaCatalog.codeIsNotValid");
        }
        return getRepository().findActiveByCodeAndOrganId(code, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.FormSchemaCatalog.codeIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormSchemaCatalog create(FormSchemaCatalogDto formSchemaCatalogDto) {
        Assert.notNull(formSchemaCatalogDto, "formSchemaCatalogDto is null");
        FormSchemaCatalog formSchemaCatalog = new FormSchemaCatalog();
        formSchemaCatalog.setName(formSchemaCatalogDto.getName());
        formSchemaCatalog.setStatus(Status.PASSIVE.name());
        formSchemaCatalog.setRunStatus(codeCategoryService.findByCategoryAndCode(FormSchemaCatalog.Status.CATEGORY, FormSchemaCatalog.Status.DRAFT.getCode()));
        return save(formSchemaCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormSchemaCatalog edit(FormSchemaCatalogDto formSchemaCatalogDto) {
        Assert.notNull(formSchemaCatalogDto, "formSchemaCatalogDto is null");
        FormSchemaCatalog formSchemaCatalog = findByIdOrElseThrow(formSchemaCatalogDto.getId());
        formSchemaCatalog.setStatus(Status.ACTIVE.name());
        formSchemaCatalog.setRunStatus(codeCategoryService.findByCategoryAndCode(FormSchemaCatalog.Status.CATEGORY, formSchemaCatalogDto.getRunStatus()));
        formSchemaCatalog.setName(formSchemaCatalogDto.getName());
        formSchemaCatalog.setJsonContent(formSchemaCatalogDto.getName());
        return save(formSchemaCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        FormSchemaCatalog formSchemaCatalog = findByIdOrElseThrow(id);
//        Optional<ErrorDto> optionalErrorDto = validateFormSchemaCanPassive(id);
//        if (optionalErrorDto.isPresent()) {
//            throw new GeneralErrorException(Collections.singletonList(optionalErrorDto.get()));
//        }
        formSchemaCatalog.setStatus(Status.PASSIVE.name());
        save(formSchemaCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public FormSchemaCatalogDto mapToDto(FormSchemaCatalog formSchemaCatalog) {
        Assert.notNull(formSchemaCatalog, "formSchemaCatalog is null");
        formSchemaCatalog = findByIdOrElseThrow(formSchemaCatalog.getId());
        FormSchemaCatalogDto formSchemaCatalogDto = new FormSchemaCatalogDto();
        formSchemaCatalogDto.setId(formSchemaCatalog.getId());
        formSchemaCatalogDto.setName(formSchemaCatalog.getName());
        formSchemaCatalogDto.setStatus(formSchemaCatalog.getStatus());
        formSchemaCatalogDto.setRunStatus(formSchemaCatalog.getRunStatus().getCode());
        formSchemaCatalogDto.setRunStatusFaName(formSchemaCatalog.getRunStatus().getFaName());

        return formSchemaCatalogDto;
    }
//    private Optional<ErrorDto> validateFormSchemaCanPassive(Long id) {
//
//    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(FormSchemaCatalogDto formSchemaCatalogDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<FormSchemaCatalog, FormSchemaCatalogDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<FormSchemaCatalog>> optionalSpecification = createSpecification(formSchemaCatalogDto);
        Page<FormSchemaCatalog> formSchemaPage = findAll(optionalSpecification.get(), pageable);
        List<FormSchemaCatalogDto> content = formSchemaPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(formSchemaPage.getTotalElements())
                .totalPages(formSchemaPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<FormSchemaCatalog>> createSpecification(FormSchemaCatalogDto formSchemaCatalogDto) {
        List<Specification<FormSchemaCatalog>> specificationList = new ArrayList<>();

        if (formSchemaCatalogDto.getName() != null) {
            Specification<FormSchemaCatalog> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(FormSchemaCatalog_.NAME), "%" + formSchemaCatalogDto.getName() + "%");
            specificationList.add(nameLike);
        }
        if (formSchemaCatalogDto.getCode() != null) {
            Specification<FormSchemaCatalog> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(FormSchemaCatalog_.code),formSchemaCatalogDto.getCode());
            specificationList.add(nameLike);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }


}
