package com.hamsoo.bpm.common.config.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FcmConfig {

    private static final Logger logger = LoggerFactory.getLogger(FcmConfig.class);

    private final FcmProperties fcmProperties;

    public FcmConfig(FcmProperties fcmProperties) {
        this.fcmProperties = fcmProperties;
    }

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount = new ClassPathResource(fcmProperties.getServiceAccountFile()).getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            logger.error("error in init FCM config");
            logger.error(e.getMessage(), e);
        }
    }

}
