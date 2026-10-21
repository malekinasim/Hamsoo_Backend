package com.hamsoo.bpm.service.impl.notificationaudience;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
import com.hamsoo.bpm.service.api.notificationaudience.NotificationAudienceService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.impl.notification.NotificationServiceImpl;
import com.hamsoo.bpm.repository.jpa.notificationaudience.NotificationAudienceRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationAudienceServiceImpl implements NotificationAudienceService {

    private final NotificationAudienceRepository notificationAudienceRepository;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationAudienceServiceImpl(  NotificationAudienceRepository notificationAudienceRepository,
                                           UserService userService,
                                             SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationAudienceRepository = notificationAudienceRepository;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public NotificationAudienceRepository getRepository() {
        return notificationAudienceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationAudience mapToEntity(NotificationAudienceDto notificationAudienceDto, Notification notification) {
        Assert.notNull(notificationAudienceDto, "notificationAudienceDto is null");
        NotificationAudience notificationAudience = new NotificationAudience();
        notificationAudience.setNotification(notification);
        notificationAudience.setUser(userService.findByUserIdAndCurrentOrganOrElseThrow(notificationAudienceDto.getUserId()));
        return notificationAudience;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationAudience> mapToEntity(List<NotificationAudienceDto> notificationAudienceList, Notification notification) {
        if (CollectionUtils.isEmpty(notificationAudienceList)) {
            return new ArrayList<>();
        }
        return notificationAudienceList.stream().map(p -> mapToEntity(p, notification)).toList();
    }


    @Transactional(rollbackFor = Throwable.class)
    public Long getUnreadNotificationByUserId(Long userId, Long organId) {
        return getRepository().getUnreadNotificationByUserId(userId, organId);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void updateReadStatus(Long notificationAudienceId) {
        Assert.notNull(notificationAudienceId, "notificationAudienceId is null");
        NotificationAudience notificationAudience = getRepository().findById(notificationAudienceId).orElseThrow(() -> new BusinessException("error.ioSupCust.idIsNotValid"));
        notificationAudience.setReadStatus(true);
        notificationAudience.setReadDate(LocalDateTime.now());
        save(notificationAudience);

        //TODO Send Notification for update unread count
        Long unreadNotificationCount = this.getUnreadNotificationByUserId(SecurityUtils.getUserIdOfCurrentUserOrElseThrow(),SecurityUtils.getCurrentOrgan().get().getId());
        simpMessagingTemplate.convertAndSend(MessageFormat.format(NotificationServiceImpl.NOTIFICATION_END_POINT, SecurityUtils.getUserIdOfCurrentUserOrElseThrow().toString()), unreadNotificationCount);

    }
}
