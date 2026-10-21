package com.hamsoo.bpm.security.config;
import com.hamsoo.bpm.security.filter.CamundaAuthProvider;
import com.hamsoo.bpm.security.group.GroupService;
import com.hamsoo.bpm.security.filter.CamundaAuthzService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;


import javax.annotation.Resource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@PropertySource("classpath:application-security.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   @Resource
      Environment environment;


   @Autowired
   GroupService groupService;

    @Autowired
    private CamundaAuthzService camundaAuthzService;


    @Autowired
    CamundaAuthProvider camundaAuthProvider;
    private static final String[] AUTH_WHITELIST = new String[]{"/api/public/**","/auth/**", "/oauth2/**", "/hamsoo/oauth2/**"};


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic().disable().exceptionHandling(exceptionHandling->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );
         http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                 .and().authorizeRequests(expressionInterceptUrlRegistry ->
                                expressionInterceptUrlRegistry.antMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                        ).oauth2Login()
                    .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.baseUri("/oauth2/authorize"))


           //  .and().addFilterAfter(new CamundaAuthFilter(camundaAuthProvider,camundaAuthzService), AuthorizationFilter.class)
//                .userInfoEndpoint(userInfoEndpoint ->userInfoEndpoint.userService(this.oauth2UserService())
//                        .oidcUserService(this.oidcUserService())
//               )
//                .and()
//                .addFilterBefore(FilterRegistrationBean, BasicAuthenticationFilter.class)
        ;
//        http .cors()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf()
//                .disable()
//                .httpBasic()
//                .disable()
////                .exceptionHandling(exceptionHandling->
////                        exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
////                )
////                .addFilterAfter(new TokenAthenticationFilter(), OAuth2AuthorizationCodeGrantFilter.class)
////                .addFilterBefore(new SimpleCorsFilter(this.environment), ChannelProcessingFilter.class)
//                .authorizeRequests(expressionInterceptUrlRegistry ->
//                        expressionInterceptUrlRegistry.antMatchers(AUTH_WHITELIST).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin().and()
//                .oauth2Login()
//                .clientRegistrationRepository(clientRegistrationRepository())
//                .authorizedClientService(authorizedClientService())
//                .authorizationEndpoint(authorizationEndpoint ->
//                        authorizationEndpoint
//                                .baseUri("/oauth2/authorize")
//                                .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
//              .redirectionEndpoint(redirectionEndpoint->redirectionEndpoint.baseUri("/oauth2/callback"))
//       // .userInfoEndpoint().userService(customOAuth2UserService)
//                .userInfoEndpoint(userInfoEndpoint ->userInfoEndpoint.userService(this.oauth2UserService())
//                        .oidcUserService(this.oidcUserService())
//               )
//       .successHandler( oAuth2AuthenticationSuccessHandler)
//        ;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth)
//            throws Exception
//    {
//        auth.authenticationProvider((AuthenticationProvider) new HttpBasicAuthenticationProvider());
//    }
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(new CamundaSecurityAuthenticationProvider(camundaAuthzService));
//    }



//}
//    @Bean
//    public TokenProvider getTokenProvider(){
//        return new TokenProvider(this.environment);
//    }



//
//    @Bean
//    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }



