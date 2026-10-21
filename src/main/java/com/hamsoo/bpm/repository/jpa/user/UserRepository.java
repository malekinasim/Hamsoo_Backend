package com.hamsoo.bpm.repository.jpa.user;

import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface UserRepository extends HamsooRepository<User, Long> {


    Page<User> findAll(Specification specification, Pageable pageable);


    Optional<User> findByNationalCode(String nationalCode);


    @Query(value = "select user from User user inner join user.bpmAccounts acc where upper(acc.username)=upper(:username)")
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "select distinct u from UserOrgan uo inner join uo.user u " +
            "inner join uo.organ o where u.id= :userId and o.id= :organId and uo.status='ACTIVE'")
    Optional<User> findByUserIdAndOrganId(@Param("userId") Long userId, @Param("organId") Long organId);


    @Query(value = "select  u from User u inner join u.userOrgans uo  inner join uo.organ o " +
            " where uo.aDefault= :aDefault and o.id= :organId and uo.status='ACTIVE'")
    Optional<User> findOrganOwnerUser(@Param("organId") Long organId, @Param("aDefault") Boolean aDefault);

    @Query(value = "select  u from User u " +
            " where u.email=:mail and u.mailVerifyCode=:verifyCode")
    Optional<User> findByMailAndVerfiyCode(String mail, String verifyCode);


    //TODO

    @Query(value = "select  u from User u  inner join u.userOrgans uo  inner join uo.organ o " +
            "left join ModelMember mm on u.id=mm.memberId  " +
            "and mm.status='ACTIVE' " +
            "and mm.modelVersion.id =:curModelVersion " +
            "left join mm.memberType mt  " +
            " where mt.code='USER' and o.id= :organId and uo.status='ACTIVE' and mm.id is null and (u.fullName like concat('%', :search ,'%')  or u.nationalCode like concat('%', :search ,'%') )")
    Page<User> listWithoutMemberRole(Pageable pageable, String search, Long curModelVersion, Long organId);

    //    @Query(value = "select  u from User u  left join u.userOrgans uo  inner join uo.organ o left join RoleMember rm on u.id=rm.user.id and rm.organ.id=o.id " +
//            " where o.id= :organId and uo.status='ACTIVE' and rm.status='ACTIVE' and rm.id is null ")
    @Query(value = "select  u from User u  left join u.userOrgans uo on u.id=uo.user.id and uo.organ.id=:organId and uo.status='ACTIVE' where uo.id is null and (u.fullName like concat('%', :search ,'%')  or u.nationalCode like concat('%', :search ,'%') ) ")
    Page<User> listWithoutRole(Pageable pageable, String search, Long organId);


    @Query(value = "select us from " +
            " PostMember postMem " +
            " inner join postMem.person p " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " inner  join o.userOrgans uo " +
            " inner  join uo.user us " +
            " inner  join uo.person p1 " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " pst.job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " p.status='ACTIVE' and" +
            " o.id= :organId and " +
            " p1.id=p.id and " +
            " pst.id=:postId")
    List<User> finaAllPostMember(Long organId, Long postId);

    @Query(value = "select us from " +
            " PostMember postMem " +
            " inner join postMem.person p " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join pst.job job" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " inner  join o.userOrgans uo " +
            "inner join uo.person pr " +
            " inner  join uo.user us " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " p.status='ACTIVE' and" +
            " o.id= :organId and " +
            " pr.id=p.id and " +
            " job.id=:jobId")
    List<User> finaAllJobMember(Long organId, Long jobId);

    @Query(value = "select us from " +
            " PostMember postMem " +
            " inner join postMem.person p " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join pst.job job" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " inner  join o.userOrgans uo " +
            "inner join uo.person pr " +
            " inner  join uo.user us " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " p.status='ACTIVE' and" +
            " o.id= :organId and " +
            " pr.id=p.id and " +
            " unt.id=:unitId")
    List<User> finaAllUnitMember(Long organId, Long unitId);

}
