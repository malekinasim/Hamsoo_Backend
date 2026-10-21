package com.hamsoo.bpm.common.config.security.handler;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.Token;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.common.utils.security.CookieUtils;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Service
public class SwitchUserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final AccountService accountService;


    @Value("${hamsoo.curernt.organ.cookiename}")
    private String curOrgCookieName;

    @Value("${hamsoo.jwt.expiration.accesstoken.cookiename}")
    private String accsessTokenCookieName;


    @Value("${hamsoo.server.ssl.enabled}")
    private Boolean isSecure;

    private RefreshTokenService refreshTokenService;


    public SwitchUserAuthenticationSuccessHandler(JwtTokenProvider tokenProvider,
                                                  AccountService accountService, RefreshTokenService refreshTokenService) {
        this.tokenProvider = tokenProvider;
        this.accountService = accountService;

        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //has already had its status code and headers written.
        if (response.isCommitted()) {
            return;
        }
        Token token = tokenProvider.issueToken(authentication);
        CookieUtils.addCookie(response, accsessTokenCookieName, token.getTokenValue(),
                (int) (token.getDuration() / 1000), isSecure, false);

        String organId = request.getParameter("organId");

        CookieUtils.addCookie(response, curOrgCookieName, organId,
                (int) (token.getDuration() / 1000), isSecure, false);

        Optional<Account> account = accountService.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername());
        RefreshToken refresh = refreshTokenService.createRefreshToken(account.get().getId());
        CookieUtils.addCookie(response, "refreshToken", refresh.getToken(),
                (int) (token.getDuration() / 1000), isSecure, false);

        try {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = response.getWriter();
            String redirect = request.getParameter("redirect");
            writer.write(UriComponentsBuilder.fromUriString(redirect)
                    .build().toUriString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // ignore
            logger.error(e.getMessage(), e);
        }
    }
}
