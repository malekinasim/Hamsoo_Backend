package com.hamsoo.bpm.service.api.kpicatalog;

import com.hamsoo.bpm.model.dto.kpicatalog.KpiCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog;
import com.hamsoo.bpm.repository.jpa.kpicatalog.KpiCatalogRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface KpiCatalogService extends BaseService<KpiCatalog, Long, KpiCatalogRepository> {

    KpiCatalog findByIdOrElseThrow(Long id);

    KpiCatalogDto mapToDto(KpiCatalog kpiCatalog);

    KpiCatalog create(KpiCatalogDto kpiCatalogDto);

    KpiCatalog edit(KpiCatalogDto kpiCatalogDto);

    void passive(Long id);

    SearchDto list(KpiCatalogDto kpiCatalogDto, int page, int size, String orderBy, Sort.Direction direction);

    List<KpiCatalog> findActiveByKpiUnitId(Long kpiUnitId);

}
