package com.hamsoo.bpm.model.entity.notification;

import com.hamsoo.bpm.model.entity.notificationapiaddress.NotificationApiAddress;
import com.hamsoo.bpm.model.entity.notificationapiparam.NotificationApiParam;
import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bpm_notifications", schema = "bpm", catalog = "BPM_DB")
public class Notification extends AuditEntity<Long> {

    private Long id;
    private NotificationApiAddress notificationApiAddress;
    private Long refId;
    private String eventType;
    private String notification;
    private LocalDateTime eventDate;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status;
    private String entityType;
    private List<NotificationApiParam> notificationApiParamList;
    private List<NotificationAudience> notificationAudienceList;

    private Organ organ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id")
    public NotificationApiAddress getNotificationApiAddress() {
        return notificationApiAddress;
    }

    public void setNotificationApiAddress(NotificationApiAddress notificationApiAddress) {
        this.notificationApiAddress = notificationApiAddress;
    }

    @Basic
    @Column(name = "ref_id")
    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @Basic
    @Column(name = "event_type")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Basic
    @Column(name = "notification", nullable = false, length = -1)
    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    @Basic
    @Column(name = "event_date", nullable = true)
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    @Basic
    @Column(name = "from_date", nullable = true)
    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    @Basic
    @Column(name = "to_date", nullable = true)
    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "entity_type")
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @OneToMany(mappedBy = "notification", cascade = {CascadeType.PERSIST})
    public List<NotificationApiParam> getNotificationApiParamList() {
        return notificationApiParamList;
    }

    public void setNotificationApiParamList(List<NotificationApiParam> notificationApiParamList) {
        this.notificationApiParamList = notificationApiParamList;
    }

    @OneToMany(mappedBy = "notification", cascade = {CascadeType.PERSIST})
    public List<NotificationAudience> getNotificationAudienceList() {
        return notificationAudienceList;
    }

    public void setNotificationAudienceList(List<NotificationAudience> notificationAudienceList) {
        this.notificationAudienceList = notificationAudienceList;
    }

    public enum EntityType {
        MODEL_VERSION("ModelVersion"),
        ORGAN("Organ");

        private String value;

        EntityType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    public enum ApiAddressCode {
        EDIT_MODEL_API("EDIT_MODEL_API"),
        APPROVE_MODEL_API("APPROVE_MODEL_API");
        //,ORGAN("Organ");

        private String value;

        ApiAddressCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
