package com.hamsoo.bpm.model.dto.notification;

import com.hamsoo.bpm.model.dto.notificationapiparam.NotificationApiParamDto;
import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import org.json.JSONObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationDto {

    private Long id;
    private String notificationApiAddressCode;
    private Long refId;
    private String eventType;
    private JSONObject notificationParamValues;

    @NotBlank(message = "error.notification.notificationIsNotValid")
    @Size(max = 1000, message = "error.notification.notificationMaxLengthIsNotValid")
    private String notification;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String entityType;
    private List<NotificationApiParamDto> notificationApiParamDtoList;
    private List<NotificationAudienceDto> notificationAudienceDtoList;

    //return property
    private LocalDateTime eventDate;
    private String uri;
    private UserDto createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationApiAddressCode() {
        return notificationApiAddressCode;
    }

    public void setNotificationApiAddressCode(String notificationApiAddressCode) {
        this.notificationApiAddressCode = notificationApiAddressCode;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public List<NotificationApiParamDto> getNotificationApiParamDtoList() {
        return notificationApiParamDtoList;
    }

    public void setNotificationApiParamDtoList(List<NotificationApiParamDto> notificationApiParamDtoList) {
        this.notificationApiParamDtoList = notificationApiParamDtoList;
    }

    public List<NotificationAudienceDto> getNotificationAudienceDtoList() {
        return notificationAudienceDtoList;
    }

    public void setNotificationAudienceDtoList(List<NotificationAudienceDto> notificationAudienceDtoList) {
        this.notificationAudienceDtoList = notificationAudienceDtoList;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public JSONObject getNotificationParamValues() {
        return notificationParamValues;
    }

    public void setNotificationParamValues(JSONObject notificationParamValues) {
        this.notificationParamValues = notificationParamValues;
    }
}
