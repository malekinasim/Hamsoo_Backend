package com.hamsoo.bpm.validator.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityIoDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class IdentityIoValidator extends HamsooValidator {

    private final CodeCategoryService codeCategoryService;

    public IdentityIoValidator(CodeCategoryService codeCategoryService) {
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return IdentityIoDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        IdentityIoDto dto = (IdentityIoDto) target;

        //if mandatory property is null not continue.
        if (errors.hasErrors()) {
            return;
        }

        CodeCategory type = codeCategoryService.findByCategoryAndCode(IdentityIo.Type.CATEGORY, dto.getTypeCode());
        if (type == null) {
            errors.rejectValue("typeCode", "", "error.identityIo.typeCodeIsNotValid");
        }

    }


}
