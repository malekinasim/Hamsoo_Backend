package com.hamsoo.bpm.service.impl.notificationapiparam;

import com.hamsoo.bpm.model.dto.notificationapiparam.NotificationApiParamDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.notificationapiparam.NotificationApiParam;
import com.hamsoo.bpm.service.api.notificationapiparam.NotificationApiParamService;
import com.hamsoo.bpm.repository.jpa.notificationapiparam.NotificationApiParamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationApiParamServiceImpl implements NotificationApiParamService {

    private final NotificationApiParamRepository notificationApiParamRepository;

    public NotificationApiParamServiceImpl(NotificationApiParamRepository notificationApiParamRepository) {
        this.notificationApiParamRepository = notificationApiParamRepository;
    }

    @Override
    public NotificationApiParamRepository getRepository() {
        return notificationApiParamRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationApiParam mapToEntity(NotificationApiParamDto notificationApiParamDto, Notification notification) {
        Assert.notNull(notificationApiParamDto, "notificationApiParamDto is null");
        NotificationApiParam notificationApiParam = new NotificationApiParam();
        notificationApiParam.setNotification(notification);
        notificationApiParam.setKey(notificationApiParamDto.getKey());
        notificationApiParam.setValue(notificationApiParamDto.getValue());
        return notificationApiParam;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationApiParam> mapToEntity(List<NotificationApiParamDto> notificationApiParamDtoList, Notification notification) {
        if (CollectionUtils.isEmpty(notificationApiParamDtoList)) {
            return new ArrayList<>();
        }
        return notificationApiParamDtoList.stream().map(p -> mapToEntity(p, notification)).toList();
    }
}
