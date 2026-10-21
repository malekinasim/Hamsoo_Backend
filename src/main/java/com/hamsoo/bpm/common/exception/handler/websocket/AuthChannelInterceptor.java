package com.hamsoo.bpm.common.exception.handler.websocket;

import com.hamsoo.bpm.common.config.security.filter.JWTAuthenticationFilter;
import com.hamsoo.bpm.common.config.security.service.impl.hamsoouserdetail.HamsooUserDetailsService;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final HamsooUserDetailsService hamsooUserDetailsService;
    @Value("${hamsoo.curernt.organ.cookiename}")
    private String curOrgCookieName;

    public AuthChannelInterceptor(JwtTokenProvider tokenProvider,
                                  @Lazy HamsooUserDetailsService hamsooUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.hamsooUserDetailsService = hamsooUserDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) { //todo test method after implement front

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new IllegalStateException("stompHeaderAccessor object is null");
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            Claims claims = tokenProvider.parseTokenAndGetClaims(extractJwtToken(accessor));
            String username = (String) claims.get("username");
            if (!StringUtils.hasText(username)) {
                throw new IllegalArgumentException("jwt token username claim not valid.");
            }

            try {
                String curOrgan = accessor.getFirstNativeHeader(curOrgCookieName);
                if (curOrgan == null)
                    throw new IllegalArgumentException(" user cur organ not valid.");
                UserDetails userDetails = hamsooUserDetailsService.loadUserByUsername(username, curOrgan);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return message;
    }

    private String extractJwtToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(JWTAuthenticationFilter.AUTHORIZATION_HEADER_KEY);
        if (!StringUtils.hasText(authorization) ||
                !authorization.startsWith(JWTAuthenticationFilter.BEARER)) {
            throw new IllegalArgumentException("authorization key not set in header");
        }
        String jwtToken = authorization.substring(7);
        if (jwtToken == null) throw new IllegalArgumentException("authorization key not set in header");
        if (tokenProvider.validateAndParseToken(jwtToken)) {
            return jwtToken;
        }
        throw new IllegalArgumentException("jwt token not valid");
    }
}
