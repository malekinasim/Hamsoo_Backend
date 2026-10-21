package com.hamsoo.bpm.validator.model;

import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;


@Component
public class ModelValidator extends HamsooValidator {

    private CodeCategoryService codeCategoryService;

    public ModelValidator(CodeCategoryService codeCategoryService) {
        this.codeCategoryService = codeCategoryService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ModelDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        ModelDto dto = (ModelDto) target;

        if (StringUtils.hasText(dto.getModelTypeCode())) {
            CodeCategory modelType = codeCategoryService.findByCategoryAndCode(Model.ModelType.CATEGORY, dto.getModelTypeCode());
            if (modelType == null) {
                errors.rejectValue("modelTypeCode", "", "error.model.modelTypeCodeNotValid");
            }
        }

    }
}
