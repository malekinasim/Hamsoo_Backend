package com.hamsoo.bpm.common.config.fcm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fcm")
public class FcmProperties {

    private String serviceAccountFile;
    private Long ttl;
    private String notificationIconUrl;

    public String getServiceAccountFile() {
        return this.serviceAccountFile;
    }

    public void setServiceAccountFile(String serviceAccountFile) {
        this.serviceAccountFile = serviceAccountFile;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public String getNotificationIconUrl() {
        return notificationIconUrl;
    }

    public void setNotificationIconUrl(String notificationIconUrl) {
        this.notificationIconUrl = notificationIconUrl;
    }
}
