package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.form.FormLovCatalogDto;
import com.hamsoo.bpm.model.entity.form.FormLovCatalog;
import com.hamsoo.bpm.repository.jpa.form.FormLovCatalogRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public interface FormLovCatalogService extends BaseService<FormLovCatalog, Long, FormLovCatalogRepository> {

    FormLovCatalog findByIdOrElseThrow(Long id);

    FormLovCatalog create(FormLovCatalogDto formLovCatalogDto);

    FormLovCatalog edit(FormLovCatalogDto formLovCatalogDto);

    void passive(Long id);

    FormLovCatalogDto mapToDto(FormLovCatalog formLovCatalog);

    SearchDto list(FormLovCatalogDto formLovCatalogDto, int page, int size, String orderBy, Sort.Direction direction, Function<FormLovCatalog, FormLovCatalogDto> mapper);

}
