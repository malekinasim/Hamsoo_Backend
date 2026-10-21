//package com.hamsoo.bpm.security.hamsoologin;
//
//
//import com.hamsoo.bpm.security.filter.CamundaAuthzService;
//import org.camunda.bpm.engine.ProcessEngine;
//import org.camunda.bpm.engine.identity.Group;
//import org.camunda.bpm.engine.identity.Tenant;
//import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
//import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
//import org.camunda.bpm.webapp.impl.security.auth.AuthenticationService;
//import org.camunda.bpm.webapp.impl.security.auth.Authentications;
//import org.camunda.bpm.webapp.impl.util.ProcessEngineUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Component;
//import org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@Component
//public class SecurityAuthenticationProvider implements AuthenticationProvider {
//
//
//
//
//
//    public static Pattern APP_PATTERN = Pattern.compile("/camunda/app/(cockpit|admin|tasklist|welcome)/([^/]+)/");
//    public static Pattern API_ENGINE_PATTERN = Pattern.compile("/camunda//api/engine/engine/([^/]+)/.*");
//    public static Pattern API_STATIC_PLUGIN_PATTERN = Pattern.compile("/camunda//api/(cockpit|admin|tasklist|welcome)/plugin/[^/]+/static/.*");
//    public static Pattern API_PLUGIN_PATTERN = Pattern.compile("/camunda//api/(cockpit|admin|tasklist|welcome)/plugin/[^/]+/([^/]+)/.*");
//
//
//
//    protected AuthenticationService userAuthentications;
////
////    public CamundaSecurityAuthenticationProvider(CamundaAuthzService camundaAuthzService) {
////        this.userAuthentications = new AuthenticationService();
////       this.camundaAuthzService=camundaAuthzService;
////    }
//
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String name = authentication.getName();
//        String password = authentication.getCredentials().toString();
//        Authentication result;
//
//        ProcessEngine  engine= ProcessEngineUtil.lookupProcessEngine("default");
//        boolean valid = engine.getIdentityService().checkPassword(name, password);
//
//        if (valid)
//        {
//
//            List<String> groups=this.getGroupsOfUser(engine,authentication.getName());
//            List<String> tenants=this.getTenantOfUser(engine,authentication.getName());
////            AuthenticationResult authenticationResult = new AuthenticationResult(authentication.getName(), true);
////            authenticationResult.setGroups(groups);
////            authenticationResult.setTenants(tenants);
////            camundaAuthzService.addAuthorization(authentication.getName(), engine.getAuthorizationService());
////            engine.getIdentityService().setAuthentication(authentication.getName(),groups);
//       // org.camunda.bpm.webapp.impl.security.auth.Authentication cmaundaauthentication =   this.userAuthentications.createAuthenticate(engine, authentication.getName(), groups, tenants);
//
//
//
//
//            engine.getIdentityService().setAuthentication(name,groups);
//
//            result = new UsernamePasswordAuthenticationToken(name, password, groups.stream().map(group-> new SimpleGrantedAuthority(group)).collect(Collectors.toList()));
//
//        }
//        else
//        {
//            result = null;
//        }
//        return result;
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication)
//    {
//        boolean result = authentication.equals(UsernamePasswordAuthenticationToken.class);
//        return result;
//    }
//
//
//
//    public List<String> getGroupsOfUser(ProcessEngine engine, String userId) {
//        List<Group> groups = engine.getIdentityService().createGroupQuery().groupMember(userId).list();
//        List<String> groupIds = new ArrayList();
//
//
//        for(Group group :  groups) {
//
//            if (group != null && group.getId() != null) {
//                groupIds.add(group.getId());
//            }
//        }
//
//        return groupIds;
//    }
//
//
//
//    public List<String> getTenantOfUser(ProcessEngine engine, String userId) {
//        List<Tenant> tenants = engine.getIdentityService().createTenantQuery().groupMember(userId).list();
//        List<String> tenantIds = new ArrayList();
//
//        for(Tenant tenant :  tenants) {
//
//            if (tenant != null && tenant.getId() != null) {
//                tenantIds.add(tenant.getId());
//            }
//        }
//
//        return tenantIds;
//    }
//
//}
