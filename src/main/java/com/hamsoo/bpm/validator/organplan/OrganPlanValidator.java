package com.hamsoo.bpm.validator.organplan;

import com.hamsoo.bpm.model.dto.organplan.OrganPlanDto;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigInteger;

@Component
public class OrganPlanValidator extends HamsooValidator {
    private OrganService organService;

    public OrganPlanValidator(OrganService organService) {
        this.organService = organService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return OrganPlanDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        OrganPlanDto dto = (OrganPlanDto) target;
        if (dto.getAmountPaid() != null) {
            errors.rejectValue("amountPaid", "", "error.organPlan.amountPaidIsNull");
        }
        if (dto.getMaxCapacity() != null) {
            errors.rejectValue("maxCapacity", "", "error.organPlan.maxCapacityIsNull");
        }
        if (dto.getMaxModelCount() != null) {
            errors.rejectValue("maxModelCount", "", "error.organPlan.maxModelCountIsNull");
        }
        if (dto.getMaxUserCount() != null) {
            errors.rejectValue("maxUserCount", "", "error.organPlan.maxUserCountIsNull");
        }
        if (dto.getId() == null) {
            if (dto.getOrganId() != null) {
                errors.rejectValue("organId", "", "error.organPlan.organIdIsNull");
            }
            if (dto.getSalePlanId() != null) {
                errors.rejectValue("salePlanId", "", "error.organPlan.salePlanIdIsNull");
            }
        }
        BigInteger modelCapacity = organService.getOrganModelCapacity(dto.getOrganId());
        if (modelCapacity.compareTo(dto.getMaxCapacity())>0)
            errors.rejectValue("maxCapacity", "", "error.organPlan.maxCapacityIsInValid");

        Integer modelCount = organService.getOrganModelCount(dto.getOrganId());
        if (modelCount >= dto.getMaxModelCount())
            errors.rejectValue("maxModelCount", "", "error.organPlan.maxModelCountIsInValid");

        Integer userCount = organService.getOrganUsersCount(dto.getOrganId());
        if (userCount >= dto.getMaxUserCount())
            errors.rejectValue("maxUserCount", "", "error.organPlan.maxUserCountIsInValid");

    }
}
