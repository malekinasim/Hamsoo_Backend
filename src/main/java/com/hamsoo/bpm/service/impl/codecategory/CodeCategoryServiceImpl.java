package com.hamsoo.bpm.service.impl.codecategory;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.repository.jpa.codecategory.CodeCategoryRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeCategoryServiceImpl implements CodeCategoryService {

    private final CodeCategoryRepository codeCategoryRepository;

    public CodeCategoryServiceImpl(CodeCategoryRepository codeCategoryRepository) {
        this.codeCategoryRepository = codeCategoryRepository;
    }

    @Override
    public CodeCategoryRepository getRepository() {
        return codeCategoryRepository;
    }

    @Override
    public CodeCategory findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.codeCategory.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.codeCategory.idIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public CodeCategoryDto mapToDto(CodeCategory codeCategory) {
        Assert.notNull(codeCategory, "codeCategory is null");
        Assert.notNull(codeCategory.getId(), "codeCategory's id is null");
        codeCategory = findByIdOrElseThrow(codeCategory.getId());
        CodeCategoryDto codeCategoryDto = new CodeCategoryDto();
        codeCategoryDto.setId(codeCategory.getId());
        codeCategoryDto.setCategory(codeCategory.getCategory());
        codeCategoryDto.setCode(codeCategory.getCode());
        codeCategoryDto.setFaName(codeCategory.getFaName());
        codeCategoryDto.setEnName(codeCategory.getEnName());
        return codeCategoryDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeCategoryDto> mapToDto(List<CodeCategory> codeCategoryList) {
        if (CollectionUtils.isEmpty(codeCategoryList)) {
            return new ArrayList<>();
        }
        return codeCategoryList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CodeCategory findByCategoryAndCode(String category, String code) {
        return getRepository().findByCategoryAndCode(category, code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeCategory> findByCategory(String category) {
        return getRepository().findByCategory(category, PageUtils.getInstance());
    }

}
