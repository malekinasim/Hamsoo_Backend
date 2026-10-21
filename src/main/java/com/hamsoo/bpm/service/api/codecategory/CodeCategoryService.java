package com.hamsoo.bpm.service.api.codecategory;

import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.repository.jpa.codecategory.CodeCategoryRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface CodeCategoryService extends BaseService<CodeCategory, Long, CodeCategoryRepository> {

    CodeCategory findByIdOrElseThrow(Long id);

    CodeCategoryDto mapToDto(CodeCategory codeCategory);

    List<CodeCategoryDto> mapToDto(List<CodeCategory> codeCategoryList);

    CodeCategory findByCategoryAndCode(String category, String code);

    List<CodeCategory> findByCategory(String category);
}
