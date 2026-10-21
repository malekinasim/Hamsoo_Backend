package com.hamsoo.bpm.controller.chart;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.chart.ChartDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.validator.chart.ChartValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/")
public class ChartController {

    private final ChartService chartService;
    private final ChartValidator chartValidator;

    public ChartController(ChartService chartService, ChartValidator chartValidator) {
        this.chartService = chartService;
        this.chartValidator = chartValidator;
    }

    @InitBinder("chartDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(chartValidator);
    }


    @GetMapping("/chart/{id}")
    @PreAuthorize("hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_MNG)")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Chart chart = chartService.findByIdOrElseThrow(id);
        ChartDto chartDto = chartService.mapToDto(chart);
        return new ResponseEntity<>(chartDto, HttpStatus.OK);
    }

    @PostMapping("/chart")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> create(@Valid @RequestBody ChartDto chartDto) {
        Chart chart = chartService.create(chartDto);
        chartDto = chartService.mapToDto(chart);
        return new ResponseEntity<>(chartDto, HttpStatus.OK);
    }

    @PutMapping("/chart")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> edit(@Valid @RequestBody ChartDto chartDto) {
        Chart chart = chartService.edit(chartDto);
        chartDto = chartService.mapToDto(chart);
        return new ResponseEntity<>(chartDto, HttpStatus.OK);
    }


    @DeleteMapping("/chart/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        chartService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/chart/page")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_MNG)")
    public ResponseEntity<?> list(@RequestBody ChartDto chartDto,
                                  @RequestParam int page,
                                  @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = chartService.list(chartDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/chart/list")
    @PreAuthorize("hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_MNG)")
    public ResponseEntity<?> list(@RequestBody ChartDto chartDto, @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = chartService.list(chartDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
