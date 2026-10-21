package com.hamsoo.bpm.repository.jpa.post;

import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends HamsooRepository<Post, Long> {
    @Query(value = "select p from Post p inner join p.unit u  " +
            " inner join u.chart ch inner join ch.organ o " +
            " where p.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " p.job.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " p.id=:id ")
    Optional<Post> findByIdAndOrganId(Long id, Long organId);

    @Query(value = "select p from Post p inner join p.unit u  " +
            " inner join u.chart ch inner join ch.organ o " +
            " where p.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " p.job.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " ch.id=:chartId and " +
            " p.code=:code")
    Optional<Post> findByCodeAndChartIdAndOrganId(String code, Long chartId, Long organId);

    @Query(value = "select p from Post p inner join p.unit u  " +
            " inner join u.chart ch inner join ch.organ o " +
            " where p.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " p.job.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " u.id=:unitId")
    List<Post> findByUnitIdAndOrganId(Long unitId, Long organId);

    @Query(value = "select p from Post p inner join p.unit u  " +
            " inner join u.chart ch " +
            " inner join ch.organ o" +
            " inner join p.job j" +
            " where p.status='ACTIVE' and" +
            " u.status='ACTIVE' and" +
            " j.status='ACTIVE' and" +
            " ch.status='ACTIVE' and" +
            " o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " j.id=:jobId")
    List<Post> findByJobIdAndOrganId(Long jobId, Long organId);
}
