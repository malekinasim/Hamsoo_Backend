package com.hamsoo.bpm.repository.jpa.identity;

import com.hamsoo.bpm.model.entity.identity.IdentityKpi;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IdentityKpiRepository extends HamsooRepository<IdentityKpi, Long> {

    @Query("select ik from IdentityKpi ik inner join ik.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where ik.id = :id and o.id = :organId and ik.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    Optional<IdentityKpi> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query("select count(ik.id) from IdentityKpi ik inner join ik.kpiCatalog kc inner join ik.modelVersion mv inner join mv.model m " +
            "where kc.id = :kpiCatalogId and ik.status = 'ACTIVE' and kc.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Long countByKpiCatalogId(@Param("kpiCatalogId") Long kpiCatalogId);

    @Query("select ik from IdentityKpi ik inner join ik.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where o.id = :organId and mv.id = :modelVersionId and ik.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    List<IdentityKpi> findActiveByModelVersionId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId);
}
