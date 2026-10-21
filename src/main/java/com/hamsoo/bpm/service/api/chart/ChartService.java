package com.hamsoo.bpm.service.api.chart;

import com.hamsoo.bpm.model.dto.chart.ChartDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.repository.jpa.chart.ChartRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface ChartService extends BaseService<Chart, Long, ChartRepository> {

    Chart findByIdOrElseThrow(Long id);

    Chart create(ChartDto chartDto);

    Chart edit(ChartDto chartDto);

    void passive(Long id);

    Optional<Chart> findByCode(String code);

    ChartDto mapToDto(Chart chart);

    SearchDto list(ChartDto chartDto, int page, int size, String orderBy, Sort.Direction direction);

}
