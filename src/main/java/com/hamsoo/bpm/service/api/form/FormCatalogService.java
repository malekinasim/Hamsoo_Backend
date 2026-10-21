package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.FormCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormCatalog;
import com.hamsoo.bpm.repository.jpa.form.FormCatalogRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.function.Function;

public interface FormCatalogService extends BaseService<FormCatalog, Long, FormCatalogRepository> {

    FormCatalog findByIdOrElseThrow(Long id);

    FormCatalog create(FormCatalogDto formCatalogDto);

    FormCatalog edit(FormCatalogDto formCatalogDto);

    FormCatalogDto mapToDto(FormCatalog formCatalog);

    SearchDto list(FormCatalogDto formCatalogDto, int page, int size, String orderBy, Sort.Direction direction, Function<FormCatalog, FormCatalogDto> mapper);

    Optional<FormCatalog> findByFormKey(String formKey);

    FormCatalogDto mapToDtoByLastVersionInfo(FormCatalog formCatalog);
}
