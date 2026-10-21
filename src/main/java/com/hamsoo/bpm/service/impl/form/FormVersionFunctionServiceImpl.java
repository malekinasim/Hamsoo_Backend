package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.form.FunctionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.model.entity.form.Function;
import com.hamsoo.bpm.service.api.form.FormVersionFunctionService;
import com.hamsoo.bpm.service.api.form.FormVersionService;
import com.hamsoo.bpm.service.api.form.FunctionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class FormVersionFunctionServiceImpl implements FormVersionFunctionService {

    private final FormVersionService formVersionService;
    private final FunctionService functionService;

    public FormVersionFunctionServiceImpl(FormVersionService formVersionService,
                                          FunctionService functionService) {
        this.formVersionService = formVersionService;
        this.functionService = functionService;
    }

    @Override
    @Transactional(readOnly = true)
    public Function findByIdOrElseThrow(Long functionId, Long formVersionId) {
        if (functionId == null || formVersionId == null) {
            throw new BusinessException("error.formVersionFunction.functionIdNotFoundForThisFormVersion");
        }
        return functionService.findByIdAndFormVersionId(functionId, formVersionId)
                .orElseThrow(() -> new BusinessException("error.formVersionFunction.functionIdNotFoundForThisFormVersion"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Function addFunction(FunctionDto functionDto) {
        Assert.notNull(functionDto, "functionDto is null");
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(functionDto.getFormVersionId());
        formVersionService.checkVersion(formVersion);
        functionDto.setFromVersion(formVersion.getFormVersion());
        Function function = functionService.create(functionDto);
        formVersion.addFunction(function);
        formVersionService.save(formVersion);
        return function;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Function editFunction(FunctionDto functionDto) {
        Assert.notNull(functionDto, "functionDto is null");
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(functionDto.getFormVersionId());
        formVersionService.checkVersion(formVersion);
        Function function = findByIdOrElseThrow(functionDto.getId(), formVersion.getId());
        Long formVersionCount = formVersionService.countByFunctionId(functionDto.getId());
        boolean editable = formVersionCount.equals(1L) && function.getFromVersion().equals(formVersion.getFormVersion());
        if (!editable) {
            throw new BusinessException("error.formVersionFunction.functionNotEditable");
        }
        return functionService.edit(functionDto);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveFunction(Long functionId, Long formVersionId) {
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(formVersionId);
        formVersionService.checkVersion(formVersion);
        if (CollectionUtils.isEmpty(formVersion.getFunctions())) {
            throw new BusinessException("error.formVersionFunction.functionIdNotFoundForThisFormVersion");
        }
        Function function = findByIdOrElseThrow(functionId, formVersion.getId());
        if (!formVersion.getFunctions().contains(function)) {
            throw new BusinessException("error.formVersionFunction.functionIdNotFoundForThisFormVersion");
        }
        formVersion.getFunctions().remove(function);
        formVersionService.save(formVersion);
        Long formVersionCount = formVersionService.countByFunctionId(function.getId());
        if (formVersionCount.equals(0L)) {
            functionService.passive(function.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(FunctionDto functionDto, int page, int size, String orderBy, Sort.Direction direction,
                          java.util.function.Function<Function, FunctionDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Page<Function> functionPage = functionService.list(functionDto.getFormVersionId(), pageable);
        List<FunctionDto> content = functionPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(functionPage.getTotalElements())
                .totalPages(functionPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    /**
     * copy relations between formVersion and function entity
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyFormVersionFunctionsIfExist(Long formVersionIdFrom, Long formVersionIdTo) {
        List<Function> functionList = functionService.findByFormVersionId(formVersionIdFrom);
        if (CollectionUtils.isEmpty(functionList)) {
            return;
        }
        FormVersion formVersionTo = formVersionService.findByIdOrElseThrow(formVersionIdTo);
        functionList.forEach(formVersionTo::addFunction);
        formVersionService.save(formVersionTo);
    }

}
