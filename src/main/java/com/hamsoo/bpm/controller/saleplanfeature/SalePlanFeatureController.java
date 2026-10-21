package com.hamsoo.bpm.controller.saleplanfeature;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.saleplanfeature.SalePlanFeature;
import com.hamsoo.bpm.service.api.saleplanfeature.SalePlanFeatureService;
import com.hamsoo.bpm.validator.saleplanfeature.SalePlanFeatureValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SalePlanFeatureController {

    private SalePlanFeatureService salePlanFeatureService;
    private SalePlanFeatureValidator salePlanFeatureValidator;

    public SalePlanFeatureController(SalePlanFeatureService salePlanFeatureService,
                                     SalePlanFeatureValidator salePlanFeatureValidator) {
        this.salePlanFeatureService = salePlanFeatureService;
        this.salePlanFeatureValidator = salePlanFeatureValidator;
    }
    @InitBinder("salePlanFeatureDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(salePlanFeatureValidator);
    }

    @GetMapping("/sale-plan-feature-feature/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        SalePlanFeature salePlanFeature = salePlanFeatureService.findByIdOrElseThrow(id);
        SalePlanFeatureDto salePlanFeatureDto = salePlanFeatureService.mapToDto(salePlanFeature);
        return new ResponseEntity<>(salePlanFeatureDto, HttpStatus.OK);
    }

    @PostMapping("/sale-plan-feature")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> create(@Valid @RequestBody SalePlanFeatureDto salePlanFeatureDto) {
        SalePlanFeature salePlanFeature = salePlanFeatureService.create(salePlanFeatureDto);
        salePlanFeatureDto = salePlanFeatureService.mapToDto(salePlanFeature);
        return new ResponseEntity<>(salePlanFeatureDto, HttpStatus.OK);
    }

    @PutMapping("/sale-plan-feature")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> edit(@Valid @RequestBody SalePlanFeatureDto salePlanFeatureDto) {
        SalePlanFeature salePlanFeature = salePlanFeatureService.edit(salePlanFeatureDto);
        salePlanFeatureDto = salePlanFeatureService.mapToDto(salePlanFeature);
        return new ResponseEntity<>(salePlanFeatureDto, HttpStatus.OK);
    }


    @DeleteMapping("/sale-plan-feature/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        salePlanFeatureService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/sale-plan-feature/page")
    public ResponseEntity<?> list(@RequestBody SalePlanFeatureDto salePlanFeatureDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = salePlanFeatureService.list(salePlanFeatureDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/sale-plan-feature/list")
    public ResponseEntity<?> list(@RequestBody SalePlanFeatureDto salePlanFeatureDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = salePlanFeatureService.list(salePlanFeatureDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
