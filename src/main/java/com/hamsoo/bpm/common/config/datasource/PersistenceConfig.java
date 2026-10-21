package com.hamsoo.bpm.common.config.datasource;

import com.hamsoo.bpm.common.config.security.audit.AuditorAwareImpl;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;

@Configuration
//@EnableTransactionManagement
@EnableJpaRepositories("com.hamsoo.bpm")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {
    private final UserService userService;
    private final EntityManager entityManager;

    public PersistenceConfig(UserService userService, EntityManager entityManager) {
        this.userService = userService;
        this.entityManager = entityManager;
    }

    @Bean
    AuditorAware<User> auditorProvider() {
        return new AuditorAwareImpl(userService, entityManager);
    }
}

