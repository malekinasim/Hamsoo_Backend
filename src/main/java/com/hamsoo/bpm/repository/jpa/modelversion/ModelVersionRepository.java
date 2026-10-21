package com.hamsoo.bpm.repository.jpa.modelversion;

import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelVersionRepository extends HamsooRepository<ModelVersion, Long> {

    @Query("select mv from ModelVersion mv inner join mv.model m inner join m.organ o " +
            "where mv.id= :id and o.id= :organId and m.status = 'ACTIVE' ")
    Optional<ModelVersion> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})
    @Query("select mv from ModelVersion mv inner join mv.model m inner join m.organ o " +
            "where mv.id= :id and o.id= :organId and m.status = 'ACTIVE' ")
    Optional<ModelVersion> findLockedByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query("SELECT mv FROM ModelVersion mv inner join mv.asisTobe at inner join mv.model m " +
            "WHERE at.code= :asisTobe and m.id= :modelId and mv.lastVersion = true and m.status = 'ACTIVE' ")
    ModelVersion getLastVersion(@Param("asisTobe") String asisTobe, @Param("modelId") Long modelId);

    @Query("SELECT mv FROM ModelVersion mv inner join mv.asisTobe at inner join mv.model m inner join mv.parent mvParent " +
            "WHERE at.code= :asisTobe and m.id= :modelId and mv.lastVersion = true and m.status = 'ACTIVE' and mvParent.id= :mvParentId ")
    Optional<ModelVersion> getLastVersionByParent(@Param("asisTobe") String asisTobe, @Param("modelId") Long modelId, @Param("mvParentId") Long mvParentId);

    @Query("SELECT mv FROM ModelVersion mv inner join mv.asisTobe at inner join mv.model m " +
            "WHERE m.id= :modelId and mv.lastVersion = true and m.status = 'ACTIVE' ")
    List<ModelVersion> getLastVersions(@Param("modelId") Long modelId);

