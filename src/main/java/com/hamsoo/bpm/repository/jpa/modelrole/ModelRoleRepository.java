package com.hamsoo.bpm.repository.jpa.modelrole;

import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelRoleRepository extends HamsooRepository<ModelRole, Long> {

    Optional<ModelRole> findByIdAndStatus(Long id, String status);


    Optional<ModelRole> findByCodeAndStatus(String code, String status);

    @Query(value = "select mr from ModelRole mr where mr.status = :status ")
    List<ModelRole> getByStatus(@Param("status") String status, Pageable pageable);

    @Query(value = "select distinct mr " +
            " from ModelMemberRole modelMemr  " +
            " inner join  modelMemr.modelRole mr  " +
            "where " +
            "modelMemr.id in " +
            " (select mmr.id from " +
            "ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "where  mm.status =:status " +
            "and u.id=mm.memberId " +
            "and mt.code='USER'" +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId ) or " +
            "modelMemr.id in " +
            " (select mmr.id from " +
            "ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Post p on p.id=mm.memberId and mt.code='POST' " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and p.status= :status " +
            "and pstmem.status= :status " +
            "and pr.status= :status " +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId ) or " +
            " modelMemr.id in " +
            " (select mmr.id from " +
            "ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Unit un on un.id=mm.memberId and mt.code='UNIT' " +
            "inner join un.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and un.status= :status " +
            "and p.status= :status " +
            "and pstmem.status= :status " +
            "and pr.status= :status " +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId ) or " +
            " modelMemr.id in " +
            " (select mmr.id from " +
            "ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Job jb on jb.id=mm.memberId and mt.code='JOB' " +
            "inner join jb.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and jb.status= :status " +
            "and p.status= :status " +
            "and pstmem.status= :status " +
            "and pr.status= :status " +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId ) "
    )
    List<ModelRole> findAllUserModelRoles(Long userId, Long organId, String status);

}
