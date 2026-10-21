package com.hamsoo.bpm.repository.jpa.organplan;

import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
public interface OrganPlanRepository extends HamsooRepository<OrganPlan, Long> {
    Optional<OrganPlan> findByIdAndOrgan_IdAndValidToAfter(Long Id, Long organId, LocalDateTime time);

    Optional<OrganPlan> findByOrgan_IdAndValidToAfter( Long organId, LocalDateTime time);

//    @Query(value = "select op from OrganPlan op inner join op.organ o " +
//            "where o.id=:organId and  " +
//            "op.validTo  " +
//            "in (select max(op.validTo) from OrganPlan op  group by op.organ.id) " +
//            " order by op.id,op.validTo desc ")
    Optional<OrganPlan> findTopByOrgan_IdOrderByValidToDesc(Long organId);
}
