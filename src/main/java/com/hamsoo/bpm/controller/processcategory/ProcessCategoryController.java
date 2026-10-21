package com.hamsoo.bpm.controller.processcategory;

import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProcessCategoryController {

    private final ProcessCategoryService processCategoryService;

    public ProcessCategoryController(ProcessCategoryService processCategoryService) {
        this.processCategoryService = processCategoryService;
    }

    @GetMapping("/process-categories/{id}")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.PROCESS_CATEGORY_MNG)")
    public ResponseEntity<?> getProcessCategoryById(@PathVariable("id") Long id) {
        ProcessCategory processCategory = processCategoryService.findByIdOrElseThrow(id);
        ProcessCategoryDto processCategoryDto = processCategoryService.mapToDto(processCategory);
        return new ResponseEntity<>(processCategoryDto, HttpStatus.OK);
    }

    @PostMapping("/process-categories")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.PROCESS_CATEGORY_MNG)")
    public ResponseEntity<?> createProcessCategory(@Valid @RequestBody ProcessCategoryDto processCategoryDto) {
        ProcessCategory processCategory = processCategoryService.create(processCategoryDto);
        processCategoryDto = processCategoryService.mapToDto(processCategory);
        return new ResponseEntity<>(processCategoryDto, HttpStatus.OK);
    }

    @PutMapping("/process-categories")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.PROCESS_CATEGORY_MNG)")
    public ResponseEntity<?> editProcessCategory(@Valid @RequestBody ProcessCategoryDto processCategoryDto) {
        ProcessCategory processCategory = processCategoryService.edit(processCategoryDto);
        processCategoryDto = processCategoryService.mapToDto(processCategory);
        return new ResponseEntity<>(processCategoryDto, HttpStatus.OK);
    }

    @DeleteMapping("/process-categories/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.PROCESS_CATEGORY_MNG)")
    public ResponseEntity<?> passiveProcessCategory(@PathVariable Long id) {
        processCategoryService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/process-categories/list")
    public ResponseEntity<?> getProcessCategoryList(@RequestParam int page, @RequestParam int size,
                                                    @RequestBody ProcessCategoryDto processCategoryDto,
                                                    @RequestParam(required = false) String orderBy,
                                                    @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = processCategoryService.list(processCategoryDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/process-categories/list/has-no-em-model-element/{modelVersionId}")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.PROCESS_CATEGORY_MNG)")
    public ResponseEntity<?> getProcessCategoryHasNoEMModelList(@RequestBody ProcessCategoryDto processCategoryDto,
                                                                @PathVariable Long modelVersionId,
                                                                @RequestParam int page, @RequestParam int size,
                                                                @RequestParam(required = false) String orderBy,
                                                                @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = processCategoryService.listHasNoEMModelElement(processCategoryDto, modelVersionId, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
