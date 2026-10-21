package com.hamsoo.bpm.validator.form;

import com.hamsoo.bpm.model.dto.form.FormVersionDto;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class FormVersionValidator extends HamsooValidator {


    @Override
    public boolean supports(Class<?> clazz) {
        return FormVersionDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        FormVersionDto dto = (FormVersionDto) target;

        //if mandatory property is null not continue.
        if (errors.hasErrors()) {
            return;
        }

        //create mode
        if (dto.getId() == null) {
            if (dto.getFormCatalogId() == null) {
                errors.rejectValue("formCatalogId", "", "error.formVersion.formCatalogIdIsNotValid");
            }
        }

    }

}
