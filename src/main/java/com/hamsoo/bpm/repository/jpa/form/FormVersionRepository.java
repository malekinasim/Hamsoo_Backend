package com.hamsoo.bpm.repository.jpa.form;

import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FormVersionRepository extends HamsooRepository<FormVersion, Long> {

    @Query("select fv from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o " +
            "where fv.id= :id and o.id= :organId and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' ")
    Optional<FormVersion> findByIdAndOrganId(Long id, Long organId);

    @Query("select fv from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o " +
            "where fc.id= :formCatalogId and o.id= :organId and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and fv.lastVersion = true")
    Optional<FormVersion> findLastVersionByFormCatalogId(Long formCatalogId, Long organId);

    @Query("select count(fv.id) from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.rules r " +
            "where r.id = :ruleId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and r.status = 'ACTIVE' ")
    Long countByRuleId(Long ruleId, Long organId);

    @Query("select count(fv.id) from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.functions f " +
            "where f.id = :functionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and f.status = 'ACTIVE' ")
    Long countByFunctionId(Long functionId, Long organId);

    @Query("select count(fv.id) from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.formEntities fe " +
            "where fe.id = :formEntityId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and fe.status = 'ACTIVE' ")
    Long countByFormEntityId(Long formEntityId, Long organId);

}
