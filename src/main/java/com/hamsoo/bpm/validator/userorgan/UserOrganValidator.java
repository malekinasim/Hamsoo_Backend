package com.hamsoo.bpm.validator.userorgan;

import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import java.util.List;

@Component
public class UserOrganValidator extends HamsooValidator {
    private final UserOrganService userOrganService;


    public UserOrganValidator(UserOrganService userOrganService) {
        this.userOrganService = userOrganService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserOrganDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        UserOrganDto dto = (UserOrganDto) target;

        if (dto.getId() == null) {
            //create mode
            if (dto.getOrganId() == null) {
                errors.rejectValue("OrganId", "", "error.userOrgan.OrganIdIsNotValid");
            }

            if (dto.getUserId() == null) {
                errors.rejectValue("userId", "", "error.userOrgan.userIdIsNotValid");
            }
        }

        List<UserOrgan> userOrgans = userOrganService.findAllByUserAndOrgan(dto.getUserId(), dto.getOrganId());
        if (!CollectionUtils.isEmpty(userOrgans)) {
            errors.rejectValue("userId", "", "error.userOrgan.userOrganIsDuplicate");
        }

    }
}