//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
//        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();// breakpoint here
//        return userRequest -> {
//            OAuth2User user = delegate.loadUser(userRequest); //and breakpoint here
//            OAuth2AccessToken accessToken = userRequest.getAccessToken();
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//            mappedAuthorities.addAll( user.getAuthorities());
//            List<Group> groups= groupService.findAllGroupofUsers(user.getAttribute("sub"));
//            if(groups!=null && !groups.isEmpty()){
//                groups.stream()
//                        .filter(group->group.getGroupAuthorities()!=null
//                                && !group.getGroupAuthorities().isEmpty())
//                        .forEach(group->{
//                            group.getGroupAuthorities().stream().forEach( groupAuthority -> {
//                                OAuth2UserAuthority authority =
//                                        new OAuth2UserAuthority("ROLE_" + groupAuthority.getAuthority(),user.getAttributes());
//                                mappedAuthorities.add(authority);
//                            });
//                        });
//            }
//            OAuth2User oAuth2User=new DefaultOAuth2User(mappedAuthorities,user.getAttributes(),"sub");
//            return oAuth2User;
//        };
//    }
//
//    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
//        final OidcUserService delegate = new OidcUserService();
//
//        return (userRequest) -> {
//            OidcUser oidcUser = delegate.loadUser(userRequest);
//            //use if authority fetch by remote service it is used
//            OAuth2AccessToken accessToken = userRequest.getAccessToken();
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//            OidcIdToken idToken = oidcUser.getIdToken();
//            OidcUserInfo userInfo = oidcUser.getUserInfo();
//           List<Group> groups= groupService.findAllGroupofUsers(userInfo.getPreferredUsername());
//            mappedAuthorities.addAll(oidcUser.getAuthorities());
//
//           if(groups!=null && !groups.isEmpty()){
//               groups.stream()
//                       .filter(group->group.getGroupAuthorities()!=null
//                               && !group.getGroupAuthorities().isEmpty())
//                       .forEach(group->{
//                               group.getGroupAuthorities().stream().forEach( groupAuthority -> {
//                                   OidcUserAuthority authority =
//                                           new OidcUserAuthority("ROLE_" + groupAuthority.getAuthority() , idToken, userInfo);
//                                   mappedAuthorities.add(authority);
//                                    });
//                                });
//           }
//            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
//            return oidcUser;
//        };
//    }
//
//

//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        List<ClientRegistration> registrations = clients.stream()
//                .map(c -> getRegistration(c))
//                .filter(registration -> registration != null)
//                .collect(Collectors.toList());
//
//        return new InMemoryClientRegistrationRepository(registrations);
//    }
//
//    private ClientRegistration getRegistration(String client) {
//        if(client==null){
//            return null;
//        }
//        String clientId = environment.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
//
//        if (clientId == null) {
//            return null;
//        }
//
//        String clientSecret = environment.getProperty(
//                CLIENT_PROPERTY_KEY + client + ".client-secret");
//        if(client.equals("hamsoo")) {
/////for hamsoo setting
//            ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(client);
//            builder.clientAuthenticationMethod(new ClientAuthenticationMethod(environment.getProperty(CLIENT_PROPERTY_KEY + client + ".client-authentication-method")));
//            builder.authorizationGrantType(new AuthorizationGrantType(environment.getProperty(CLIENT_PROPERTY_KEY + client + ".authorization-grant-type")));
//            builder.redirectUri(environment.getProperty(CLIENT_PROPERTY_KEY + client + ".redirect-uri"));
//            builder.scope(Arrays.asList(environment.getProperty(CLIENT_PROPERTY_KEY + client + ".scope").split(" ")));
//            builder.issuerUri(environment.getProperty(PROVIDER_PROPERTY_KEY + client + ".issuer-uri"));
//            builder.tokenUri(environment.getProperty(PROVIDER_PROPERTY_KEY + client + ".token-uri"));
//            builder.authorizationUri(environment.getProperty(PROVIDER_PROPERTY_KEY + client + ".authorization-uri"));
//            builder.jwkSetUri(environment.getProperty(PROVIDER_PROPERTY_KEY + client + ".jwk-set-uri"));
//            builder.clientName(client);
//            builder.clientId(clientId);
//            builder.clientSecret(clientSecret);
//            return builder.build();
//        }
//        return null;
//
//    }
//
//
//
//    @Bean
//    public OAuth2AuthorizedClientService authorizedClientService() {
//
//        return new InMemoryOAuth2AuthorizedClientService(
//                clientRegistrationRepository());
//    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList(environment.getProperty("cors.allowedOrigins")));
//        configuration.setAllowedMethods(Arrays.asList(environment.getProperty("cors.allowedMethods").split(",")));
//        configuration.setAllowedHeaders(Arrays.asList(environment.getProperty("cors.allowedHeaders").split(",")));
//        configuration.setMaxAge(Long.valueOf(environment.getProperty("cors.maxAge")));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

//    @Bean
//    public FilterRegistrationBean authFilter() {
//        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
//        filterRegistration.setFilter(new CamundaAuthFilter());
//        filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider", CamundaAuthProvider.class.getCanonicalName()));
//        filterRegistration.addUrlPatterns("/app/*", "/api/*", "/lib/*");
//        return filterRegistration;
//    }

}
