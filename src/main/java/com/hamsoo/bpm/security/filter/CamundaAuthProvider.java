package com.hamsoo.bpm.security.filter;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationProvider;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Component
public class CamundaAuthProvider implements AuthenticationProvider {

    private static final String USER_ID = "admin";

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest httpServletRequest, ProcessEngine processEngine) {

        Authentications authentications = Authentications.getFromSession(httpServletRequest.getSession());
        httpServletRequest.getAuthType();
        httpServletRequest.getRequestedSessionId();
        String userId = null;
        if (authentications != null && authentications.hasAuthenticationForProcessEngine(processEngine.getName())) {
            userId = authentications.getAuthenticationForProcessEngine(processEngine.getName()).getName();
            AuthenticationResult authenticationResult = new AuthenticationResult(userId, true);
            authenticationResult.setGroups(new ArrayList<>());
            return authenticationResult;
        }
        return null;

    }

    @Override
    public void augmentResponseByAuthenticationChallenge(HttpServletResponse httpServletResponse, ProcessEngine processEngine) {

    }
}
