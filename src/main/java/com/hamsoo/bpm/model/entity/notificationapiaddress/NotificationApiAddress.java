package com.hamsoo.bpm.model.entity.notificationapiaddress;

import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_notification_api_addresses", schema = "bpm", catalog = "BPM_DB")
public class NotificationApiAddress extends AuditEntity<Long> {

    private Long id;
    private String uri;
    private String code;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_id", nullable = false)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "uri", nullable = false, length = -1)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Basic
    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public enum Code {
        MODEL_VERSION("MODEL_VERSION");

        private String value;

        Code(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
