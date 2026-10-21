package com.hamsoo.bpm.repository.jpa.saleplan;

import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface SalePlanRepository extends HamsooRepository<SalePlan,Long> {
    Optional<SalePlan> findByIdAndStatus(Long id, String status);

    Optional<SalePlan> findByCodeAndStatus(String code, String status);
    Optional<SalePlan> findBySaleServiceTypeAndStatus(String serviceType, String status);
}
