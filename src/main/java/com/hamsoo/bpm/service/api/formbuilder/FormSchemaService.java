package com.hamsoo.bpm.service.api.formbuilder;

import com.hamsoo.bpm.model.dto.FormBuilder.FormSchemaCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.formbuilder.FormSchemaCatalog;
import com.hamsoo.bpm.repository.jpa.formbuilder.FormSchemaRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

public interface FormSchemaService extends BaseService<FormSchemaCatalog, Long, FormSchemaRepository> {

    FormSchemaCatalog findByIdOrElseThrow(Long id);

    FormSchemaCatalog findActiveByCodeAndOrganId(String code);

    FormSchemaCatalog create(FormSchemaCatalogDto identityIoDto);

    FormSchemaCatalog edit(FormSchemaCatalogDto identityIoDto);

    SearchDto list(FormSchemaCatalogDto formSchemaCatalogDto, int page, int size, String orderBy, Sort.Direction direction, Function<FormSchemaCatalog, FormSchemaCatalogDto> mapper);

    @Transactional(rollbackFor = Throwable.class)
    void passive(Long id);

    @Transactional(readOnly = true)
    FormSchemaCatalogDto mapToDto(FormSchemaCatalog formSchemaCatalog);

}
