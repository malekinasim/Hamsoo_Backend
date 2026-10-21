package com.hamsoo.bpm.validator.unit;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class UnitValidator extends HamsooValidator {
    private UnitService unitService;
    private ChartService chartService;

    public UnitValidator(UnitService unitService, ChartService chartService) {
        this.unitService = unitService;
        this.chartService = chartService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UnitDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);

        UnitDto dto = (UnitDto) target;

        if (dto.getName() == null) {
            errors.rejectValue("name", "", "error.unit.nameIsNotValid");
        }
        if (dto.getCode() == null) {
            errors.rejectValue("code", "", "error.unit.codeIsNotValid");
        }
        if (dto.getChartId() == null) {
            errors.rejectValue("chartId", "", "error.unit.chartIdsNotValid");
        }
        if (dto.getId() == null) {
            Chart chart = null;
            try {
                chart = chartService.findByIdOrElseThrow(dto.getChartId());
            } catch (BusinessException e) {
                errors.rejectValue("chartId", "", "error.unit.chartIdsNotValid");
            }
            Optional<Unit> unit = unitService.findByCodeAndChartId(dto.getCode(), chart.getId());
            if (unit.isPresent()) {
                errors.rejectValue("code", "", "error.unit.unitCodeIsDuplicate");
            }
        }
    }
}
