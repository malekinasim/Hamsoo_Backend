package com.hamsoo.bpm.validator.feature;

import com.hamsoo.bpm.model.dto.feature.FeatureDto;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.feature.FeatureService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
@Component
public class FeatureValidator extends HamsooValidator {
    private CodeCategoryService codeCategoryService;
    private FeatureService featureService;

    public FeatureValidator(CodeCategoryService codeCategoryService, FeatureService featureService) {
        this.codeCategoryService = codeCategoryService;
        this.featureService = featureService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(FeatureDto.class);
    }
    @Override
    public void validate(Object target, Errors errors) {

        super.validate(target, errors);
        FeatureDto dto = (FeatureDto) target;

        if (dto.getName() == null) {
            errors.rejectValue("name", "", "error.feature.nameIsNotValid");
        }
//        if (dto.getCalcType() == null) {
//            errors.rejectValue("calcType", "", "error.feature.calcTypeIsNotValid");
//        }
//        List<CodeCategory> codeCategoryList= codeCategoryService.findByCategory(Feature.CalcType.CATEGORY);
//        if(codeCategoryList.stream().noneMatch(item->item.getCode().equals(dto.getCalcType()))){
//            errors.rejectValue("calcType", "", "error.feature.calcTypeIsNotValid");
//        }

        //create Mode
//        if (dto.getId() == null) {
//            if(!dto.getCalcType().equals(Feature.CalcType.NO_CALC.getCode()) ) {
//                Optional<Feature> feature = featureService.findByFeatureCalcType(dto.getCalcType());
//                if (feature.isPresent()) {
//                    errors.rejectValue("calcType", "", "error.feature.calcTypeIsDuplicate");
//                }
//            }
//        }

    }
}
