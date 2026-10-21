package com.hamsoo.bpm.validator.chart;

import com.hamsoo.bpm.model.dto.chart.ChartDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class ChartValidator extends HamsooValidator {
    private ChartService chartService;

    public ChartValidator(ChartService chartService) {
        this.chartService = chartService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ChartDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        super.validate(target, errors);
        ChartDto dto = (ChartDto) target;

        if (dto.getName() == null) {
            errors.rejectValue("name", "", "error.chart.nameIsNotValid");
        }
        if (dto.getCode() == null) {
            errors.rejectValue("code", "", "error.chart.codeIsNotValid");
        }
        //create Mode
        if (dto.getId() == null) {
            Optional<Chart> chart = chartService.findByCode(dto.getCode());
            if (chart.isPresent()) {
                errors.rejectValue("code", "", "error.chart.chartCodeIsDuplicate");
            }
        }

    }
}
