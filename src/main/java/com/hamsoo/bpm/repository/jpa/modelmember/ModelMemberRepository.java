package com.hamsoo.bpm.repository.jpa.modelmember;

import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelMemberRepository extends HamsooRepository<ModelMember, Long> {

    @Query(value = "select mm from ModelMember mm inner join mm.modelVersion mv inner join mv.model m inner join m.organ o " +
            "where mm.id= :id and mm.status =:status and o.id= :organId and m.status = 'ACTIVE' ")
    Optional<ModelMember> findByIdAndStatusAndOrganId(@Param("id") Long id, @Param("status") String status, @Param("organId") Long organId);


    @Query(value = "select user from User user where " +
            "user.id in (select u.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join User u on u.id=mm.memberId " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and mt.code='USER') or " +
            "user.id in (select u.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join Post p on p.id=mm.memberId " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and uo.status='ACTIVE' " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and p.status = 'ACTIVE' " +
            " and pstmem.status = 'ACTIVE' " +
            " and pr.status = 'ACTIVE' " +
            " and pr.nationalCode =u.nationalCode " +
            " and mt.code='POST') or " +
            "user.id in (select u.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join Unit un on un.id=mm.memberId " +
            "inner join un.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and uo.status='ACTIVE' " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and un.status = 'ACTIVE' " +
            " and p.status = 'ACTIVE' " +
            " and pstmem.status = 'ACTIVE' " +
            " and pr.status = 'ACTIVE' " +
            " and pr.nationalCode =u.nationalCode " +
            " and mt.code='UNIT') or " +
            "user.id in (select u.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join Job jb on jb.id=mm.memberId " +
            "inner join jb.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and uo.status='ACTIVE' " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and jb.status = 'ACTIVE' " +
            " and p.status = 'ACTIVE' " +
            " and pstmem.status = 'ACTIVE' " +
            " and pr.status = 'ACTIVE' " +
            " and pr.nationalCode =u.nationalCode " +
            " and mt.code='JOB') "
    )
    List<User> findAllModelMemberByModelVersionId(@Param("modelVersionId") Long modelVersionId, @Param("organId") Long organId);

    @Query(value = "select modelMem from ModelMember modelMem where " +
            "modelMem.id in (select mm.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join User u on u.id=mm.memberId " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and u.id= :userId  " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and mt.code='USER') or " +
            "modelMem.id in (select mm.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join Post p on p.id=mm.memberId " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and u.id= :userId  " +
            " and uo.status='ACTIVE' " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and p.status = 'ACTIVE' " +
            " and pstmem.status = 'ACTIVE' " +
            " and pr.status = 'ACTIVE' " +
            " and pr.nationalCode =u.nationalCode " +
            " and mt.code='POST') or " +
            "modelMem.id in (select mm.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join Unit un on un.id=mm.memberId " +
            "inner join un.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and u.id= :userId  " +
            " and uo.status='ACTIVE' " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and un.status = 'ACTIVE' " +
            " and p.status = 'ACTIVE' " +
            " and pstmem.status = 'ACTIVE' " +
            " and pr.status = 'ACTIVE' " +
            " and pr.nationalCode =u.nationalCode " +
            " and mt.code='UNIT') or " +
            "modelMem.id in (select mm.id from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join Job jb on jb.id=mm.memberId " +
            "inner join jb.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId" +
            " and o.id = :organId " +
            " and u.id= :userId  " +
            " and uo.status='ACTIVE' " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE'" +
            " and jb.status = 'ACTIVE' " +
            " and p.status = 'ACTIVE' " +
            " and pstmem.status = 'ACTIVE' " +
            " and pr.status = 'ACTIVE' " +
            " and pr.nationalCode =u.nationalCode " +
            " and mt.code='JOB') "
    )
    List<ModelMember> findAllUserModelMemberByModelVersionIdAndUserId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "update bpm.bpm_model_members set model_id = :newModelVersionId " +
            "where model_id = :oldModelVersionId ", nativeQuery = true)
    void updateModelVersionId(@Param("newModelVersionId") Long newModelVersionId, @Param("oldModelVersionId") Long oldModelVersionId);

    @Query(value = "select mm from ModelMemberRole mmr inner join  " +
            "mmr.modelMember mm inner join mm.modelVersion mv " +
            "inner join mv.model m inner join mmr.modelRole mr " +
            "where mv.id= :modelVersionId and mm.status = 'ACTIVE' " +
            "and mr.code= :modelRoleCode and m.status = 'ACTIVE' ")
    List<ModelMember> findByModelVersionIdAndModelRoleCode(@Param("modelVersionId") Long modelVersionId, @Param("modelRoleCode") String modelRoleCode);

    @Query(value = "select modelmem from ModelMember modelmem where " +
            " modelmem.id in " +
            "(select mm.id from ModelMemberRole mmr " +
            "inner join  mmr.modelMember mm " +
            "inner join  mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join mmr.modelRole mr " +
            "inner join User u  on u.id=mm.memberId and mt.code='USER' " +
            "where m.id= :modelId " +
            "and mv.lastVersion = true " +
            "and mr.code= :modelRoleCode " +
            "and u.id= :userId " +
            "and mm.status = 'ACTIVE' " +
            "and m.status = 'ACTIVE' " +
            "and mr.status = 'ACTIVE') " +
            "or modelmem.id in " +
            "(select mm.id from ModelMemberRole mmr " +
            "inner join  mmr.modelMember mm " +
            "inner join  mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join mmr.modelRole mr " +
            "inner join Post p  on p.id=mm.memberId and mt.code='POST' " +
            "inner join p.postMembers pmem " +
            "inner join pmem.person pr " +
            "where m.id= :modelId " +
            "and mv.lastVersion = true " +
            "and pr.nationalCode = u.nationalCode " +
            "and mr.code= :modelRoleCode " +
            "and u.id= :userId " +
            "and mm.status = 'ACTIVE' " +
            "and p.status = 'ACTIVE' " +
            "and pmem.status = 'ACTIVE' " +
            "and pr.status = 'ACTIVE' " +
            "and o.status = 'ACTIVE' " +
            "and uo.status = 'ACTIVE' " +
            "and m.status = 'ACTIVE' " +
            "and mr.status = 'ACTIVE') " +
            "or modelmem.id in " +
            "(select mm.id from ModelMemberRole mmr " +
            "inner join  mmr.modelMember mm " +
            "inner join  mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join mmr.modelRole mr " +
            "inner join Unit un  on un.id=mm.memberId and mt.code='UNIT' " +
            "inner join un.posts p " +
            "inner join p.postMembers pmem " +
            "inner join pmem.person pr " +
            "where m.id= :modelId " +
            "and mv.lastVersion = true " +
            "and pr.nationalCode = u.nationalCode " +
            "and mr.code= :modelRoleCode " +
            "and u.id= :userId " +
            "and mm.status = 'ACTIVE' " +
            "and o.status = 'ACTIVE' " +
            "and un.status = 'ACTIVE' " +
            "and p.status = 'ACTIVE' " +
            "and pmem.status = 'ACTIVE' " +
            "and pr.status = 'ACTIVE' " +
            "and uo.status = 'ACTIVE' " +
            "and m.status = 'ACTIVE' " +
            "and mr.status = 'ACTIVE') " +
            "or modelmem.id in " +
            "(select mm.id from ModelMemberRole mmr " +
            "inner join  mmr.modelMember mm " +
            "inner join  mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u " +
            "inner join mmr.modelRole mr " +
            "inner join Job jb  on jb.id=mm.memberId and mt.code='UNIT' " +
            "inner join jb.posts p " +
            "inner join p.postMembers pmem " +
            "inner join pmem.person pr " +
            "where m.id= :modelId " +
            "and mv.lastVersion = true " +
            "and pr.nationalCode = u.nationalCode " +
            "and mr.code= :modelRoleCode " +
            "and u.id= :userId " +
            "and jb.status = 'ACTIVE' " +
            "and p.status = 'ACTIVE' " +
            "and pmem.status = 'ACTIVE' " +
            "and pr.status = 'ACTIVE' " +
            "and mm.status = 'ACTIVE' " +
            "and o.status = 'ACTIVE' " +
            "and uo.status = 'ACTIVE' " +
            "and m.status = 'ACTIVE' " +
            "and mr.status = 'ACTIVE') "
    )
    List<ModelMember> findByModelIdAndModelRoleCodeAndUserId(@Param("modelId") Long modelId, @Param("modelRoleCode") String modelRoleCode, @Param("userId") Long userId);

    @Query(value = "select mm from ModelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "where m.id= :modelId " +
            "and mv.lastVersion = true " +
            "and mm.id= :modelId " +
            "and mt.id= :memberTypeId " +
            "and mm.memberId= :memberId " +
            "and mm.status = 'ACTIVE' " +
            "and m.status = 'ACTIVE' ")
    List<ModelMember> findByModelIdAndMemberIdAndMemberType(@Param("modelId") Long modelId, @Param("memberId") Long memberId, @Param("memberTypeId") Long memberTypeId);

    @Query(value = "select count(distinct model.id) as count from Model model" +
            " where (model.id in " +
            " (select m.id from  ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            " inner join User u  on u.id = mm.memberId" +
            " where u.id = :userId " +
            " and mm.status= 'ACTIVE'" +
            " and m.status = 'ACTIVE' " +
            " and mv.lastVersion=true " +
            " and o.id= :organId " +
            " and mm.memberType.code='USER' ) or " +
            " model.id in" +
            "(select m.id from  ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Post p on p.id = mm.memberId " +
            "inner join p.postMembers pstMem " +
            "inner join pstMem.person pr " +
            "where u.id = :userId and " +
            "pr.id=pr1.id and " +
            "mm.status= 'ACTIVE' and " +
            "m.status = 'ACTIVE' and " +
            "p.status = 'ACTIVE' and " +
            "pstMem.status = 'ACTIVE' and " +
            "pr.status = 'ACTIVE' and " +
            "mv.lastVersion=true " +
            "and o.id= :organId  " +
            "and mm.memberType.code='POST' ) or " +
            " model.id in " +
            " (select m.id from  ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Unit un  on un.id = mm.memberId " +
            "inner join un.posts p  " +
            "inner join p.postMembers pstMem " +
            "inner join pstMem.person pr " +
            " where u.id = :userId and " +
            "pr.id=pr1.id and " +
            "mm.status= 'ACTIVE' and " +
            "m.status = 'ACTIVE' and " +
            "un.status = 'ACTIVE' and " +
            "p.status = 'ACTIVE' and " +
            "pstMem.status = 'ACTIVE' and " +
            "pr.status = 'ACTIVE' and " +
            "mv.lastVersion=true " +
            "and o.id= :organId  " +
            " and mm.memberType.code='UNIT' ) or " +
            " model.id in " +
            " (select m.id from  ModelMember mm " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Job jb  on jb.id = mm.memberId " +
            "inner join jb.posts p  " +
            "inner join p.postMembers pstMem " +
            "inner join pstMem.person pr " +
            " where u.id = :userId and " +
            "pr.id=pr1.id and " +
            "mm.status= 'ACTIVE' and " +
            "m.status = 'ACTIVE' and " +
            "jb.status = 'ACTIVE' and " +
            "p.status = 'ACTIVE' and " +
            "pstMem.status = 'ACTIVE' and " +
            "pr.status = 'ACTIVE' and " +
            "mv.lastVersion=true " +
            "and o.id= :organId " +
            "and mm.memberType.code='JOB' ) " +
            ") "
    )
    int getCountAllLastVersionByUserId(@Param("userId") Long userId, @Param("organId") Long organId);


}
