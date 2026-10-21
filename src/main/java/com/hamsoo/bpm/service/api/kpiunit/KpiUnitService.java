package com.hamsoo.bpm.service.api.kpiunit;

import com.hamsoo.bpm.model.dto.kpiunit.KpiUnitDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit;
import com.hamsoo.bpm.repository.jpa.kpiunit.KpiUnitRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface KpiUnitService extends BaseService<KpiUnit, Long, KpiUnitRepository> {

    KpiUnit findByIdOrElseThrow(Long id);

    KpiUnit create(KpiUnitDto kpiUnitDto);

    KpiUnit edit(KpiUnitDto kpiUnitDto);

    void passive(Long id);

    KpiUnitDto mapToDto(KpiUnit kpiUnit);

    List<KpiUnitDto> mapToDto(List<KpiUnit> kpiUnitList);

    SearchDto list(KpiUnitDto kpiUnitDto, int page, int size, String orderBy, Sort.Direction direction);

}
