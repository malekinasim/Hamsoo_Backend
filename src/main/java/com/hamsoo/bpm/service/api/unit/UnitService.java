package com.hamsoo.bpm.service.api.unit;

import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.repository.jpa.unit.UnitRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface UnitService extends BaseService<Unit, Long, UnitRepository> {

    Unit findByIdOrElseThrow(Long id);


    Unit create(UnitDto unitDto);

    Unit edit(UnitDto unitDto);

    void passive(Long id);

    UnitDto mapToDto(Unit unit);

    SearchDto list(UnitDto unitDto, int page, int size, String orderBy, Sort.Direction direction);

    Optional<Unit> findByCodeAndChartId(String code, Long chartId);

    List<Unit> findAllByChartId(Long id);
}
