package com.hamsoo.bpm.repository.jpa.modelcomment;

import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelCommentRepository extends HamsooRepository<ModelComment, Long> {

    @Query(value = "select mc from ModelComment mc inner join mc.modelVersion mv inner join mv.model m inner join m.organ o " +
            "where mc.id= :id and o.id= :organId and m.status= 'ACTIVE' and mc.status= 'ACTIVE' ")
    Optional<ModelComment> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Modifying
    @Query(value = "update bpm.bpm_model_comments set model_version_id = :newModelVersionId where model_version_id = :oldModelVersionId ", nativeQuery = true)
    void updateModelVersionId(@Param("newModelVersionId") Long newModelVersionId, @Param("oldModelVersionId") Long oldModelVersionId);

    @Query(value = "select new com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto(c.id,c.text,u.name,u.family,c.modifiedDate,mer.id,mer.modifiedDate,mer.createdDate,mer.elementId) " +
            "from ModelComment c inner join c.modelVersion mv inner join mv.model m inner join m.organ o inner join c.user u " +
            "left join ModelElementRefrence mer on mer.referenceId = c.id and mer.modelVersion.id = mv.id left join mer.referenceType rt " +
            "where (rt.code is null or rt.code = :referenceTypeCode) and o.id = :organId and mv.id = :modelVersionId " +
            "and mv.lastVersion = true and c.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Page<ModelCommentDto> getCommentAndElementCommentListOfLastVersion(@Param("referenceTypeCode") String referenceTypeCode, @Param("modelVersionId") Long modelVersionId,


                                                                       @Param("organId") Long organId, Pageable pageable);

    @Query(value = "select new com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto(c.id,c.text,u.name,u.family,c.modifiedDate,mer.id,mer.modifiedDate,mer.createdDate,mer.elementId) " +
            "from ModelComment c inner join c.modelVersion mv inner join mv.model m inner join m.organ o inner join c.user u " +
            "inner join ModelElementRefrence mer on mer.referenceId = c.id and mer.modelVersion.id = mv.id inner join mer.referenceType rt " +
            "where (rt.code is null or rt.code = :referenceTypeCode) and o.id = :organId and mv.id = :modelVersionId" +
            " and mer.elementId=:elementId " +
            "and mv.lastVersion = true and c.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Page<ModelCommentDto> getElementCommentListOfLastVersion(String referenceTypeCode, Long modelVersionId, String elementId, Long organId, Pageable pageable);

    @Query(value = "select new com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto(c.id,c.text,u.name,u.family,c.modifiedDate,mer.id,mer.modifiedDate,mer.createdDate,mer.elementId) " +
            "from ModelComment c inner join c.modelVersion mv inner join mv.model m inner join m.organ o inner join c.user u " +
            "inner join ModelElementRefrence mer on mer.referenceId = c.id and mer.modelVersion.id = mv.id inner join mer.referenceType rt " +
            "where (rt.code is null or rt.code = :referenceTypeCode) and o.id = :organId and mv.id = :modelVersionId" +
            " and mer.elementId=:elementId " +
            "and mv.lastVersion = true and c.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    List<ModelCommentDto> getElementCommentListOfLastVersion(String referenceTypeCode, Long modelVersionId, String elementId, Long organId);

}
