package com.hamsoo.bpm.repository.jpa.model;

import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelRepository extends HamsooRepository<Model, Long> {

    @Query(value = "select m from Model m inner join m.organ o " +
            "where m.id= :modelId and o.id= :organId and m.status = 'ACTIVE' ")
    Optional<Model> findByIdAndOrganId(@Param("modelId") Long modelId, @Param("organId") Long organId);

    @Query(value = "select m from Model m inner join m.organ o " +
            "where m.code= :code and o.id= :organId and m.status = 'ACTIVE' ")
    Optional<Model> findByCodeAndOrganId(@Param("code") String code, @Param("organId") Long organId);

    @Query(value = "select m from Model m inner join m.organ o inner join m.modelType mt " +
            "where mt.code= :modelTypeCode and o.id= :organId and m.status = 'ACTIVE' ")
    Optional<Model> findByModelTypeCodeAndOrganId(@Param("modelTypeCode") String modelTypeCode, @Param("organId") Long organId);

    @Query("select  m from Model m  where " +
            "(m.id in" +
            " (select  m.id from  " +
            "ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join mm.memberType mt " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            " where mv.lastVersion = true and " +
            "mm.status= 'ACTIVE' and " +
            "m.status='ACTIVE' and " +
            "u.id=:ownerUserId and " +
            "o.id= :organId)  or " +

            "m.id in (select m.id from  " +
            "ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join o.charts chrt " +
            "inner join chrt.units unt " +
            "inner join unt.posts pst " +
            "inner join pst.postMembers pstmem " +
            "inner join pstmem.person p " +
            "where mv.lastVersion = true and " +
            "mm.status= 'ACTIVE' and " +
            "m.status='ACTIVE' and " +
            "pstmem.status='ACTIVE' and " +
            "pst.status='ACTIVE' and " +
            "p.status='ACTIVE' and " +
            "unt.status='ACTIVE' and " +
            "chrt.status='ACTIVE' and " +
            "pst.id=mm.memberId and " +
            "u.id=:ownerUserId and " +
            "o.id= :organId and " +
            "mt.code='POST' ) or " +

            "m.id in (select m.id from  " +
            "ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join mm.memberType mt " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join o.charts chrt " +
            "inner join chrt.jobs job " +
            "inner join job.posts pst " +
            "inner join pst.postMembers pstmem " +
            "inner join pstmem.person p " +
            "where mv.lastVersion = true and " +
            "mm.status= 'ACTIVE' and " +
            "m.status='ACTIVE' and " +
            "pstmem.status='ACTIVE' and " +
            "pst.status='ACTIVE' and " +
            "job.status='ACTIVE' and " +
            "p.status='ACTIVE' and " +
            "chrt.status='ACTIVE' and " +
            "job.id=mm.memberId and " +
            "u.id=:ownerUserId and " +
            "o.id= :organId and " +
            "mt.code='JOB' )  or "+

            "m.id in (select m.id from  " +
            "ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join mm.memberType mt " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join o.charts chrt " +
            "inner join chrt.units unt " +
            "inner join unt.posts pst " +
            "inner join pst.postMembers pstmem " +
            "inner join pstmem.person p " +
            "where mv.lastVersion = true and " +
            "mm.status= 'ACTIVE' and " +
            "m.status='ACTIVE' and " +
            "pstmem.status='ACTIVE' and " +
            "pst.status='ACTIVE' and " +
            "p.status='ACTIVE' and " +
            "unt.status='ACTIVE' and " +
            "chrt.status='ACTIVE' and " +
            "unt.id=mm.memberId and " +
            "u.id=:ownerUserId and " +
            "o.id= :organId and " +
            "mt.code='UNIT' ) "+
            ") and (:modelName is null or m.name like %:modelName% )" )
    Page<Model> listSingleUserModels(Pageable pageable, Long ownerUserId, Long organId, String modelName);


    @Query(value = "select count(m) from Model m inner join m.organ o  " +
            "where  o.id= :organId and m.status = 'ACTIVE' ")
    Integer getOrganModelCount(Long organId);
}
