package com.hamsoo.bpm.validator.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityDataDto;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component
public class IdentityDataSchemaValidator extends HamsooValidator {

    private final ProcessCategoryService processCategoryService;

    public IdentityDataSchemaValidator(ProcessCategoryService processCategoryService) {
        this.processCategoryService = processCategoryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return IdentityDataDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        IdentityDataDto dto = (IdentityDataDto) target;

        if (dto.getProcessCategoryId() != null) {
            //validate processCategoryId
            processCategoryService.findByIdOrElseThrow(dto.getProcessCategoryId());
        }
    }
}
