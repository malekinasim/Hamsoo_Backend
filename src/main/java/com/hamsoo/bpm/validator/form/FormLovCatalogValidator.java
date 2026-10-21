package com.hamsoo.bpm.validator.form;

import com.hamsoo.bpm.model.dto.form.FormLovCatalogDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.form.FormLovCatalog;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

@Component
public class FormLovCatalogValidator extends HamsooValidator {

    private final CodeCategoryService codeCategoryService;

    public FormLovCatalogValidator(CodeCategoryService codeCategoryService) {
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormLovCatalogDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        FormLovCatalogDto dto = (FormLovCatalogDto) target;

        //if mandatory property is null not continue.
        if (errors.hasErrors()) {
            return;
        }

        //create mode
        if (dto.getId() == null) {
            if (!StringUtils.hasText(dto.getTypeCode())){
                errors.rejectValue("typeCode", "", "error.taskCatalog.typeCodeIsNotValid");
                return;
            }
            CodeCategory type = codeCategoryService.findByCategoryAndCode(FormLovCatalog.Type.CATEGORY, dto.getTypeCode());
            if (type == null) {
                errors.rejectValue("typeCode", "", "error.taskCatalog.typeCodeIsNotValid");
            }
        }


    }

}
