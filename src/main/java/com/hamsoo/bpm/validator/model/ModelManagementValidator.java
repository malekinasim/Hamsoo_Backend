package com.hamsoo.bpm.validator.model;

import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.model.ModelManagementDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;


@Component
public class ModelManagementValidator extends HamsooValidator {

    private final CodeCategoryService codeCategoryService;
    private final ModelService modelService;

    public ModelManagementValidator(CodeCategoryService codeCategoryService,
                                    ModelService modelService) {
        this.codeCategoryService = codeCategoryService;
        this.modelService = modelService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ModelManagementDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        ModelManagementDto dto = (ModelManagementDto) target;

        //if mandatory property is null not continue.
        if (errors.hasErrors()) {
            return;
        }

        CodeCategory modelType = codeCategoryService.findByCategoryAndCode(Model.ModelType.CATEGORY, dto.getModelTypeCode());
        if (modelType == null) {
            errors.rejectValue("modelTypeCode", "", "error.modelManagement.modelTypeCodeNotValid");
        }

        if (dto.getModelTypeCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            Optional<Model> enterpriseMapModelOptional = modelService.findByModelTypeCodeAndOrganId(Model.ModelType.ENTERPRISE_MAP.getCode(),
                    SecurityUtils.getCurrentOrganIdOrElseThrow());
            if (enterpriseMapModelOptional.isPresent()) {
                errors.rejectValue("modelTypeCode", "", "error.modelManagement.enterpriseMapModelIsAlreadyExist");
            }
        }

    }
}
