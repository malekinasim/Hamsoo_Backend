package com.hamsoo.bpm.repository.jpa.notification;

import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface NotificationRepository extends HamsooRepository<Notification, Long> {

    @Query("select n from NotificationAudience na inner join na.notification n inner join na.user u " +
            "where n.status = 'ACTIVE' and (n.fromDate is null or n.fromDate <= CURRENT_DATE) and (n.toDate is null or n.toDate >= CURRENT_DATE) " +
            " and n.id= :notificationId and u.id= :userId ")
    Optional<Notification> findNotificationById(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    @Query("select n from NotificationAudience na inner join na.notification n inner join na.user u " +
            "where n.status = 'ACTIVE' and (n.fromDate is null or n.fromDate <= CURRENT_DATE) and (n.toDate is null or n.toDate >= CURRENT_DATE) " +
            " and u.id= :userId and n.organ.id = :currentOrganId and ( :readStatus is null or na.readStatus=:readStatus ) order by na.readStatus,n.id desc")
    Page<Notification> findByUserId(@Param("userId") Long userId, Long currentOrganId, Pageable pageable, Boolean readStatus);

    @Query("select n from Notification n " +
            "where n.status = 'ACTIVE' and n.refId= :refId and n.entityType= :entityType ")
    Page<Notification> findByRefIdAndEntityType(@Param("refId") Long refId, @Param("entityType") String entityType, Pageable pageable);


    @Modifying
    @Query(value = "update bpm.bpm_notifications set ref_id = :newModelVersionId " +
            " where ref_id = :oldModelVersionId and entity_type = :entityType ", nativeQuery = true)
    void updateModelVersionId(@Param("newModelVersionId") Long newModelVersionId, @Param("oldModelVersionId") Long oldModelVersionId, @Param("entityType") String entityType);


}
