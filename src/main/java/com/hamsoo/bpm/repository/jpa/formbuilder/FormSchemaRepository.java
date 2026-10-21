package com.hamsoo.bpm.repository.jpa.formbuilder;

import com.hamsoo.bpm.model.entity.formbuilder.FormSchemaCatalog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FormSchemaRepository extends HamsooRepository<FormSchemaCatalog, Long>
{

    @Query("select fsc from FormSchemaCatalog fsc " +
            " where fsc.id = :id and fsc.organ.id = :organId and  fsc.status='ACTIVE' ")
    Optional<FormSchemaCatalog> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query("select fsc from FormSchemaCatalog fsc " +
            " where fsc.code = :code and fsc.organ.id = :organId and  fsc.status='ACTIVE' ")
    Optional<FormSchemaCatalog> findActiveByCodeAndOrganId(@Param("code") String code, @Param("organId") Long organId);



}
