package com.hamsoo.bpm.common.exception.handler.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;
    @Resource
    Environment environment;

    public WebSocketConfiguration(AuthChannelInterceptor authChannelInterceptor) {
        this.authChannelInterceptor = authChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Set prefixes for the endpoint that the client listens for our messages from
        registry.enableSimpleBroker("/topic", "/queue");
        // Set prefix for endpoints the client will send messages to
        registry.setApplicationDestinationPrefixes("/web-socket");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Registers the endpoint where the handshake will take place
        // Tell Spring to ignore securing the handshake endpoint and must set /stomp/** path unsecured.
        registry.addEndpoint("/stomp")
                .setAllowedOrigins(this.environment.getProperty("cors.allowedOrigins"))
                .withSockJS();// Enable SockJS fallback options
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add our interceptor for authentication/authorization
        registration.interceptors(authChannelInterceptor);
    }
}
