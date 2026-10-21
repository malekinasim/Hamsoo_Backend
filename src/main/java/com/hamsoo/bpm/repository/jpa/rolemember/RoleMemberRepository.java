package com.hamsoo.bpm.repository.jpa.rolemember;

import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface RoleMemberRepository extends HamsooRepository<RoleMember, Long> {

    Page<RoleMember> findAll(Specification specification, Pageable pageable);

    @Query(value = "select roleMem " +
            " from RoleMember roleMem " +
            " inner join roleMem.user u" +
            "  where  u.id=:userId " +
            "and roleMem.organ is null " +
            "and roleMem.status='ACTIVE' ")
    List<RoleMember> findAllByUser(Long userId);

    @Query(value = "select roleMem from RoleMember roleMem" +
            " inner join roleMem.role r " +
            " inner join roleMem.user u " +
            " inner join roleMem.organ o " +
            "  where  u.id=:userId " +
            " and o.id=:organId" +
            " and r.roleType=:rolType" +
            " and roleMem.status='ACTIVE' ")
    List<RoleMember> findAllByUserAndOrgan(Long userId, Long organId, String rolType);

    @Query(value = " select roleMem " +
            " from RoleMember roleMem " +
            " inner join roleMem.organ o " +
            " inner join roleMem.role r " +
            "  where  o.id=:organId " +
            " and roleMem.status='ACTIVE'" +
            " and r.name <> 'WORKSPACE_USER' ")
    List<RoleMember> findAllByOrgan(Long organId);

    @Query(value = "select roleMem from RoleMember roleMem " +
            "inner join roleMem.role r " +
            "inner join roleMem.organ o " +
            "  where  o.id=:organId " +
            " and roleMem.status='ACTIVE'" +
            " and r.name=:roleName and r.status='ACTIVE' ")
    List<RoleMember> findAllByOrganAndRoleName(Long organId, String roleName);


    @Query(value = "select roleMem from RoleMember roleMem " +
            "inner join roleMem.role r" +
            " inner join roleMem.user u" +
            "  where  u.id=:userId  and roleMem.status='ACTIVE'" +
            " and r.roleType=:roleType and r.status='ACTIVE' ")
    List<RoleMember> findAllByUserAndRoleType(Long userId, String roleType);


}