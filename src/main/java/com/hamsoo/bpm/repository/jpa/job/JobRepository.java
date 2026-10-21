package com.hamsoo.bpm.repository.jpa.job;

import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends HamsooRepository<Job, Long> {

    @Query(value = "select job from Job job inner join job.chart chrt inner join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " chrt.status='ACTIVE' and" +
            " job.status='ACTIVE'  and " +
            " job.id=:id")
    Optional<Job> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query(value = "select job from Job job inner join job.chart chrt inner join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " chrt.status='ACTIVE' and" +
            " job.status='ACTIVE' and" +
            " job.code=:code and" +
            " chrt.id=:chartId")
    Optional<Job> findByCodeAndChartIdAndOrganId(String code, Long chartId, Long organId);

    @Query(value = "select job from Job job " +
            " inner join job.chart chrt" +
            " inner join chrt.organ o " +
            " where o.status='ACTIVE' and" +
            " o.id=:organId and" +
            " chrt.status='ACTIVE' and" +
            " job.status='ACTIVE' and" +
            " chrt.id=:chartId")
    List<Job> findByChartIdAndOrganId(Long chartId, Long organId);
}
