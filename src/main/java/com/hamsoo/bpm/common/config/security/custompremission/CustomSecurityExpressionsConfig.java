package com.hamsoo.bpm.common.config.security.custompremission;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class CustomSecurityExpressionsConfig extends GlobalMethodSecurityConfiguration {
    private final ApplicationContext applicationContext;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        final PermissionEvaluator permissionEvaluator = new DenyAllPermissionEvaluator();
        final CustomSecurityExpressionHandler expressionHandler = new CustomSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

    public CustomSecurityExpressionsConfig(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


}
