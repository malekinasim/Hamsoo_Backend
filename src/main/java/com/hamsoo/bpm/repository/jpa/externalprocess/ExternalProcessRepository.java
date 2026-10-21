package com.hamsoo.bpm.repository.jpa.externalprocess;

import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ExternalProcessRepository extends HamsooRepository<ExternalProcess, Long> {


    @Query("select xp from ExternalProcess xp inner join xp.organ o " +
            "where xp.id= :id and o.id= :organId and xp.status = 'ACTIVE' ")
    Optional<ExternalProcess> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

}
