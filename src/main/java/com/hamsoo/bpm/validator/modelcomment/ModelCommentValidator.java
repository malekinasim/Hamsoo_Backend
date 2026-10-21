package com.hamsoo.bpm.validator.modelcomment;

import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class ModelCommentValidator extends HamsooValidator {


    @Override
    public boolean supports(Class<?> clazz) {
        return ModelCommentDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        ModelCommentDto dto = (ModelCommentDto) target;

        if (dto.getId() == null) {
            //create mode
            if (dto.getModelVersionId() == null) {
                errors.rejectValue("modelVersionId", "", "error.modelComment.modelVersionIdIsNotValid");
            }
        }

    }
}
