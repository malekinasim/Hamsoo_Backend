package com.hamsoo.bpm.repository.jpa.kpiunit;

import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface KpiUnitRepository extends HamsooRepository<KpiUnit, Long> {

    Optional<KpiUnit> findByIdAndStatus(Long id, String status);

    Optional<KpiUnit> findByUnitCodeAndStatus(String unitCode, String status);

}
