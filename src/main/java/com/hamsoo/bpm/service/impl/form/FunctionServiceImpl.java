package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.form.FunctionDto;
import com.hamsoo.bpm.model.entity.form.FormLovCatalog;
import com.hamsoo.bpm.model.entity.form.Function;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.form.FunctionRepository;
import com.hamsoo.bpm.service.api.form.FormLovCatalogService;
import com.hamsoo.bpm.service.api.form.FunctionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class FunctionServiceImpl implements FunctionService {

    private final FunctionRepository functionRepository;
    private final FormLovCatalogService formLovCatalogService;

    public FunctionServiceImpl(FunctionRepository functionRepository,
                               FormLovCatalogService formLovCatalogService) {
        this.functionRepository = functionRepository;
        this.formLovCatalogService = formLovCatalogService;
    }

    @Override
    public FunctionRepository getRepository() {
        return functionRepository;
    }

    private Function findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.function.idIsNotValid");
        }
        return getRepository().findActiveById(id)
                .orElseThrow(() -> new BusinessException("error.function.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Function create(FunctionDto functionDto) {
        Assert.notNull(functionDto, "functionDto is null");
        Function function = new Function();
        function.setTitle(functionDto.getTitle());
        function.setDescription(functionDto.getDescription());
        function.setType(formLovCatalogService.findByIdOrElseThrow(functionDto.getTypeId()));
        function.setFromVersion(functionDto.getFromVersion());
        FormLovCatalog usageStatus = functionDto.getUsageStatusId() == null ? null :
                formLovCatalogService.findByIdOrElseThrow(functionDto.getUsageStatusId());
        function.setUsageStatus(usageStatus);
        function.setStatus(Status.ACTIVE.name());
        return save(function);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Function edit(FunctionDto functionDto) {
        Assert.notNull(functionDto, "functionDto is null");
        Function function = findByIdOrElseThrow(functionDto.getId());
        function.setTitle(functionDto.getTitle());
        function.setDescription(functionDto.getDescription());
        function.setType(formLovCatalogService.findByIdOrElseThrow(functionDto.getTypeId()));
        FormLovCatalog usageStatus = functionDto.getUsageStatusId() == null ? null :
                formLovCatalogService.findByIdOrElseThrow(functionDto.getUsageStatusId());
        function.setUsageStatus(usageStatus);
        return save(function);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        Function function = findByIdOrElseThrow(id);
        function.setStatus(Status.PASSIVE.name());
        save(function);
    }

    @Override
    @Transactional(readOnly = true)
    public FunctionDto mapToDto(Function function) {
        Assert.notNull(function, "function is null");
        function = findByIdOrElseThrow(function.getId());
        FunctionDto functionDto = new FunctionDto();
        functionDto.setId(function.getId());
        functionDto.setTitle(function.getTitle());
        functionDto.setDescription(function.getDescription());
        functionDto.setType(formLovCatalogService.mapToDto(function.getType()));
        functionDto.setFromVersion(function.getFromVersion());
        if (function.getUsageStatus() != null) {
            functionDto.setUsageStatus(formLovCatalogService.mapToDto(function.getUsageStatus()));
        }
        return functionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Function> findByIdAndFormVersionId(Long id, Long formVersionId) {
        return getRepository().findByIdAndFormVersionId(id, formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Function> list(Long formVersionId, Pageable pageable) {
        return getRepository().list(formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Function> findByFormVersionId(Long formVersionId) {
        return getRepository().findByFormVersionId(formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

}