//    @Query(value = "select distinct mv.* from bpm.bpm_model_versions mv inner join jsonb_array_elements(mv.resource_json) resource on true " +
//            " inner join bpm.bpm_models  m on m.model_id = mv.model_id " +
//            "where CAST ( resource ->> 'resourceCatalogId' AS INTEGER) = :resourceCatalogId and m.status= 'ACTIVE' ", nativeQuery = true)
//    List<ModelVersion> getByResourceSchemaResourceCatalogId(@Param("resourceCatalogId") Long resourceCatalogId);

    @Query(value = "select mv from IdentityResource ir inner join ir.modelVersion mv " +
            " inner join mv.model  m  " +
            "where ir.resourceCatalog.id =:resourceCatalogId and m.status= 'ACTIVE' ")
    List<ModelVersion> getByResourceSchemaResourceCatalogId(@Param("resourceCatalogId") Long resourceCatalogId);

    @Query("select mv from ModelVersion mv inner join mv.model m inner join mv.asisTobe asisTobe " +
            "where m.id= :modelId and  asisTobe.code= :asisTobeCode and mv.lastVersion = true and m.status = 'ACTIVE' ")
    Optional<ModelVersion> findByModelIdAndAsisTobeCode(@Param("modelId") Long modelId, @Param("asisTobeCode") String asisTobeCode);

    @Query(value = "select mv from ModelVersion mv inner join mv.model m inner join mv.status modelStatus " +
            "where m.id= :modelId and mv.lastVersion = true and modelStatus.code <> :modelStatusCode and m.status = 'ACTIVE' ")
    List<ModelVersion> findByModelIdAndModelStatusCodeNot(@Param("modelId") Long modelId, @Param("modelStatusCode") String modelStatusCode);

    @Query(value = "select mv from ModelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join mv.asisTobe asisTobe inner join m.modelType mt " +
            "where asisTobe.code in :asisTobeCodeList and mv.lastVersion = true and mv.id not in :notInModelVersionIdList " +
            "and o.id = :organId and m.status= 'ACTIVE' and (:modelName = '' or m.name like concat('%', :modelName ,'%')) " +
            "and (coalesce(:modelTypeCodeList, null) is null or (mt.code in :modelTypeCodeList)) ")
    Page<ModelVersion> getInternalProcessList(@Param("asisTobeCodeList") List<String> asisTobeCodeList,
                                              @Param("notInModelVersionIdList") List<Long> notInModelVersionIdList,
                                              @Param("organId") Long organId,
                                              @Param("modelName") String modelName,
                                              @Param("modelTypeCodeList") List<String> modelTypeCodeList,
                                              Pageable pageable);


    @Query(value = "select mv from ModelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join mv.asisTobe asisTobe inner join m.modelType mt " +
            "where asisTobe.code in :asisTobeCodeList and mv.lastVersion = true and mv.id not in :notInModelVersionIdList " +
            "and o.id = :organId and m.status= 'ACTIVE' and (:modelName = '' or m.name like concat('%', :modelName ,'%')) " +
            "and (coalesce(:modelTypeCodeList, null) is null or (mt.code in :modelTypeCodeList)) " +
            "and not exists (" +
            " select reference from ModelElementRefrence  reference " +
            " inner join  reference.referenceType refType" +
            " where reference.referenceId=mv.id" +
            " and refType.code=:extReferenceTypeCode)")
    Page<ModelVersion> getFilterInternalProcessListByElementReference(@Param("asisTobeCodeList") List<String> asisTobeCodeList,
                                                                      @Param("notInModelVersionIdList") List<Long> notInModelVersionIdList,
                                                                      @Param("organId") Long organId,
                                                                      @Param("modelName") String modelName,
                                                                      @Param("modelTypeCodeList") List<String> modelTypeCodeList,
                                                                      @Param("extReferenceTypeCode") String extReferenceTypeCode,
                                                                      Pageable pageable);

    @Query(value = " select count(*) from ( " +
            "select distinct mv.* from bpm.bpm_model_versions mv inner join bpm.bpm_models m on m.model_id = mv.model_id " +
            "where CAST ( mv.identity_data_json ->> 'processCategoryId' AS INTEGER) = :processCategoryId and m.status= 'ACTIVE' " +
            ") as pcc ", nativeQuery = true)
    Long getCountByProcessCategoryId(@Param("processCategoryId") Long processCategoryId);


    @Query(value = " select mv.model_version_id as id, m.model_id , m.name, mv.asis_tobe_id  from  bpm.bpm_model_versions mv inner join bpm.bpm_models m on m.model_id = mv.model_id inner join bpm.bpm_organs o on m.organ_id =o.organ_id " +
            " where  mv.identity_data_json ->> 'processCategoryId' is null and m.status= 'ACTIVE'  " +
           // " and (coalesce(:modelName, null) is null or m.name like concat('%', :modelName ,'%')) " +
            " and o.organ_id= :organId and mv.last_version=true "
            , nativeQuery = true)
    Page<Tuple> listModelNoProcessCategory(Pageable pageable, @Param("organId") Long organId);

    @Query(value = " select count(model_version_id)  from  bpm.bpm_model_versions mv inner join bpm.bpm_models m on m.model_id = mv.model_id inner join bpm.bpm_organs o on m.organ_id =o.organ_id " +
            " where  mv.identity_data_json ->> 'processCategoryId' is null and m.status= 'ACTIVE'  " +
          //  " and (coalesce(:modelName, null) is null or m.name like concat('%', :modelName ,'%')) " +
            " and o.organ_id= :organId and mv.last_version=true "
            , nativeQuery = true)
    Long getcountModelNoProcessCategory(  @Param("organId") Long organId);

    @Query("select mv from IdentityIo ii right join ii.type cc on ii.status='ACTIVE' and cc.code = :IdentityType and  cc.category='IDENTITY_IO_TYPE'" +
            " right join ii.modelVersion mv inner join mv.model m inner join m.organ o" +
            " where  o.id= :organId and m.status = 'ACTIVE' and mv.lastVersion=true and ii.id is null and (:modelName = '' or m.name like concat('%', :modelName ,'%')) ")
    Page<ModelVersion> listModelsWithoutIdentityType(Pageable pageable, @Param("IdentityType") String IdentityType, @Param("organId") Long organId, @Param("modelName") String modelName);

    @Query("select count(mv.id) from IdentityIo ii right join ii.type cc on ii.status='ACTIVE' and cc.code = :IdentityType and  cc.category='IDENTITY_IO_TYPE'" +
            " right join ii.modelVersion mv inner join mv.model m inner join m.organ o" +
            " where  o.id= :organId and m.status = 'ACTIVE' and mv.lastVersion=true and ii.id is null and (:modelName = '' or m.name like concat('%', :modelName ,'%')) ")

    Long getCountModelsWithoutIdentityType(@Param("IdentityType") String IdentityType, @Param("organId") Long organId, @Param("modelName") String modelName);
    @Query( " select mv from IoSupCust iis right join  iis.identityIo ii on  iis.status='ACTIVE' " +
            " inner join ii.type cc on ii.status='ACTIVE' and cc.code = :IdentityType and  cc.category='IDENTITY_IO_TYPE' " +
            " inner join ii.modelVersion mv inner join mv.model m inner join m.organ o " +
            " where  o.id= :organId and m.status = 'ACTIVE' and mv.lastVersion=true  and iis.id is null and (:modelName = '' or m.name like concat('%', :modelName ,'%')) ")
    Page<ModelVersion> listModelsWithoutCustomerSuplier(Pageable pageable, @Param("IdentityType") String IdentityType, @Param("modelName") String modelName, @Param("organId") Long organId);

    @Query( " select count(mv.id) from IoSupCust iis right join  iis.identityIo ii on  iis.status='ACTIVE' " +
            " inner join ii.type cc on ii.status='ACTIVE' and cc.code = :IdentityType and  cc.category='IDENTITY_IO_TYPE' " +
            " inner join ii.modelVersion mv inner join mv.model m inner join m.organ o " +
            " where  o.id= :organId and m.status = 'ACTIVE' and mv.lastVersion=true  and iis.id is null and (:modelName = '' or m.name like concat('%', :modelName ,'%')) ")
    Long getcountModelsWithoutCustomerSuplier(@Param("IdentityType") String IdentityType, @Param("modelName") String modelName, @Param("organId") Long organId);

    @Query(value = " select scInput.model_version_id as id , scInput.model_id , scInput.name, scInput.asis_tobe_id  from " +
            "         (select mv.model_version_id as inputModel,mv.model_version_id, m.model_id , m.name, mv.asis_tobe_id,ii.io_catalog_id  from bpm.bpm_identity_io_sup_custs sc " +
            "               inner join bpm.bpm_code_category sct on sct.code_category_id = sc.type " +
            "               inner join bpm.bpm_identity_io ii on ii.identity_io_id = sc.identity_io_id" +
            "               inner join bpm.bpm_code_category bcc on bcc.code_category_id = ii.type " +
            "               inner join bpm.bpm_model_versions mv on mv.model_version_id = ii.model_version_id " +
            "               inner join bpm.bpm_models m on m.model_id = mv.model_id " +
            "               inner join bpm.bpm_organs o on o.organ_id = m.organ_id " +
            "              where  mv.last_version = true and m.status = 'ACTIVE' and ii.status = 'ACTIVE' and sc.status = 'ACTIVE' and  m.organ_id=:organId "+//" and (coalesce(:modelName, null) is null or m.name like concat('%', :modelName ,'%')) " +
            "                and bcc.code = 'INPUT' and bcc.category = 'IDENTITY_IO_TYPE' ) scInput           " +
            "                left join bpm.bpm_identity_io_sup_custs scOutput on  scInput.inputModel = scOutput.internal_sup_cust AND scOutput.status='ACTIVE'" +
            "                left join bpm.bpm_identity_io bii on bii.identity_io_id = scOutput.identity_io_id and  bii.status = 'ACTIVE' " +
            "                left join bpm.bpm_code_category bcc on bcc.code_category_id = bii.type and bcc.code = 'OUTPUT' and bcc.category = 'IDENTITY_IO_TYPE'" +
            "                left join bpm.bpm_model_versions mv on mv.model_version_id = bii.model_version_id and mv.last_version=true " +
            "                left join bpm.bpm_models m on m.model_id = mv.model_id and m.status='ACTIVE' where  scOutput.sup_cust_id is null or bii.io_catalog_id != scInput.io_catalog_id " +
            "  union    " +
            "  select scOutput.model_version_id as id, scOutput.model_id , scOutput.name, scOutput.asis_tobe_id  from " +
            "         (select mv.model_version_id as outputModel,mv.model_version_id, m.model_id , m.name, mv.asis_tobe_id,ii.io_catalog_id  from bpm.bpm_identity_io_sup_custs sc" +
            "             inner join bpm.bpm_code_category sct on sct.code_category_id = sc.type" +
            "             inner join bpm.bpm_identity_io ii on ii.identity_io_id = sc.identity_io_id" +
            "             inner join bpm.bpm_code_category bcc on bcc.code_category_id = ii.type" +
            "             inner join bpm.bpm_model_versions mv on mv.model_version_id = ii.model_version_id" +
            "             inner join bpm.bpm_models m on m.model_id = mv.model_id" +
            "             inner join bpm.bpm_organs o on o.organ_id = m.organ_id" +
            "          where mv.last_version = true and m.status = 'ACTIVE' and ii.status = 'ACTIVE' and sc.status = 'ACTIVE' and  m.organ_id=:organId "+//" and (coalesce(:modelName, null) is null or m.name like concat('%', :modelName ,'%')) " +
            "           and bcc.code = 'OUTPUT' and bcc.category = 'IDENTITY_IO_TYPE' ) scOutput" +
            "         left join bpm.bpm_identity_io_sup_custs scInput on  scOutput.outputModel = scInput.internal_sup_cust  and scInput.status = 'ACTIVE'" +
            "         left join bpm.bpm_identity_io bii on bii.identity_io_id = scInput.identity_io_id and  bii.status = 'ACTIVE'" +
            "         left join bpm.bpm_code_category bcc on bcc.code_category_id = bii.type and bcc.code = 'INPUT' and bcc.category = 'IDENTITY_IO_TYPE' " +
            "         left join bpm.bpm_model_versions mv on mv.model_version_id = bii.model_version_id  and mv.last_version=true" +
            "         left join bpm.bpm_models m on m.model_id = mv.model_id and  m.status = 'ACTIVE'   where  scInput.sup_cust_id is null or bii.io_catalog_id != scOutput.io_catalog_id " , nativeQuery = true )

    Page<Tuple> listSIPOCInvalidModel(Pageable pageable,  @Param("organId") Long organId);

    @Query(value = " select count(*)  from " +
            " (select scInput.model_version_id as id , scInput.model_id , scInput.name, scInput.asis_tobe_id  from " +
            "         (select mv.model_version_id as inputModel,mv.model_version_id, m.model_id , m.name, mv.asis_tobe_id,ii.io_catalog_id  from bpm.bpm_identity_io_sup_custs sc " +
            "               inner join bpm.bpm_code_category sct on sct.code_category_id = sc.type " +
            "               inner join bpm.bpm_identity_io ii on ii.identity_io_id = sc.identity_io_id" +
            "               inner join bpm.bpm_code_category bcc on bcc.code_category_id = ii.type " +
            "               inner join bpm.bpm_model_versions mv on mv.model_version_id = ii.model_version_id " +
            "               inner join bpm.bpm_models m on m.model_id = mv.model_id " +
            "               inner join bpm.bpm_organs o on o.organ_id = m.organ_id " +
            "              where  mv.last_version = true and m.status = 'ACTIVE' and ii.status = 'ACTIVE' and sc.status = 'ACTIVE' and  m.organ_id=:organId "+//and (coalesce(:modelName, null) is null or m.name like concat('%', :modelName ,'%')) " +
            "                and bcc.code = 'INPUT' and bcc.category = 'IDENTITY_IO_TYPE' ) scInput           " +
            "                left join bpm.bpm_identity_io_sup_custs scOutput on  scInput.inputModel = scOutput.internal_sup_cust AND scOutput.status='ACTIVE'" +
            "                left join bpm.bpm_identity_io bii on bii.identity_io_id = scOutput.identity_io_id and  bii.status = 'ACTIVE' " +
            "                left join bpm.bpm_code_category bcc on bcc.code_category_id = bii.type and bcc.code = 'OUTPUT' and bcc.category = 'IDENTITY_IO_TYPE'" +
            "                left join bpm.bpm_model_versions mv on mv.model_version_id = bii.model_version_id and mv.last_version=true " +
            "                left join bpm.bpm_models m on m.model_id = mv.model_id and m.status='ACTIVE' where  scOutput.sup_cust_id is null or bii.io_catalog_id != scInput.io_catalog_id " +
            "  union    " +
            "  select scOutput.model_version_id as id, scOutput.model_id , scOutput.name, scOutput.asis_tobe_id  from " +
            "         (select mv.model_version_id as outputModel,mv.model_version_id, m.model_id , m.name, mv.asis_tobe_id,ii.io_catalog_id  from bpm.bpm_identity_io_sup_custs sc" +
            "             inner join bpm.bpm_code_category sct on sct.code_category_id = sc.type" +
            "             inner join bpm.bpm_identity_io ii on ii.identity_io_id = sc.identity_io_id" +
            "             inner join bpm.bpm_code_category bcc on bcc.code_category_id = ii.type" +
            "             inner join bpm.bpm_model_versions mv on mv.model_version_id = ii.model_version_id" +
            "             inner join bpm.bpm_models m on m.model_id = mv.model_id" +
            "             inner join bpm.bpm_organs o on o.organ_id = m.organ_id" +
            "          where mv.last_version = true and m.status = 'ACTIVE' and ii.status = 'ACTIVE' and sc.status = 'ACTIVE' and  m.organ_id=:organId  "+//and (coalesce(:modelName, null) is null or m.name like concat('%', :modelName ,'%')) " +
            "           and bcc.code = 'OUTPUT' and bcc.category = 'IDENTITY_IO_TYPE' ) scOutput" +
            "         left join bpm.bpm_identity_io_sup_custs scInput on  scOutput.outputModel = scInput.internal_sup_cust  and scInput.status = 'ACTIVE'" +
            "         left join bpm.bpm_identity_io bii on bii.identity_io_id = scInput.identity_io_id and  bii.status = 'ACTIVE'" +
            "         left join bpm.bpm_code_category bcc on bcc.code_category_id = bii.type and bcc.code = 'INPUT' and bcc.category = 'IDENTITY_IO_TYPE' " +
            "         left join bpm.bpm_model_versions mv on mv.model_version_id = bii.model_version_id  and mv.last_version=true " +
            "         left join bpm.bpm_models m on m.model_id = mv.model_id and  m.status = 'ACTIVE'   where  scInput.sup_cust_id is null or bii.io_catalog_id != scOutput.io_catalog_id)s " , nativeQuery = true )
    Long getcountSIPOCInvalidModel( @Param("organId") Long organId);

    @Query(value = "select mv from ModelVersion mv  " +
            " where  " +
            "  exists (" +
            "  select  map from ModelApprover map inner join " +
            " map.modelMemberRole mmr inner join " +
            " mmr.modelMember mm inner join " +
            " mm.modelVersion mv1" +
            " where mv1.id=mv.id and " +
            " mm.status='ACTIVE' and mmr.status='ACTIVE'" +
            " and map.status='ACTIVE' and map.id=:modelApproverId) ")
    Optional<ModelVersion> findModelVersionOfModelApprover(Long modelApproverId);


    @Query(value = "select mv from ModelVersion mv  " +
            " where  " +
            "  exists (" +
            "  select  mer from ModelElementRefrence mer inner join " +
            " mer.modelVersion mv1 " +
            " where mv1.id=mv.id  " +
            " and mer.id=:modelElementReferenceId) ")
    Optional<ModelVersion> findModelVersionOfModelElementReferenceId(Long modelElementReferenceId);

    @Query(value = "select mv from ModelVersion mv  " +
            " where  " +
            "  exists (" +
            "  select  pme from ProcessCategoryModelElement pme inner join " +
            " pme.modelVersion mv1 " +
            " where mv1.id=mv.id  " +
            " and pme.id=:id) ")
    ModelElementRefrence findModelVersionOfProcessCategoryModelElement(Long id);

    @Query(value = "select mv from ModelVersion mv  " +
            " where  " +
            "  exists (" +
            "  select  mmr from ModelMemberRole mmr inner join " +
            " mmr.modelMember mm" +
            " inner join mm.modelVersion mv1 " +
            " where mv1.id=mv.id  " +
            " and mmr.id=:id and mmr.status='ACTIVE' and mm.status='ACTIVE' ) ")
    Optional<ModelVersion> findModelVersionOfModelMemberRole(Long id);
}
