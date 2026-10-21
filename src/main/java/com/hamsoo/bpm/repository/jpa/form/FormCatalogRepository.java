package com.hamsoo.bpm.repository.jpa.form;

import com.hamsoo.bpm.model.entity.form.FormCatalog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FormCatalogRepository extends HamsooRepository<FormCatalog, Long> {

    @Query("select fc from FormCatalog fc inner join fc.organ o " +
            "where fc.id= :id and o.id= :organId and fc.status = 'ACTIVE' ")
    Optional<FormCatalog> findActiveByIdAndOrganId(Long id, Long organId);

    @Query("select fc from FormCatalog fc inner join fc.organ o " +
            "where fc.formKey= :formKey and o.id= :organId and fc.status = 'ACTIVE' ")
    Optional<FormCatalog> findActiveByFormKeyAndOrganId(String formKey, Long organId);

}
