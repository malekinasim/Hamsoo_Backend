package com.hamsoo.bpm.validator.saleplanfeature;

import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class SalePlanFeatureValidator extends HamsooValidator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SalePlanFeatureDto.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {

        super.validate(target, errors);
        SalePlanFeatureDto dto = (SalePlanFeatureDto) target;
        //for create
        if(dto.getId()==null) {
            if (dto.getSalePlanId() == null) {
                errors.rejectValue("salePlanId", "", "error.salePlanFeature.salePlanIdIsNotValid");
            }
            if (dto.getFeatureId() == null) {
                errors.rejectValue("featureId", "", "error.salePlanFeature.featureIdIsNotValid");
            }
        }
    }
}
