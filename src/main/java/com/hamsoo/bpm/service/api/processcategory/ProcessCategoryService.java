package com.hamsoo.bpm.service.api.processcategory;

import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory;
import com.hamsoo.bpm.repository.jpa.processcategory.ProcessCategoryRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;


public interface ProcessCategoryService extends BaseService<ProcessCategory, Long, ProcessCategoryRepository> {

    ProcessCategory findByIdOrElseThrow(Long id);

    ProcessCategory create(ProcessCategoryDto processCategoryDto);

    ProcessCategory edit(ProcessCategoryDto processCategoryDto);

    void passive(Long id);

    ProcessCategoryDto mapToDto(ProcessCategory processCategory);

    SearchDto list(ProcessCategoryDto processCategoryDto, int page, int size, String orderBy, Sort.Direction direction);


    SearchDto listHasNoEMModelElement(ProcessCategoryDto processCategoryDto, Long modelVersionId, int page, int size, String orderBy, Sort.Direction direction);

    Optional<ProcessCategory> findActiveByOrganIdAndName(Long organId, String name);

}
