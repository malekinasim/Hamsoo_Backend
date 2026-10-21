package com.hamsoo.bpm.common.config.security.handler;

import com.hamsoo.bpm.common.config.security.config.HttpCookieOAuth2AuthorizationRequestRepository;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.Token;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.common.utils.security.CookieUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.refreshtoken.RefreshToken;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.refreshtoken.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.hamsoo.bpm.common.utils.security.CookieUtils.REDIRECT_URI_PARAM_COOKIE_NAME;

@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    @Value("${hamsoo.jwt.expiration.accesstoken.cookiename}")
    private String accsessTokenCookieName;

    @Value("${hamsoo.curernt.organ.cookiename}")
    private String curOrgCookieName;

    @Value("${hamsoo.server.ssl.enabled}")
    private Boolean isSecure;

    private final AccountService accountService;

    private JwtTokenProvider tokenProvider;
    private RefreshTokenService refreshTokenService;
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    public OAuth2AuthenticationSuccessHandler(AccountService accountService, JwtTokenProvider tokenProvider, RefreshTokenService refreshTokenService, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.accountService = accountService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;

        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response, authentication);
        //  has already had its status code and headers written.
        if (response.isCommitted()) {
            return;
        }
        this.clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //remove cookie
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        Token token = tokenProvider.issueToken(authentication);

        CookieUtils.addCookie(response, accsessTokenCookieName, token.getTokenValue(),
                (int) (token.getDuration() / 1000), isSecure, false);

        if (SecurityUtils.getCurrentOrgan().isPresent()) {
            CookieUtils.addCookie(response, curOrgCookieName, SecurityUtils.getCurrentOrgan().get().getId().toString(),
                    (int) (token.getDuration() / 1000), isSecure, false);
        } else {
            if (authentication.getPrincipal() instanceof HamsooUserDetail) {
                HamsooUserDetail hamsooUserDetail = (HamsooUserDetail) authentication.getPrincipal();
                if (hamsooUserDetail.getCurrentOrgan() != null) {
                    CookieUtils.addCookie(response, curOrgCookieName, hamsooUserDetail.getCurrentOrgan().getId().toString(),
                            (int) (token.getDuration() / 1000), isSecure, false);
                }
            }
        }
        Optional<Account> account = accountService.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername());
        RefreshToken refresh = refreshTokenService.createRefreshToken(account.get().getId());
        CookieUtils.addCookie(response, "refreshToken", refresh.getToken(),
                (int) (refresh.getDuration() / 1000), isSecure, false);
        //TODO
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("maxage", refresh.getDuration())
                .build().toUriString();


    }


}
