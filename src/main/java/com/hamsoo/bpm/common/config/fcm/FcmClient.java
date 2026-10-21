package com.hamsoo.bpm.common.config.fcm;

import com.google.firebase.messaging.*;
import com.hamsoo.bpm.common.exception.ServiceException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import org.apache.http.util.Asserts;
import org.castor.core.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Firebase Cloud Messaging client
 */
@Service
public class FcmClient {

    private static final Logger logger = LoggerFactory.getLogger(FcmClient.class);
    private static final String TTL_HEADER_KEY = "ttl";
    private static final String USER_TOPIC_NAME = "user-topic-%s";

    private final FcmProperties fcmProperties;

    public FcmClient(FcmProperties fcmProperties) {
        this.fcmProperties = fcmProperties;
    }

    public Optional<TopicManagementResponse> subscribe(String topicName, List<String> clientTokenList) {
        if (!StringUtils.hasText(topicName) || CollectionUtils.isEmpty(clientTokenList)) {
            throw new ServiceException("invalid argument");
        }
        Optional<TopicManagementResponse> result;
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopicAsync(clientTokenList, topicName).get();
            logger.info(response.getSuccessCount() + " tokens were subscribed successfully");
            logger.info(response.getFailureCount() + " tokens fail to subscribe. with cause : ");
            logger.info(response.getErrors().toString());
            result = Optional.of(response);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            result = Optional.empty();
        }
        return result;
    }

    public Optional<TopicManagementResponse> unsubscribe(String topicName, List<String> clientTokenList) {
        if (!StringUtils.hasText(topicName) || CollectionUtils.isEmpty(clientTokenList)) {
            throw new ServiceException("invalid argument");
        }
        Optional<TopicManagementResponse> result;
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopicAsync(clientTokenList, topicName).get();
            logger.info(response.getSuccessCount() + " tokens were unsubscribed successfully");
            logger.info(response.getFailureCount() + " tokens fail to unsubscribe. with cause : ");
            logger.info(response.getErrors().toString());
            result = Optional.of(response);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            result = Optional.empty();
        }
        return result;
    }

    public void send(FcmRequest fcmRequest) {
        Assert.notNull(fcmRequest.getTopicName(), "topicName is null");
        WebpushNotification webpushNotification = new WebpushNotification(fcmRequest.getNotificationTitle(),
                fcmRequest.getNotificationBody(), fcmProperties.getNotificationIconUrl());
        WebpushConfig webpushConfig = WebpushConfig.builder()
                .putHeader(TTL_HEADER_KEY, fcmProperties.getTtl().toString())
                .setNotification(webpushNotification)
                .build();

        Message.Builder messageBuilder = Message.builder()
                .setTopic(fcmRequest.getTopicName())
                .setWebpushConfig(webpushConfig);
        if (!CollectionUtils.isEmpty(fcmRequest.getData())) {
            messageBuilder.putAllData(fcmRequest.getData());
        }
        Message message = messageBuilder.build();
        new Thread(() -> {
            try {
                logger.info("FCM message request topic: " + fcmRequest.getTopicName());
                String response = FirebaseMessaging.getInstance().sendAsync(message).get();
                logger.info("FCM message response: " + response);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("error in send FCM notification with message: " + e.getMessage(), e);
            }
        }).start();

    }

    public String getTopicName(Long userId) {
        Asserts.notNull(userId, "userId is null");
        return String.format(USER_TOPIC_NAME, userId);
    }

    public String getCurrentUserTopicName() {
        return getTopicName(SecurityUtils.getCurrentUserOrElseThrow().getId());
    }
}
