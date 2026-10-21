package com.hamsoo.bpm.repository.jpa.userorgan;

import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(rollbackFor = Exception.class)
public interface UserOrganRepository extends HamsooRepository<UserOrgan, Long> {


    Page<UserOrgan> findAll(Specification specification, Pageable pageable);

    Optional<UserOrgan> findByIdAndStatus(Long Id, String status);


    Set<UserOrgan> findAllByUser(User u);

    @Query("SELECT uo FROM UserOrgan uo inner join uo.user u inner join uo.organ o " +
            " WHERE u.id= :userId and o.id= :organId " +
            " and uo.status=:status" +
            " and o.status=:status")
    UserOrgan findByUserAndOrgan(@Param("userId") Long userId, @Param("organId") Long organId, @Param("status") String status);


    @Query("SELECT uo FROM UserOrgan uo inner join uo.user u inner join uo.organ o " +
            " WHERE u.id= :userId and o.id= :organId " +
            " and uo.status=:status" +
            " and o.status=:status")
    List<UserOrgan> findAllByUserAndOrgan(@Param("userId") Long userId, @Param("organId") Long organId, @Param("status") String status);


    @Query("SELECT uo FROM UserOrgan uo inner join uo.user u inner join uo.organ o " +
            " WHERE  o.id= :organId " +
            " and uo.status=:status" +
            " and o.status=:status")
    List<UserOrgan> findALLByOrgan(@Param("organId") Long organId, @Param("status") String status);

    @Query("SELECT uo FROM UserOrgan uo inner join uo.person p inner join uo.user u inner join uo.organ o " +
            " WHERE  p.id= :personId " +
            " and uo.status=:status" +
            " and o.status=:status" +
            " and p.status=:status ")
    List<UserOrgan> findAllByPersonId(Long personId, String status);


    @Query("SELECT  u FROM RoleMember rm " +
            "inner join rm.user u " +
            "inner join rm.role r " +
            "inner join rm.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.user u1 " +
            " WHERE  o.id= :organId " +
            " and o.status=:status" +
            " and rm.status=:status" +
            " and uo.status=:status" +
            " and r.name=:roleName" +
            " and u.id=u1.id")
    List<User> findMemberOfOrganBySpecificRole(Long organId, String status, String roleName);


    @Query("SELECT  count(uo) FROM "+
            "UserOrgan uo " +
            "inner join uo.organ o " +
            " WHERE  o.id= :organId " +
            " and uo.status='ACTIVE'" +
            " and o.status='ACTIVE'" )
    Integer CountUserOrganByOrganIdByLock(Long organId);

}
