package com.hamsoo.bpm.service.api.notificationapiparam;

import com.hamsoo.bpm.model.dto.notificationapiparam.NotificationApiParamDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.notificationapiparam.NotificationApiParam;
import com.hamsoo.bpm.repository.jpa.notificationapiparam.NotificationApiParamRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface NotificationApiParamService extends BaseService<NotificationApiParam, Long, NotificationApiParamRepository> {

    NotificationApiParam mapToEntity(NotificationApiParamDto notificationApiParamDto, Notification notification);

    List<NotificationApiParam> mapToEntity(List<NotificationApiParamDto> notificationApiParamDtoList, Notification notification);

}
