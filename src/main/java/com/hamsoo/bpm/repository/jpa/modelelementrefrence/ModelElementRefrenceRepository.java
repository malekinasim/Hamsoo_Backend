package com.hamsoo.bpm.repository.jpa.modelelementrefrence;

import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface ModelElementRefrenceRepository extends HamsooRepository<ModelElementRefrence, Long> {

    @Query(value = "select modelElmentRef from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType" +
            " inner join modelElmentRef.modelVersion mdlver" +
            " inner join mdlver.model model " +
            " inner join model.organ org1" +
            " inner join ExternalProcess  extrenalProcess on extrenalProcess.id=modelElmentRef.referenceId " +
            " inner join extrenalProcess.organ org" +
            " where org.id=:organId" +
            " and org1.id=:organId" +
            " and org.status='ACTIVE'" +
            " and org1.status='ACTIVE'" +
            " and model.status='ACTIVE'" +
            " and extrenalProcess.status='ACTIVE'" +
            " and extrenalProcess.id=:externalProcessId" +
            " and refType.code=:referenceType" +
            " and  mdlver.lastVersion=true")
    Optional<ModelElementRefrence> findExternalProcessRefByReferenceId(@Param("externalProcessId") Long externalProcessId, @Param("organId") Long organId, @Param("referenceType") String referenceType);


    @Query(value = "select modelElmentRef from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.modelVersion mdlver" +
            " inner join mdlver.model model1 " +
            " inner join model1.organ org1" +
            " inner join modelElmentRef.referenceType refType" +
            " inner join ModelVersion refModelVersion on refModelVersion.id=modelElmentRef.referenceId " +
            " inner join refModelVersion.model model " +
            " inner join model.organ org" +
            " where org1.id=:organId" +
            " and model1.status='ACTIVE'" +
            " and org.status='ACTIVE'" +
            " and org.id=:organId" +
            " and model.status='ACTIVE'" +
            " and refModelVersion.id=:refModelVersionId" +
            " and refType.code=:referenceType " +
            "and mdlver.lastVersion=true " +
            "and refModelVersion.lastVersion=true")
    Optional<ModelElementRefrence> findModelVersionRefByReferenceId(@Param("refModelVersionId") Long refModelVersionId, @Param("organId") Long organId, @Param("referenceType") String referenceType);


    @Query(value = "select modelElmentRef from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType" +
            " inner join modelElmentRef.modelVersion mdlver" +
            " inner join ModelComment  modelComment on modelComment.id=modelElmentRef.referenceId " +
            " inner join modelComment.modelVersion commentMdlver" +
            " inner join commentMdlver.model model" +
            " inner join model.organ org" +
            " where org.id=:organId" +
            " and mdlver.id=commentMdlver.id" +
            " and org.status='ACTIVE'" +
            " and modelComment.status='ACTIVE' " +
            " and model.status='ACTIVE' " +
            " and modelComment.id=:commentId" +
            " and refType.code=:referenceType " +
            " and commentMdlver.lastVersion=true" +
            " and mdlver.lastVersion=true")
    List<ModelElementRefrence> findAllCommentRefByReferenceId(@Param("commentId") Long commentId, @Param("organId") Long organId, @Param("referenceType") String referenceType);

    @Query(value = "select modelElmentRef from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType" +
            " inner join modelElmentRef.modelVersion mdlver" +
            " inner join mdlver.model model " +
            " inner join model.organ org1" +
            " inner join Post  post on post.id=modelElmentRef.referenceId " +
            " inner join post.unit unt" +
            " inner join unt.chart chrt" +
            " inner join chrt.organ org" +
            " where org.id=:organId" +
            " and org1.id=:organId" +
            " and org.status='ACTIVE'" +
            " and org1.status='ACTIVE'" +
            " and model.status='ACTIVE'" +
            " and chrt.status='ACTIVE' " +
            " and unt.status='ACTIVE' " +
            " and post.status='ACTIVE' " +
            " and post.id=:postId" +
            " and refType.code=:referenceType" +
            " and mdlver.lastVersion=true ")
    Optional<ModelElementRefrence> findPostRefByReferenceId(
            @Param("postId") Long postId,
            @Param("organId") Long organId,
            @Param("referenceType") String referenceType);

    @Query(value = "select modelElementRef from ModelElementRefrence modelElementRef " +
            " inner join modelElementRef.modelVersion modelVersion" +
            " inner join modelVersion.model model " +
            " inner join model.organ org" +
            " where org.id=:organId" +
            " and model.status='ACTIVE'" +
            " and modelVersion.id=:modelVersionId " +
            " and modelElementRef.elementId=:elementId" +
            " and modelElementRef.referenceType.code in (:refTypeList)" +
            " and modelVersion.lastVersion=true")
    List<ModelElementRefrence> findByElementIdAndModelVersionIdAndReferenceTypeList(
            @Param("elementId") String elementId,
            @Param("modelVersionId") Long modelVersionId,
            @Param("organId") Long organId,
            @Param("refTypeList") List<String> refTypeList);


    @Query(value = "select modelElementRef from ModelElementRefrence modelElementRef " +
            " inner join modelElementRef.modelVersion modelVersion" +
            " inner join modelVersion.model model " +
            " inner join model.organ org" +
            " where org.id=:organId" +
            " and model.status='ACTIVE'" +
            " and modelVersion.id=:modelVersionId " +
            " and modelElementRef.elementId in (:elementId)" +
            " and modelElementRef.referenceType.code in (:refTypeList)" +
            " and modelVersion.lastVersion=true")
    List<ModelElementRefrence> findByElementIdListAndModelVersionIdAndReferenceTypeList(
            @Param("elementId") List<String> elementId,
            @Param("modelVersionId") Long modelVersionId,
            @Param("organId") Long organId,
            @Param("refTypeList") List<String> refTypeList);


    @Query(value = "select modelElementRef from ModelElementRefrence modelElementRef" +
            " inner join modelElementRef.modelVersion mdlver" +
            " inner join modelElementRef.referenceType refType" +
            " left join mdlver.model model " +
            " left join model.organ org" +
            " where org.id=:organId and" +
            " modelElementRef.elementId=:elementId and" +
            " mdlver.id=:modelVersionId and" +
            " model.status='ACTIVE' and" +
            " mdlver.lastVersion=true and" +
            " modelElementRef.referenceId=:referenceId and" +
            " refType.code=:referenceType")
    Optional<ModelElementRefrence> findByElementIdAndModelVersionIdAndModelRefrenceIdAndReferenceType(
            @Param("elementId") String elementId,
            @Param("modelVersionId") Long modelVersionId,
            @Param("referenceId") Long referenceId,
            @Param("referenceType") String referenceType,
            @Param("organId") Long organId);


    @Query(value = "select modelElementRef from ModelElementRefrence modelElementRef " +
            " inner join modelElementRef.modelVersion modelVersion" +
            " inner join modelVersion.model model " +
            " inner join model.organ org" +
            " where org.id=:organId" +
            " and org.status='ACTIVE'" +
            " and model.status='ACTIVE'" +
            " and modelVersion.id=:modelVersionId" +
            " and modelElementRef.referenceType.code in (:refTypeList)" +
            " and modelVersion.lastVersion=true")
    List<ModelElementRefrence> findAllByModelVersionIdAndOrganId(
            @Param("modelVersionId") Long modelVersionId,
            @Param("organId") Long organId,
            @Param("refTypeList") List<String> refTypeList);


    @Query(value = "select modelElmentRef from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.modelVersion modelVersion" +
            " inner join modelVersion.model model " +
            " inner join model.organ org" +
            " where org.id=:organId" +
            " and org.status='ACTIVE'" +
            " and model.status='ACTIVE'" +
            " and modelElmentRef.id=:id")
    Optional<ModelElementRefrence> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})
    List<ModelElementRefrence> findLockedByModelVersionId(@Param("modelVersionId") Long modelVersionId);

    @Query(value = "select me from ModelElementRefrence me inner join me.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join me.referenceType referenceType " +
            "where o.id=:organId and m.status = 'ACTIVE' and mv.lastVersion = true " +
            "and referenceType.code = :referenceTypeCode and me.referenceId = :referenceId ")
    List<ModelElementRefrence> findLastByReferenceTypeCodeAndReferenceId(@Param("organId") Long organId, @Param("referenceTypeCode") String referenceTypeCode,
                                                                         @Param("referenceId") Long referenceId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})
    @Query(value = "select me from ModelElementRefrence me inner join me.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join me.referenceType referenceType " +
            "where o.id=:organId and m.status = 'ACTIVE' and mv.lastVersion = true " +
            "and referenceType.code = :referenceTypeCode and me.referenceId = :referenceId ")
    List<ModelElementRefrence> findLastLockedByReferenceTypeCodeAndReferenceId(@Param("organId") Long organId, @Param("referenceTypeCode") String referenceTypeCode,
                                                                               @Param("referenceId") Long referenceId);

    @Modifying
    @Query(value = "with t as ( " +
            "  select me.* from bpm.bpm_model_element_references me " +
            "  inner join bpm.bpm_model_versions mv on mv.model_version_id = me.parent_model_version_id " +
            "  inner join bpm.bpm_models m on m.model_id = mv.model_id " +
            "  inner join bpm.bpm_organs o on o.organ_id = m.organ_id " +
            "  inner join bpm.bpm_code_category reference_type on reference_type.code_category_id = me.reference_type " +
            "  where o.organ_id= :organId and mv.last_version = true and reference_type.code = :referenceTypeCode " +
            "  and me.reference_id = :oldReferenceId and m.status= 'ACTIVE' " +
            ") " +
            "update bpm.bpm_model_element_references me2 " +
            "set reference_id = :newReferenceId " +
            "from t " +
            "where me2.reference_id = :oldReferenceId and me2.reference_id = t.reference_id ", nativeQuery = true)
    void updateInternalReferenceId(@Param("organId") Long organId, @Param("referenceTypeCode") String referenceTypeCode,
                                   @Param("newReferenceId") Long newReferenceId, @Param("oldReferenceId") Long oldReferenceId);

    @Query(value = "select count(me.id) from ModelElementRefrence me inner join me.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join me.referenceType referenceType " +
            "inner join ModelVersion referencedModelversion on referencedModelversion.id = me.referenceId " +
            "inner join referencedModelversion.model referencedModel " +
            "where o.id=:organId and m.status = 'ACTIVE' and referenceType.code = :referenceTypeCode " +
            "and referencedModel.id = :referencedModelId ")
    Long countByReferencedModelId(@Param("organId") Long organId, @Param("referenceTypeCode") String referenceTypeCode,
                                  @Param("referencedModelId") Long referencedModelId);

    @Query(value = "select count(me.id) from ModelElementRefrence me inner join me.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join me.referenceType referenceType " +
            "where o.id=:organId and m.status = 'ACTIVE' and referenceType.code = :referenceTypeCode " +
            "and me.referenceId = :referenceId ")
    Long countByReferenceTypeCodeAndReferenceId(@Param("organId") Long organId, @Param("referenceTypeCode") String referenceTypeCode,
                                                @Param("referenceId") Long referenceId);

}
