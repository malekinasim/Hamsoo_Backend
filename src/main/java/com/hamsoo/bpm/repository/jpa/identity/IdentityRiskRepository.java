package com.hamsoo.bpm.repository.jpa.identity;

import com.hamsoo.bpm.model.entity.identity.IdentityRisk;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IdentityRiskRepository extends HamsooRepository<IdentityRisk, Long> {

    @Query("select ir from IdentityRisk ir inner join ir.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where ir.id = :id and o.id = :organId and ir.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    Optional<IdentityRisk> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);


    @Query("select ir from IdentityRisk ir inner join ir.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where o.id = :organId and mv.id = :modelVersionId and ir.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    List<IdentityRisk> findActiveByModelVersionId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId);

}
