package com.hamsoo.bpm.controller.organplan;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.organplan.OrganPlanDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.validator.organplan.OrganPlanValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class OrganPlanController {

    private OrganPlanService organPlanService;
    private OrganPlanValidator organPlanValidator;

    public OrganPlanController(OrganPlanService organPlanService, OrganPlanValidator organPlanValidator) {
        this.organPlanService = organPlanService;
        this.organPlanValidator = organPlanValidator;
    }


    @InitBinder("organPlanDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(organPlanValidator);
    }

    @GetMapping("/organ-plan/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        OrganPlan organPlan = organPlanService.findByIdOrElseThrow(id);
        OrganPlanDto organPlanDto = organPlanService.mapToDto(organPlan);
        return new ResponseEntity<>(organPlanDto, HttpStatus.OK);
    }

    @GetMapping("/organ-plan/last-organ-plan/{organId}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> getLastPlanOrgan(@PathVariable("organId") Long organId) {
        Optional<OrganPlan> organPlan = organPlanService.getLastOrganSalePlan(organId);
        if(organPlan.isPresent()) {
            OrganPlanDto organPlanDto = organPlanService.mapToDto(organPlan.get());
            return new ResponseEntity<>(organPlanDto, HttpStatus.OK);
        }else{
            return new ResponseEntity<>( HttpStatus.EXPECTATION_FAILED);
        }
    }


    @PostMapping("/organ-plan")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> create(@Valid @RequestBody OrganPlanDto organPlanDto) {
        OrganPlan organPlan = organPlanService.create(organPlanDto);
        organPlanDto = organPlanService.mapToDto(organPlan);
        return new ResponseEntity<>(organPlanDto, HttpStatus.OK);
    }

    @PutMapping("/organ-plan")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> edit(@Valid @RequestBody OrganPlanDto organPlanDto) {
        OrganPlan organPlan = organPlanService.edit(organPlanDto);
            organPlanDto = organPlanService.mapToDto(organPlan);
            return new ResponseEntity<>(organPlanDto, HttpStatus.OK);


    }

    @PutMapping("/organ-plan/expire/{organId}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> expireOrganPlan(@PathVariable Long organId) {
        OrganPlan organPlan =  organPlanService.expiredCurOrganPlan(organId);
        return getResponseEntityOfExpirOrgan(organPlan);
    }
    @PutMapping("/organ-plan/expire-cur-organ-plan")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> expireOrganPlan() {
        OrganPlan organPlan =  organPlanService.expiredCurOrganPlan(SecurityUtils.getCurrentOrganIdOrElseThrow());
        return getResponseEntityOfExpirOrgan(organPlan);
    }

    private ResponseEntity<?> getResponseEntityOfExpirOrgan(OrganPlan organPlan) {
        if(organPlan==null) throw new BusinessException("error.organPlan.organDontHaveSalePlan");
        else if(organPlan.getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.EXPIRED.name())) {
            OrganPlanDto organPlanDto = organPlanService.mapToDto(organPlan);
            return new ResponseEntity<>(organPlanDto, HttpStatus.OK);
        }else{
            throw new BusinessException("error.organPlan.doseNotExpire");
        }
    }

    @DeleteMapping("/organ-plan/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        organPlanService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/organ-plan/page")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> list(@RequestBody OrganPlanDto organPlanDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = organPlanService.list(organPlanDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/organ-plan/list")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.FINANCIAL_PANEL)")
    public ResponseEntity<?> list(@RequestBody OrganPlanDto organPlanDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = organPlanService.list(organPlanDto,0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
