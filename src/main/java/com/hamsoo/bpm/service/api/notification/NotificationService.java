package com.hamsoo.bpm.service.api.notification;

import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.repository.jpa.notification.NotificationRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

public interface NotificationService extends BaseService<Notification, Long, NotificationRepository> {

    Notification findByIdOrElseThrow(Long id);

    Notification create(NotificationDto notificationDto);

    Notification validateAndCreate(NotificationDto notificationDto);

    NotificationDto mapToDto(Notification notification);

    void passive(Long id);

    SearchDto notificationOfCurrentUser(int page, int size, String orderBy, Sort.Direction direction,Boolean readStatus);

    SearchDto notificationByModelVersion(Long modelVersionId, int page, int size, String orderBy, Sort.Direction direction);

    void updateModelVersionId(Long newModelVersionId, Long oldModelVersionId, String entityType);


}
