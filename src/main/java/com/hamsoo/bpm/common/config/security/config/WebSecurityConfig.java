package com.hamsoo.bpm.common.config.security.config;

//import com.hamsoo.bpm.common.config.security.filter.AdditionalRegisterationFilter;

import com.hamsoo.bpm.common.config.security.handler.*;
import com.hamsoo.bpm.common.config.security.filter.AdditionalRegisterationFilter;
import com.hamsoo.bpm.common.config.security.filter.HamsooSwitchUserFilter;
import com.hamsoo.bpm.common.config.security.filter.JWTAuthenticationFilter;
import com.hamsoo.bpm.common.config.security.filter.SimpleCorsFilter;
import com.hamsoo.bpm.common.config.security.service.impl.hamsooldapuserdetail.HamsooLdapUserDetailService;
import com.hamsoo.bpm.common.config.security.service.impl.hamsoooauth2userdetail.HamsooOauth2UserDetailsService;
import com.hamsoo.bpm.common.config.security.service.impl.hamsoooidcuserdetail.HamsooOidcUserDetailService;
import com.hamsoo.bpm.common.config.security.service.impl.hamsoouserdetail.HamsooUserDetailsService;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.util.List;


@Configuration
@EnableWebSecurity
//@PropertySource("classpath:application-security-dev.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    Environment environment;


    @Autowired
    private HamsooUserDetailsService userDetailsService;


    @Autowired
    private HamsooLdapUserDetailService hamsooLdapUserDetailService;

    @Autowired
    private HamsooOauth2UserDetailsService oauth2DetailsService;

    @Autowired
    private HamsooOidcUserDetailService hamsooOidcUserDetailsService;


    @Autowired
    private UnAuthorizedHandler unauthorizedHandler;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private UserOrganService userOrganService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private OauthProviderService oauthProviderService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SwitchUserAuthenticationSuccessHandler successHandler;


    private static final String[] AUTH_WHITELIST = new String[]{"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/api/v1/public/**","/api/v1/auth/**", "/oauth2/**", "/oauth2/authorize/**", "/oauth2/callback/**", "/stomp/**"};


    @Bean
    public JWTAuthenticationFilter tokenAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public HamsooSwitchUserFilter switchUserFilter() {
        return new HamsooSwitchUserFilter(userDetailsService
                , userOrganService, accountService, successHandler);
    }

    @Bean
    public SimpleCorsFilter simpleCorsFilter() {
        return new SimpleCorsFilter(environment);
    }


    //add this filter for complate Registeration Info for login from google or HamsooFrame
    @Bean
    public AdditionalRegisterationFilter additionalRegisterationFilter() {
        return new AdditionalRegisterationFilter(AUTH_WHITELIST);
    }

    /*
      By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
      the authorization request. But, since our service is stateless, we can't save it in
      the session. We'll save the request in a Base64 encoded cookie instead.
    */
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }


    /*  AuthenticationManagerBuilder is for authenticating a user.we’ve provided our HamsooUserDetailsService and a passwordEncoder to build the AuthenticationManager by JDBC authentication.
         We’ll use the configured AuthenticationManager to authenticate a user  whit Hamsoo Account in the login API.
     */
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //basaic /jwt authentication
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());


        //ldap Based AuthenticationProvider configs
        List<OauthProvider> oauthProviders = oauthProviderService.findAll();
        oauthProviders.stream()
                .filter(oauthProvider -> oauthProvider.getType().equals(OauthProvider.providerType.LDAP.name())).forEach(
                        oauthProvider ->{
                            authenticationManagerBuilder
                                    .authenticationProvider(new LdapAuthenticationProvider(jwtTokenProvider,
                                    oauthProviderService, hamsooLdapUserDetailService));
                        }
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //bean for get AuthenticationManager that we configured by HamsooUserDetailsService and a passwordEncoder
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        httpSecurity.cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(unauthorizedHandler);
                    exceptionHandling.accessDeniedHandler(customAccessDeniedHandler);


                })
                //route http to https for ssl
//                .requiresChannel(channel ->
//                        channel.anyRequest().requiresSecure())
                .authorizeRequests(expressionInterceptUrlRegistry ->
                        expressionInterceptUrlRegistry
                                .antMatchers("/api/v1/switch_user").hasAnyAuthority(Role.ApplicationRole.HAMSOO_ADMIN.getCode())
                                .antMatchers("/api/v1/switch_user_exit").hasAnyAuthority(HamsooSwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)
                                .antMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated())

                .oauth2Login()
                .clientRegistrationRepository(clientRegistrationRepository)
                .authorizedClientService(authorizedClientService)
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                /*
                 The OAuth2 protocol recommends using a state parameter to prevent CSRF attacks. During authentication,
                 the application sends this parameter in the authorization request, and the OAuth2 provider returns
                 this parameter unchanged in the OAuth2 callback.
                 The application compares the value of the state parameter returned from the OAuth2 provider
                 with the value that it had sent initially.
                 If theydon’t match then it denies the authentication request.
                 To achieve this flow, the application needs to store the state parameter
                 somewhere so that it can later compare it with the state returned from the OAuth2 provider.
                 We’ll be storing the state as well as the redirect_uri in a short-lived cookie.
                 The following class provides functionality for storing the authorization request in cookies and retrieving it.
              */
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(oauth2DetailsService)
                .oidcUserService(hamsooOidcUserDetailsService)
                .and()
                /* If the OAuth2 callback is successful and it contains the authorization code, Spring Security will exchange the authorization_code for an access_token and invoke the HamsooOAuth2UserService specified in the above SecurityConfig.
                the HamsooOAuth2UserService retrieves the details of the authenticated user and creates a new entry in the database or updates the existing entry with the same email.
                finally, the oAuth2AuthenticationSuccessHandler is invoked. It creates a JWT authentication token for the user and sends the user to the redirect_uri along with the JWT token in a query string. */
                .successHandler(oAuth2AuthenticationSuccessHandler)
                //if the OAuth2 callback results in an error, Spring security will invoke the oAuth2AuthenticationFailureHandler
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and().logout().logoutUrl("/user/logout");

        httpSecurity.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(additionalRegisterationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(simpleCorsFilter(), ChannelProcessingFilter.class)
                .addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class);


    }


}
