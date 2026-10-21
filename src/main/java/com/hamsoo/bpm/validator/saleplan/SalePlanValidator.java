package com.hamsoo.bpm.validator.saleplan;

import com.hamsoo.bpm.model.dto.saleplan.SalePlanDto;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;

@Component
public class SalePlanValidator extends HamsooValidator{
    @Override
    public boolean supports(Class<?> clazz) {
        return SalePlanDto.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {

        super.validate(target, errors);
        SalePlanDto dto = (SalePlanDto) target;

        if (dto.getName() == null) {
            errors.rejectValue("name", "", "error.salePlan.nameIsNotValid");
        }
        if (dto.getServiceTypes() == null) {
            errors.rejectValue("serviceType", "", "error.salePlan.serviceTypeIsNotValid");
        }
        if(Arrays.stream(SalePlan.ServicesType.values()).noneMatch(item->item.equals(dto.getServiceType()))){
            errors.rejectValue("serviceType", "", "error.salePlan.serviceTypeIsNotValid");
        }

        if (dto.getPrice() == null) {
            errors.rejectValue("price", "", "error.salePlan.priceIsNotValid");
        }
        if (dto.getCode() == null) {
            errors.rejectValue("code", "", "error.salePlan.codeIsNotValid");
        }
    }
}
