package com.hamsoo.bpm.repository.jpa.unitcodehistory;

import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface UnitCodeHistoryRepository extends HamsooRepository<UnitCodeHistory, Long> {

    @Query(value = "select uc from UnitCodeHistory uc inner join uc.unit u" +
            " inner join u.chart ch " +
            " inner join ch.organ o " +
            "where uc.id= :unitCodeHistoryId and o.id= :organId " +
            " and uc.status = 'ACTIVE' and ch.status='ACTIVE' ")
    Optional<UnitCodeHistory> findActiveByIdAndOrganId(@Param("unitCodeHistoryId") Long unitCodeHistoryId, @Param("organId") Long organId);

    @Query(value = "select uc from UnitCodeHistory uc " +
            " inner join uc.unit u " +
            " inner join u.chart ch " +
            " inner join ch.organ o  " +
            "where o.id= :organId and uc.code= :code " +
            " and uc.status = 'ACTIVE'  and ch.status='ACTIVE'")
    List<UnitCodeHistory> findActiveByOrganIdAndCode(@Param("organId") Long organId, @Param("code") String code);

    @Query(value = "select uc from UnitCodeHistory uc " +
            " inner join uc.unit u " +
            " inner join u.chart ch " +
            " inner join ch.organ o " +
            "where u.id= :unitId and o.id= :organId " +
            " and uc.status = 'ACTIVE'   and ch.status='ACTIVE' ")
    Optional<UnitCodeHistory> findActiveUnitCodeHistory(@Param("unitId") Long unitId, @Param("organId") Long organId);

}
