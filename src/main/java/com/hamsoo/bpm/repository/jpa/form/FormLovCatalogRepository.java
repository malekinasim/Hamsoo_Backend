package com.hamsoo.bpm.repository.jpa.form;

import com.hamsoo.bpm.model.entity.form.FormLovCatalog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FormLovCatalogRepository extends HamsooRepository<FormLovCatalog, Long> {

    @Query("select flc from FormLovCatalog flc inner join flc.organ o " +
            "where flc.id= :id and o.id= :organId and flc.status = 'ACTIVE' ")
    Optional<FormLovCatalog> findByIdAndOrganId(Long id, Long organId);

}
