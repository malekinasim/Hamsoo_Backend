package com.hamsoo.bpm.security.filter;



import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.webapp.impl.security.auth.Authentication;
import org.camunda.bpm.webapp.impl.security.auth.AuthenticationService;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.camunda.bpm.webapp.impl.util.ProcessEngineUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modified copy of ContainerBasedAuthenticationFilter.java
 * @see org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter
 */

public class CamundaAuthFilter implements Filter {
    public static Pattern APP_PATTERN = Pattern.compile("/camunda/app/(cockpit|admin|tasklist|welcome)/([^/]+)/");
    public static Pattern API_ENGINE_PATTERN = Pattern.compile("/camunda//api/engine/engine/([^/]+)/.*");
    public static Pattern API_STATIC_PLUGIN_PATTERN = Pattern.compile("/camunda//api/(cockpit|admin|tasklist|welcome)/plugin/[^/]+/static/.*");
    public static Pattern API_PLUGIN_PATTERN = Pattern.compile("/camunda//api/(cockpit|admin|tasklist|welcome)/plugin/[^/]+/([^/]+)/.*");

    protected AuthenticationService userAuthentications;
    protected CamundaAuthProvider camundaAuthProvider;
    protected CamundaAuthzService camundaAuthzService;

    public CamundaAuthFilter(CamundaAuthProvider camundaAuthProvider, CamundaAuthzService camundaAuthzService) {
        this.userAuthentications = new AuthenticationService();
        this.camundaAuthProvider=camundaAuthProvider;
        this.camundaAuthzService=camundaAuthzService;
    }


    public void init(FilterConfig filterConfig) throws ServletException {
      //  this.userAuthentications = new AuthenticationService();

    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        String engineName = this.extractEngineName(req);
        if (engineName == null) {
            chain.doFilter(request, response);
        } else {
            ProcessEngine engine = this.getAddressedEngine(engineName);
            if (engine == null) {
                resp.sendError(404, "Process engine " + engineName + " not available");
            } else {
                AuthenticationResult authenticationResult = camundaAuthProvider.extractAuthenticatedUser(req, engine);
                if(authenticationResult!=null){
                if (authenticationResult !=null && authenticationResult.isAuthenticated()) {
                    Authentications authentications = Authentications.getFromSession(req.getSession());
                    String authenticatedUser = authenticationResult.getAuthenticatedUser();
                    if (!this.existisAuthentication(authentications, engineName, authenticatedUser)) {
                        List<String> groups = authenticationResult.getGroups();
                        List<String> tenants = authenticationResult.getTenants();
                        camundaAuthzService.addAuthorization(authenticatedUser, engine.getAuthorizationService());
                        Authentication authentication = this.createAuthentication(engine, authenticatedUser, groups, tenants);
                        authentications.addAuthentication(authentication);
                    }

                   }
                    chain.doFilter(request, response);
                } else {
                    resp.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
                    camundaAuthProvider.augmentResponseByAuthenticationChallenge(resp, engine);
                }

            }
        }
    }

    protected String getRequestUri(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return request.getRequestURI().substring(contextPath.length());
    }

    protected String extractEngineName(HttpServletRequest request) {
        String requestUri = this.getRequestUri(request);
        String requestMethod = request.getMethod();
        Matcher appMatcher = APP_PATTERN.matcher(requestUri);
        if (appMatcher.matches()) {
            return appMatcher.group(2);
        } else {
            Matcher apiEngineMatcher = API_ENGINE_PATTERN.matcher(requestUri);
            if (apiEngineMatcher.matches()) {
                return apiEngineMatcher.group(1);
            } else {
                Matcher apiStaticPluginPattern = API_STATIC_PLUGIN_PATTERN.matcher(requestUri);
                if (requestMethod.equals("GET") && apiStaticPluginPattern.matches()) {
                    return null;
                } else {
                    Matcher apiPluginPattern = API_PLUGIN_PATTERN.matcher(requestUri);
                    return apiPluginPattern.matches() ? apiPluginPattern.group(2) : "default";
                }
            }
        }
    }

    protected ProcessEngine getAddressedEngine(String engineName) {
        return ProcessEngineUtil.lookupProcessEngine(engineName);
    }

    protected boolean existisAuthentication(Authentications authentications, String engineName, String username) {
        Authentication authentication = authentications.getAuthenticationForProcessEngine(engineName);
        return authentication != null && this.isAuthenticated(authentication, engineName, username);
    }

    protected boolean isAuthenticated(Authentication authentication, String engineName, String username) {
        String processEngineName = authentication.getProcessEngineName();
        String identityId = authentication.getIdentityId();
        return processEngineName.equals(engineName) && identityId.equals(username);
    }

    protected Authentication createAuthentication(ProcessEngine processEngine, String username, List<String> groups, List<String> tenants) {
        return this.userAuthentications.createAuthenticate(processEngine, username, groups, tenants);
    }
}