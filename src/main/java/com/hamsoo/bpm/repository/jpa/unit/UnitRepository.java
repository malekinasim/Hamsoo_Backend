package com.hamsoo.bpm.repository.jpa.unit;

import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface UnitRepository extends HamsooRepository<Unit, Long> {

    @Query(value = "select u from Unit u inner join u.chart ch inner  join ch.organ o " +
            " where o.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " o.id=:organId and" +
            " u.id=:id ")
    Optional<Unit> findByIdAndOrganIdAndStatus(Long id, Long organId);

    @Query(value = "select u from UnitCodeHistory uc inner join uc.unit u   inner join u.chart ch inner  join ch.organ o " +
            " where o.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " uc.status='ACTIVE' and" +
            " o.id=:organId and" +
            " uc.code=:code and" +
            " ch.id=:chartId ")
    Optional<Unit> findByCodeAndChartIdAndOrganIdAndStatus(String code, Long chartId, Long organId);

    @Query(value = "select u from Unit u " +
            "inner join u.parentUnit pu   " +
            "inner join u.chart ch " +
            "inner  join ch.organ o " +
            " where o.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " pu.status='ACTIVE' and" +
            " o.id=:organId and" +
            " pu.id=:parenUnitId and" +
            " ch.id=:chartId ")
    List<Unit> findAllByParentUnitIdAndChartId(Long parenUnitId, Long chartId, Long organId);


    @Query(value = "select u from Unit u " +
            "inner join u.parentUnit pu   " +
            "inner join u.chart ch " +
            "inner  join ch.organ o " +
            " where o.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " pu.status='ACTIVE' and" +
            " ch.id=:chartId ")
    List<Unit> findAllByChartId(Long chartId);
}
