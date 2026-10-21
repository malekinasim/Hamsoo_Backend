package com.hamsoo.bpm.repository.jpa.chart;

import com.hamsoo.bpm.model.entity.chart.ChartTree;
import com.hamsoo.bpm.repository.jpa.HamsooReadOnlyRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CharTreeRepository extends HamsooReadOnlyRepository<ChartTree, String> {

    @Query(value = "select chartTree from ChartTree  chartTree where " +
            " chartTree.chartId=:chartId and" +
            " chartTree.organId=:organId and" +
            " ( " +
            "  upper(chartTree.code) like concat('%', upper(:searchWord) ,'%') or " +
            "  upper(chartTree.name) like concat('%', upper(:searchWord) ,'%'))")
    List<ChartTree> getChartTree(Long chartId, String searchWord, Long organId);

    @Query(value = "select chartTree from ChartTree  chartTree where " +
            " chartTree.chartId=:chartId and " +
            " chartTree.organId=:organId and "+
            " chartTree.entityType = 'UNIT' "
    )
    List<ChartTree> getChartTree(Long chartId, Long organId);
}
