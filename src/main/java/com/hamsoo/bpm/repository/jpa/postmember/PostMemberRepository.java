package com.hamsoo.bpm.repository.jpa.postmember;

import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostMemberRepository extends HamsooRepository<PostMember, Long> {


    @Query(value = "select postMem from PostMember postMem " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " pst.job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " postMem.person.status='ACTIVE' and" +
            " o.id=:organId and" +
            " postMem.id=:id")
    Optional<PostMember> findPostMemberByIdAndOrganId(Long id, Long organId);


    @Query(value = "select postMem from PostMember postMem " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " pst.job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " postMem.person.status='ACTIVE' and" +
            " o.id=:organId and" +
            " postMem.person.id= :personId and" +
            " pst.id= :postId and" +
            " chrt.id= :chartId")
    Optional<PostMember> findPostMemberByPersonIdAndPostIdAndOrganIdAndChartId(Long personId, Long postId, Long chartId, Long organId);

    @Query(value = "select postMem from PostMember postMem " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " pst.job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " postMem.person.status='ACTIVE' and" +
            " o.id=:organId and" +
            " postMem.person.id= :personId")
    List<PostMember> findAllPostMemberByPersonIdAndOrganId(Long personId, Long organId);

    @Query(value = "select postMem from PostMember postMem " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " pst.job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            " postMem.person.id= :personId")
    List<PostMember> findAllByPersonIdAndAllOrgan(Long personId);

    @Query(value = "select postMem from PostMember postMem " +
            " inner join postMem.post pst " +
            " inner join pst.unit unt" +
            " inner join unt.chart chrt" +
            " inner  join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " chrt.status='ACTIVE' and" +
            " unt.status='ACTIVE' and" +
            " pst.status='ACTIVE' and" +
            " pst.job.status='ACTIVE' and" +
            " postMem.status='ACTIVE' and" +
            "  pst.id= :postId and" +
            " chrt.id=:chartId and" +
            " o.id=:organId")
    List<PostMember> findAllByPostIdAndOrganId(Long postId, Long chartId, Long organId);
}
