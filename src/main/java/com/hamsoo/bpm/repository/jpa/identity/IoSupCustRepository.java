package com.hamsoo.bpm.repository.jpa.identity;

import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IoSupCustRepository extends HamsooRepository<IoSupCust, Long> {

    @Query("select sc from IoSupCust sc inner join sc.identityIo ii inner join ii.modelVersion mv " +
            "inner join mv.model m inner join m.organ o " +
            "where sc.id = :id and o.id = :organId and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Optional<IoSupCust> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query("select sc from IoSupCust sc inner join sc.identityIo ii inner join ii.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join sc.type t left join sc.internalSupCust insc left join sc.externalSupCust exsc " +
            "where o.id = :organId and t.code = :typeCode and ii.id = :identityIoId " +
            "and (coalesce(insc.id, :internalSupCustId) is null or (insc.id = :internalSupCustId)) " +
            "and (coalesce(exsc.id, :externalSupCustId) is null or (exsc.id = :externalSupCustId)) " +
            "and (coalesce(sc.eventSupCust, :eventSupCust) is null or (sc.eventSupCust = :eventSupCust)) " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Optional<IoSupCust> findEqualActiveIoSupCust(@Param("organId") Long organId, @Param("typeCode") String typeCode,
                                                 @Param("identityIoId") Long identityIoId, @Param("internalSupCustId") Long internalSupCustId,
                                                 @Param("externalSupCustId") Long externalSupCustId, @Param("eventSupCust") String eventSupCust);

    @Query("select sc from IoSupCust sc inner join sc.identityIo ii inner join ii.modelVersion mv " +
            "inner join mv.model m inner join m.organ o inner join sc.type sct inner join ii.type iit " +
            "where o.id = :organId and ii.id = :identityIoId " +
            "and (:identityIoTypeCode is null or (iit.code = :identityIoTypeCode)) " +
            "and (coalesce(:ioSupCustTypeCodeList, null) is null or (sct.code in :ioSupCustTypeCodeList)) " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    List<IoSupCust> findByIdentityIoId(@Param("organId") Long organId, @Param("identityIoId") Long identityIoId,
                                       @Param("identityIoTypeCode") String identityIoTypeCode, @Param("ioSupCustTypeCodeList") List<String> ioSupCustTypeCodeList);

    @Query("select count(sc) from IoSupCust sc inner join sc.identityIo ii inner join ii.modelVersion mv " +
            "inner join mv.model m inner join m.organ o inner join sc.type sct inner join ii.type iit " +
            "where o.id = :organId and ii.id = :identityIoId " +
            "and (:identityIoTypeCode is null or (iit.code = :identityIoTypeCode)) " +
            "and (coalesce(:ioSupCustTypeCodeList, null) is null or (sct.code in :ioSupCustTypeCodeList)) " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Long countByIdentityIoId(@Param("organId") Long organId, @Param("identityIoId") Long identityIoId,
                             @Param("identityIoTypeCode") String identityIoTypeCode, @Param("ioSupCustTypeCodeList") List<String> ioSupCustTypeCodeList);

    @Query("select sc from IoSupCust sc inner join sc.identityIo ii inner join ii.modelVersion mv " +
            "inner join mv.model m inner join m.organ o inner join sc.type sct " +
            "inner join ii.type iit inner join sc.internalSupCust insc inner join ii.ioCatalog ic inner join mv.status mvstatus " +
            "where o.id = :organId and mv.id= :modelVersionId and iit.code = :identityIoTypeCode and ic.id = :ioCatalogId " +
            "and sct.code = :ioSupCustTypeCode and insc.id = :internalSupCustId " +
            "and (coalesce(:modelVersionStatusCodeList, null) is null or (mvstatus.code in :modelVersionStatusCodeList)) " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Optional<IoSupCust> findActiveInternalIoSupCust(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId,
                                                    @Param("identityIoTypeCode") String identityIoTypeCode, @Param("ioCatalogId") Long ioCatalogId,
                                                    @Param("ioSupCustTypeCode") String ioSupCustTypeCode, @Param("internalSupCustId") Long internalSupCustId,
                                                    @Param("modelVersionStatusCodeList") List<String> modelVersionStatusCodeList);

    @Query("select sc from IoSupCust sc inner join sc.identityIo ii inner join ii.modelVersion mv " +
            "inner join mv.model m inner join m.organ o inner join sc.type sct " +
            "where o.id = :organId and mv.id= :modelVersionId and sct.code = :ioSupCustTypeCode " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    List<IoSupCust> findActiveInternalIoSupCust(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId,
                                                @Param("ioSupCustTypeCode") String ioSupCustTypeCode);

    @Modifying
    @Query(value = "with t as ( " +
            "  select sc.* from bpm.bpm_identity_io_sup_custs sc " +
            "  inner join bpm.bpm_code_category sct on sct.code_category_id = sc.\"type\" " +
            "  inner join bpm.bpm_identity_io ii on ii.identity_io_id = sc.identity_io_id " +
            "  inner join bpm.bpm_model_versions mv on mv.model_version_id = ii.model_version_id " +
            "  inner join bpm.bpm_models m on m.model_id = mv.model_id " +
            "  inner join bpm.bpm_organs o on o.organ_id = m.organ_id " +
            "  where o.organ_id= :organId and mv.last_version = true and sct.code = :ioSupCustTypeCode and sc.internal_sup_cust = :oldSupCustId " +
            "  and m.status= 'ACTIVE' and ii.status= 'ACTIVE' and sc.status= 'ACTIVE'  " +
            ") " +
            "update bpm.bpm_identity_io_sup_custs sc2 " +
            "set internal_sup_cust = :newSupCustId " +
            "from t " +
            "where sc2.internal_sup_cust = :oldSupCustId and sc2.sup_cust_id = t.sup_cust_id ", nativeQuery = true)
    void updateInternalSupCust(@Param("organId") Long organId, @Param("ioSupCustTypeCode") String ioSupCustTypeCode,
                               @Param("newSupCustId") Long newSupCustId, @Param("oldSupCustId") Long oldSupCustId);

    @Query("select count(sc.id) from IoSupCust sc inner join sc.type sct inner join sc.externalSupCust exsc inner join sc.identityIo ii " +
            "inner join ii.modelVersion mv inner join mv.model m " +
            "where sct.code = :ioSupCustTypeCode and exsc.id = :externalProcessId " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' and exsc.status = 'ACTIVE' ")
    Long countByExternalProcessId(@Param("ioSupCustTypeCode") String ioSupCustTypeCode, @Param("externalProcessId") Long externalProcessId);

    @Query("select count(sc.id) from IoSupCust sc inner join sc.identityIo ii inner join sc.internalSupCust insc " +
            "inner join insc.model m inner join sc.type sct " +
            "where m.id= :modelId and sct.code = :ioSupCustTypeCode " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Long countInternalSupCustByModelId(@Param("modelId") Long modelId, @Param("ioSupCustTypeCode") String ioSupCustTypeCode);


    @Query("select sc " +
            "from IoSupCust sc " +
            "inner join sc.identityIo ii " +
            "inner join ii.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join sc.type sct " +
            "inner join ii.type iit " +
            "where o.id = :organId " +
            "and mv.id= :modelVersionId " +
            "and ii.id = :identityIoId " +
            "and sct.code = :ioSupCustTypeCode " +
            "and sc.status = 'ACTIVE' and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    List<IoSupCust> findAllIoCatalogActiveInternalIoSupCust(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId, @Param("identityIoId") Long identityIoId, @Param("ioSupCustTypeCode") String ioSupCustTypeCode);
}
