package com.hamsoo.bpm.service.api.notificationaudience;

import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
import com.hamsoo.bpm.repository.jpa.notificationaudience.NotificationAudienceRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface NotificationAudienceService extends BaseService<NotificationAudience, Long, NotificationAudienceRepository> {

    NotificationAudience mapToEntity(NotificationAudienceDto notificationAudienceDto, Notification notification);

    List<NotificationAudience> mapToEntity(List<NotificationAudienceDto> notificationAudienceList, Notification notification);

    Long getUnreadNotificationByUserId(Long userId,Long organId);

    void updateReadStatus(Long notificationAudienceId);
}
