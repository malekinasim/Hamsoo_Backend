package com.hamsoo.bpm.common.config.security.config;

import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class OAuth2Config {

    @Resource
    private OauthProviderService oauthProviderService;

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<OauthProvider> oauthProviders = oauthProviderService.findAll();
        List<ClientRegistration> registrations = oauthProviders.stream()
                .filter(p -> p.getType().equals(OauthProvider.providerType.OIDC.name()))
                .map(c -> getRegistration(c)).filter(registration -> registration != null)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(OauthProvider client) {
        if (client == null && client.getRegisterationId() == null) {
            return null;
        }


        if (client.getRegisterationId().toUpperCase().equals(CommonOAuth2Provider.GOOGLE.name())) {

            return CommonOAuth2Provider.GOOGLE.getBuilder(client.getRegisterationId())
                    .clientId(client.getClientId())
                    .clientSecret(client.getClientSecret())
                    .redirectUri(client.getRedirectUri())
                    .scope("profile", "email", "address", "phone")
                    .build();
        } else if (client.getRegisterationId().toUpperCase().equals(CommonOAuth2Provider.GITHUB.name())) {
            return CommonOAuth2Provider.GITHUB.getBuilder(client.getRegisterationId())
                    .clientId(client.getClientId())
                    .clientSecret(client.getClientSecret())
                    .redirectUri(client.getRedirectUri())
                    .build();
        } else {
            try {
                ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(client.getRegisterationId());
                builder.clientAuthenticationMethod(new ClientAuthenticationMethod(client.getClientAuthenticationMethod()));
                builder.authorizationGrantType(new AuthorizationGrantType(client.getAuthorizationGrantType()));
                builder.redirectUri(client.getRedirectUri());
                builder.scope(Arrays.asList(client.getScope().split(",")));
                builder.issuerUri(client.getIssuerUri());
                builder.tokenUri(client.getTokenUri());
                builder.authorizationUri(client.getAuthorizationUri());
                builder.jwkSetUri(client.getJwkSetUri());
                builder.clientName(client.getRegisterationId());
                builder.clientId(client.getClientId());
                builder.clientSecret(client.getClientSecret());
                return builder.build();
            } catch (Exception e) {
                return null;
            }
        }
    }

}
