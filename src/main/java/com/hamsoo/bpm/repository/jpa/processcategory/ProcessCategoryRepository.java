package com.hamsoo.bpm.repository.jpa.processcategory;

import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ProcessCategoryRepository extends HamsooRepository<ProcessCategory, Long> {

    Optional<ProcessCategory> findByIdAndStatusAndOrganId(Long id, String status, Long organId);

    @Query(value = "select pc from ProcessCategory pc inner join pc.organ o " +
            "where o.id= :organId and pc.status = 'ACTIVE' and pc.name = :name ")
    Optional<ProcessCategory> findActiveByOrganIdAndName(@Param("organId") Long organId, @Param("name") String name);

}
