package com.hamsoo.bpm.model.dto.notificationaudience;

import java.time.LocalDateTime;

public class NotificationAudienceDto {

    private Long id;
    private Long notificationId;
    private Long userId;
    private LocalDateTime readDate;


    private Boolean readStatus;

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public NotificationAudienceDto() {
    }

    public NotificationAudienceDto(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getReadDate() {
        return readDate;
    }

    public void setReadDate(LocalDateTime readDate) {
        this.readDate = readDate;
    }
}
