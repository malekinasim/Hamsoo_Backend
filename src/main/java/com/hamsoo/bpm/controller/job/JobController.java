package com.hamsoo.bpm.controller.job;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.job.JobDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.validator.job.JobValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/")
public class JobController {
    private final JobService jobService;
    private final JobValidator jobValidator;

    public JobController(JobService jobService, JobValidator jobValidator) {
        this.jobService = jobService;
        this.jobValidator = jobValidator;
    }

    @InitBinder("jobDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(jobValidator);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Job job = jobService.findByIdOrElseThrow(id);
        JobDto jobDto = jobService.mapToDto(job);
        return new ResponseEntity<>(jobDto, HttpStatus.OK);
    }

    @PostMapping("/job")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_JOB_MNG)")
    public ResponseEntity<?> create(@Valid @RequestBody JobDto jobDto) {
        Job job = jobService.create(jobDto);
        jobDto = jobService.mapToDto(job);
        return new ResponseEntity<>(jobDto, HttpStatus.OK);
    }

    @PutMapping("/job")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> edit(@Valid @RequestBody JobDto jobDto) {
        Job job = jobService.edit(jobDto);
        jobDto = jobService.mapToDto(job);
        return new ResponseEntity<>(jobDto, HttpStatus.OK);
    }


    @DeleteMapping("/job/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        jobService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/job/page")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_JOB_MNG)")
    public ResponseEntity<?> list(@RequestBody JobDto jobDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = jobService.list(jobDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/job/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_JOB_MNG)")
    public ResponseEntity<?> list(@RequestBody JobDto jobDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = jobService.list(jobDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
