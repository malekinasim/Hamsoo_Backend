package com.hamsoo.bpm.service.impl.job;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.job.JobDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.job.Job_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.job.JobRepository;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final ChartService chartService;
    private final PostService postService;
    private final UserService userService;

    public JobServiceImpl(JobRepository jobRepository,
                          @Lazy ChartService chartService,
                          @Lazy PostService postService, UserService userService) {
        this.jobRepository = jobRepository;
        this.chartService = chartService;
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public JobRepository getRepository() {
        return jobRepository;
    }

    @Override
    public Job findByIdOrElseThrow(Long id) {
        return this.getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(
                () -> new BusinessException("error.job.idIsNotValid")
        );
    }

    @Override
    public JobDto mapToDto(Job job) {

        JobDto jobDto = new JobDto();
        jobDto.setId(job.getId());
        jobDto.setCode(job.getCode());
        jobDto.setName(job.getName());
        jobDto.setDescription(job.getDescription());
        jobDto.setChartId(job.getChart().getId());
        jobDto.setUserDtoList(userService.findAllJobMember(job.getId()).stream().map(u -> userService.mapToDto(u)).toList());
        return jobDto;
    }

    @Override
    public Job create(JobDto jobDto) {
        Job job = new Job();
        job.setCode(jobDto.getCode());
        job.setName(jobDto.getName());
        job.setDescription(jobDto.getDescription());
        job.setValidFrom(LocalDateTime.now());
        job.setStatus(Status.ACTIVE.name());
        job.setChart(chartService.findByIdOrElseThrow(jobDto.getChartId()));
        return this.save(job);

    }

    @Override
    public void passive(Long id) {

        Job job = this.findByIdOrElseThrow(id);
        List<Post> posts = postService.findAllByJobId(job.getId());
        if (!posts.isEmpty())
            throw new BusinessException("error.job.canNotDeleteByPost");


        job.setStatus(Status.PASSIVE.name());
        job.setValidTo(LocalDateTime.now());
        this.save(job);
    }

    @Override
    public Job edit(JobDto jobDto) {
        Job job = this.findByIdOrElseThrow(jobDto.getId());
        job.setName(jobDto.getName());
        job.setDescription(jobDto.getDescription());
        return this.save(job);
    }

    @Override
    public SearchDto list(JobDto jobDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Job>> optionalSpecification = createSpecification(jobDto);
        Page<Job> jobPage = findAll(optionalSpecification.get(), pageable);
        List<JobDto> content = jobPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public Optional<Job> findByCodeAndChartId(String code, Long chartId) {
        return this.getRepository().findByCodeAndChartIdAndOrganId(code, chartId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public List<Job> findAllByChartId(Long chartId) {
        return this.getRepository().findByChartIdAndOrganId(chartId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    private Optional<Specification<Job>> createSpecification(JobDto jobDto) {
        List<Specification<Job>> specificationList = new ArrayList<>();
        Specification<Job> organEq = (root, query, criteriaBuilder) -> {
            Join<Job, Chart> chartnJoin = root.join(Job_.CHART);
            Join<Chart, Organ> organJoin = chartnJoin.join(Chart_.ORGAN);
            Predicate[] predicates = new Predicate[5];

            predicates[0] = criteriaBuilder.equal(root.get(Chart_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[3] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            predicates[4] = criteriaBuilder.equal(chartnJoin.get(Chart_.ID), jobDto.getChartId());


            if (jobDto.getSearchWord() != null) {
                List<Predicate> predicatesOr = new ArrayList<>();
                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Job_.NAME)),
                        "%" + jobDto.getSearchWord().toLowerCase() + "%"));

                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Job_.CODE)),
                        "%" + jobDto.getSearchWord().toLowerCase() + "%"));
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.or(predicatesOr.toArray(new Predicate[0])));

            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }


}
