package com.hamsoo.bpm.validator.notification;

import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.notificationapiparam.NotificationApiParamDto;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class NotificationValidator extends HamsooValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return NotificationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        NotificationDto dto = (NotificationDto) target;

        if (dto.getFromDate() != null && dto.getToDate() != null &&
                dto.getFromDate().isAfter(dto.getToDate())) {
            errors.rejectValue("fromDate", "", "error.notification.fromDateAfterToDate");
        }

        if ((dto.getEntityType() == null && dto.getRefId() != null) ||
                (dto.getEntityType() != null && dto.getRefId() == null)) {
            errors.rejectValue("entityType", "", "error.notification.bothEntityTypeAndRefIdMustEnter");
        }

        if (!CollectionUtils.isEmpty(dto.getNotificationApiParamDtoList())) {
            Optional<NotificationApiParamDto> notificationApiParamDtoOptional = dto.getNotificationApiParamDtoList().stream()
                    .filter(p -> !StringUtils.hasText(p.getKey()) || !StringUtils.hasText(p.getValue())).findFirst();
            if (notificationApiParamDtoOptional.isPresent()) {
                errors.rejectValue("notificationApiParamDtoList", "", "error.notification.apiParamNotValid");
            }
        }

    }
}
