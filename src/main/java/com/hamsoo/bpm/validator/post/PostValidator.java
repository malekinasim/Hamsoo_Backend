package com.hamsoo.bpm.validator.post;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.post.PostDto;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class PostValidator extends HamsooValidator {

    private UnitService unitService;
    private JobService jobService;
    private PostService postService;

    public PostValidator(UnitService unitService, JobService jobService, PostService postService) {
        this.unitService = unitService;
        this.jobService = jobService;
        this.postService = postService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PostDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        PostDto dto = (PostDto) target;

        if (dto.getName() == null) {
            errors.rejectValue("name", "", "error.post.nameIsNotValid");
        }
        if (dto.getCode() == null) {
            errors.rejectValue("code", "", "error.post.codeIsNotValid");
        }
        if (dto.getJobId() == null) {
            errors.rejectValue("jobId", "", "error.post.jobIdIsNotValid");
        }
        if (dto.getUnitId() == null) {
            errors.rejectValue("unitId", "", "error.post.unitIdIsNotValid");
        }
        if (dto.getId() == null) {
            Job job = null;
            try {
                job = jobService.findByIdOrElseThrow(dto.getJobId());
            } catch (BusinessException e) {
                errors.rejectValue("jobId", "", "error.post.jobIdIsNotValid");
            }
            Unit unit = null;
            try {
                unit = unitService.findByIdOrElseThrow(dto.getUnitId());
            } catch (BusinessException e) {
                errors.rejectValue("unitId", "", "error.post.unitIdIsNotValid");
            }

            if (!unit.getChart().getId().equals(job.getChart().getId()) ||
                    !unit.getChart().getOrgan().getId().equals(SecurityUtils.getCurrentOrganIdOrElseThrow())
            ) {
                errors.rejectValue("chartId", "", "error.post.chartIdIsNotValid");
            }


            Optional<Post> post = postService.findByCodeAndChartId(dto.getCode(), unit.getChart().getId());
            if (post.isPresent()) {
                errors.rejectValue("code", "", "error.post.postCodeIsDuplicate");
            }
        }

    }
}
