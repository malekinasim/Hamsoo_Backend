package com.hamsoo.bpm.common.config.security.config;

import com.hamsoo.bpm.common.config.security.service.impl.hamsooldapuserdetail.HamsooLdapUserDetailService;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final OauthProviderService oauthProviderService;
    private final HamsooLdapUserDetailService hamsooLdapUserDetailService;

    public LdapAuthenticationProvider(JwtTokenProvider jwtTokenProvider, OauthProviderService oauthProviderService, HamsooLdapUserDetailService hamsooLdapUserDetailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauthProviderService = oauthProviderService;
        this.hamsooLdapUserDetailService = hamsooLdapUserDetailService;
    }


    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        String curOrgan = jwtTokenProvider.getCurOrganFromRequest(request);
        Long id=null;
        if (StringUtils.hasText(curOrgan)) {
            try {
                id = Long.valueOf(curOrgan);
            } catch (NumberFormatException e) {

            }
        }
        if(id!=null){
            Optional<OauthProvider> oauthProvider = oauthProviderService.findByOrganIdAndType(Long.valueOf(curOrgan), OauthProvider.providerType.LDAP.name());
            if (oauthProvider.isPresent()) {
                LdapTemplate ldapTemplate = initContext(oauthProvider.get());
                OrFilter uidFilter = new OrFilter();
                uidFilter.or(new EqualsFilter("uid", authentication.getName()));
                if(StringUtils.hasText(oauthProvider.get().getSearchFilter())) {
                    Arrays.stream(oauthProvider.get().getSearchFilter().split(",")).forEach(
                            filterWord ->
                                    uidFilter.or(new EqualsFilter(filterWord, authentication.getName()))
                    );
                }

                try {
                    Boolean authenticate = ldapTemplate.authenticate("", uidFilter.toString(), authentication.getCredentials().toString());
                    if (authenticate) {
                        UserDetails userDetails = hamsooLdapUserDetailService.loadUserByUsername(authentication.getName(), oauthProvider.get(), curOrgan);
                        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials().toString(), userDetails.getAuthorities());
                        return auth;
                    }
                }catch (AuthenticationException e){
                   throw e;
                }
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private LdapTemplate initContext(OauthProvider oauthProvider) {
        LdapContextSource contextSource = new LdapContextSource();

        contextSource.setUrl(oauthProvider.getLdapUrl());
        contextSource.setBase(oauthProvider.getLdapBaseDn());
        contextSource.setUserDn(oauthProvider.getLdapManagerDn());
//        Map<String, Object> baseEnvironmentProperties=new HashMap<>();
//        baseEnvironmentProperties.put(java.naming.provider.url,"user");
//        baseEnvironmentProperties.put("userSearchBase","OU=Users,OU=Hamsoo,DC=Hamsood,DC=net");
//        baseEnvironmentProperties.put("searchScope", SearchScope.SUBTREE);
//        contextSource.setBaseEnvironmentProperties(baseEnvironmentProperties);


//        auth.ldapAuthentication()
//                .userDnPatterns("uid={0},ou=people")
//                .userSearchBase("ou=people")
//                .userSearchFilter("uid={0}")
//                .groupSearchBase("ou=groups")
//                .groupSearchFilter("uniqueMember={0}")
//                .contextSource()
//                .url("ldap://localhost:2389/dc=concretepage,dc=com")
//                .and()
//                .passwordCompare()
//                .passwordEncoder(passwordEncoder())
//                .passwordAttribute("userPassword");

        if (oauthProvider.getLdapManagerPassword() == null)
            contextSource.setAnonymousReadOnly(true);
        else
            contextSource.setPassword(oauthProvider.getLdapManagerPassword());
        contextSource.afterPropertiesSet();

        return new LdapTemplate(contextSource);
    }
    private class UserAttributesMapper implements AttributesMapper<User> {
        @Override
        public User mapFromAttributes(Attributes attributes) throws NamingException {
            if (attributes == null) {
                return null;
            }
            Attribute sn = attributes.get("sAMAccountName");
            User user = new User((String) sn.get(), "", null);
            return user;
        }
    }

}
