package com.hamsoo.bpm.controller.kpiunit;

import com.hamsoo.bpm.model.dto.kpiunit.KpiUnitDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit;
import com.hamsoo.bpm.service.api.kpiunit.KpiUnitService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize(value = "hasAuthority(@resourceItemService.UNIT_MNG)")
public class KpiUnitController {
    private final KpiUnitService kpiUnitService;

    public KpiUnitController(KpiUnitService kpiUnitService) {
        this.kpiUnitService = kpiUnitService;
    }

    @GetMapping("/kpi-units/{id}")
    public ResponseEntity<?> findKpiUnitById(@PathVariable("id") Long id) {
        KpiUnit kpiUnit = kpiUnitService.findByIdOrElseThrow(id);
        KpiUnitDto kpiUnitDto = kpiUnitService.mapToDto(kpiUnit);
        return new ResponseEntity<>(kpiUnitDto, HttpStatus.OK);
    }

    @PostMapping("/kpi-units")
    public ResponseEntity<?> createKpiUnit(@Valid @RequestBody KpiUnitDto kpiUnitDto) {
        KpiUnit kpiUnit = kpiUnitService.create(kpiUnitDto);
        kpiUnitDto = kpiUnitService.mapToDto(kpiUnit);
        return new ResponseEntity<>(kpiUnitDto, HttpStatus.OK);
    }

    @PutMapping("/kpi-units")
    public ResponseEntity<?> editKpiUnit(@Valid @RequestBody KpiUnitDto kpiUnitDto) {
        KpiUnit kpiUnit = kpiUnitService.edit(kpiUnitDto);
        kpiUnitDto = kpiUnitService.mapToDto(kpiUnit);
        return new ResponseEntity<>(kpiUnitDto, HttpStatus.OK);
    }

    @DeleteMapping("/kpi-units/{id}")
    public ResponseEntity<?> passiveKpiUnit(@PathVariable Long id) {
        kpiUnitService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/kpi-units/list")
    public ResponseEntity<?> getKpiUnitList(@RequestBody KpiUnitDto kpiUnitDto, @RequestParam int page, @RequestParam int size,
                                            @RequestParam(required = false) String orderBy,
                                            @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = kpiUnitService.list(kpiUnitDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
