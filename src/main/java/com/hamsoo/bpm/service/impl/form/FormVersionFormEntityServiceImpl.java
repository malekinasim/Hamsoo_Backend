package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.form.FormEntityDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormEntity;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.service.api.form.FormEntityService;
import com.hamsoo.bpm.service.api.form.FormVersionFormEntityService;
import com.hamsoo.bpm.service.api.form.FormVersionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;

@Service
public class FormVersionFormEntityServiceImpl implements FormVersionFormEntityService {

    private final FormVersionService formVersionService;
    private final FormEntityService formEntityService;

    public FormVersionFormEntityServiceImpl(FormVersionService formVersionService,
                                            FormEntityService formEntityService) {
        this.formVersionService = formVersionService;
        this.formEntityService = formEntityService;
    }

    @Override
    @Transactional(readOnly = true)
    public FormEntity findByIdOrElseThrow(Long formEntityId, Long formVersionId) {
        if (formEntityId == null || formVersionId == null) {
            throw new BusinessException("error.formVersionFormEntity.formEntityIdNotFoundForThisFormVersion");
        }
        return formEntityService.findByIdAndFormVersionId(formEntityId, formVersionId)
                .orElseThrow(() -> new BusinessException("error.formVersionFormEntity.formEntityIdNotFoundForThisFormVersion"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormEntity addFormEntity(FormEntityDto formEntityDto) {
        Assert.notNull(formEntityDto, "formEntityDto is null");
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(formEntityDto.getFormVersionId());
        formVersionService.checkVersion(formVersion);
        formEntityDto.setFromVersion(formVersion.getFormVersion());
        FormEntity formEntity = formEntityService.create(formEntityDto);
        formVersion.addFormEntity(formEntity);
        formVersionService.save(formVersion);
        return formEntity;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FormEntity editFormEntity(FormEntityDto formEntityDto) {
        Assert.notNull(formEntityDto, "formEntityDto is null");
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(formEntityDto.getFormVersionId());
        formVersionService.checkVersion(formVersion);
        FormEntity formEntity = findByIdOrElseThrow(formEntityDto.getId(), formVersion.getId());
        Long formVersionCount = formVersionService.countByFormEntityId(formEntityDto.getId());
        boolean editable = formVersionCount.equals(1L) && formEntity.getFromVersion().equals(formVersion.getFormVersion());
        if (!editable) {
            throw new BusinessException("error.formVersionFormEntity.formEntityNotEditable");
        }
        return formEntityService.edit(formEntityDto);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveFormEntity(Long formEntityId, Long formVersionId) {
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(formVersionId);
        formVersionService.checkVersion(formVersion);
        if (CollectionUtils.isEmpty(formVersion.getFormEntities())) {
            throw new BusinessException("error.formVersionFormEntity.formEntityIdNotFoundForThisFormVersion");
        }
        FormEntity formEntity = findByIdOrElseThrow(formEntityId, formVersion.getId());
        if (!formVersion.getFormEntities().contains(formEntity)) {
            throw new BusinessException("error.formVersionFormEntity.formEntityIdNotFoundForThisFormVersion");
        }
        formVersion.getFormEntities().remove(formEntity);
        formVersionService.save(formVersion);
        Long formVersionCount = formVersionService.countByFormEntityId(formEntity.getId());
        if (formVersionCount.equals(0L)) {
            formEntityService.passive(formEntity.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(FormEntityDto formEntityDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<FormEntity, FormEntityDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Page<FormEntity> formEntityPage = formEntityService.list(formEntityDto.getFormVersionId(), pageable);
        List<FormEntityDto> content = formEntityPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(formEntityPage.getTotalElements())
                .totalPages(formEntityPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    /**
     * copy relations between formVersion and formEntity entity
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyFormVersionFormEntityIfExist(Long formVersionIdFrom, Long formVersionIdTo) {
        List<FormEntity> formEntityList = formEntityService.findByFormVersionId(formVersionIdFrom);
        if (CollectionUtils.isEmpty(formEntityList)) {
            return;
        }
        FormVersion formVersionTo = formVersionService.findByIdOrElseThrow(formVersionIdTo);
        formEntityList.forEach(formVersionTo::addFormEntity);
        formVersionService.save(formVersionTo);
    }

}
