package com.hamsoo.bpm.controller.chart;

import com.hamsoo.bpm.model.entity.chart.ChartTree;
import com.hamsoo.bpm.service.api.chart.ChartTreeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class ChartTreeController {
    private final ChartTreeService chartTreeService;

    public ChartTreeController(ChartTreeService chartTreeService) {
        this.chartTreeService = chartTreeService;
    }

    @PostMapping(value = "/chart-tree/list")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_TREE)")
    public ResponseEntity<?> list(@RequestBody ChartTree chartTree) {
        List<ChartTree> chartTrees = chartTreeService.getChartTree(chartTree);
        return new ResponseEntity<>(chartTrees, HttpStatus.OK);
    }
}
