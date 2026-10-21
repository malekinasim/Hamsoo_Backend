package com.hamsoo.bpm.repository.jpa.identity;

import com.hamsoo.bpm.model.entity.identity.IdentityComplication;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IdentityComplicationRepository extends HamsooRepository<IdentityComplication, Long> {

    @Query("select ic from IdentityComplication ic inner join ic.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where ic.id = :id and o.id = :organId and ic.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    Optional<IdentityComplication> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);


    @Query("select ic from IdentityComplication ic inner join ic.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where o.id = :organId and mv.id = :modelVersionId and ic.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    List<IdentityComplication> findActiveByModelVersionId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId);


}
