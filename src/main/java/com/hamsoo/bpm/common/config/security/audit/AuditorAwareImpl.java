package com.hamsoo.bpm.common.config.security.audit;

import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<User> {
    private final UserService userService;
    private final EntityManager entityManager;

    public AuditorAwareImpl(UserService userService, EntityManager entityManager) {
        this.userService = userService;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        UserDetails user = SecurityUtils.getImpersonator();
        if (SecurityUtils.getCurrentUser().isPresent() && user==null)
            return this.userService.findById(SecurityUtils.getCurrentUser().get().getId());

         if (user!=null && user.getUsername() != null) {
             entityManager.setFlushMode(FlushModeType.COMMIT);
                Optional<User> userOptional= userService.findByAccountUsername(user.getUsername());
             entityManager.setFlushMode(FlushModeType.AUTO);
                return userOptional;
        }
         return Optional.empty();
    }
}
