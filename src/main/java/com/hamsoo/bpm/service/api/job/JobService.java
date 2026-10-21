package com.hamsoo.bpm.service.api.job;

import com.hamsoo.bpm.model.dto.job.JobDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.service.api.base.BaseService;
import com.hamsoo.bpm.repository.jpa.job.JobRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface JobService extends BaseService<Job, Long, JobRepository> {

    Job findByIdOrElseThrow(Long id);

    JobDto mapToDto(Job job);


    Job create(JobDto jobDto);

    void passive(Long id);

    Job edit(JobDto jobDto);

    SearchDto list(JobDto jobDto, int page, int size, String orderBy, Sort.Direction direction);

    Optional<Job> findByCodeAndChartId(String code, Long id);

    List<Job> findAllByChartId(Long id);
}
