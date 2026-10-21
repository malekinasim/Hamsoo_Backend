package com.hamsoo.bpm.repository.jpa.processcategory;

import com.hamsoo.bpm.model.entity.processcategory.ProcessCategoryModelElement;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ProcessCategoryModelElementRepository extends HamsooRepository<ProcessCategoryModelElement, Long> {

    @Query(value = "select pcme from ProcessCategoryModelElement pcme inner join pcme.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where pcme.id= :id and o.id= :organId and m.status= 'ACTIVE' ")
    Optional<ProcessCategoryModelElement> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query(value = "select pcme from ProcessCategoryModelElement pcme inner join pcme.modelVersion mv inner join pcme.processCategory pc " +
            "inner join mv.model m inner join m.organ o " +
            "where mv.id= :modelVersionId and pc.id= :processCategoryId and o.id= :organId and m.status= 'ACTIVE' ")
    Optional<ProcessCategoryModelElement> findByModelVersionIdAndProcessCategoryId(@Param("modelVersionId") Long modelVersionId,
                                                                                   @Param("processCategoryId") Long processCategoryId,
                                                                                   @Param("organId") Long organId);

    @Query(value = "select pcme from ProcessCategoryModelElement pcme inner join pcme.modelVersion mv " +
            "inner join mv.model m inner join m.organ o " +
            "where mv.id= :modelVersionId and pcme.elementId= :elementId and o.id= :organId and m.status= 'ACTIVE' ")
    Optional<ProcessCategoryModelElement> findByModelVersionIdAndElementId(@Param("modelVersionId") Long modelVersionId,
                                                                           @Param("elementId") String elementId,
                                                                           @Param("organId") Long organId);

    @Query(value = "select count(pcme.id) from ProcessCategoryModelElement pcme inner join pcme.modelVersion mv inner join pcme.processCategory pc " +
            "inner join mv.model m inner join m.organ o " +
            "where pc.id= :processCategoryId and o.id= :organId and m.status= 'ACTIVE' ")
    Long countByProcessCategoryId(@Param("processCategoryId") Long processCategoryId,
                                  @Param("organId") Long organId);

    List<ProcessCategoryModelElement> findByModelVersionId(@Param("modelVersionId") Long modelVersionId);

    @Query(value = "select pcme from ProcessCategoryModelElement pcme inner join pcme.modelVersion mv " +
            "inner join mv.model m inner join m.organ o " +
            "where o.id = :organId and m.status= 'ACTIVE' and m.id = :modelId ")
    List<ProcessCategoryModelElement> findByModelId(@Param("organId") Long organId, @Param("modelId") Long modelId);

}
