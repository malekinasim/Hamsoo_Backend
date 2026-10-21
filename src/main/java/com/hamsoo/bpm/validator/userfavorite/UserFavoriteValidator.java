package com.hamsoo.bpm.validator.userfavorite;

import com.hamsoo.bpm.model.dto.userfavorite.UserFavoriteDto;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;

@Component
public class UserFavoriteValidator extends HamsooValidator {

    private final ModelService modelService;

    public UserFavoriteValidator(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserFavoriteDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        UserFavoriteDto dto = (UserFavoriteDto) target;

        boolean anyMatch = Arrays.stream(UserFavorite.ReferenceType.values())
                .anyMatch(referenceType -> referenceType.name().equals(dto.getReferenceType()));
        if (!anyMatch) {
            errors.rejectValue("referenceType", "", "error.userFavorite.referenceTypeIsNotValid");
        }

        //validate reference id
        if (dto.getReferenceType().equals(UserFavorite.ReferenceType.MODEL.name())) {
            modelService.findByIdOrElseThrow(dto.getReferenceId());
        }

    }
}
