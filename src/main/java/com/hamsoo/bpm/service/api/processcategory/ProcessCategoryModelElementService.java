package com.hamsoo.bpm.service.api.processcategory;

import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryModelElementDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement;
import com.hamsoo.bpm.repository.jpa.processcategory.ProcessCategoryModelElementRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public interface ProcessCategoryModelElementService extends BaseService<ProcessCategoryModelElement, Long, ProcessCategoryModelElementRepository> {

    ProcessCategoryModelElement findByIdOrElseThrow(Long id);

    ProcessCategoryModelElement create(ProcessCategoryModelElementDto processCategoryModelElementDto);

    ProcessCategoryModelElement edit(ProcessCategoryModelElementDto processCategoryModelElementDto);

    ProcessCategoryModelElement createOrEditByModelVersionIdAndElementId(ProcessCategoryModelElementDto processCategoryModelElementDto);

    void delete(Long id);

    void delete(Long modelVersionId, String elementId);

    ProcessCategoryModelElementDto mapToDto(ProcessCategoryModelElement processCategoryModelElement);

    SearchDto list(ProcessCategoryModelElementDto processCategoryModelElementDto, int page, int size, String orderBy, Sort.Direction direction,
                   Function<ProcessCategoryModelElement, ProcessCategoryModelElementDto> mapper);

    Optional<ProcessCategoryModelElement> findByModelVersionIdAndProcessCategoryId(Long modelVersionId, Long processCategoryId);

    Optional<ProcessCategoryModelElement> findByModelVersionIdAndElementId(Long modelVersionId, String elementId);

    Long countByProcessCategoryId(Long processCategoryId);

    List<ProcessCategoryModelElement> findByModelVersionId(Long modelVersionId);

    ProcessCategoryModelElement clone(ProcessCategoryModelElement processCategoryModelElement);

    void copyProcessCategoryModelElementsIfExist(Long modelVersionIdFrom, Long modelVersionIdTo);

    List<ProcessCategoryModelElement> findByModelId(Long modelId);

    void deleteByModelId(Long modelId);

}
