package com.hamsoo.bpm.repository.jpa.form;

import com.hamsoo.bpm.model.entity.form.FormEntity;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FormEntityRepository extends HamsooRepository<FormEntity, Long> {

    @Query("select fe from FormEntity fe " +
            "where fe.id= :id and fe.status = 'ACTIVE' ")
    Optional<FormEntity> findActiveById(Long id);

    @Query("select fe from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.formEntities fe " +
            "where fe.id = :id and fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and fe.status = 'ACTIVE' ")
    Optional<FormEntity> findByIdAndFormVersionId(Long id, Long formVersionId, Long organId);

    @Query("select fe from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.formEntities fe " +
            "where fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and fe.status = 'ACTIVE' ")
    Page<FormEntity> list(Long formVersionId, Long organId, Pageable pageable);

    @Query("select fe from FormVersion fv inner join fv.formCatalog fc inner join fc.organ o inner join fv.formEntities fe " +
            "where fv.id = :formVersionId and o.id = :organId " +
            "and fv.status = 'ACTIVE' and fc.status = 'ACTIVE' and fe.status = 'ACTIVE' ")
    List<FormEntity> findByFormVersionId(Long formVersionId, Long organId);

}
