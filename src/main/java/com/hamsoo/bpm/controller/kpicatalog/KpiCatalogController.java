package com.hamsoo.bpm.controller.kpicatalog;

import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.dto.kpicatalog.KpiCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.identity.IdentityKpi;
import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.kpicatalog.KpiCatalogService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")

public class KpiCatalogController {
    private final KpiCatalogService kpiCatalogService;
    private final CodeCategoryService codeCategoryService;

    public KpiCatalogController(KpiCatalogService kpiCatalogService, CodeCategoryService codeCategoryService) {
        this.kpiCatalogService = kpiCatalogService;
        this.codeCategoryService = codeCategoryService;
    }

    @GetMapping("/kpi-catalogs/{id}")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.KPI_MNG)")
    public ResponseEntity<?> findKpiCatalogById(@PathVariable("id") Long id) {
        KpiCatalog kpiCatalog = kpiCatalogService.findByIdOrElseThrow(id);
        KpiCatalogDto kpiCatalogDto = kpiCatalogService.mapToDto(kpiCatalog);
        return new ResponseEntity<>(kpiCatalogDto, HttpStatus.OK);
    }

    @PostMapping("/kpi-catalogs")
    @PreAuthorize(value = "hasAuthority(@resourceItemService.KPI_MNG)")
    public ResponseEntity<?> createKpiCatalog(@Valid @RequestBody KpiCatalogDto kpiCatalogDto) {
        KpiCatalog kpiCatalog = kpiCatalogService.create(kpiCatalogDto);
        kpiCatalogDto = kpiCatalogService.mapToDto(kpiCatalog);
        return new ResponseEntity<>(kpiCatalogDto, HttpStatus.OK);
    }

    @PutMapping("/kpi-catalogs")
    @PreAuthorize(value = "hasAuthority(@resourceItemService.KPI_MNG)")
    public ResponseEntity<?> editKpiCatalog(@Valid @RequestBody KpiCatalogDto kpiCatalogDto) {
        KpiCatalog kpiCatalog = kpiCatalogService.edit(kpiCatalogDto);
        kpiCatalogDto = kpiCatalogService.mapToDto(kpiCatalog);
        return new ResponseEntity<>(kpiCatalogDto, HttpStatus.OK);
    }

    @DeleteMapping("/kpi-catalogs/{id}")
    @PreAuthorize(value = "hasAuthority(@resourceItemService.KPI_MNG)")
    public ResponseEntity<?> passiveKpiCatalog(@PathVariable Long id) {
        kpiCatalogService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/kpi-catalogs/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.KPI_MNG)")
    public ResponseEntity<?> getKpiCatalogList(@RequestBody KpiCatalogDto kpiCatalogDto, @RequestParam int page, @RequestParam int size,
                                               @RequestParam(required = false) String orderBy,
                                               @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = kpiCatalogService.list(kpiCatalogDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }


    @GetMapping("/kpi-catalogs/kpi-trend-type-list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.KPI_TREND_TYPE_LIST)")
    public ResponseEntity<?> getTrendTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(IdentityKpi.TrendType.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }
    @GetMapping("/kpi-catalogs/kpi-monitor-type-list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.KPI_MONITOR_TYPE_LIST)")
    public ResponseEntity<?> getMonitorTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(IdentityKpi.MonitorType.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }

    @GetMapping("/kpi-catalogs/kpi-evl-duration-type-list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.KPI_DURATION_TYPE_LIST)")
    public ResponseEntity<?> geEvlDurationTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(IdentityKpi.EvlDurationType.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }
}
