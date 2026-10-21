package com.hamsoo.bpm.repository.jpa.kpicatalog;

import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface KpiCatalogRepository extends HamsooRepository<KpiCatalog, Long> {

    Optional<KpiCatalog> findByIdAndStatusAndOrganId(Long id, String status, Long organId);

    @Query(value = "select kc from KpiCatalog kc inner join kc.kpiUnit ku " +
            "where kc.status = 'ACTIVE' and ku.id= :kpiUnitId  ")
    List<KpiCatalog> findActiveByKpiUnitId(@Param("kpiUnitId") Long kpiUnitId);

}
