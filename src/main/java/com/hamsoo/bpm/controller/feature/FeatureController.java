package com.hamsoo.bpm.controller.feature;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.feature.FeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.feature.Feature;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.feature.FeatureService;
import com.hamsoo.bpm.validator.feature.FeatureValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1")
public class FeatureController {
    private FeatureService featureService;
    private CodeCategoryService codeCategoryService;
    private FeatureValidator featureValidator;

    public FeatureController(FeatureService featureService, CodeCategoryService codeCategoryService, FeatureValidator featureValidator) {
        this.featureService = featureService;
        this.codeCategoryService = codeCategoryService;
        this.featureValidator = featureValidator;
    }

    @InitBinder("featureDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(featureValidator);
    }

    @GetMapping("/feature/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Feature feature = featureService.findByIdOrElseThrow(id);
        FeatureDto featureDto = featureService.mapToDto(feature);
        return new ResponseEntity<>(featureDto, HttpStatus.OK);
    }

    @PostMapping("/feature")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> create(@Valid @RequestBody FeatureDto featureDto) {
        Feature feature = featureService.create(featureDto);
        featureDto = featureService.mapToDto(feature);
        return new ResponseEntity<>(featureDto, HttpStatus.OK);
    }

    @PutMapping("/feature")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> edit(@Valid @RequestBody FeatureDto featureDto) {
        Feature feature = featureService.edit(featureDto);
        featureDto = featureService.mapToDto(feature);
        return new ResponseEntity<>(featureDto, HttpStatus.OK);
    }


    @DeleteMapping("/feature/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        featureService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/feature/page")
    public ResponseEntity<?> list(@RequestBody FeatureDto featureDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = featureService.list(featureDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/feature/list")
    public ResponseEntity<?> list(@RequestBody FeatureDto featureDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = featureService.list(featureDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }

}
