package com.hamsoo.bpm.repository.jpa.chart;

import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ChartRepository extends HamsooRepository<Chart, Long> {

    @Query(value = "select chart from Chart chart inner join chart.organ o " +
            " where chart.status='ACTIVE' and" +
            " o.status='ACTIVE' " +
            " and chart.organ.id=:organId" +
            " and chart.code= :code")
    Optional<Chart> findByByCodeAndOrganId(@Param("code") String code, @Param("organId") Long organId);

    @Query(value = "select chart from Chart chart inner join chart.organ o " +
            " where chart.status='ACTIVE' and" +
            " o.status='ACTIVE' " +
            " and chart.organ.id=:organId" +
            " and chart.id= :id")
    Optional<Chart> findByByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

}
