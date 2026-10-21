package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.FormEntityDto;
import com.hamsoo.bpm.model.entity.form.FormEntity;
import com.hamsoo.bpm.repository.jpa.form.FormEntityRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FormEntityService extends BaseService<FormEntity, Long, FormEntityRepository> {

    FormEntity create(FormEntityDto formEntityDto);

    FormEntity edit(FormEntityDto formEntityDto);

    void passive(Long id);

    FormEntityDto mapToDto(FormEntity formEntity);

    Optional<FormEntity> findByIdAndFormVersionId(Long id, Long formVersionId);

    Page<FormEntity> list(Long formVersionId, Pageable pageable);

    List<FormEntity> findByFormVersionId(Long formVersionId);

}
