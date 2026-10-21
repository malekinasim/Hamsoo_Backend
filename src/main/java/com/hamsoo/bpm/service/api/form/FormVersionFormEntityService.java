package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.FormEntityDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormEntity;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public interface FormVersionFormEntityService {

    FormEntity findByIdOrElseThrow(Long formEntityId, Long formVersionId);

    FormEntity addFormEntity(FormEntityDto formEntityDto);

    FormEntity editFormEntity(FormEntityDto formEntityDto);

    void passiveFormEntity(Long formEntityId, Long formVersionId);

    SearchDto list(FormEntityDto formEntityDto, int page, int size, String orderBy, Sort.Direction direction, Function<FormEntity, FormEntityDto> mapper);

    void copyFormVersionFormEntityIfExist(Long formVersionIdFrom, Long formVersionIdTo);

}
