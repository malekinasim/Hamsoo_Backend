package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.FormVersionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.repository.jpa.form.FormVersionRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.function.Function;

public interface FormVersionService extends BaseService<FormVersion, Long, FormVersionRepository> {

    FormVersion findByIdOrElseThrow(Long id);

    FormVersion create(FormVersionDto formVersionDto);

    FormVersion createNewVersion(FormVersionDto formVersionDto);

    FormVersion edit(FormVersionDto formVersionDto);

    FormVersionDto mapToDto(FormVersion formVersion);

    SearchDto list(FormVersionDto formVersionDto, int page, int size, String orderBy, Sort.Direction direction, Function<FormVersion, FormVersionDto> mapper);

    Optional<FormVersion> findLastVersionByFormCatalogId(Long formCatalogId);

    Long countByRuleId(Long ruleId);

    Long countByFunctionId(Long functionId);

    Long countByFormEntityId(Long formEntityId);

    void checkVersion(FormVersion formVersion);

}
