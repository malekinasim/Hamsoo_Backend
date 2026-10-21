package com.hamsoo.bpm.repository.jpa.form;

import com.hamsoo.bpm.model.entity.form.Function;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FunctionRepository extends HamsooRepository<Function, Long> {

    @Query("select f from Function f " +
            "where f.id= :id and f.status = 'ACTIVE' ")
    Optional<Function> findActiveById(Long id);

    @Query("select f from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.functions f " +
            "where f.id = :id and fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and f.status = 'ACTIVE' ")
    Optional<Function> findByIdAndFormVersionId(Long id, Long formVersionId, Long organId);

    @Query("select f from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.functions f " +
            "where fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and f.status = 'ACTIVE' ")
    Page<Function> list(Long formVersionId, Long organId, Pageable pageable);

    @Query("select f from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.functions f " +
            "where fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and f.status = 'ACTIVE' ")
    List<Function> findByFormVersionId(Long formVersionId, Long organId);

}
