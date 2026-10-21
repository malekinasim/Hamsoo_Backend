package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.form.FormEntityDto;
import com.hamsoo.bpm.model.entity.form.FormEntity;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.form.FormEntityRepository;
import com.hamsoo.bpm.service.api.form.FormEntityService;
import com.hamsoo.bpm.service.api.form.FormLovCatalogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class FormEntityServiceImpl implements FormEntityService {

    private final FormEntityRepository formEntityRepository;
    private final FormLovCatalogService formLovCatalogService;

    public FormEntityServiceImpl(FormEntityRepository formEntityRepository,
                                 FormLovCatalogService formLovCatalogService) {
        this.formEntityRepository = formEntityRepository;
        this.formLovCatalogService = formLovCatalogService;
    }

    @Override
    public FormEntityRepository getRepository() {
        return formEntityRepository;
    }

    private FormEntity findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.formEntity.idIsNotValid");
        }
        return getRepository().findActiveById(id)
                .orElseThrow(() -> new BusinessException("error.formEntity.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormEntity create(FormEntityDto formEntityDto) {
        Assert.notNull(formEntityDto, "formEntityDto is null");
        FormEntity formEntity = new FormEntity();
        formEntity.setTitle(formEntityDto.getTitle());
        formEntity.setDescription(formEntityDto.getDescription());
        formEntity.setFromEntity(formEntityDto.getFromEntity());
        formEntity.setEntityConditions(formEntityDto.getEntityConditions());
        formEntity.setFieldCount(formEntityDto.getFieldCount());
        formEntity.setFieldTitle(formEntityDto.getFieldTitle());
        formEntity.setFromVersion(formEntityDto.getFromVersion());
        formEntity.setType(formLovCatalogService.findByIdOrElseThrow(formEntityDto.getTypeId()));
        formEntity.setStatus(Status.ACTIVE.name());
        return save(formEntity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormEntity edit(FormEntityDto formEntityDto) {
        Assert.notNull(formEntityDto, "formEntityDto is null");
        FormEntity formEntity = findByIdOrElseThrow(formEntityDto.getId());
        formEntity.setTitle(formEntityDto.getTitle());
        formEntity.setDescription(formEntityDto.getDescription());
        formEntity.setFromEntity(formEntityDto.getFromEntity());
        formEntity.setEntityConditions(formEntityDto.getEntityConditions());
        formEntity.setFieldCount(formEntityDto.getFieldCount());
        formEntity.setFieldTitle(formEntityDto.getFieldTitle());
        formEntity.setType(formLovCatalogService.findByIdOrElseThrow(formEntityDto.getTypeId()));
        return save(formEntity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        FormEntity formEntity = findByIdOrElseThrow(id);
        formEntity.setStatus(Status.PASSIVE.name());
        save(formEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public FormEntityDto mapToDto(FormEntity formEntity) {
        Assert.notNull(formEntity, "formEntity is null");
        formEntity = findByIdOrElseThrow(formEntity.getId());
        FormEntityDto formEntityDto = new FormEntityDto();
        formEntityDto.setId(formEntity.getId());
        formEntityDto.setTitle(formEntity.getTitle());
        formEntityDto.setDescription(formEntity.getDescription());
        formEntityDto.setFromEntity(formEntity.getFromEntity());
        formEntityDto.setEntityConditions(formEntity.getEntityConditions());
        formEntityDto.setFieldCount(formEntity.getFieldCount());
        formEntityDto.setFieldTitle(formEntity.getFieldTitle());
        formEntityDto.setFromVersion(formEntity.getFromVersion());
        formEntityDto.setType(formLovCatalogService.mapToDto(formEntity.getType()));
        return formEntityDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FormEntity> findByIdAndFormVersionId(Long id, Long formVersionId) {
        return getRepository().findByIdAndFormVersionId(id, formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormEntity> list(Long formVersionId, Pageable pageable) {
        return getRepository().list(formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormEntity> findByFormVersionId(Long formVersionId) {
        return getRepository().findByFormVersionId(formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

}
