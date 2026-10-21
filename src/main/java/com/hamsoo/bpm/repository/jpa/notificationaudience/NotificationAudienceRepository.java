package com.hamsoo.bpm.repository.jpa.notificationaudience;

import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface NotificationAudienceRepository extends HamsooRepository<NotificationAudience, Long> {

    @Query(" select count(n.id) from NotificationAudience n where n.user.id= :userId and n.readStatus=false and n.notification.organ.id=:organId ")
    Long getUnreadNotificationByUserId(Long userId, Long organId);

    Optional<NotificationAudience> findById(@Param("id") Long id);

}
