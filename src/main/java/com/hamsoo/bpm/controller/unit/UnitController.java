package com.hamsoo.bpm.controller.unit;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.validator.unit.UnitValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UnitController {

    private final UnitService unitService;
    private final UnitValidator unitValidator;

    public UnitController(UnitService unitService, UnitValidator unitValidator) {
        this.unitService = unitService;
        this.unitValidator = unitValidator;
    }

    @InitBinder("unitDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(unitValidator);
    }

    @GetMapping("/units/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_UNIT_MNG)")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Unit unit = unitService.findByIdOrElseThrow(id);
        UnitDto unitDto = unitService.mapToDto(unit);
        return new ResponseEntity<>(unitDto, HttpStatus.OK);
    }

    @PostMapping("/units")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> create(@Valid @RequestBody UnitDto unitDto) {
        Unit unit = unitService.create(unitDto);
        unitDto = unitService.mapToDto(unit);
        return new ResponseEntity<>(unitDto, HttpStatus.OK);
    }

    @PutMapping("/units")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> edit(@Valid @RequestBody UnitDto unitDto) {
        Unit unit = unitService.edit(unitDto);
        unitDto = unitService.mapToDto(unit);
        return new ResponseEntity<>(unitDto, HttpStatus.OK);
    }


    @DeleteMapping("/units/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        unitService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/units/page")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_UNIT_MNG)")
    public ResponseEntity<?> page(@RequestBody UnitDto unitDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = unitService.list(unitDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/units/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_UNIT_MNG)")
    public ResponseEntity<?> list(@RequestBody UnitDto unitDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = unitService.list(unitDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
