package com.hamsoo.bpm.service.api.chart;

import com.hamsoo.bpm.model.entity.chart.ChartTree;
import com.hamsoo.bpm.repository.jpa.chart.CharTreeRepository;
import com.hamsoo.bpm.service.api.base.ReadOnlyBaseService;

import java.util.List;

public interface ChartTreeService extends ReadOnlyBaseService<ChartTree, String, CharTreeRepository> {
    List<ChartTree> getChartTree(ChartTree chartTree);

}
