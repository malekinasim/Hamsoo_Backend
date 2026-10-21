package com.hamsoo.bpm.controller.processcategory;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryModelElementDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryModelElementService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProcessCategoryModelElementController {

    private final ProcessCategoryModelElementService processCategoryModelElementService;

    public ProcessCategoryModelElementController(ProcessCategoryModelElementService processCategoryModelElementService) {
        this.processCategoryModelElementService = processCategoryModelElementService;
    }

    @GetMapping("/process-category-model-elements/{id}")
    @PreAuthorize("hasAnyResourceByProcessCategoryModelElementId(#id,@resourceItemService.EM_MODEL_LANE_CATEGORY_MNG)")
    public ResponseEntity<?> getProcessCategoryModelElementById(@PathVariable("id") Long id) {
        ProcessCategoryModelElement processCategoryModelElement = processCategoryModelElementService.findByIdOrElseThrow(id);
        ProcessCategoryModelElementDto processCategoryModelElementDto = processCategoryModelElementService.mapToDto(processCategoryModelElement);
        return new ResponseEntity<>(processCategoryModelElementDto, HttpStatus.OK);
    }

    @PostMapping("/process-category-model-elements")
    @PreAuthorize("hasAnyResourceByModelVersionId(#processCategoryModelElementDto.modelVersionId,@resourceItemService.EM_MODEL_LANE_CATEGORY_MNG)")
    public ResponseEntity<?> createProcessCategoryModelElement(@Valid @RequestBody ProcessCategoryModelElementDto processCategoryModelElementDto) {
        ProcessCategoryModelElement processCategoryModelElement = processCategoryModelElementService.create(processCategoryModelElementDto);
        processCategoryModelElementDto = processCategoryModelElementService.mapToDto(processCategoryModelElement);
        return new ResponseEntity<>(processCategoryModelElementDto, HttpStatus.OK);
    }

    @PostMapping("/create-or-edit-process-category-model-elements-by-mv-id-and-element-id")
    @PreAuthorize("hasAnyResourceByModelVersionId(#processCategoryModelElementDto.modelVersionId,@resourceItemService.EM_MODEL_LANE_CATEGORY_MNG)")
    public ResponseEntity<?> createOrEditProcessCategoryModelElementByModelVersionIdAndElementId(@Valid @RequestBody ProcessCategoryModelElementDto processCategoryModelElementDto) {
        ProcessCategoryModelElement processCategoryModelElement =
                processCategoryModelElementService.createOrEditByModelVersionIdAndElementId(processCategoryModelElementDto);
        processCategoryModelElementDto = processCategoryModelElementService.mapToDto(processCategoryModelElement);
        return new ResponseEntity<>(processCategoryModelElementDto, HttpStatus.OK);
    }

    @DeleteMapping("/process-category-model-elements/{id}")
    @PreAuthorize("hasAnyResourceByProcessCategoryModelElementId(#id,@resourceItemService.EM_MODEL_LANE_CATEGORY_MNG)")
    public ResponseEntity<?> deleteProcessCategoryModelElement(@PathVariable Long id) {
        processCategoryModelElementService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/process-category-model-elements/{modelVersionId:^[1-9]+[0-9]*$}/{elementId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.EM_MODEL_LANE_CATEGORY_MNG)")
    public ResponseEntity<?> deleteProcessCategoryModelElementByModelVersionIdAndElementId(@PathVariable Long modelVersionId,
                                                                                           @PathVariable String elementId) {
        processCategoryModelElementService.delete(modelVersionId, elementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/process-category-model-elements/list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.EM_MODEL_LANE_CATEGORY_MNG)")
    public ResponseEntity<?> getProcessCategoryModelElementList(@PathVariable Long modelVersionId,
                                                                @RequestBody ProcessCategoryModelElementDto processCategoryModelElementDto,
                                                                @RequestParam(required = false) String orderBy,
                                                                @RequestParam(required = false) Sort.Direction direction) {
        processCategoryModelElementDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = processCategoryModelElementService.list(processCategoryModelElementDto, 0, PageUtils.MAX_PAGE_SIZE,
                orderBy, direction, processCategoryModelElementService::mapToDto);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }

}
