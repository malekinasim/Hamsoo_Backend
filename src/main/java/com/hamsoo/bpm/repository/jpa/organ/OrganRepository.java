package com.hamsoo.bpm.repository.jpa.organ;

import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface OrganRepository extends HamsooRepository<Organ, Long> {
    @Query("select distinct o from Organ o where " +
            " o.name=:name ")
    Optional<Organ> findByName(String name);

    @Query("select distinct o from Organ o inner join o.userOrgans uo  inner join uo.user u where uo.aDefault= :aDefault " +
            " and u.id= :userId and uo.status='ACTIVE' and o.status='ACTIVE' ")
    Optional<Organ> findUserDefaultOrgan(@Param("userId") Long userId, @Param("aDefault") Boolean aDefault);

    @Query("select distinct o from Organ o inner join o.userOrgans uo  inner join uo.user u where  " +
            "  u.id= :userId and uo.status='ACTIVE' and  o.id=:organId and o.status='ACTIVE'")
    Optional<Organ> findByIdAndUserId(Long userId, Long organId);


    @Query("select distinct o from Organ o inner join o.userOrgans uo  inner join uo.user u where " +
            " u.id= :userId and uo.status='ACTIVE' and o.status='ACTIVE' ")
    List<Organ> findUserOrgans(Long userId);



//    @Query(value = "select COALESCE(COALESCE(sum(xmlFile.size),0)+COALESCE(sum(imageFile.size),0),0) from ModelVersion mv " +
//            "inner join mv.model m " +
//            "inner join m.organ o " +
//            "inner join mv.xml xmlFile " +
//            "inner join mv.image imageFile " +
//            "where  o.id= :organId and m.status = 'ACTIVE'")
//    BigInteger getOrganModelBpmCapacity(Long organId);






}