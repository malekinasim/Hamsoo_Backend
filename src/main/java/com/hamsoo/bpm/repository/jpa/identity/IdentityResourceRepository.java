package com.hamsoo.bpm.repository.jpa.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityResourceDto;
import com.hamsoo.bpm.model.entity.identity.IdentityResource;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IdentityResourceRepository extends HamsooRepository<IdentityResource, Long> {

    @Query("select ir from IdentityResource ir inner join ir.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where ir.id = :id and o.id = :organId and ir.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    Optional<IdentityResource> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query("select ir from IdentityResource ir inner join ir.modelVersion mv inner join mv.model m " +
            "inner join m.organ o  " +
            "where o.id = :organId and mv.id = :modelVersionId and ir.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    List<IdentityResource> findActiveByModelVersionId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId);

    @Query("select new com.hamsoo.bpm.model.dto.identity.IdentityResourceDto(ir.resourceCatalog.name,ir.id,'resource',mv.id,ir.resourceCatalog.id,ir.resourceCatalog.resourceType.id) from IdentityResource ir inner join ir.modelVersion mv inner join mv.model m " +
            "inner join m.organ o  " +
            "where o.id = :organId and mv.id = :modelVersionId and ir.resourceCatalog.resourceType.id = :resourceTypeId  and ir.status = 'ACTIVE' and ir.resourceCatalog.status = 'ACTIVE' and ir.resourceCatalog.resourceType.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    List<IdentityResourceDto> findActiveByModelVersionIdResourceTypeId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId, @Param("resourceTypeId") Long resourceTypeId);


    @Query(value = "select distinct new com.hamsoo.bpm.model.dto.identity.IdentityResourceDto(post.name,post.id,'post',mdlver.id,post.id,post.id) from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType" +
            " inner join modelElmentRef.modelVersion mdlver" +
            " inner join mdlver.model model " +
            " inner join model.organ org1" +
            " inner join Post post on post.id=modelElmentRef.referenceId " +
            " inner join post.unit unit inner join unit.chart chrt" +
            " inner join chrt.organ org" +
            " where org.id=:organId" +
            " and org1.id=:organId" +
            " and model.status='ACTIVE'" +
            " and post.status='ACTIVE' " +
            " and refType.code=:referenceType" +
            " and mdlver.lastVersion = true and mdlver.id = :modelversionId ")
    List<IdentityResourceDto> findPostRefByReferenceType(@Param("organId") Long organId, @Param("modelversionId") Long modelversionId, @Param("referenceType") String referenceType);

    @Query(value = "select distinct new com.hamsoo.bpm.model.dto.identity.IdentityResourceDto(unt.name,unt.id,'unit',mdlver.id,unt.id,unt.id) from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType" +
            " inner join modelElmentRef.modelVersion mdlver" +
            " inner join mdlver.model model " +
            " inner join model.organ org1" +
            " inner join Unit unt on unt.id=modelElmentRef.referenceId" +
            " inner join unt.chart chrt" +
            " inner join chrt.organ org" +
            " where org.id=:organId" +
            " and org1.id=:organId" +
            " and org.status='ACTIVE'" +
            " and org1.status='ACTIVE'" +
            " and model.status='ACTIVE'" +
            " and chrt.status='ACTIVE' " +
            " and unt.status='ACTIVE' " +
            " and refType.code=:referenceType" +
            " and mdlver.lastVersion=true and mdlver.id = :modelversionId ")
    List<IdentityResourceDto> findUnitRefByReferenceType(@Param("organId") Long organId, @Param("modelversionId") Long modelversionId, @Param("referenceType") String referenceType);

    @Query(value = " select distinct new com.hamsoo.bpm.model.dto.identity.IdentityResourceDto(job.name,job.id,'job',mdlver.id,job.id,job.id)  from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType " +
            " inner join modelElmentRef.modelVersion mdlver " +
            " inner join mdlver.model model " +
            " inner join model.organ org1 " +
            " inner join Job job on job.id=modelElmentRef.referenceId " +
            " inner join job.chart chrt inner join chrt.organ org " +
            " where org.id=:organId  " +
            " and org1.id=:organId " +
            " and org1.status='ACTIVE' " +
            " and org.status='ACTIVE' " +
            " and model.status='ACTIVE' " +
            " and chrt.status='ACTIVE' " +
            " and refType.code=:referenceType" +
            " and mdlver.lastVersion=true and mdlver.id = :modelversionId ")
    List<IdentityResourceDto> findJobRefByReferenceType(@Param("organId") Long organId, @Param("modelversionId") Long modelversionId,@Param("referenceType") String referenceType);

    @Query(value = " select distinct new com.hamsoo.bpm.model.dto.identity.IdentityResourceDto(user.fullName,user.id,'user',mdlver.id,user.id,user.id) from ModelElementRefrence modelElmentRef " +
            " inner join modelElmentRef.referenceType refType " +
            " inner join modelElmentRef.modelVersion mdlver " +
            " inner join mdlver.model model " +
            " inner join model.organ org1 " +
            " inner join UserOrgan userorg on org1.id=userorg.organ.id inner join userorg.user user on user.id=modelElmentRef.referenceId   " +
            " where userorg.organ.id=:organId" +
            " and org1.id=:organId " +
            " and org1.status='ACTIVE' " +
            " and model.status='ACTIVE' " +
            " and refType.code=:referenceType" +
            " and mdlver.lastVersion=true and mdlver.id = :modelversionId ")
    List<IdentityResourceDto> findUserRefByReferenceType(@Param("organId") Long organId, @Param("modelversionId") Long modelversionId,@Param("referenceType") String referenceType);

}
