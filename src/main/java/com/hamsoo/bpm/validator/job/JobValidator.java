package com.hamsoo.bpm.validator.job;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.job.JobDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class JobValidator extends HamsooValidator {
    private ChartService chartService;
    private JobService jobService;

    public JobValidator(ChartService chartService, JobService jobService) {
        this.chartService = chartService;
        this.jobService = jobService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(JobDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        JobDto dto = (JobDto) target;


        if (dto.getName() == null) {
            errors.rejectValue("name", "", "error.job.nameIsNotValid");
        }
        if (dto.getCode() == null) {
            errors.rejectValue("code", "", "error.job.codeIsNotValid");
        }
        if (dto.getChartId() == null) {
            errors.rejectValue("chartId", "", "error.job.chartIdsNotValid");
        }
        if (dto.getId() == null) {
            Chart chart = null;
            try {
                chart = chartService.findByIdOrElseThrow(dto.getChartId());
            } catch (BusinessException e) {
                errors.rejectValue("chartId", "", "error.job.chartIdsNotValid");
            }
            Optional<Job> job = jobService.findByCodeAndChartId(dto.getCode(), chart.getId());
            if (job.isPresent()) {
                errors.rejectValue("code", "", "error.job.CodeIsDuplicate");
            }
        }
    }
}
