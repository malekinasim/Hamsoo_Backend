package com.hamsoo.bpm.controller.saleplan;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.saleplan.SalePlanDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.service.api.saleplan.SalePlanService;
import com.hamsoo.bpm.validator.saleplan.SalePlanValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("api/v1")
public class SalePlanController {

    private SalePlanService salePlanService;
    private SalePlanValidator salePlanValidator;

    public SalePlanController(SalePlanService salePlanService, SalePlanValidator salePlanValidator) {
        this.salePlanService = salePlanService;
        this.salePlanValidator = salePlanValidator;
    }

    @InitBinder("salePlanDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(salePlanValidator);
    }

    @GetMapping("/sale-plan/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") java.lang.Long id) {
        SalePlan salePlan = salePlanService.findByIdOrElseThrow(id);
        SalePlanDto salePlanDto = salePlanService.mapToDto(salePlan);
        return new ResponseEntity<>(salePlanDto, HttpStatus.OK);
    }

    @PostMapping("/sale-plan")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> create(@Valid @RequestBody SalePlanDto salePlanDto) {
        SalePlan salePlan = salePlanService.create(salePlanDto);
        salePlanDto = salePlanService.mapToDto(salePlan);
        return new ResponseEntity<>(salePlanDto, HttpStatus.OK);
    }

    @PutMapping("/sale-plan")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> edit(@Valid @RequestBody SalePlanDto salePlanDto) {
        SalePlan salePlan = salePlanService.edit(salePlanDto);
        salePlanDto = salePlanService.mapToDto(salePlan);
        return new ResponseEntity<>(salePlanDto, HttpStatus.OK);
    }


    @DeleteMapping("/sale-plan/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.SALE_PLAN_MNG)")
    public ResponseEntity<?> passive(@PathVariable java.lang.Long id) {
        salePlanService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/sale-plan/page")
    public ResponseEntity<?> list(@RequestBody SalePlanDto salePlanDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = salePlanService.list(salePlanDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/sale-plan/list")
    public ResponseEntity<?> list(@RequestBody SalePlanDto salePlanDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = salePlanService.list(salePlanDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }

}


