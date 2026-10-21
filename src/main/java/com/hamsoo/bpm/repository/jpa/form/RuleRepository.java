package com.hamsoo.bpm.repository.jpa.form;

import com.hamsoo.bpm.model.entity.form.Rule;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface RuleRepository extends HamsooRepository<Rule, Long> {

    @Query("select r from Rule r " +
            "where r.id= :id and r.status = 'ACTIVE' ")
    Optional<Rule> findActiveById(Long id);

    @Query("select r from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.rules r " +
            "where r.id = :id and fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and r.status = 'ACTIVE' ")
    Optional<Rule> findByIdAndFormVersionId(Long id, Long formVersionId, Long organId);

    @Query("select r from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.rules r " +
            "where fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and r.status = 'ACTIVE' ")
    Page<Rule> list(Long formVersionId, Long organId, Pageable pageable);

    @Query("select r from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.rules r " +
            "where fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and r.status = 'ACTIVE' ")
    List<Rule> findByFormVersionId(Long formVersionId, Long organId);

}
