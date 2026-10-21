package com.hamsoo.bpm.security.filter;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class TokenAthenticationFilter extends GenericFilterBean {

    Environment environment;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
//            this.destroy();
//        } else {

        servletRequest.getClass();
        servletRequest.getClass().getName();
       HttpServletRequest httpServletRequest= ((HttpServletRequest) servletRequest);
       String accessToken = getTokenFromRequest(httpServletRequest);
            if (StringUtils.hasText(accessToken) &&  validateToken(accessToken)) {
//                OAuth2AuthenticationToken oAuth2AuthenticationToken=new OAuth2AuthenticationToken();
//                SecurityContextHolder.getContext().getAuthentication();
            }else{
                this.destroy();
            }


       // }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private boolean validateToken(String accessToken) {
//        try {
//            Jwts.parser().setSigningKey().parseClaimsJws(accessToken);
//        }catch (Exception ){
//
//        }
        return false;

    }


    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());

        }
        return null;
    }

    @Override
    public void destroy() {
    }
}
