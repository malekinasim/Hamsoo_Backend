package com.hamsoo.bpm.common.config.fcm;

import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import org.castor.core.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class FcmRequest {

    private String topicName;
    private String notificationTitle;
    private String notificationBody;
    private Map<String, String> data;

    private FcmRequest(String topicName, String notificationTitle, String notificationBody, Map<String, String> data) {
        this.topicName = topicName;
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.data = data;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public Map<String, String> getData() {
        return data;
    }

    public static class Builder {

        private String topicName;
        private String notificationTitle = ResourceBundleUtils.getMessage("title.hamsoo");
        private String notificationBody;
        private Map<String, String> data;

        public Builder topicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public Builder notificationTitle(String notificationTitle) {
            this.notificationTitle = notificationTitle;
            return this;
        }

        public Builder notificationBody(String notificationBody) {
            this.notificationBody = notificationBody;
            return this;
        }

        public Builder data(String key, String value) {
            Assert.notNull(key, "key is null");
            Assert.notNull(value, "value is null");
            if (this.data == null) {
                this.data = new HashMap<>();
            }
            this.data.put(key, value);
            return this;
        }

        public FcmRequest build() {
            return new FcmRequest(topicName, notificationTitle, notificationBody, data);
        }
    }
}
