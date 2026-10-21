package com.hamsoo.bpm.common.config.security.filter;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.config.security.service.impl.hamsoouserdetail.HamsooUserDetailsService;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.common.utils.security.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private HamsooUserDetailsService hamsooUserDetailsService;

    @Value("${hamsoo.curernt.organ.cookiename}")
    private String curOrgCookieName;

    @Value("${hamsoo.jwt.expiration.accesstoken.cookiename}")
    private String accsessTokenCookieName;

    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        removeAuthorizationTokenCookies(request, response);
        try {
            String jwtToken = tokenProvider.getJwtToken(request);
            if (StringUtils.hasText(jwtToken)
                    && tokenProvider.validateAndParseToken(jwtToken)) {
                Claims claims = tokenProvider.parseTokenAndGetClaims(jwtToken);
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                if (existingAuth == null || !existingAuth.isAuthenticated()) {
                    Authentication authentication = setAuthentication(claims, request, jwtToken);
                    if (authentication == null) {
                        SecurityContextHolder.clearContext();
                    } else {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            if (!request.getRequestURI().equals("/hamsoo/api/v1/auth/refreshtoken")
                    && !request.getRequestURI().equals("/hamsoo/api/v1/auth/logoff")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            } else {
                chain.doFilter(request, response);
            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private Authentication setAuthentication(Claims claims, HttpServletRequest request, String jwtToken) {
        if (claims.get("username") != null && StringUtils.hasText(claims.get("username").toString())) {
            String username = claims.get("username").toString();
            UserDetails userDetails = hamsooUserDetailsService.loadUserByUsername(username);
            if (userDetails == null || (((HamsooUserDetail) userDetails).getUser() != null && ((HamsooUserDetail) userDetails).getCurrentOrgan() == null)) {
                return null;
            } else {
                UsernamePasswordAuthenticationToken authentication = tokenProvider.getAuthenticationToken(jwtToken, userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                return authentication;
            }
        } else {
            return null;
        }
    }

    private void removeAuthorizationTokenCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, curOrgCookieName);
        CookieUtils.deleteCookie(request, response, accsessTokenCookieName);
        CookieUtils.deleteCookie(request, response, "refreshToken");
    }
}
